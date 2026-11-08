package com.nexusdrive.system;

/**
 * Manages the player's nitro boost resource.
 *
 * <p>Charge drains while the player holds the boost input and recharges
 * automatically when unused. The nitro upgrade level may expand the
 * maximum charge pool and reduce the drain rate.
 */
public class NitroSystem {

    /** Current charge level, range [0, maxCharge]. */
    private double charge;

    /** Upper bound of the charge pool (modified by upgrade level). */
    private double maxCharge;

    /** Whether the nitro is currently being discharged. */
    private boolean active;

    /** Charge gained per second when the boost is not in use. */
    private double rechargeRate = 5.0;

    /** Charge consumed per second while boosting. */
    private double dischargeRate = 25.0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constructs a NitroSystem with the given maximum charge.
     * The initial charge is seeded at 50 % of maximum.
     *
     * @param maxCharge maximum storable nitro charge (usually 100)
     */
    public NitroSystem(double maxCharge) {
        this.maxCharge = maxCharge;
        this.charge    = maxCharge * 0.5;
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Advances the nitro state by one simulation step.
     *
     * @param nitroPressedHeld {@code true} while the player holds the boost input
     * @param dt               delta-time in seconds
     */
    public void update(boolean nitroPressedHeld, double dt) {
        if (nitroPressedHeld && charge > 0) {
            active = true;
            charge = Math.max(0.0, charge - dischargeRate * dt);
        } else {
            active = false;
            charge = Math.min(maxCharge, charge + rechargeRate * dt);
        }
    }

    /**
     * Externally adds charge (e.g. from a pick-up collectible).
     *
     * @param amount amount of charge to add
     */
    public void addCharge(double amount) {
        charge = Math.min(maxCharge, charge + amount);
    }

    // ── Upgrade ───────────────────────────────────────────────────────────────

    /**
     * Applies a nitro upgrade level (0–5).
     * Higher levels expand the charge pool and reduce discharge rate.
     *
     * <ul>
     *   <li>maxCharge    = 100 + level × 15</li>
     *   <li>dischargeRate = max(10, 25 − level × 3)</li>
     * </ul>
     *
     * @param level upgrade level (0–5)
     */
    public void setUpgradeLevel(int level) {
        maxCharge    = 100.0 + level * 15.0;
        dischargeRate = Math.max(10.0, 25.0 - level * 3.0);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Returns {@code true} when the boost is actively discharging. */
    public boolean isActive()         { return active && charge > 0; }

    /** Current charge level. */
    public double  getCharge()        { return charge; }

    /** Maximum charge capacity. */
    public double  getMaxCharge()     { return maxCharge; }

    /** Current charge as a fraction [0, 1] for HUD display. */
    public double  getChargePercent() { return charge / maxCharge; }
}
