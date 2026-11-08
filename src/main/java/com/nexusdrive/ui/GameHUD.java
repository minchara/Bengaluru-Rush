package com.nexusdrive.ui;

import com.nexusdrive.GameMode;
import com.nexusdrive.world.LocationType;
import com.nexusdrive.world.WeatherSystem;
import com.nexusdrive.entity.PlayerCar;
import com.nexusdrive.system.ScoreManager;
import com.nexusdrive.system.NitroSystem;
import com.nexusdrive.system.TimeRewindManager;
import com.nexusdrive.system.NearMissDetector;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * In-game HUD overlay drawn onto a transparent {@link Canvas}.
 *
 * <p>The canvas is laid over the game viewport with
 * {@code canvas.setMouseTransparent(true)} so all mouse/key events pass
 * through to the game layer underneath.</p>
 *
 * <p>Call {@link #draw} once per game-loop frame, passing in the live game
 * state objects. All drawing is done via a single {@link GraphicsContext};
 * the canvas is fully cleared each frame.</p>
 */
public class GameHUD {

    private final Canvas         canvas;
    private final GraphicsContext gc;

    // ── Flash-text timers ──────────────────────────────────────────────────────
    private String nearMissText      = null;
    private double nearMissFlashTimer = 0;

    private String comboText         = null;
    private double comboFlashTimer   = 0;

    private String locationText      = null;
    private double locationFlashTimer = 0;

    private boolean showPauseHint    = false;

    // ── Rewind overlay state ───────────────────────────────────────────────────
    private boolean isRewinding      = false;
    private double  rewindEffectTime = 0;

    public GameHUD(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        canvas.setMouseTransparent(true);
    }

    // ── Main draw entry-point ─────────────────────────────────────────────────

    /**
     * Clears the canvas and redraws all HUD elements for the current frame.
     *
     * @param player    the player car (speed, maxSpeed)
     * @param score     score / distance / coin counters
     * @param nitro     nitro charge state
     * @param rewind    time-rewind energy state
     * @param weather   current weather
     * @param location  current location
     * @param mode      current game mode
     * @param nearMiss  near-miss detector (flash state)
     * @param dt        delta time in seconds since the last frame
     */
    public void draw(PlayerCar player,
                     ScoreManager score,
                     NitroSystem nitro,
                     TimeRewindManager rewind,
                     WeatherSystem weather,
                     LocationType location,
                     GameMode mode,
                     NearMissDetector nearMiss,
                     double dt) {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawTopBar(score, location, mode, weather);
        drawSpeedometer(player.getSpeedKmh(), player.getMaxSpeed());
        drawNitroBar(nitro);
        drawRewindBar(rewind);

        if (nearMiss != null && nearMiss.isFlashing()) {
            String lbl = nearMiss.getLastNearMissLabel();
            drawNearMissFlash(lbl != null ? lbl : "NEAR MISS!");
            nearMissFlashTimer = 0.6; // sustain slightly longer than NearMissDetector timer
        }

        if (rewind.isRewinding()) {
            drawRewindOverlay();
        }

        if (score.getComboMultiplier() > 1) {
            drawComboIndicator(score.getComboMultiplier(), score.getComboCount());
        }

        // Location intro banner
        if (locationFlashTimer > 0) {
            drawLocationIntro();
            locationFlashTimer -= dt;
        }

        // Update flash timers
        nearMissFlashTimer -= dt;
        if (nearMissFlashTimer < 0) nearMissText = null;
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private void drawTopBar(ScoreManager score, LocationType location,
                            GameMode mode, WeatherSystem weather) {
        double w = canvas.getWidth();

        // Semi-transparent panel
        gc.setFill(Color.web("#0A0A1A", 0.82));
        gc.fillRect(0, 0, w, 50);

        // ── Score ──
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        gc.setFill(Color.web("#00CCFF"));
        gc.fillText("SCORE: " + String.format("%,d", score.getScore()), 20, 32);

        // ── Distance ──
        gc.setFill(Color.web("#AAAACC"));
        gc.fillText(String.format("%.2f KM", score.getDistanceKm()), 240, 32);

        // ── Coins ──
        gc.setFill(Color.web("#FF8800"));
        gc.fillText("🪙 " + score.getCoins(), 400, 32);

        // ── Location (center) ──
        gc.setFill(Color.web("#CC00FF"));
        String locText = location.getDisplayName() + "  |  " + location.getKannadaName();
        double locX = w / 2.0 - locText.length() * 5.5;
        gc.fillText(locText, locX, 32);

        // ── Mode tag ──
        gc.setFont(Font.font("Monospace", 12));
        gc.setFill(Color.web("#666699"));
        gc.fillText(mode.getDisplayName(), locX, 46);

        // ── Weather (right) ──
        String weatherIcon = switch (weather.getWeather()) {
            case MONSOON -> "🌧 MONSOON";
            case FOGGY   -> "🌫 FOGGY";
            case NIGHT   -> "🌙 NIGHT";
            default      -> "☀ CLEAR";
        };
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        gc.setFill(Color.web("#AAAACC"));
        gc.fillText(weatherIcon, w - 160, 32);

        // Separator line
        gc.setStroke(Color.web("#333366"));
        gc.setLineWidth(1);
        gc.strokeLine(0, 50, w, 50);
    }

    // ── Speedometer ───────────────────────────────────────────────────────────

    private void drawSpeedometer(double speedKmh, double maxSpeed) {
        double cx = 90, cy = canvas.getHeight() - 90;
        double radius = 70;

        // Outer ring
        gc.setFill(Color.web("#0A0A1A", 0.75));
        gc.fillOval(cx - radius - 4, cy - radius - 4, (radius + 4) * 2, (radius + 4) * 2);

        gc.setStroke(Color.web("#1A1A3A"));
        gc.setLineWidth(3);
        gc.strokeOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Speed arc
        double fraction = Math.min(1.0, speedKmh / maxSpeed);
        gc.setStroke(speedKmh > maxSpeed * 0.9 ? Color.web("#FF2244") : Color.web("#00CCFF"));
        gc.setLineWidth(6);
        gc.strokeArc(cx - radius + 5, cy - radius + 5,
                (radius - 5) * 2, (radius - 5) * 2,
                210, -fraction * 240,
                ArcType.OPEN);

        // Tick marks (every 20%)
        for (int t = 0; t <= 5; t++) {
            double ang = Math.toRadians(210 - t * 48);
            double x1 = cx + (radius - 10) * Math.cos(ang);
            double y1 = cy - (radius - 10) * Math.sin(ang);
            double x2 = cx + (radius - 4) * Math.cos(ang);
            double y2 = cy - (radius - 4) * Math.sin(ang);
            gc.setStroke(Color.web("#444488"));
            gc.setLineWidth(1.5);
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Speed text
        String speedStr = String.format("%d", (int) speedKmh);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 22));
        gc.setFill(Color.WHITE);
        gc.fillText(speedStr, cx - speedStr.length() * 7, cy + 6);

        gc.setFont(Font.font("Monospace", 11));
        gc.setFill(Color.web("#00CCFF"));
        gc.fillText("KM/H", cx - 18, cy + 20);
    }

    // ── Nitro bar ─────────────────────────────────────────────────────────────

    private void drawNitroBar(NitroSystem nitro) {
        double x = canvas.getWidth() - 170;
        double y = canvas.getHeight() - 110;
        double w = 150, h = 20;

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        gc.setFill(nitro.isActive() ? Color.web("#FF8800") : Color.web("#00CCFF"));
        gc.fillText("NITRO" + (nitro.isActive() ? " ⚡" : ""), x, y - 5);

        gc.setFill(Color.web("#111128"));
        gc.fillRect(x, y, w, h);

        double fill = nitro.getChargePercent();
        Color nitroColor = nitro.isActive() ? Color.web("#FF8800") : Color.web("#00CCFF");
        gc.setFill(nitroColor);
        gc.fillRect(x, y, w * fill, h);

        gc.setStroke(Color.web("#333366"));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);

        // Segment dividers
        gc.setStroke(Color.web("#0A0A1A"));
        gc.setLineWidth(1.5);
        for (int i = 1; i < 5; i++) {
            double lx = x + w * i / 5.0;
            gc.strokeLine(lx, y, lx, y + h);
        }
    }

    // ── Rewind bar ────────────────────────────────────────────────────────────

    private void drawRewindBar(TimeRewindManager rewind) {
        double x = canvas.getWidth() - 170;
        double y = canvas.getHeight() - 150;
        double w = 150, h = 14;

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        gc.setFill(rewind.isRewinding() ? Color.web("#FF44FF") : Color.web("#CC00FF"));
        gc.fillText("REWIND" + (rewind.isRewinding() ? " ◀" : ""), x, y - 3);

        gc.setFill(Color.web("#111128"));
        gc.fillRect(x, y, w, h);

        gc.setFill(rewind.isRewinding() ? Color.web("#FF44FF") : Color.web("#9900CC"));
        gc.fillRect(x, y, w * rewind.getEnergyPercent(), h);

        gc.setStroke(Color.web("#333366"));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
    }

    // ── Near-miss flash ───────────────────────────────────────────────────────

    private void drawNearMissFlash(String text) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        gc.setEffect(new DropShadow(20, Color.web("#FF8800")));
        gc.setFill(Color.web("#FF8800"));
        double tw = text.length() * 20.0;
        gc.fillText(text, w / 2.0 - tw / 2.0, h * 0.3);
        gc.setEffect(null);
    }

    // ── Rewind overlay ────────────────────────────────────────────────────────

    private void drawRewindOverlay() {
        gc.setFill(Color.web("#CC00FF", 0.15));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 48));
        gc.setFill(Color.web("#FF44FF"));
        gc.setEffect(new DropShadow(30, Color.web("#CC00FF")));
        double w = canvas.getWidth();
        gc.fillText("◀ REWIND", w / 2.0 - 120, canvas.getHeight() / 2.0);
        gc.setEffect(null);
    }

    // ── Combo indicator ───────────────────────────────────────────────────────

    private void drawComboIndicator(int multiplier, int count) {
        double x = canvas.getWidth() - 210;
        double y = 70;

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 26));
        gc.setFill(Color.web("#FF8800"));
        gc.setEffect(new DropShadow(12, Color.web("#FF8800")));
        gc.fillText("x" + multiplier + "  COMBO!", x, y);
        gc.setEffect(null);

        gc.setFont(Font.font("Monospace", 14));
        gc.setFill(Color.web("#FFCC88"));
        gc.fillText(count + " near misses", x, y + 22);
    }

    // ── Location intro banner ─────────────────────────────────────────────────

    private void drawLocationIntro() {
        if (locationText == null) return;
        double w = canvas.getWidth();
        double alpha = Math.min(1.0, locationFlashTimer);

        gc.setFill(Color.web("#0A0A1A", 0.75 * alpha));
        gc.fillRect(0, canvas.getHeight() * 0.38, w, 90);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        gc.setFill(Color.web("#00CCFF", alpha));
        gc.setEffect(new DropShadow(20, Color.web("#00CCFF", alpha)));
        double tw = locationText.length() * 21.0;
        gc.fillText(locationText, w / 2.0 - tw / 2.0, canvas.getHeight() * 0.38 + 50);
        gc.setEffect(null);
    }

    // ── Game-over overlay ─────────────────────────────────────────────────────

    /**
     * Draws the game-over overlay. Call only when the game has ended.
     *
     * @param score the final score manager containing score, distance, and coins
     */
    public void drawGameOver(ScoreManager score) {
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.setFill(Color.web("#0A0A1A", 0.88));
        gc.fillRect(0, 0, w, h);

        // Title
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 56));
        gc.setFill(Color.web("#FF2244"));
        gc.setEffect(new DropShadow(30, Color.web("#FF2244")));
        gc.fillText("GAME OVER", w / 2.0 - 170, h / 2.0 - 60);
        gc.setEffect(null);

        // Stats
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 24));
        gc.setFill(Color.WHITE);
        gc.fillText("Score:    " + String.format("%,d", score.getScore()),  w / 2.0 - 120, h / 2.0);
        gc.fillText(String.format("Distance: %.2f KM", score.getDistanceKm()), w / 2.0 - 120, h / 2.0 + 36);
        gc.fillText("Coins:    " + score.getCoins(),                        w / 2.0 - 120, h / 2.0 + 72);

        // Hint
        gc.setFont(Font.font("Monospace", 18));
        gc.setFill(Color.web("#00CCFF"));
        gc.fillText("SPACE to retry  |  ESC for menu", w / 2.0 - 195, h / 2.0 + 125);

        // Decorative neon lines
        gc.setStroke(Color.web("#FF2244", 0.5));
        gc.setLineWidth(2);
        gc.strokeLine(w / 2.0 - 240, h / 2.0 - 10, w / 2.0 + 240, h / 2.0 - 10);
        gc.strokeLine(w / 2.0 - 240, h / 2.0 + 94, w / 2.0 + 240, h / 2.0 + 94);
    }

    // ── Pause overlay ─────────────────────────────────────────────────────────

    /**
     * Draws the pause overlay. Call when the game is paused.
     */
    public void drawPaused() {
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.setFill(Color.web("#0A0A1A", 0.72));
        gc.fillRect(0, 0, w, h);

        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 52));
        gc.setFill(Color.web("#00CCFF"));
        gc.setEffect(new DropShadow(20, Color.web("#00CCFF")));
        gc.fillText("PAUSED", w / 2.0 - 118, h / 2.0);
        gc.setEffect(null);

        gc.setFont(Font.font("Monospace", 20));
        gc.setFill(Color.WHITE);
        gc.fillText("ESC to resume  |  M for menu", w / 2.0 - 185, h / 2.0 + 52);
    }

    // ── Public utilities ──────────────────────────────────────────────────────

    /**
     * Triggers a 3-second location intro banner at the start of a race.
     *
     * @param locationName English display name
     * @param kannadaName  Kannada display name
     */
    public void showLocationIntro(String locationName, String kannadaName) {
        locationText = locationName + "  |  " + kannadaName;
        locationFlashTimer = 3.0;
    }

    /** Returns the underlying canvas so it can be added to the scene graph. */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Resizes the HUD canvas (call when the window is resized).
     *
     * @param w new width in pixels
     * @param h new height in pixels
     */
    public void resize(double w, double h) {
        canvas.setWidth(w);
        canvas.setHeight(h);
    }
}
