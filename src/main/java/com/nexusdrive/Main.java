package com.nexusdrive;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import com.nexusdrive.data.SaveManager;
import com.nexusdrive.data.PlayerProfile;
import com.nexusdrive.world.LocationType;
import com.nexusdrive.ui.SceneManager;
import com.nexusdrive.ui.MainMenuController;
import com.nexusdrive.ui.LocationSelectScreen;
import com.nexusdrive.ui.GameModesScreen;
import com.nexusdrive.ui.GarageScreen;
import com.nexusdrive.ui.AchievementsScreen;
import com.nexusdrive.ui.SettingsScreen;

/**
 * Main JavaFX Application entry point for NEXUS DRIVE – BENGALURU RUSH.
 */
public class Main extends Application implements SceneManager {

    private Stage primaryStage;
    private Scene mainScene;
    private PlayerProfile profile;
    private GameEngine gameEngine;

    private MainMenuController mainMenu;
    private LocationSelectScreen locationSelect;
    private GameModesScreen modeSelect;
    private GarageScreen garage;
    private AchievementsScreen achievements;
    private SettingsScreen settings;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Load player profile from disk
        profile = SaveManager.load();

        // Set up stage
        stage.setTitle("NEXUS DRIVE – BENGALURU RUSH");
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setResizable(false);

        // Try to set app icon
        try {
            // No icon file required - will just skip
        } catch (Exception ignored) {}

        // Show main menu
        showMainMenu();
        stage.show();
    }

    @Override
    public void showMainMenu() {
        // Stop any running game
        if (gameEngine != null) {
            gameEngine.stop();
            gameEngine = null;
        }

        mainMenu = new MainMenuController(this, profile);
        StackPane root = new StackPane(mainMenu);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showLocationSelect() {
        // Stop background animation from main menu if switching
        if (mainMenu != null) {
            mainMenu.stop();
            mainMenu = null;
        }
        locationSelect = new LocationSelectScreen(this, profile);
        StackPane root = new StackPane(locationSelect);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showModeSelect() {
        modeSelect = new GameModesScreen(this, profile);
        StackPane root = new StackPane(modeSelect);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showGarage() {
        if (mainMenu != null) {
            mainMenu.stop();
            mainMenu = null;
        }
        garage = new GarageScreen(this, profile);
        StackPane root = new StackPane(garage);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showGame(LocationType location, GameMode mode) {
        // Apply mode location override if applicable
        LocationType effectiveLocation = (mode.locationOverride != null) ? mode.locationOverride : location;

        // Create and start game engine
        gameEngine = new GameEngine(1280, 720, effectiveLocation, mode, profile, this);
        mainScene = new Scene(gameEngine.getRoot(), 1280, 720);

        // Wire keyboard input
        mainScene.setOnKeyPressed(e -> gameEngine.getInputHandler().handleKeyPress(e));
        mainScene.setOnKeyReleased(e -> gameEngine.getInputHandler().handleKeyRelease(e));

        primaryStage.setScene(mainScene);
        gameEngine.start();
    }

    @Override
    public void showAchievements() {
        if (mainMenu != null) {
            mainMenu.stop();
            mainMenu = null;
        }
        achievements = new AchievementsScreen(this, profile);
        StackPane root = new StackPane(achievements);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showSettings() {
        if (mainMenu != null) {
            mainMenu.stop();
            mainMenu = null;
        }
        settings = new SettingsScreen(this, profile);
        StackPane root = new StackPane(settings);
        mainScene = new Scene(root, 1280, 720);
        primaryStage.setScene(mainScene);
    }

    @Override
    public void showPauseScreen() {
        // Pause is handled within GameEngine/HUD overlay
        if (gameEngine != null) gameEngine.setPaused(true);
    }

    @Override
    public void resumeGame() {
        if (gameEngine != null) gameEngine.setPaused(false);
    }

    @Override
    public void returnToMainMenu() {
        // Save profile before leaving game
        SaveManager.save(profile);
        showMainMenu();
    }

    @Override
    public void stop() {
        // Clean up when window closes
        if (gameEngine != null) gameEngine.stop();
        SaveManager.save(profile);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
