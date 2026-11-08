package com.nexusdrive.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Singleton sound manager for NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Short one-shot effects are played via {@link AudioClip} (low latency).
 * Looping background sounds (engine hum, rain, ambient city) are managed via
 * {@link MediaPlayer}. All resource loading is guarded by try-catch so that
 * the game runs correctly even when audio files are absent from the classpath.
 *
 * <p>Sound files are expected at {@code src/main/resources/sounds/} and should
 * be in WAV or MP3 format.
 */
public class SoundManager {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static SoundManager instance;

    private SoundManager() {
        loadSounds();
    }

    /** Returns the singleton instance, creating it on first access. */
    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ── Volume state ──────────────────────────────────────────────────────────
    private double  masterVolume  = 0.8;
    private double  sfxVolume     = 0.8;
    private double  musicVolume   = 0.8;
    private boolean soundEnabled  = true;

    // ── One-shot AudioClips ───────────────────────────────────────────────────
    private AudioClip collisionClip;
    private AudioClip nearMissClip;
    private AudioClip nitroClip;
    private AudioClip coinClip;
    private AudioClip rewindClip;
    private AudioClip hornClip;

    // ── Looping MediaPlayers ──────────────────────────────────────────────────
    private MediaPlayer enginePlayer;
    private MediaPlayer rainPlayer;
    private MediaPlayer ambientPlayer;

    // ─────────────────────────────────────────────────────────────────────────
    // Initialisation
    // ─────────────────────────────────────────────────────────────────────────

    private void loadSounds() {
        collisionClip  = tryLoadClip("collision.wav");
        nearMissClip   = tryLoadClip("near_miss.wav");
        nitroClip      = tryLoadClip("nitro.wav");
        coinClip       = tryLoadClip("coin.wav");
        rewindClip     = tryLoadClip("rewind.wav");
        hornClip       = tryLoadClip("horn.wav");

        enginePlayer   = tryLoadMedia("engine_loop.wav");
        rainPlayer     = tryLoadMedia("rain_loop.wav");
        ambientPlayer  = tryLoadMedia("city_ambient.wav");
    }

    /**
     * Attempts to load a short sound as an {@link AudioClip}.
     * Returns {@code null} silently if the resource is not found.
     */
    private AudioClip tryLoadClip(String filename) {
        try {
            var url = getClass().getResource("/sounds/" + filename);
            if (url != null) return new AudioClip(url.toExternalForm());
        } catch (Exception ignored) { /* degrade gracefully */ }
        return null;
    }

    /**
     * Attempts to load a looping background track as a {@link MediaPlayer}.
     * Returns {@code null} silently if the resource is not found.
     */
    private MediaPlayer tryLoadMedia(String filename) {
        try {
            var url = getClass().getResource("/sounds/" + filename);
            if (url != null) {
                Media       media  = new Media(url.toExternalForm());
                MediaPlayer player = new MediaPlayer(media);
                player.setCycleCount(MediaPlayer.INDEFINITE);
                return player;
            }
        } catch (Exception ignored) { /* degrade gracefully */ }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // One-shot playback
    // ─────────────────────────────────────────────────────────────────────────

    public void playCollision() { play(collisionClip); }
    public void playNearMiss()  { play(nearMissClip);  }
    public void playNitro()     { play(nitroClip);     }
    public void playCoin()      { play(coinClip);      }
    public void playRewind()    { play(rewindClip);    }
    public void playHorn()      { play(hornClip);      }

    /**
     * Plays a one-shot clip at the current effective SFX volume.
     * No-op when sound is disabled or the clip is {@code null}.
     */
    private void play(AudioClip clip) {
        if (!soundEnabled || clip == null) return;
        clip.setVolume(sfxVolume * masterVolume);
        clip.play();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Looping playback control
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Starts or adjusts the engine loop.
     * The playback rate is modulated by {@code speedFraction} to simulate
     * RPM changes: 0.7× at idle, 1.3× at full speed.
     *
     * @param speedFraction current speed as a fraction of max speed [0, 1]
     */
    public void startEngine(double speedFraction) {
        if (enginePlayer == null) return;
        enginePlayer.setRate(0.7 + speedFraction * 0.6);
        if (enginePlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            enginePlayer.play();
        }
    }

    /**
     * Starts the rain ambient loop (called when weather switches to rain/storm).
     */
    public void startRain() {
        if (rainPlayer != null
                && rainPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            rainPlayer.play();
        }
    }

    /** Stops the rain ambient loop. */
    public void stopRain() {
        if (rainPlayer != null) rainPlayer.stop();
    }

    /**
     * Starts the city ambient loop (traffic noise, distant honking, etc.).
     */
    public void startAmbient() {
        if (ambientPlayer != null
                && ambientPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            ambientPlayer.play();
        }
    }

    /** Stops all looping background sounds. */
    public void stopAll() {
        if (enginePlayer  != null) enginePlayer.stop();
        if (rainPlayer    != null) rainPlayer.stop();
        if (ambientPlayer != null) ambientPlayer.stop();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Volume control
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sets the master volume [0, 1] and updates all active players.
     *
     * @param vol new master volume
     */
    public void setMasterVolume(double vol) {
        masterVolume = Math.max(0.0, Math.min(1.0, vol));
        updateVolumes();
    }

    /**
     * Sets the SFX volume [0, 1] (applied on top of master volume for clips).
     *
     * @param vol new SFX volume
     */
    public void setSfxVolume(double vol) {
        sfxVolume = Math.max(0.0, Math.min(1.0, vol));
        updateVolumes();
    }

    private void updateVolumes() {
        if (enginePlayer  != null) enginePlayer.setVolume(masterVolume * 0.5);
        if (rainPlayer    != null) rainPlayer.setVolume(masterVolume * sfxVolume);
        if (ambientPlayer != null) ambientPlayer.setVolume(masterVolume * 0.4);
    }

    // ── Global mute ───────────────────────────────────────────────────────────

    /**
     * Enables or disables all sound output.
     * When disabled, looping players are paused rather than stopped so they
     * can be resumed cleanly.
     *
     * @param enabled {@code true} to allow audio, {@code false} to mute
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            if (enginePlayer  != null) enginePlayer.pause();
            if (rainPlayer    != null) rainPlayer.pause();
            if (ambientPlayer != null) ambientPlayer.pause();
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────
    public double  getMasterVolume() { return masterVolume; }
    public double  getSfxVolume()    { return sfxVolume; }
    public double  getMusicVolume()  { return musicVolume; }
    public void    setMusicVolume(double vol) {
        musicVolume = Math.max(0.0, Math.min(1.0, vol));
        updateVolumes();
    }
    public boolean isSoundEnabled()  { return soundEnabled; }
}
