package com.nexusdrive;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.nexusdrive.world.*;
import com.nexusdrive.entity.*;
import com.nexusdrive.system.*;
import com.nexusdrive.ui.*;
import com.nexusdrive.audio.SoundManager;
import com.nexusdrive.data.CarDefinition;
import com.nexusdrive.data.PlayerProfile;
import com.nexusdrive.data.SaveManager;

import java.util.List;

/**
 * Main game loop coordinator.
 * Runs at 60fps via AnimationTimer.
 * Owns all game systems and coordinates updates + rendering each frame.
 */
public class GameEngine {

    // --- Configuration ---
    private static final int TARGET_FPS = 60;

    // --- Screen ---
    private final double screenW;
    private final double screenH;

    // --- JavaFX nodes ---
    private final StackPane root;
    private final Canvas roadCanvas;    // Pseudo-3D road rendering
    private final GraphicsContext roadGc;

    // --- Game systems ---
    private final InputHandler input;
    private final PlayerCar player;
    private final BengaluruMap map;
    private final TrafficManager trafficManager;
    private final WeatherSystem weather;
    private final CollisionManager collisionManager;
    private final NitroSystem nitro;
    private final TimeRewindManager rewind;
    private final ScoreManager score;
    private final NearMissDetector nearMiss;
    private final RoadRenderer roadRenderer;
    private final BackgroundRenderer backgroundRenderer;
    private final GameHUD hud;
    private final SoundManager sound;

    // --- State ---
    private final LocationType location;
    private final GameMode mode;
    private final PlayerProfile profile;
    private final SceneManager sceneManager;
    private GameState gameState = GameState.PLAYING;

    // --- Game loop ---
    private AnimationTimer gameLoop;
    private long lastFrameTime = 0;

    // --- Mode-specific ---
    private double timeLimitSeconds = 0;   // for TIME_TRIAL
    private double timeRemainingSeconds = 0;
    private int landmarkCheckpointsHit = 0;

    // --- Camera/rendering state ---
    private double bgScrollX = 0;  // parallax background X offset

    public GameEngine(double screenW, double screenH,
                      LocationType location, GameMode mode,
                      PlayerProfile profile, SceneManager sceneManager) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.location = location;
        this.mode = mode;
        this.profile = profile;
        this.sceneManager = sceneManager;

        // Build canvas and root
        roadCanvas = new Canvas(screenW, screenH);
        roadGc = roadCanvas.getGraphicsContext2D();
        roadCanvas.setFocusTraversable(false);

        root = new StackPane(roadCanvas);

        // Initialize systems
        input = new InputHandler();
        player = new PlayerCar();
        map = new BengaluruMap();
        weather = new WeatherSystem();
        trafficManager = new TrafficManager();
        collisionManager = new CollisionManager();
        nitro = new NitroSystem(100.0);
        rewind = new TimeRewindManager();
        score = new ScoreManager();
        nearMiss = new NearMissDetector();
        roadRenderer = new RoadRenderer(screenW, screenH);
        backgroundRenderer = new BackgroundRenderer();
        sound = SoundManager.getInstance();

        // HUD sits on top of canvas (transparent canvas layer)
        hud = new GameHUD(screenW, screenH);
        root.getChildren().add(hud.getCanvas());

