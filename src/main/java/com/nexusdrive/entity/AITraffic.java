package com.nexusdrive.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

/**
 * Represents a single AI-controlled traffic vehicle.
 * Implements a simple state machine (CRUISING → LANE_CHANGE → STOPPING →
 * STOPPED → ACCELERATING) to simulate realistic Bengaluru traffic behaviour,
 * including erratic lane changes by autos and bikes.
 */
public class AITraffic {

    // ── Road position ─────────────────────────────────────────────────────────
    /** Lateral position within road: -1 (far left) to 1 (far right). */
    public double x;
    /** Absolute world-unit distance along the road. */
    public double z;
    /** Current speed in km/h. */
    public double speed;

    // ── Classification ────────────────────────────────────────────────────────
    public VehicleType type;
    public int targetLaneIndex;
    public int currentLaneIndex;
    public int totalLanes;

    // ── State machine ─────────────────────────────────────────────────────────
    public AIState state = AIState.CRUISING;
    /** Time remaining in the current non-CRUISING state. */
    public double stateTimer = 0;
    /** Cooldown (seconds) before this vehicle may change lanes again. */
    public double laneChangeDelay = 0;

    // ── Flags ─────────────────────────────────────────────────────────────────
    public boolean stoppedAtSignal = false;
    /** True when the vehicle is within near-miss range of the player. */
    public boolean isNearPlayer = false;

    // ── Internals ─────────────────────────────────────────────────────────────
    private final double baseSpeed;
    private static final Random RAND = new Random();

    // ── State enum ────────────────────────────────────────────────────────────
    public enum AIState {
        CRUISING, LANE_CHANGE, STOPPING, STOPPED, ACCELERATING
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs an AI vehicle at the given lane and world-z position.
     *
     * @param type       vehicle type (determines base speed and behaviour)
     * @param lane       initial lane index (0 … totalLanes-1)
     * @param totalLanes total number of driveable lanes at this location
     * @param z          initial world-z position
     */
    public AITraffic(VehicleType type, int lane, int totalLanes, double z) {
        this.type             = type;
        this.currentLaneIndex = lane;
        this.targetLaneIndex  = lane;
        this.totalLanes       = totalLanes;
        this.z                = z;
        this.baseSpeed        = type.baseSpeed + (RAND.nextDouble() - 0.5) * 15.0;
        this.speed            = baseSpeed;
        this.x                = laneIndexToX(lane, totalLanes);
        // Randomise first lane-change attempt
        this.laneChangeDelay  = RAND.nextDouble() * 5.0;
    }

    /**
     * Maps a lane index to a lateral road position in the range [-0.9, 0.9].
     */
    private double laneIndexToX(int lane, int total) {
        if (total <= 1) return 0.0;
        return -0.9 + (double) lane / (total - 1) * 1.8;
    }

