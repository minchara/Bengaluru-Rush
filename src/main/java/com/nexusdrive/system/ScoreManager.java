package com.nexusdrive.system;

/**
 * Tracks the player's race score, coins, near-miss combo, session distance,
 * and elapsed time.
 *
 * <p>Score accumulates continuously based on speed, with a multiplier
 * that grows each time the player completes a near-miss without crashing.
 * A collision resets the combo and applies a small score penalty.
 */
public class ScoreManager {

    // ── Persistent session values ─────────────────────────────────────────────
    private long   score        = 0;
    private int    coins        = 0;
    private double distanceKm   = 0;
    private double sessionTime  = 0;
    private int    nearMissCount = 0;

    // ── Combo state ───────────────────────────────────────────────────────────
    /** Number of near-misses in the current unbroken streak. */
    private int    comboCount       = 0;
    /** Score multiplier derived from the combo streak (capped at 10×). */
    private int    comboMultiplier  = 1;
    /** Countdown timer; a combo expires when this reaches zero. */
    private double comboResetTimer  = 0;

    // ── Constants ─────────────────────────────────────────────────────────────
    /** Time (seconds) the combo persists after the last near-miss. */
    private static final double COMBO_TIMEOUT       = 3.0;
    /** Base score awarded per near-miss. */
    private static final int    NEAR_MISS_BASE      = 100;
    /** Score awarded per coin collected (before multiplier). */
    private static final int    COIN_VALUE          = 50;
    /** Score added per km/h per second of travel. */
    private static final double SPEED_SCORE_RATE    = 0.1;
    /** Fixed score penalty applied on each collision. */
    private static final long   COLLISION_PENALTY   = 500L;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Advances all time-based score and stat tracking.
     * Should be called once per simulation step.
     *
     * @param speedKmh current player speed in km/h
     * @param dt       delta-time in seconds
     */
    public void update(double speedKmh, double dt) {
        // Speed-based score (scaled by active combo multiplier)
        score       += (long) (speedKmh * SPEED_SCORE_RATE * dt * comboMultiplier);
        distanceKm  += speedKmh / 3_600.0 * dt;
        sessionTime += dt;

        // Combo decay
        if (comboResetTimer > 0) {
            comboResetTimer -= dt;
            if (comboResetTimer <= 0) {
                comboCount      = 0;
                comboMultiplier = 1;
            }
        }
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    /**
     * Records a near-miss event: increments combo, extends the timeout, and
     * awards combo-scaled score.
     */
    public void addNearMiss() {
        comboCount++;
        nearMissCount++;
        // Every 3 near-misses in a streak adds +1 multiplier, capped at 10×
        comboMultiplier = Math.min(10, 1 + comboCount / 3);
        score          += (long) (NEAR_MISS_BASE * comboMultiplier);
        comboResetTimer = COMBO_TIMEOUT;
    }

    /**
     * Records a coin collection: increments coin count and awards
     * combo-scaled score.
     */
    public void addCoin() {
        coins++;
        score += (long) COIN_VALUE * comboMultiplier;
    }

    /**
     * Applies a collision penalty: resets the combo immediately and
     * subtracts a fixed amount from the score (floor: 0).
     */
    public void addCollisionPenalty() {
        comboCount      = 0;
        comboMultiplier = 1;
        comboResetTimer = 0;
        score           = Math.max(0L, score - COLLISION_PENALTY);
    }

    /**
     * Awards an arbitrary bonus (e.g. for finishing a stage or a challenge).
     *
     * @param bonus score amount to add
     */
    public void addBonusScore(long bonus) {
        score += bonus;
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /** Resets all stats to zero — called at the start of a new run. */
    public void reset() {
        score           = 0;
        coins           = 0;
        comboCount      = 0;
        comboMultiplier = 1;
        comboResetTimer = 0;
        distanceKm      = 0;
        sessionTime     = 0;
        nearMissCount   = 0;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public long   getScore()           { return score; }
    public int    getCoins()           { return coins; }
    public int    getComboMultiplier() { return comboMultiplier; }
    public int    getComboCount()      { return comboCount; }
    public double getDistanceKm()      { return distanceKm; }
    public double getSessionTime()     { return sessionTime; }
    public int    getNearMissCount()   { return nearMissCount; }
}