        // Setup game
        setupGame();
    }

    private void setupGame() {
        // Load map for this location
        map.loadLocation(location);

        // Apply weather from mode override
        WeatherSystem.WeatherType targetWeather = mode.weatherOverride;
        if (targetWeather == null) {
            targetWeather = location.defaultWeather;
        }
        weather.setWeather(targetWeather);

        // Setup traffic
        trafficManager.setup(location, map);

        // Load player car from profile
        List<CarDefinition> cars = CarDefinition.getDefaultCars();
        CarDefinition selectedCar = cars.stream()
            .filter(c -> c.getId().equals(profile.getSelectedCarId()))
            .findFirst().orElse(cars.get(0));

        int[] upgrades = profile.getCarUpgrades().getOrDefault(
            selectedCar.getId(), new int[]{0, 0, 0, 0});
        String color = profile.getCarColors().getOrDefault(
            selectedCar.getId(), selectedCar.getDefaultColor());

        player.setCar(selectedCar, upgrades, color);
        nitro.setUpgradeLevel(upgrades[3]);

        // Setup nitro from car def
        nitro.setUpgradeLevel(upgrades.length > 3 ? upgrades[3] : 0);

        // Mode-specific setup
        if (mode == GameMode.TIME_TRIAL) {
            timeLimitSeconds = 180.0; // 3 minutes
            timeRemainingSeconds = timeLimitSeconds;
        }

        // Show location intro text in HUD
        hud.showLocationIntro(location.displayName, location.kannadaName);

        // Start ambient sounds
        if (targetWeather == WeatherSystem.WeatherType.MONSOON) {
            sound.startRain();
        }
    }

    public void start() {
        lastFrameTime = 0;
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime == 0) {
                    lastFrameTime = now;
                    return;
                }
                double dt = Math.min((now - lastFrameTime) / 1_000_000_000.0, 0.05); // cap at 50ms
                lastFrameTime = now;
                tick(dt);
            }
        };
        gameLoop.start();
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        sound.stopAll();
    }

    private void tick(double dt) {
        // Clear just-pressed state at start of each frame
        input.clearJustPressed();

        switch (gameState) {
            case PLAYING -> tickPlaying(dt);
            case PAUSED  -> tickPaused(dt);
            case REWIND  -> tickRewind(dt);
            case GAME_OVER -> tickGameOver(dt);
            default      -> {}
        }

        // Always render
        render(dt);
    }

    private void tickPlaying(double dt) {
        // --- Check ESC for pause ---
        if (input.isPauseJustPressed()) {
            gameState = GameState.PAUSED;
            return;
        }

        // --- Check R for rewind ---
        if (input.isRewindJustPressed() && rewind.canRewind()) {
            rewind.startRewind();
            gameState = GameState.REWIND;
            sound.playRewind();
            return;
        }

        // --- Nitro ---
        nitro.update(input.isNitro(), dt);

        // --- Player update ---
        player.update(input, dt, weather, nitro.isActive());

        // --- Traffic update ---
        trafficManager.update(player.z, player.x, player.speed, dt);

        // --- Weather update ---
        weather.update(dt);

        // --- Collision detection ---
        List<AITraffic> allTraffic = trafficManager.getAllVehicles();
        CollisionManager.CollisionResult collision = collisionManager.check(player, allTraffic);

        if (collision.collided()) {
            player.triggerCollision();
            score.addCollisionPenalty();
            sound.playCollision();
        }

        // --- Near-miss detection ---
        for (AITraffic vehicle : allTraffic) {
            if (nearMiss.checkNearMiss(player, vehicle, dt)) {
                score.addNearMiss();
                sound.playNearMiss();
            }
        }
        nearMiss.update(dt);

        // --- Score update ---
        score.update(player.getSpeedKmh(), dt);

        // --- Background parallax ---
        // bgScrollX shifts based on road curvature (accumulates with track curves)
        int segIdx = (int)(player.z / 200.0) % map.getSegmentCount();
        double curvature = map.getSegment(segIdx).curve;
        bgScrollX -= curvature * player.speed * 0.001 * dt;

        // --- Snapshot for rewind ---
        rewind.snapshot(player, allTraffic);

        // --- Sound ---
        sound.startEngine(player.getSpeedKmh() / player.maxSpeed);

        // --- Mode-specific tick ---
        tickModeLogic(dt);
    }

    private void tickModeLogic(double dt) {
        switch (mode) {
            case TIME_TRIAL -> {
                timeRemainingSeconds -= dt;
                if (timeRemainingSeconds <= 0) {
                    gameState = GameState.GAME_OVER;
                    endGame();
                }
            }
            case BENGALURU_ENDLESS_RUSH -> {
                // Endless: game ends only on collision (handled above via speed drain)
                // Traffic density increases with distance
                // No specific end condition; player escapes
            }
            case MONSOON_SURVIVAL -> {
                // Harder handling, speed penalty applied by weather system
                if (player.getSpeedKmh() <= 0 && player.collisionRecoveryTime > 1.0) {
                    // Completely stopped after collision = game over
                    gameState = GameState.GAME_OVER;
                    endGame();
                }
            }
            default -> {} // Other modes run until player quits
        }
    }

    private void tickPaused(double dt) {
        if (input.isPauseJustPressed()) {
            gameState = GameState.PLAYING;
        }
        // M key to return to menu
        // This is checked in the HUD draw via ESC
    }

    private void tickRewind(double dt) {
        if (!input.isRewind()) {
            // Player released R key
            rewind.stopRewind();
            gameState = GameState.PLAYING;
            return;
        }

        List<AITraffic> allTraffic = trafficManager.getAllVehicles();
        rewind.applyRewindFrame(player, allTraffic, dt);

        if (!rewind.isRewinding()) {
            gameState = GameState.PLAYING;
        }
    }

    private void tickGameOver(double dt) {
        // ESC = menu, SPACE = retry
        if (input.isPauseJustPressed()) {
            endGame();
            sceneManager.returnToMainMenu();
        }
        // Space to retry would be input.isNitroJustPressed()
    }

    private void endGame() {
        // Update profile with results
        String modeKey = mode.name();
        long prev = profile.getHighScores().getOrDefault(modeKey, 0L);
        if (score.getScore() > prev) {
            profile.getHighScores().put(modeKey, score.getScore());
        }
        profile.setCoins(profile.getCoins() + score.getCoins());
        profile.setTotalRaces(profile.getTotalRaces() + 1);
        profile.setTotalDistanceKm(profile.getTotalDistanceKm() + score.getDistanceKm());

        // Check achievements
        checkAchievements();

        SaveManager.save(profile);
        stop();
    }

    private void checkAchievements() {
        List<String> achieved = profile.getAchievements();
        if (!achieved.contains("First Race")) {
            achieved.add("First Race");
        }
        if (player.getSpeedKmh() >= 200 && !achieved.contains("Speed Demon")) {
            achieved.add("Speed Demon");
        }
        if (score.getNearMissCount() >= 50 && !achieved.contains("Near Miss Master")) {
            achieved.add("Near Miss Master");
        }
        if (mode == GameMode.MONSOON_SURVIVAL && !achieved.contains("Monsoon Racer")) {
            achieved.add("Monsoon Racer");
        }
        if (score.getComboMultiplier() >= 10 && !achieved.contains("Combo King")) {
            achieved.add("Combo King");
        }
        if (profile.getTotalDistanceKm() >= 100 && !achieved.contains("Distance Champion")) {
            achieved.add("Distance Champion");
        }
        if (profile.getCoins() + score.getCoins() >= 10000 && !achieved.contains("High Roller")) {
            achieved.add("High Roller");
        }
    }

    private void render(double dt) {
        // Clear canvas
        roadGc.setFill(Color.BLACK);
        roadGc.fillRect(0, 0, screenW, screenH);

        // 1. Draw background (sky + parallax buildings)
        backgroundRenderer.draw(roadGc, location, weather, bgScrollX, screenW, screenH);

        // 2. Draw pseudo-3D road with traffic
        List<AITraffic> trafficForRender = trafficManager.getVehiclesSortedForRendering(player.z);
        roadRenderer.render(roadGc, player.z, player.x, map, trafficForRender, weather, location);

        // 3. Draw player car at fixed bottom-center position
        double carScreenX = screenW / 2.0;
        double carScreenY = screenH - 120;
        player.draw(roadGc, carScreenX, carScreenY, 3.0);

        // 4. Draw weather overlay (rain, fog)
        weather.drawBackground(roadGc, screenW, screenH);

        // 5. Draw HUD
        hud.draw(player, score, nitro, rewind, weather, location, mode, nearMiss, dt);

        // Draw state-specific overlays
        if (gameState == GameState.PAUSED) {
            hud.drawPaused();
        } else if (gameState == GameState.GAME_OVER) {
            hud.drawGameOver(score);
        }

        // Time trial countdown
        if (mode == GameMode.TIME_TRIAL && gameState == GameState.PLAYING) {
            drawTimeTrialHUD();
        }
    }

    private void drawTimeTrialHUD() {
        int mins = (int)(timeRemainingSeconds / 60);
        int secs = (int)(timeRemainingSeconds % 60);
        String timeStr = String.format("%02d:%02d", mins, secs);

        roadGc.setFont(javafx.scene.text.Font.font("Monospace",
            javafx.scene.text.FontWeight.BOLD, 28));

        Color timerColor = timeRemainingSeconds < 30
            ? Color.web("#FF2244")
            : Color.web("#00CCFF");
        roadGc.setFill(timerColor);
        roadGc.fillText("TIME: " + timeStr, screenW / 2 - 70, screenH - 20);
    }

    public void setPaused(boolean paused) {
        gameState = paused ? GameState.PAUSED : GameState.PLAYING;
    }

    public InputHandler getInputHandler() { return input; }
    public StackPane getRoot() { return root; }
    public PlayerCar getPlayer() { return player; }
    public ScoreManager getScore() { return score; }
    public GameState getGameState() { return gameState; }
}
