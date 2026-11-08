package com.nexusdrive.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

/**
 * Persistent player profile that is serialised to / from JSON via
 * {@link SaveManager}.
 *
 * <p>All fields have sensible defaults set in the no-arg constructor so that a
 * brand-new player can be created by simply calling {@code new PlayerProfile()}.</p>
 *
 * <h3>Upgrade array layout</h3>
 * {@code carUpgrades} maps {@code carId → int[4]} where the indices are:
 * <pre>
 *   [0] engine   (0–5)
 *   [1] brakes   (0–5)
 *   [2] handling (0–5)
 *   [3] nitro    (0–5)
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerProfile {

    // -------------------------------------------------------------------------
    // Fields (all nullable-safe with defaults from constructor)
    // -------------------------------------------------------------------------

    private String              playerName;
    private int                 coins;
    private Map<String, Long>   highScores;
    private List<String>        unlockedCars;
    private List<String>        unlockedLocations;
    private List<String>        achievements;
    /** carId → int[4]: engine / brakes / handling / nitro, each 0–5. */
    private Map<String, int[]>  carUpgrades;
    /** carId → "#RRGGBB" chosen tint. */
    private Map<String, String> carColors;
    private String              selectedCarId;
    private String              selectedLocation;
    private String              selectedMode;
    private int                 totalRaces;
    private double              totalDistanceKm;
    private int                 masterVolume;
    private int                 sfxVolume;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a brand-new player profile with safe defaults.
     * Jackson also uses this constructor when deserialising — missing JSON fields
     * keep their default values thanks to {@code @JsonIgnoreProperties(ignoreUnknown=true)}.
     */
    public PlayerProfile() {
        playerName        = "Racer";
        coins             = 500;
        highScores        = new HashMap<>();
        unlockedCars      = new ArrayList<>(List.of("street_racer"));
        unlockedLocations = new ArrayList<>(List.of("MG_ROAD", "SILK_BOARD"));
        achievements      = new ArrayList<>();
        carUpgrades       = new HashMap<>();
        carColors         = new HashMap<>();
        selectedCarId     = "street_racer";
        selectedLocation  = "MG_ROAD";
        selectedMode      = "BENGALURU_ENDLESS_RUSH";
        totalRaces        = 0;
        totalDistanceKm   = 0.0;
        masterVolume      = 80;
        sfxVolume         = 80;
    }

    // -------------------------------------------------------------------------
    // Convenience helpers (non-serialised logic)
    // -------------------------------------------------------------------------

    /**
     * Returns the upgrade level (0–5) for a specific slot.
     *
     * @param carId the car identifier
     * @param slot  0=engine, 1=brakes, 2=handling, 3=nitro
     * @return upgrade level, or 0 if not found
     */
    public int getUpgradeLevel(String carId, int slot) {
        int[] levels = carUpgrades.get(carId);
        if (levels == null || slot < 0 || slot >= levels.length) return 0;
        return levels[slot];
    }

    /**
     * Sets the upgrade level for a specific slot.
     * Creates the upgrade array for the car if it doesn't exist yet.
     *
     * @param carId the car identifier
     * @param slot  0=engine, 1=brakes, 2=handling, 3=nitro
     * @param level 0–5
     */
    public void setUpgradeLevel(String carId, int slot, int level) {
        carUpgrades.computeIfAbsent(carId, k -> new int[4])[slot] =
                Math.max(0, Math.min(5, level));
    }

    /**
     * Records a new high score for a game mode if it beats the existing one.
     *
     * @param modeName display name of the mode (or {@code GameMode.name()})
     * @param score    new score to try to record
     * @return {@code true} if the new score is a personal best
     */
    public boolean tryUpdateHighScore(String modeName, long score) {
        long current = highScores.getOrDefault(modeName, 0L);
        if (score > current) {
            highScores.put(modeName, score);
            return true;
        }
        return false;
    }

    /** Returns the best score for a mode, or 0 if none recorded. */
    public long getHighScore(String modeName) {
        return highScores.getOrDefault(modeName, 0L);
    }

    /** Unlocks a car if not already unlocked. */
    public void unlockCar(String carId) {
        if (!unlockedCars.contains(carId)) {
            unlockedCars.add(carId);
        }
    }

    /** Unlocks a location if not already unlocked. */
    public void unlockLocation(String locationId) {
        if (!unlockedLocations.contains(locationId)) {
            unlockedLocations.add(locationId);
        }
    }

    public boolean isLocationUnlocked(com.nexusdrive.world.LocationType loc) {
        return unlockedLocations.contains(loc.name());
    }

    public void unlockLocation(com.nexusdrive.world.LocationType loc) {
        unlockLocation(loc.name());
    }

    public boolean isCarOwned(String carId) {
        return unlockedCars.contains(carId);
    }

    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void setCarUpgrades(String carId, int[] upg) {
        carUpgrades.put(carId, upg);
    }

    public void setCarColor(javafx.scene.paint.Color color) {
        String hex = String.format("#%02X%02X%02X",
                (int)(color.getRed()*255),
                (int)(color.getGreen()*255),
                (int)(color.getBlue()*255));
        if (selectedCarId != null) {
            carColors.put(selectedCarId, hex);
        }
    }

    public void saveToFile() {
        SaveManager.save(this);
    }

    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    public int getLevel() {
        return 1 + (int)(totalDistanceKm / 10.0);
    }

    public int getXp() {
        return (int)((totalDistanceKm % 10.0) * 100);
    }

    public int getTotalCoinsEarned() {
        return coins + (int)(totalDistanceKm * 50);
    }

    public void resetAll() {
        coins = 500;
        totalRaces = 0;
        totalDistanceKm = 0.0;
        highScores.clear();
        achievements.clear();
        unlockedCars = new ArrayList<>(List.of("street_racer"));
        unlockedLocations = new ArrayList<>(List.of("MG_ROAD", "SILK_BOARD"));
        carUpgrades.clear();
        carColors.clear();
        selectedCarId = "street_racer";
        selectedLocation = "MG_ROAD";
        selectedMode = "BENGALURU_ENDLESS_RUSH";
    }

    public void setMusicVolume(int v) {
        // volume stored in SoundManager directly
    }

    /** Grants an achievement if not already earned. */
    public void grantAchievement(String achievementId) {
        if (!achievements.contains(achievementId)) {
            achievements.add(achievementId);
        }
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getPlayerName()                       { return playerName; }
    public void   setPlayerName(String v)               { this.playerName = v; }

    public int    getCoins()                            { return coins; }
    public void   setCoins(int v)                       { this.coins = v; }

    public Map<String, Long> getHighScores()            { return highScores; }
    public void              setHighScores(Map<String, Long> v) { this.highScores = v; }

    public List<String> getUnlockedCars()               { return unlockedCars; }
    public void         setUnlockedCars(List<String> v) { this.unlockedCars = v; }

    public List<String> getUnlockedLocations()                  { return unlockedLocations; }
    public void         setUnlockedLocations(List<String> v)    { this.unlockedLocations = v; }

    public List<String> getAchievements()               { return achievements; }
    public void         setAchievements(List<String> v) { this.achievements = v; }

    public Map<String, int[]> getCarUpgrades()              { return carUpgrades; }
    public void               setCarUpgrades(Map<String, int[]> v){ this.carUpgrades = v; }

    public Map<String, String> getCarColors()               { return carColors; }
    public void                setCarColors(Map<String, String> v){ this.carColors = v; }

    public String getSelectedCarId()                    { return selectedCarId; }
    public void   setSelectedCarId(String v)            { this.selectedCarId = v; }

    public String getSelectedLocation()                 { return selectedLocation; }
    public void   setSelectedLocation(String v)         { this.selectedLocation = v; }

    public String getSelectedMode()                     { return selectedMode; }
    public void   setSelectedMode(String v)             { this.selectedMode = v; }

    public int    getTotalRaces()                       { return totalRaces; }
    public void   setTotalRaces(int v)                  { this.totalRaces = v; }

    public double getTotalDistanceKm()                  { return totalDistanceKm; }
    public void   setTotalDistanceKm(double v)          { this.totalDistanceKm = v; }

    public int    getMasterVolume()                     { return masterVolume; }
    public void   setMasterVolume(int v)                { this.masterVolume = v; }

    public int    getSfxVolume()                        { return sfxVolume; }
    public void   setSfxVolume(int v)                   { this.sfxVolume = v; }
}