    /**
     * Advances the vehicle simulation by one time step.
     *
     * @param playerZ     player's world-z (used for proximity-based slowdown)
     * @param playerX     player's lateral position
     * @param playerSpeed player's current speed in km/h
     * @param dt          delta-time in seconds
     */
    public void update(double playerZ, double playerX, double playerSpeed, double dt) {
        stateTimer    -= dt;
        laneChangeDelay -= dt;

        switch (state) {
            case CRUISING -> {
                // Smoothly approach target lane
                double targetX = laneIndexToX(targetLaneIndex, totalLanes);
                x += (targetX - x) * 3.0 * dt;

                // Probabilistic lane change (erratic Bengaluru behaviour)
                if (laneChangeDelay <= 0 && RAND.nextDouble() < type.laneChangeFreq * dt) {
                    int delta  = RAND.nextBoolean() ? 1 : -1;
                    int newLane = Math.max(0, Math.min(totalLanes - 1, currentLaneIndex + delta));
                    targetLaneIndex  = newLane;
                    state            = AIState.LANE_CHANGE;
                    stateTimer       = 2.0;
                    laneChangeDelay  = 3.0 + RAND.nextDouble() * 5.0;
                }

                // Loosely match player speed when nearby (congestion simulation)
                double targetSpeed = baseSpeed;
                if (Math.abs(z - playerZ) < 500 && speed > playerSpeed * 0.8) {
                    targetSpeed = playerSpeed * 0.75;
                }
                speed += (targetSpeed - speed) * dt;

                z += speed / 3.6 * 100.0 * dt;
            }

            case LANE_CHANGE -> {
                double targetX = laneIndexToX(targetLaneIndex, totalLanes);
                x += (targetX - x) * 2.0 * dt;
                z += speed / 3.6 * 100.0 * dt;

                if (stateTimer <= 0 || Math.abs(x - targetX) < 0.05) {
                    currentLaneIndex = targetLaneIndex;
                    state = AIState.CRUISING;
                }
            }

            case STOPPING -> {
                speed = Math.max(0, speed - 60.0 * dt);
                z    += speed / 3.6 * 100.0 * dt;
                if (speed <= 0) state = AIState.STOPPED;
            }

            case STOPPED -> {
                speed = 0;
                if (stateTimer <= 0) {
                    state      = AIState.ACCELERATING;
                    stateTimer = 3.0;
                }
            }

            case ACCELERATING -> {
                speed = Math.min(baseSpeed, speed + 40.0 * dt);
                z    += speed / 3.6 * 100.0 * dt;
                if (speed >= baseSpeed * 0.9) state = AIState.CRUISING;
            }
        }
    }

    /** Initiates a stop sequence (called when a traffic signal turns red). */
    public void stopAtSignal() {
        if (state != AIState.STOPPED && state != AIState.STOPPING) {
            state          = AIState.STOPPING;
            stoppedAtSignal = true;
        }
    }

    /** Resumes movement after a signal turns green. */
    public void resumeFromSignal() {
        if (stoppedAtSignal) {
            state           = AIState.ACCELERATING;
            stateTimer      = 2.0;
            stoppedAtSignal = false;
        }
    }

    /**
     * Returns {@code [xMin, xMax]} in road-fraction coordinates for
     * collision / near-miss detection.
     */
    public double[] getBoundsX() {
        double halfWidth = (type.widthPx / 32.0) * 0.08;
        return new double[]{ x - halfWidth, x + halfWidth };
    }

    /**
     * Draws the vehicle on the road at the given screen position.
     *
     * @param gc      graphics context
     * @param screenX screen X centre of the vehicle
     * @param screenY screen Y centre of the vehicle
     * @param scale   size scale factor
     */
    public void draw(GraphicsContext gc, double screenX, double screenY, double scale) {
        int[][] sprite      = PixelSprites.getSpriteForType(type);
        double  spriteScale = Math.max(0.5, Math.min(scale * 2.0, 6.0));
        PixelSprites.drawSprite(gc, sprite, screenX, screenY, spriteScale, null);

        // Karnataka number plate beneath the vehicle (only when large enough)
        if (spriteScale > 2.0 && type != VehicleType.NAMMA_METRO) {
            drawNumberPlate(gc, screenX,
                            screenY + sprite.length * spriteScale / 2.0 + 2.0,
                            spriteScale);
        }
    }

    /**
     * Renders a simplified white Karnataka number plate.
     */
    private void drawNumberPlate(GraphicsContext gc, double cx, double y, double scale) {
        double pw = scale * 8.0;
        double ph = scale * 2.0;
        double px = cx - pw / 2.0;

        gc.setFill(Color.WHITE);
        gc.fillRect(px, y, pw, ph);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(ph * 0.8));
        gc.fillText("KA", px + 1, y + ph - 1);
    }

    public int getSegmentIndex() {
        return (int) (z / 200.0);
    }

    public double getLateralOffset() {
        return x;
    }

    public VehicleType getVehicleType() {
        return type;
    }
}
