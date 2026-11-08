package com.nexusdrive.entity;

import com.nexusdrive.InputHandler;
import com.nexusdrive.world.WeatherSystem;
import com.nexusdrive.data.CarDefinition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Represents the player-controlled car.
 * Handles physics simulation, upgrade-derived stats, visual drawing,
 * nitro boost rendering, and collision recovery.
 */
public class PlayerCar {

    // ── Position along road track ─────────────────────────────────────────────
    /** Accumulated road distance in world units. */
    public double z = 0;
    /** Lateral position: -1.0 (far left) to 1.0 (far right). */
    public double x = 0;
    /** Current speed in km/h. */
    public double speed = 0;
    /** Speed in m/s (derived). */
    public double speedMs = 0;

    // ── Car definition and customisation ─────────────────────────────────────
    public CarDefinition carDef;
    /** Hex colour string applied as a tint over the sprite. */
    public String selectedColor;
    /** Upgrade levels: [engine(0), brakes(1), handling(2), nitro(3)], each 0–5. */
    public int[] upgrades = new int[4];

    // ── Computed stats based on upgrades ──────────────────────────────────────
    /** Maximum speed in km/h after applying engine upgrades. */
    public double maxSpeed;
    /** Acceleration in km/h per second. */
    public double acceleration;
    /** Brake deceleration in km/h per second. */
    public double brakePower;
    /** Lateral movement speed (road-fraction units per second). */
    public double handling;

    // ── Visual state ──────────────────────────────────────────────────────────
    /** Visual tilt during turns, range -1 to 1. */
    public double tiltAngle = 0;
    public boolean isNitroBoosting = false;

    // ── Collision state ───────────────────────────────────────────────────────
    public boolean isColliding = false;
    public double collisionRecoveryTime = 0;

    // Reusable Random for flame particles (re-created each draw is fine for
    // pixel-art particle effects where true randomness is desired).
    private final Random flameRand = new Random();

    // ─────────────────────────────────────────────────────────────────────────

    public PlayerCar() {
        // Default state: car stands still at road origin.
    }

    /**
     * Assigns a car definition, upgrades, and paint colour, then recomputes
     * derived stats.
     */
    public void setCar(CarDefinition def, int[] upgradesIn, String color) {
        this.carDef = def;
        this.upgrades = upgradesIn.clone();
        this.selectedColor = color;
        recalculateStats();
    }

    /**
     * Recomputes maxSpeed, acceleration, brakePower, and handling from the
     * current CarDefinition and upgrade levels.
     *
     * <p>Upgrade bonuses:
     * <ul>
     *   <li>Engine  (index 0): +5 km/h max speed and +2 km/h·s⁻¹ accel per level</li>
     *   <li>Brakes  (index 1): +10 % brake power per level</li>
     *   <li>Handling(index 2): +0.1 lateral speed per level</li>
     *   <li>Nitro   (index 3): handled exclusively by NitroSystem</li>
     * </ul>
     */
    private void recalculateStats() {
        if (carDef == null) {
            maxSpeed     = 120.0;
            acceleration = 40.0;
            brakePower   = 60.0;
            handling     = 1.2;
            return;
        }
        maxSpeed     = carDef.getBaseSpeed()   + upgrades[0] * 5.0;
        acceleration = carDef.getAcceleration()+ upgrades[0] * 2.0;
        brakePower   = carDef.getBraking()     * (1.0 + upgrades[1] * 0.1);
        handling     = carDef.getHandling()    + upgrades[2] * 0.1;
    }

