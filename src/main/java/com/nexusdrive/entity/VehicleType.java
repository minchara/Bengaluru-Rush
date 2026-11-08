package com.nexusdrive.entity;

/**
 * Catalogue of every NPC / traffic vehicle type that can appear on Bengaluru roads.
 *
 * <p>All dimensional values ({@link #widthPx}, {@link #heightPx}) are in <em>sprite pixels</em>
 * before the global scale factor is applied.  The {@link #laneChangeFreq} is the probability
 * per second (0–1) that this vehicle will spontaneously change lanes — auto-rickshaws are
 * notorious, buses barely ever do it.</p>
 */
public enum VehicleType {

    // -------------------------------------------------------------------------
    // Values
    // -------------------------------------------------------------------------

    AUTO_RICKSHAW(
            "Auto Rickshaw",
            14, 20,
            35.0,
            0.4,
            "Erratic yellow-black autos"),

    BMTC_BUS(
            "BMTC Bus",
            22, 36,
            25.0,
            0.05,
            "Red BMTC buses"),

    DELIVERY_BIKE(
            "Delivery Bike",
            10, 18,
            50.0,
            0.3,
            "Speedy delivery bikes"),

    MOTORCYCLE(
            "Motorcycle",
            10, 18,
            60.0,
            0.2,
            "Fast motorcycles"),

    CAR(
            "Car",
            16, 24,
            45.0,
            0.1,
            "Regular cars"),

    SUV(
            "SUV",
            18, 26,
            40.0,
            0.1,
            "SUVs and larger cars"),

    TRUCK(
            "Truck",
            22, 40,
            30.0,
            0.03,
            "Heavy trucks"),

    OLA_TAXI(
            "Ola Taxi",
            16, 24,
            42.0,
            0.15,
            "White Ola/Uber taxis"),

    NAMMA_METRO(
            "Namma Metro",
            28, 48,
            70.0,
            0.0,
            "Purple Namma Metro trains");

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Human-readable name shown in the UI. */
    public final String displayName;

    /** Width of the pixel-art sprite (before scaling), in pixels. */
    public final int widthPx;

    /** Height of the pixel-art sprite (before scaling), in pixels. */
    public final int heightPx;

    /** Base speed in km/h (before weather / traffic modifiers). */
    public final double baseSpeed;

    /**
     * Probability per second (0–1) that this vehicle spontaneously changes lanes.
     * Multiply by {@code dt} in the update loop:
     * {@code if (random.nextDouble() < laneChangeFreq * dt) changeLane();}
     */
    public final double laneChangeFreq;

    /** Short flavour text shown when this vehicle type is highlighted. */
    public final String description;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    VehicleType(String displayName,
                int widthPx, int heightPx,
                double baseSpeed,
                double laneChangeFreq,
                String description) {
        this.displayName    = displayName;
        this.widthPx        = widthPx;
        this.heightPx       = heightPx;
        this.baseSpeed      = baseSpeed;
        this.laneChangeFreq = laneChangeFreq;
        this.description    = description;
    }

    // -------------------------------------------------------------------------
    // Helper queries
    // -------------------------------------------------------------------------

    /**
     * @return {@code true} if this vehicle is small enough to squeeze between lanes
     *         (bikes and autos can filter traffic).
     */
    public boolean canFilterTraffic() {
        return this == MOTORCYCLE || this == DELIVERY_BIKE || this == AUTO_RICKSHAW;
    }

    /**
     * @return {@code true} for large vehicles that block an entire lane.
     */
    public boolean isWide() {
        return this == BMTC_BUS || this == TRUCK || this == NAMMA_METRO;
    }

    /**
     * @return the total sprite area in pixels (useful for collision-box sizing).
     */
    public int spriteArea() {
        return widthPx * heightPx;
    }
}
