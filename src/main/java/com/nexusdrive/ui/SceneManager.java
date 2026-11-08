package com.nexusdrive.ui;

import com.nexusdrive.GameMode;
import com.nexusdrive.world.LocationType;

/**
 * Central scene-switching contract for NEXUS DRIVE.
 *
 * <p>All screens receive a {@code SceneManager} at construction time and call
 * its methods to trigger navigation. The concrete implementation
 * ({@code AppSceneManager}) swaps the JavaFX {@link javafx.scene.Scene}'s root
 * node and manages lifecycle (start/stop animations, etc.).</p>
 */
public interface SceneManager {

    /** Navigate to the main menu. */
    void showMainMenu();

    /** Navigate to the location-selection screen. */
    void showLocationSelect();

    /** Navigate to the game-mode selection screen. */
    void showModeSelect();

    /** Navigate to the garage screen. */
    void showGarage();

    /**
     * Start a game session with the given location and mode.
     *
     * @param location the chosen {@link LocationType}
     * @param mode     the chosen {@link GameMode}
     */
    void showGame(LocationType location, GameMode mode);

    /** Navigate to the achievements screen. */
    void showAchievements();

    /** Navigate to the settings screen. */
    void showSettings();

    /** Overlay the pause screen on top of the running game. */
    void showPauseScreen();

    /** Dismiss the pause overlay and resume the game loop. */
    void resumeGame();

    /** Stop the game loop and return to the main menu. */
    void returnToMainMenu();
}