    /**
     * Updates the car's physics for one simulation step.
     *
     * @param input         current frame input state
     * @param dt            delta-time in seconds
     * @param weather       active weather system (affects speed/handling multipliers)
     * @param nitroBoosting whether the nitro system is currently active
     */
    public void update(InputHandler input, double dt, WeatherSystem weather, boolean nitroBoosting) {
        double weatherSpeedMult    = weather.getSpeedMultiplier();
        double weatherHandlingMult = weather.getHandlingMultiplier();

        double effectiveMaxSpeed = maxSpeed * weatherSpeedMult;
        if (nitroBoosting) effectiveMaxSpeed *= 1.5;

        // ── Collision recovery ──────────────────────────────────────────────
        if (collisionRecoveryTime > 0) {
            collisionRecoveryTime -= dt;
            speed *= 0.95; // rapid slowdown
        } else if (isColliding) {
            isColliding = false;
        }

        // ── Acceleration / braking ──────────────────────────────────────────
        if (!isColliding) {
            if (input.isAccelerating()) {
                speed = Math.min(speed + acceleration * dt, effectiveMaxSpeed);
            } else if (input.isBraking()) {
                speed = Math.max(speed - brakePower * dt, -20.0); // allow reverse
            } else {
                // Natural deceleration (road friction)
                if (speed > 0)      speed = Math.max(0,  speed - 30.0 * dt);
                else if (speed < 0) speed = Math.min(0,  speed + 30.0 * dt);
            }
        }
        isNitroBoosting = nitroBoosting;

        // ── Lateral movement ────────────────────────────────────────────────
        double lateralSpeed = handling * weatherHandlingMult;
        if (input.isLeft())  x = Math.max(-1.0, x - lateralSpeed * dt);
        if (input.isRight()) x = Math.min( 1.0, x + lateralSpeed * dt);

        // ── Visual tilt (smooth lerp) ───────────────────────────────────────
        double targetTilt = 0;
        if (input.isLeft())  targetTilt = -1.0;
        if (input.isRight()) targetTilt =  1.0;
        tiltAngle += (targetTilt - tiltAngle) * 5.0 * dt;

        // ── Advance world position ──────────────────────────────────────────
        // 1 km/h = 1/3.6 m/s; WORLD_SCALE: 1 m = 100 world units
        speedMs = speed / 3.6;
        final double WORLD_SCALE = 100.0;
        z += speedMs * WORLD_SCALE * dt;

        // ── Off-road penalty ────────────────────────────────────────────────
        if (x < -1.2 || x > 1.2) {
            speed = Math.max(0, speed - 80.0 * dt);
        }
        x = Math.max(-1.5, Math.min(1.5, x));
    }

    /**
     * Triggers a collision event: sets recovering state and reduces speed to 30 %.
     */
    public void triggerCollision() {
        isColliding          = true;
        collisionRecoveryTime = 1.5;
        speed                = Math.max(0, speed * 0.3);
    }

    /**
     * Returns the player's bounding box in road space as
     * {@code [xMin, xMax, zMin, zMax]}.
     */
    public double[] getBounds() {
        double carWidth  = 0.12;
        double carLength = 0.08;
        return new double[]{
            x - carWidth,
            x + carWidth,
            z - carLength * 5000,
            z + carLength * 5000
        };
    }

    /**
     * Draws the player car at a fixed screen position.
     *
     * @param gc      graphics context
     * @param screenX screen X (usually horizontal centre of view)
     * @param screenY screen Y (bottom-centre area of road view)
     * @param scale   pixels per sprite-pixel
     */
    public void draw(GraphicsContext gc, double screenX, double screenY, double scale) {
        String spriteId = (carDef != null) ? carDef.getId() : "street_racer";
        int[][] sprite  = PixelSprites.getPlayerSprite(spriteId);

        Color tint = null;
        if (selectedColor != null) {
            try { tint = Color.web(selectedColor); } catch (Exception ignored) {}
        }

        gc.save();
        gc.translate(screenX, screenY);

        // Skew transform to simulate cornering tilt
        double tiltFactor = tiltAngle * 0.1;
        gc.transform(1, 0, tiltFactor, 1, 0, 0);

        PixelSprites.drawSprite(gc, sprite, 0, 0, scale, tint);

        // Nitro flame trail behind car
        if (isNitroBoosting) {
            drawNitroFlame(gc, 0, sprite.length * scale / 2.0);
        }

        gc.restore();
    }

    /**
     * Renders particle-style flame behind the car during a nitro boost.
     *
     * @param x       horizontal centre (relative to saved transform origin)
     * @param flameY  Y offset where the flame starts (bottom of sprite)
     */
    private void drawNitroFlame(GraphicsContext gc, double x, double flameY) {
        for (int i = 0; i < 8; i++) {
            double fx   = x + (flameRand.nextDouble() - 0.5) * 10.0;
            double fy   = flameY + flameRand.nextDouble() * 20.0;
            double size = 4.0 + flameRand.nextDouble() * 6.0;
            Color flameColor = (i % 2 == 0) ? Color.ORANGERED : Color.CYAN;
            gc.setFill(flameColor);
            gc.fillOval(fx - size / 2.0, fy, size, size * 1.5);
        }
    }

    // ── Convenience getters ───────────────────────────────────────────────────

    public double getSpeedKmh() { return speed; }
    public double getSpeedMs()  { return speedMs; }
    public double getMaxSpeed() { return maxSpeed; }
}
