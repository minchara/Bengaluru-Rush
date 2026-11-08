package com.nexusdrive.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable-ish descriptor for a playable car.
 *
 * <p>Jackson-serialisable: has a no-arg constructor for deserialisation and
 * an all-args constructor annotated with {@code @JsonCreator} for
 * round-tripping through {@code ObjectMapper}.  Use
 * {@link #getDefaultCars()} to seed a fresh player profile.</p>
 *
 * <h3>Stat ranges</h3>
 * <ul>
 *   <li>{@code baseSpeed}       – km/h, 150–300</li>
 *   <li>{@code acceleration}    – 0–10 (higher = quicker 0→max)</li>
 *   <li>{@code handling}        – 0–1  (higher = sharper steering)</li>
 *   <li>{@code braking}         – 0–10 (higher = shorter stopping distance)</li>
 *   <li>{@code nitroCapacity}   – 0–100 (100 = longest nitro burst)</li>
 * </ul>
 */
public class CarDefinition {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private String id;
    private String name;
    private String description;
    private double baseSpeed;
    private double acceleration;
    private double handling;
    private double braking;
    private double nitroCapacity;
    private int    unlockCost;
    private boolean unlockedByDefault;
    private String  defaultColor;
    private String[] availableColors;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** No-arg constructor required by Jackson for deserialisation. */
    public CarDefinition() {}

    /**
     * All-args constructor — also the primary way to build a {@code CarDefinition}
     * in code (e.g., inside {@link #getDefaultCars()}).
     */
    @JsonCreator
    public CarDefinition(
            @JsonProperty("id")               String   id,
            @JsonProperty("name")             String   name,
            @JsonProperty("description")      String   description,
            @JsonProperty("baseSpeed")        double   baseSpeed,
            @JsonProperty("acceleration")     double   acceleration,
            @JsonProperty("handling")         double   handling,
            @JsonProperty("braking")          double   braking,
            @JsonProperty("nitroCapacity")    double   nitroCapacity,
            @JsonProperty("unlockCost")       int      unlockCost,
            @JsonProperty("unlockedByDefault")boolean  unlockedByDefault,
            @JsonProperty("defaultColor")     String   defaultColor,
            @JsonProperty("availableColors")  String[] availableColors) {

        this.id               = id;
        this.name             = name;
        this.description      = description;
        this.baseSpeed        = baseSpeed;
        this.acceleration     = acceleration;
        this.handling         = handling;
        this.braking          = braking;
        this.nitroCapacity    = nitroCapacity;
        this.unlockCost       = unlockCost;
        this.unlockedByDefault = unlockedByDefault;
        this.defaultColor     = defaultColor;
        this.availableColors  = availableColors;
    }

    // -------------------------------------------------------------------------
    // Static factory
    // -------------------------------------------------------------------------

    /**
     * Returns the five canonical cars that ship with the game.
     * Call once and persist to {@link PlayerProfile#getUnlockedCars()}.
     *
     * @return mutable list of {@code CarDefinition}s, cheapest first
     */
    public static List<CarDefinition> getDefaultCars() {
        List<CarDefinition> cars = new ArrayList<>();

        // 1 — Street Racer (starter, free)
        cars.add(new CarDefinition(
                "street_racer",
                "Bengaluru Street Racer",
                "A nimble hatchback tuned for Bengaluru's stop-start chaos. "
                        + "Balanced stats make it forgiving for new drivers.",
                180.0,   // baseSpeed km/h
                5.5,     // acceleration
                0.72,    // handling
                5.5,     // braking
                80.0,    // nitroCapacity
                0,       // unlockCost
                true,    // unlockedByDefault
                "#CC2200",  // defaultColor — classic red
                new String[]{
                        "#CC2200", "#1A1A2E", "#FFFFFF", "#2244AA",
                        "#22AA44", "#FF8800", "#880088", "#AAAAAA"
                }
        ));

        // 2 — Neon Phantom
        cars.add(new CarDefinition(
                "neon_phantom",
                "Neon Phantom",
                "A sleek sports coupé wrapped in cyan neon strips. "
                        + "Faster top speed, slightly twitchy handling.",
                200.0,
                6.5,
                0.65,
                6.0,
                90.0,
                1500,
                false,
                "#00FFEE",
                new String[]{
                        "#00FFEE", "#00BBAA", "#FF00FF", "#0044FF",
                        "#FFFFFF", "#111111", "#00FF44", "#FF4400"
                }
        ));

        // 3 — Turbo GT
        cars.add(new CarDefinition(
                "turbo_gt",
                "Turbo GT",
                "A fire-orange GT with a twin-turbo engine. "
                        + "Excellent braking, great for overtakes on NICE Road.",
                220.0,
                7.5,
                0.68,
                7.5,
                95.0,
                3000,
                false,
                "#FF6600",
                new String[]{
                        "#FF6600", "#FF3300", "#FFAA00", "#882200",
                        "#FFFFFF", "#1A1A1A", "#44AAFF", "#AAFFAA"
                }
        ));

        // 4 — Cyber Beast
        cars.add(new CarDefinition(
                "cyber_beast",
                "Cyber Beast",
                "A futuristic hyper-car with an electric drivetrain. "
                        + "Devastating acceleration, longest nitro bar.",
                240.0,
                8.8,
                0.60,
                7.0,
                100.0,
                6000,
                false,
                "#8800FF",
                new String[]{
                        "#8800FF", "#AA00CC", "#FF00AA", "#220044",
                        "#0044FF", "#00FFAA", "#FFFFFF", "#111111"
                }
        ));

        // 5 — Thunder X
        cars.add(new CarDefinition(
                "thunder_x",
                "Thunder X",
                "The ultimate machine — a gold-plated supercar that dominates "
                        + "every road in Bengaluru. Hardest to control, highest reward.",
                260.0,
                9.5,
                0.55,
                8.0,
                100.0,
                10000,
                false,
                "#FFD700",
                new String[]{
                        "#FFD700", "#FFA500", "#DAA520", "#B8860B",
                        "#FFFFFF", "#FF4444", "#4444FF", "#111111"
                }
        ));

        return cars;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getId()                       { return id; }
    public void   setId(String id)              { this.id = id; }

    public String getName()                     { return name; }
    public void   setName(String name)          { this.name = name; }

    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d; }

    public double getBaseSpeed()                { return baseSpeed; }
    public double getBaseTopSpeed()             { return baseSpeed; }
    public void   setBaseSpeed(double v)        { this.baseSpeed = v; }

    public double getAcceleration()             { return acceleration; }
    public double getBaseAccel()                { return acceleration; }
    public void   setAcceleration(double v)     { this.acceleration = v; }

    public double getHandling()                 { return handling; }
    public double getBaseHandling()             { return handling; }
    public void   setHandling(double v)         { this.handling = v; }

    public double getBraking()                  { return braking; }
    public void   setBraking(double v)          { this.braking = v; }

    public double getNitroCapacity()            { return nitroCapacity; }
    public double getBaseNitro()                { return nitroCapacity; }
    public void   setNitroCapacity(double v)    { this.nitroCapacity = v; }

    public int    getUnlockCost()               { return unlockCost; }
    public void   setUnlockCost(int v)          { this.unlockCost = v; }

    public boolean isUnlockedByDefault()        { return unlockedByDefault; }
    public void    setUnlockedByDefault(boolean v){ this.unlockedByDefault = v; }

    public String  getDefaultColor()            { return defaultColor; }
    public void    setDefaultColor(String v)    { this.defaultColor = v; }

    public String[] getAvailableColors()        { return availableColors; }
    public void     setAvailableColors(String[] v){ this.availableColors = v; }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "CarDefinition{id='" + id + "', name='" + name
                + "', speed=" + baseSpeed + ", cost=" + unlockCost + "}";
    }
}
