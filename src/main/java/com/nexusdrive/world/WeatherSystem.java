package com.nexusdrive.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the active weather state and all weather-related visual effects
 * (rain particles, fog overlays, sky tint).
 *
 * <h3>Integration</h3>
 * <pre>{@code
 *   WeatherSystem weather = new WeatherSystem();
 *   weather.setWeather(WeatherType.MONSOON);
 *
 *   // Inside game loop:
 *   weather.update(dt);
 *   weather.drawBackground(gc, width, height);
 *
 *   // Query physics modifiers:
 *   double speed   = car.baseSpeed * weather.getSpeedMultiplier();
 *   double control = car.handling  * weather.getHandlingMultiplier();
 * }</pre>
 */
public class WeatherSystem {

    // =========================================================================
    // Inner types
    // =========================================================================

    /** All supported weather conditions. */
    public enum WeatherType {
        /** Bright sunny day — no penalties. */
        CLEAR,
        /** Heavy Bengaluru rain — reduced speed and handling. */
        MONSOON,
        /** Low visibility fog — reduced speed, slightly reduced handling. */
        FOGGY,
        /** Night-time racing — mild handling penalty. */
        NIGHT
    }

    // -------------------------------------------------------------------------

    /** A single diagonal rain streak rendered each frame. */
    private static class RainParticle {
        double x;
        double y;
        /** Vertical fall speed in px/s. */
        double speed;
        /** Visible streak length in px. */
        double length;
        /** Whether the particle is currently visible. */
        boolean active;

        RainParticle() {}
    }

    // =========================================================================
    // Constants
    // =========================================================================

    private static final int   RAIN_PARTICLE_COUNT = 500;
    private static final double RAIN_ANGLE_DX       = 0.25; // px of horizontal drift per px vertical
    private static final double WETNESS_LERP_RATE   = 0.3;  // how fast wetness approaches target
    private static final double FOG_LERP_RATE       = 0.2;

    // =========================================================================
    // State
    // =========================================================================

    private WeatherType       currentWeather  = WeatherType.CLEAR;
    private final List<RainParticle> rainParticles  = new ArrayList<>(RAIN_PARTICLE_COUNT);
    private double            fogDensity      = 0.0;  // 0–1
    private double            wetness         = 0.0;  // 0–1, lerps up during MONSOON
    private double            screenWidth     = 800;
    private double            screenHeight    = 600;
    private final Random      random          = new Random();

    // =========================================================================
    // Constructor
    // =========================================================================

    public WeatherSystem() {
        // Pre-allocate all rain particles (most will be inactive until MONSOON).
        for (int i = 0; i < RAIN_PARTICLE_COUNT; i++) {
            rainParticles.add(new RainParticle());
        }
    }

    // =========================================================================
    // Public API — control
    // =========================================================================

    /**
     * Changes the active weather type and (re-)initialises all particles and
     * overlay values accordingly.
     *
     * @param type the new weather type; must not be {@code null}
     */
    public void setWeather(WeatherType type) {
        if (type == null) throw new IllegalArgumentException("WeatherType must not be null");
        this.currentWeather = type;

        boolean rainy = (type == WeatherType.MONSOON);

        // Activate / deactivate particles and scatter them across the screen.
        for (RainParticle p : rainParticles) {
            p.active = rainy;
            if (rainy) {
                p.x      = random.nextDouble() * screenWidth;
                p.y      = random.nextDouble() * screenHeight;
                p.speed  = 300 + random.nextDouble() * 300;   // 300–600 px/s
                p.length = 5   + random.nextDouble() * 10;    // 5–15 px
            }
        }

        // Set instant fog target; actual density lerps in update().
        switch (type) {
            case FOGGY   -> fogDensity = 0.0; // will lerp to target in update
            case MONSOON -> fogDensity = 0.0;
            default      -> fogDensity = 0.0;
        }
    }

    // =========================================================================
    // Public API — game-loop update
    // =========================================================================

    /**
     * Advances all weather particles and smoothly transitions overlay values.
     *
     * @param dt seconds elapsed since the last frame
     */
    public void update(double dt) {
        double targetWetness   = (currentWeather == WeatherType.MONSOON) ? 1.0 : 0.0;
        double targetFogDensity;
        switch (currentWeather) {
            case FOGGY   -> targetFogDensity = 0.6;
            case MONSOON -> targetFogDensity = 0.2;
            default      -> targetFogDensity = 0.0;
        }

        // Smooth lerp
        wetness   += (targetWetness    - wetness)   * WETNESS_LERP_RATE * dt * 60;
        fogDensity += (targetFogDensity - fogDensity) * FOG_LERP_RATE   * dt * 60;

        // Update rain particles
        if (currentWeather == WeatherType.MONSOON) {
            for (RainParticle p : rainParticles) {
                if (!p.active) continue;
                p.y += p.speed * dt;
                p.x += p.speed * RAIN_ANGLE_DX * dt;

                // Wrap when particle exits bottom or right edge
                if (p.y > screenHeight + p.length) {
                    p.y = -p.length;
                    p.x = random.nextDouble() * screenWidth;
                }
                if (p.x > screenWidth) {
                    p.x = random.nextDouble() * screenWidth * 0.5;
                }
            }
        }
    }

    // =========================================================================
    // Public API — rendering
    // =========================================================================

