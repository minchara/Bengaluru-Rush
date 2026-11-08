package com.nexusdrive;

import com.nexusdrive.world.WeatherSystem.WeatherType;
import com.nexusdrive.world.LocationType;
import com.nexusdrive.entity.VehicleType;

/**
 * Central game mode enum. Each mode modifies gameplay objectives.
 */
public enum GameMode {
    BENGALURU_ENDLESS_RUSH("Bengaluru Endless Rush", "ಬೆಂಗಳೂರು ರಷ್",
            "Survive endless Bengaluru traffic", WeatherType.CLEAR, null),
    SILK_BOARD_CHALLENGE("Silk Board Challenge", "ಸಿಲ್ಕ್ ಬೋರ್ಡ್ ಸವಾಲ್",
            "Navigate the notorious junction", WeatherType.CLEAR, LocationType.SILK_BOARD),
    MG_ROAD_NIGHT_RACE("MG Road Night Race", "ಎಂ.ಜಿ. ರಸ್ತೆ ರಾತ್ರಿ",
            "Race through neon-lit MG Road at night", WeatherType.NIGHT, LocationType.MG_ROAD),
    NICE_ROAD_HIGHWAY("NICE Road Highway", "ನೈಸ್ ರಸ್ತೆ ಹೈವೇ",
            "High-speed highway racing", WeatherType.CLEAR, LocationType.NICE_ROAD),
    MONSOON_SURVIVAL("Monsoon Survival", "ಮಾನ್ಸೂನ್ ಸರ್ವೈವಲ್",
            "Survive Bengaluru's legendary rains", WeatherType.MONSOON, null),
    TIME_TRIAL("Time Trial", "ಟೈಮ್ ಟ್ರಯಲ್",
            "Beat the clock across the city", WeatherType.CLEAR, null),
    LANDMARK_CHALLENGE("Landmark Challenge", "ಲ್ಯಾಂಡ್‌ಮಾರ್ಕ್ ಸವಾಲ್",
            "Race past Bengaluru's landmarks", WeatherType.CLEAR, null);

    public final String displayName;
    public final String kannadaName;
    public final String description;
    public final WeatherType weatherOverride;
    public final LocationType locationOverride; // null = use selected location

    GameMode(String displayName, String kannadaName, String description,
             WeatherType weatherOverride, LocationType locationOverride) {
        this.displayName = displayName;
        this.kannadaName = kannadaName;
        this.description = description;
        this.weatherOverride = weatherOverride;
        this.locationOverride = locationOverride;
    }

    public String getDisplayName() { return displayName; }
    public String getKannadaName() { return kannadaName; }
    public String getDescription() { return description; }
}
