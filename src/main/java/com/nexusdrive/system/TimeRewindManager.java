package com.nexusdrive.system;

import com.nexusdrive.entity.AITraffic;
import com.nexusdrive.entity.PlayerCar;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a 5-second time-rewind mechanic backed by a fixed circular
 * snapshot buffer.
 *
 * <p><b>Lifecycle:</b>
 * <ol>
 *   <li>Call {@link #snapshot} every frame (60 fps) during normal play.</li>
 *   <li>When the player triggers rewind, call {@link #startRewind}.</li>
 *   <li>While rewinding, call {@link #applyRewindFrame} each frame instead of
 *       the normal update – it restores state from the buffer and walks the
 *       read head backward.</li>
 *   <li>Call {@link #stopRewind} when the player releases the rewind input or
 *       energy is exhausted.</li>
 * </ol>
 *
 * <p>The rewind uses a separate <em>energy</em> resource (0–100) that
 * discharges while rewinding and slowly recharges during normal play.
 */
public class TimeRewindManager {

    // ── Buffer configuration ──────────────────────────────────────────────────
    private static final int FPS           = 60;
    private static final int REWIND_SECONDS = 5;
    private static final int BUFFER_SIZE   = FPS * REWIND_SECONDS; // 300 frames

    // ── Circular snapshot buffer ──────────────────────────────────────────────
    private final GameSnapshot[] buffer = new GameSnapshot[BUFFER_SIZE];
    /** Points to the slot where the NEXT snapshot will be written. */
    private int writeHead     = 0;
    /** Number of valid snapshots currently in the buffer. */
    private int snapshotCount = 0;
    /** Points to the snapshot that will be applied on the NEXT rewind frame. */
    private int readHead      = 0;

    // ── Rewind state ──────────────────────────────────────────────────────────
    private boolean rewinding = false;

    // ── Rewind energy ─────────────────────────────────────────────────────────
    private double energy                  = 100.0;
    private static final double DISCHARGE_RATE = 20.0; // per second during rewind
    private static final double RECHARGE_RATE  =  8.0; // per second during normal play

    // ── Internal snapshot records ─────────────────────────────────────────────

    /**
     * Lightweight snapshot of a single AI vehicle's state.
     *
     * @param x     lateral position
     * @param z     road position
     * @param speed speed in km/h
     * @param state AI state machine state
     */
    private record VehicleSnapshot(
            double            x,
            double            z,
            double            speed,
            AITraffic.AIState state
    ) {}

    /**
     * Full-frame snapshot including player and all AI vehicles.
     *
     * @param playerX     player lateral position
     * @param playerZ     player road position
     * @param playerSpeed player speed in km/h
     * @param vehicles    list of vehicle snapshots
     */
    private record GameSnapshot(
            double               playerX,
            double               playerZ,
            double               playerSpeed,
            List<VehicleSnapshot> vehicles
    ) {}

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Records a full-frame snapshot of the player and all AI vehicles.
     * No-op while rewinding (buffer is consumed during rewind, not written to).
     *
     * @param player  current player state
     * @param traffic current AI vehicle list
     */
    public void snapshot(PlayerCar player, List<AITraffic> traffic) {
        if (rewinding) return;

        List<VehicleSnapshot> vSnaps = new ArrayList<>(traffic.size());
        for (AITraffic v : traffic) {
            vSnaps.add(new VehicleSnapshot(v.x, v.z, v.speed, v.state));
        }

        buffer[writeHead] = new GameSnapshot(player.x, player.z, player.speed, vSnaps);
        writeHead         = (writeHead + 1) % BUFFER_SIZE;
        snapshotCount     = Math.min(snapshotCount + 1, BUFFER_SIZE);

        // Recharge energy passively each frame
        energy = Math.min(100.0, energy + RECHARGE_RATE / FPS);
    }

    // ── Rewind control ────────────────────────────────────────────────────────

    /** Returns {@code true} if there are snapshots to replay and energy remains. */
    public boolean canRewind() {
        return snapshotCount > 1 && energy > 0;
    }

    /**
     * Initiates a rewind session.
     * The read head is positioned one frame before the current write head so
     * the first {@link #applyRewindFrame} call restores the most recent frame.
     */
    public void startRewind() {
        if (!canRewind()) return;
        rewinding = true;
        readHead  = (writeHead - 1 + BUFFER_SIZE) % BUFFER_SIZE;
    }

    /**
     * Ends the rewind session.
     * The write head is repositioned at the current read head so that normal
     * recording continues from the restored timeline point without overwriting
     * future (now-discarded) states.
     */
    public void stopRewind() {
        rewinding     = false;
        writeHead     = readHead;
        // Halve the recorded count to prevent phantom frames from previous timeline
        snapshotCount = Math.max(0, snapshotCount / 2);
    }

    /** Whether the system is currently in rewind mode. */
    public boolean isRewinding() { return rewinding; }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Applies one rewind frame: restores the player and AI vehicles from the
     * buffer, discharges energy, and walks the read head backward.
     *
     * <p>Must only be called while {@link #isRewinding()} is {@code true}.
     *
     * @param player  the player car to modify
     * @param traffic the AI vehicle list to modify
     * @param dt      delta-time in seconds (used for energy discharge)
     */
    public void applyRewindFrame(PlayerCar player, List<AITraffic> traffic, double dt) {
        if (!rewinding || snapshotCount <= 0) {
            stopRewind();
            return;
        }

        GameSnapshot snap = buffer[readHead];
        if (snap == null) {
            stopRewind();
            return;
        }

        // ── Restore player ────────────────────────────────────────────────────
        player.x     = snap.playerX();
        player.z     = snap.playerZ();
        player.speed = snap.playerSpeed();

        // ── Restore vehicles (index-matched) ─────────────────────────────────
        int matchCount = Math.min(traffic.size(), snap.vehicles().size());
        for (int i = 0; i < matchCount; i++) {
            VehicleSnapshot vs = snap.vehicles().get(i);
            AITraffic       v  = traffic.get(i);
            v.x     = vs.x();
            v.z     = vs.z();
            v.speed = vs.speed();
            v.state = vs.state();
        }

        // ── Discharge energy ──────────────────────────────────────────────────
        energy = Math.max(0.0, energy - DISCHARGE_RATE * dt);
        if (energy <= 0) {
            stopRewind();
            return;
        }

        // ── Advance read head backward through the buffer ─────────────────────
        readHead      = (readHead - 1 + BUFFER_SIZE) % BUFFER_SIZE;
        snapshotCount = Math.max(0, snapshotCount - 1);
        if (snapshotCount <= 0) stopRewind();
    }

    // ── Energy accessors ──────────────────────────────────────────────────────

    /** Raw energy value in [0, 100]. */
    public double getEnergy()        { return energy; }

    /** Energy as a fraction [0, 1] for HUD display. */
    public double getEnergyPercent() { return energy / 100.0; }

    /**
     * Externally adds rewind energy (e.g. from a pick-up collectible).
     *
     * @param amount amount to add
     */
    public void addEnergy(double amount) {
        energy = Math.min(100.0, energy + amount);
    }
}