    /**
     * Draws all weather overlays onto the provided {@link GraphicsContext}.
     * This should be called <em>after</em> the road/world has been drawn and
     * <em>before</em> UI elements are drawn.
     *
     * @param gc     the canvas graphics context
     * @param width  current canvas width  (may differ from cached value)
     * @param height current canvas height
     */
    public void drawBackground(GraphicsContext gc, double width, double height) {
        // Cache screen dimensions for particle wrap-around.
        this.screenWidth  = width;
        this.screenHeight = height;

        // --- Rain streaks ---
        if (currentWeather == WeatherType.MONSOON) {
            gc.save();
            // Semi-transparent rain tint over the scene
            gc.setFill(Color.rgb(100, 140, 200, 0.08));
            gc.fillRect(0, 0, width, height);

            // Individual rain streaks
            gc.setStroke(Color.rgb(180, 220, 255, 0.55));
            gc.setLineWidth(1.0);
            for (RainParticle p : rainParticles) {
                if (!p.active) continue;
                double endX = p.x + p.length * RAIN_ANGLE_DX;
                double endY = p.y + p.length;
                gc.strokeLine(p.x, p.y, endX, endY);
            }
            gc.restore();
        }

        // --- Fog overlay ---
        if (fogDensity > 0.01) {
            drawFogOverlay(gc, width, height);
        }

        // --- Night vignette ---
        if (currentWeather == WeatherType.NIGHT) {
            drawNightVignette(gc, width, height);
        }
    }

    // =========================================================================
    // Private rendering helpers
    // =========================================================================

    private void drawFogOverlay(GraphicsContext gc, double width, double height) {
        gc.save();

        // Bottom fog band (ground-hugging fog is densest near the horizon).
        double bandHeight = height * 0.4 * fogDensity;
        double startY     = height - bandHeight;

        // Create a gradient from transparent at the top to foggy at the bottom.
        // JavaFX LinearGradient requires a Paint — we approximate with two rects.
        // Upper half of fog band — translucent
        gc.setFill(Color.rgb(200, 200, 200,
                Math.min(0.6, fogDensity * 0.4)));
        gc.fillRect(0, startY, width, bandHeight * 0.5);

        // Lower half — opaque-ish
        gc.setFill(Color.rgb(210, 210, 210,
                Math.min(0.75, fogDensity * 0.6)));
        gc.fillRect(0, startY + bandHeight * 0.5, width, bandHeight * 0.5);

        // Global haze tint on entire screen
        gc.setFill(Color.rgb(220, 220, 220, Math.min(0.35, fogDensity * 0.3)));
        gc.fillRect(0, 0, width, height);

        gc.restore();
    }

    private void drawNightVignette(GraphicsContext gc, double width, double height) {
        gc.save();
        // Dark vignette around edges simulating limited headlight range
        double vignetteAlpha = 0.45;
        // Top bar
        gc.setFill(Color.rgb(0, 0, 10, vignetteAlpha));
        gc.fillRect(0, 0, width, height * 0.2);
        // Left bar
        gc.fillRect(0, 0, width * 0.08, height);
        // Right bar
        gc.fillRect(width - width * 0.08, 0, width * 0.08, height);
        gc.restore();
    }

    // =========================================================================
    // Public API — physics multipliers
    // =========================================================================

    /**
     * @return a multiplier applied to the player's handling stat (0.55–1.0).
     */
    public double getHandlingMultiplier() {
        return switch (currentWeather) {
            case MONSOON -> 0.60;
            case FOGGY   -> 0.60;
            case NIGHT   -> 0.85;
            case CLEAR   -> 1.00;
        };
    }

    /**
     * @return a multiplier applied to the player's top speed (0.75–1.0).
     */
    public double getSpeedMultiplier() {
        return switch (currentWeather) {
            case MONSOON -> 0.80;
            case FOGGY   -> 0.90;
            case NIGHT   -> 1.00;
            case CLEAR   -> 1.00;
        };
    }

    // =========================================================================
    // Public API — colour palette
    // =========================================================================

    /**
     * Sky / background colour used for the top portion of the road renderer.
     *
     * @return a {@link Color} matching the current weather mood
     */
    public Color getSkyColor() {
        return switch (currentWeather) {
            case CLEAR   -> Color.web("#1A3A6E");   // deep twilight blue
            case MONSOON -> Color.web("#2D2D35");   // dark charcoal-grey
            case FOGGY   -> Color.web("#9A9A9A");   // flat medium grey
            case NIGHT   -> Color.web("#05050F");   // near-black navy
        };
    }

    /**
     * Colour used for the fog overlay tint (useful if the caller wants to draw
     * custom fog shapes instead of using {@link #drawBackground}).
     *
     * @return semi-transparent fog colour
     */
    public Color getFogColor() {
        return Color.rgb(210, 210, 215, Math.min(0.8, fogDensity));
    }

    // =========================================================================
    // Public API — state queries
    // =========================================================================

    /** @return the currently active {@link WeatherType}. */
    public WeatherType getWeather() { return currentWeather; }

    /**
     * @return how wet the road surface is, 0 (dry) → 1 (fully saturated).
     *         Affects tyre friction in the physics engine.
     */
    public double getWetness() { return wetness; }

    /**
     * @return current atmospheric fog density, 0 (none) → 1 (pea-soup).
     */
    public double getFogDensity() { return fogDensity; }

    /**
     * @return {@code true} if lighting conditions are night-like
     *         (NIGHT weather, or any location that is inherently dark).
     */
    public boolean isNight() {
        return currentWeather == WeatherType.NIGHT;
    }

    /**
     * @return {@code true} if currently raining (MONSOON).
     */
    public boolean isRaining() {
        return currentWeather == WeatherType.MONSOON;
    }
}
