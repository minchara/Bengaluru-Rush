package com.nexusdrive.system;

import com.nexusdrive.entity.AITraffic;
import com.nexusdrive.entity.PlayerCar;

import java.util.List;

/**
 * Detects collisions and near-misses between the player car and AI traffic.
 *
 * <p>All spatial comparisons are done in road-fraction units (x in [-1.5, 1.5],
 * z in world units). A <em>collision</em> requires both lateral overlap and
 * proximity in depth. A <em>near-miss</em> is a close lateral pass without
 * actual overlap.
 */
public class CollisionManager {

    /** Maximum lateral gap (road-fraction units) that counts as a near-miss. */
    private static final double NEAR_MISS_DISTANCE = 0.08;

    /** Maximum z-gap (world units) considered for collision. */
    private static final double COLLISION_Z_RANGE  = 300.0;

    /** Maximum z-gap (world units) considered for near-miss. */
    private static final double NEAR_MISS_Z_RANGE  = 400.0;

    /** Maximum z-gap for any interaction check at all (early-out optimisation). */
    private static final double MAX_CHECK_Z        = 1_000.0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Immutable result returned by {@link #check}.
     *
     * @param collided        true if a hard collision was detected
     * @param collidedWith    the vehicle that was hit (null when no collision)
     * @param nearMiss        true if a near-miss event occurred this frame
     * @param nearMissTarget  the vehicle that was narrowly avoided (null otherwise)
     */
    public record CollisionResult(
            boolean    collided,
            AITraffic  collidedWith,
            boolean    nearMiss,
            AITraffic  nearMissTarget
    ) {}

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Checks the player car against every AI vehicle and returns the first
     * significant event found.
     *
     * <p>Priority: a hard collision takes precedence over a near-miss. The
     * method returns as soon as a collision is found; near-miss detection
     * continues only while no collision has been detected.
     *
     * @param player  the player car
     * @param traffic list of active AI vehicles
     * @return a {@link CollisionResult} describing the frame's interaction
     */
    public CollisionResult check(PlayerCar player, List<AITraffic> traffic) {
        double[] playerBounds = player.getBounds();
        double   playerXMin   = playerBounds[0];
        double   playerXMax   = playerBounds[1];

        CollisionResult pendingNearMiss = new CollisionResult(false, null, false, null);

        for (AITraffic vehicle : traffic) {

            // Early-out: ignore vehicles far ahead or behind
            if (Math.abs(vehicle.z - player.z) > MAX_CHECK_Z) continue;

            double[] vBounds = vehicle.getBoundsX();
            double   vXMin   = vBounds[0];
            double   vXMax   = vBounds[1];

            // Determine lateral relationship
            boolean xOverlap = !(playerXMax < vXMin || playerXMin > vXMax);
            double  xGap     = xOverlap
                    ? 0.0
                    : Math.max(vXMin - playerXMax, playerXMin - vXMax);

            double zDist = Math.abs(vehicle.z - player.z);

            if (xOverlap && zDist < COLLISION_Z_RANGE) {
                // Hard collision — return immediately
                return new CollisionResult(true, vehicle, false, null);
            }

            if (!xOverlap && xGap < NEAR_MISS_DISTANCE && zDist < NEAR_MISS_Z_RANGE) {
                // Record the first near-miss; keep checking for a collision
                if (!pendingNearMiss.nearMiss()) {
                    pendingNearMiss = new CollisionResult(false, null, true, vehicle);
                }
            }
        }

        // Return near-miss (or empty result) if no collision was found
        return pendingNearMiss;
    }
}
