package com.nexusdrive.system;

import com.nexusdrive.entity.AITraffic;
import com.nexusdrive.entity.PlayerCar;
import com.nexusdrive.entity.VehicleType;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects near-miss events between the player car and individual AI vehicles,
 * enforces per-vehicle cooldowns to prevent double-counting rapid passes,
 * and maintains HUD flash state for the most recent event.
 *
 * <p>A near-miss is triggered when the player passes within
 * {@value #NEAR_MISS_X_THRESHOLD} road-fraction units of a vehicle laterally
 * while also being within {@value #NEAR_MISS_Z_THRESHOLD} world units in depth.
 * A minimum x-distance of 0.01 is required so that an actual overlap
 * (collision) is not erroneously counted as a near-miss.
 */
public class NearMissDetector {

    // ── Detection thresholds ──────────────────────────────────────────────────
    private static final double NEAR_MISS_X_THRESHOLD = 0.09;  // road-fraction units
    private static final double NEAR_MISS_Z_THRESHOLD = 500.0; // world units
    /** Minimum lateral gap to exclude actual collisions from near-miss counting. */
    private static final double MIN_X_GAP             = 0.01;

    // ── Per-vehicle cooldown ──────────────────────────────────────────────────
    /** Per-vehicle cooldown in seconds to prevent repeated triggers. */
    private static final double COOLDOWN = 2.0;

    /** Maps each vehicle to the remaining cooldown time (seconds). */
    private final Map<AITraffic, Double> cooldowns = new HashMap<>();

    // ── HUD flash state ───────────────────────────────────────────────────────
    /** Label text for the most recent near-miss event (shown on HUD). */
    public String lastNearMissLabel   = null;
    /** Countdown (seconds) during which the HUD label flashes. */
    public double lastNearMissFlashTime = 0.0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks whether the player has just had a near-miss with {@code vehicle}.
     *
     * <p>Also advances all per-vehicle cooldown timers by {@code dt}.
     *
     * @param player  the player car
     * @param vehicle a specific AI vehicle to test
     * @param dt      delta-time in seconds
     * @return {@code true} if a new near-miss was detected this frame
     */
    public boolean checkNearMiss(PlayerCar player, AITraffic vehicle, double dt) {
        // Advance and prune cooldown timers
        cooldowns.replaceAll((v, t) -> t - dt);
        cooldowns.entrySet().removeIf(e -> e.getValue() <= 0);

        // Vehicle is still on cooldown — skip
        if (cooldowns.containsKey(vehicle)) return false;

        double xDist = Math.abs(player.x - vehicle.x);
        double zDist = Math.abs(player.z - vehicle.z);

        boolean laterallyClose = xDist < NEAR_MISS_X_THRESHOLD && xDist > MIN_X_GAP;
        boolean depthClose     = zDist < NEAR_MISS_Z_THRESHOLD;

        if (laterallyClose && depthClose) {
            cooldowns.put(vehicle, COOLDOWN);
            lastNearMissLabel     = buildLabel(vehicle.type);
            lastNearMissFlashTime = 2.0;
            return true;
        }
        return false;
    }

    /**
     * Advances the HUD flash timer.
     * Should be called once per frame even when no near-miss check is performed.
     *
     * @param dt delta-time in seconds
     */
    public void update(double dt) {
        if (lastNearMissFlashTime > 0) {
            lastNearMissFlashTime -= dt;
        }
    }

    /** Returns {@code true} while the HUD near-miss label should be visible. */
    public boolean isFlashing() { return lastNearMissFlashTime > 0; }

    public String getLastNearMissLabel() { return lastNearMissLabel; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Builds a vehicle-type–specific near-miss label string with a bonus
     * score hint.
     */
    private String buildLabel(VehicleType type) {
        return switch (type) {
            case AUTO_RICKSHAW -> "AUTO NEAR MISS! +" + 150;
            case BMTC_BUS      -> "BUS NEAR MISS! +"  + 300;
            case TRUCK         -> "TRUCK NEAR MISS! +" + 400;
            case NAMMA_METRO   -> "METRO NEAR MISS! +" + 500;
            default            -> "CLOSE CALL! +"      + 100;
        };
    }
}
