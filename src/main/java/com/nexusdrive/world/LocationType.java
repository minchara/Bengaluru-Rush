package com.nexusdrive.world;

import com.nexusdrive.world.WeatherSystem.WeatherType;

/**
 * All driveable locations in NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Each location encodes its display properties, gameplay parameters
 * (traffic density, lane count), unlock cost, and the default weather
 * condition that initialises {@link WeatherSystem} when the race begins.</p>
 *
 * <h3>Unlocking</h3>
 * Locations with {@code unlockCost == 0} are available from the start.
 * Others must be purchased with in-game coins stored in the player profile.
 *
 * <h3>Metro overlay</h3>
 * When {@code hasMetro == true} the road renderer draws the purple Namma
 * Metro viaduct above the road and may spawn {@code VehicleType.NAMMA_METRO}
 * in the background rail lane.
 */
public enum LocationType {

    // =========================================================================
    // Values
    // =========================================================================

    MG_ROAD(
            "MG Road",
            "ಎಂ.ಜಿ. ರಸ್ತೆ",
            "Urban commercial district with metro overhead, dense pedestrian zones "
                    + "and neon-lit shop fronts.",
            /* unlockCost   */ 0,
            /* weather      */ WeatherType.CLEAR,
            /* density      */ 0.70,
            /* lanes        */ 4,
            /* hasMetro     */ true
    ),

    SILK_BOARD(
            "Silk Board Junction",
            "ಸಿಲ್ಕ್ ಬೋರ್ಡ್",
            "Bengaluru's most notorious traffic junction — a convergence of four "
                    + "major roads with almost no breathing room.",
            0,
            WeatherType.CLEAR,
            0.95,
            6,
            false
    ),

    OUTER_RING_ROAD(
            "Outer Ring Road",
            "ಔಟರ್ ರಿಂಗ್ ರಸ್ತೆ",
            "The tech-park corridor linking Marathahalli to Hebbal; "
                    + "smooth tarmac but relentlessly busy during office hours.",
            500,
            WeatherType.CLEAR,
            0.60,
            6,
            false
    ),

    VIDHANA_SOUDHA(
            "Vidhana Soudha",
            "ವಿಧಾನ ಸೌಧ",
            "Night racing past Bengaluru's iconic granite parliament building, "
                    + "Cubbon Park glowing under street lamps.",
            1000,
            WeatherType.NIGHT,
            0.40,
            4,
            false
    ),

    NICE_ROAD(
            "NICE Road",
            "ನೈಸ್ ರಸ್ತೆ",
            "Nandi Infrastructure Corridor — a rare high-speed expressway "
                    + "where you can truly open up the throttle.",
            2000,
            WeatherType.CLEAR,
            0.30,
            6,
            false
    ),

    LALBAGH(
            "Lalbagh & Cubbon Park",
            "ಲಾಲ್ ಬಾಗ್",
            "Scenic tree-lined roads threading through Bengaluru's botanical gardens; "
                    + "tight lanes and sudden speed bumps await.",
            3000,
            WeatherType.FOGGY,
            0.50,
            2,
            false
    );

    // =========================================================================
    // Fields
    // =========================================================================

    /** English name shown in location select. */
    public final String displayName;

    /** Kannada name shown beneath the English name in the UI. */
    public final String kannadaName;

    /** Descriptive flavour text shown on the location card. */
    public final String description;

    /** Coin cost to unlock; 0 means available from the start. */
    public final int unlockCost;

    /**
     * Default weather initialised for this location.
     * The player or the game mode may override this mid-race.
     */
    public final WeatherType defaultWeather;

    /**
     * Traffic density factor (0.0 = empty road, 1.0 = fully gridlocked).
     * Used by the traffic spawner to decide how many vehicles to place per km.
     */
    public final double trafficDensity;

    /**
     * Number of driveable road lanes (2–6).
     * Wider roads allow more NPC vehicles and wider overtaking opportunities.
     */
    public final int roadLanes;

    /**
     * Whether the Namma Metro viaduct runs overhead on this route.
     * Enables the purple metro trains in the background and adds the viaduct
     * visual element to the road renderer.
     */
    public final boolean hasMetro;

    // =========================================================================
    // Constructor
    // =========================================================================

    LocationType(String displayName,
                 String kannadaName,
                 String description,
                 int    unlockCost,
                 WeatherType defaultWeather,
                 double trafficDensity,
                 int    roadLanes,
                 boolean hasMetro) {

        this.displayName    = displayName;
        this.kannadaName    = kannadaName;
        this.description    = description;
        this.unlockCost     = unlockCost;
        this.defaultWeather = defaultWeather;
        this.trafficDensity = trafficDensity;
        this.roadLanes      = roadLanes;
        this.hasMetro       = hasMetro;
    }

    public String getDisplayName() { return displayName; }
    public String getKannadaName() { return kannadaName; }
    public String getDescription() { return description; }

    // =========================================================================
    // Helper queries
    // =========================================================================

    /**
     * @return {@code true} if this location is unlocked from the start
     *         (i.e., {@code unlockCost == 0}).
     */
    public boolean isDefaultUnlocked() {
        return unlockCost == 0;
    }

    /**
     * @return {@code true} if racing here at night (useful for headlight logic).
     */
    public boolean isNightLocation() {
        return defaultWeather == WeatherType.NIGHT;
    }

    /**
     * Returns the maximum number of NPC vehicles that can be on-screen
     * simultaneously, derived from lane count and traffic density.
     *
     * @return a soft cap on simultaneous NPC vehicles
     */
    public int maxNpcVehicles() {
        return (int) Math.ceil(roadLanes * trafficDensity * 8);
    }
}
