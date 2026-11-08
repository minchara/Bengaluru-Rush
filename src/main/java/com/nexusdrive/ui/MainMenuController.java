package com.nexusdrive.ui;

import com.nexusdrive.data.PlayerProfile;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Main menu screen for NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Built entirely in code (no FXML). Features an animated pixel-art
 * Bengaluru city skyline scrolling in the background, the game title with
 * neon glow, and a vertical stack of navigation buttons.</p>
 */
public class MainMenuController extends BorderPane {

    private final SceneManager sceneManager;
    private final PlayerProfile profile;

    // Animated background
    private Canvas bgCanvas;
    private AnimationTimer bgAnimator;
    private double bgScrollX = 0;

    public MainMenuController(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        setupUI();
        startBackgroundAnimation();
    }

    // ── Layout ──────────────────────────────────────────────────────────────────

    private void setupUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        bgCanvas = new Canvas(1280, 720);

        VBox titleBox   = createTitleSection();
        VBox buttonBox  = createButtonSection();
        HBox bottomBar  = createBottomBar();

        StackPane root = new StackPane();
        root.getChildren().addAll(bgCanvas, createMainLayout(titleBox, buttonBox, bottomBar));
        setCenter(root);
    }

    private VBox createTitleSection() {
        // ── Main title ──
        Label title = new Label("NEXUS DRIVE");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 56));
        title.setTextFill(UIColors.NEON_BLUE);
        title.setEffect(new DropShadow(20, UIColors.NEON_BLUE));

        // ── Sub-title ──
        Label subtitle = new Label("BENGALURU RUSH");
        subtitle.setFont(Font.font("Monospace", FontWeight.BOLD, 32));
        subtitle.setTextFill(UIColors.NEON_PURPLE);
        subtitle.setEffect(new DropShadow(15, UIColors.NEON_PURPLE));

        // ── Kannada subtitle ──
        Label kannadaSub = new Label("ಬೆಂಗಳೂರು ರಷ್");
        kannadaSub.setFont(Font.font("SansSerif", 20));
        kannadaSub.setTextFill(UIColors.TEXT_DIM);

        // ── Decorative separator line ──
        Canvas sep = new Canvas(400, 2);
        GraphicsContext sg = sep.getGraphicsContext2D();
        LinearGradient sepGrad = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, UIColors.NEON_BLUE),
                new Stop(0.7, UIColors.NEON_PURPLE),
                new Stop(1, Color.TRANSPARENT));
        sg.setFill(sepGrad);
        sg.fillRect(0, 0, 400, 2);

        VBox box = new VBox(8, title, subtitle, kannadaSub, sep);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private VBox createButtonSection() {
        Button startBtn    = createMenuButton("▶  START RACE",        UIColors.NEON_GREEN,  e -> sceneManager.showLocationSelect());
        Button locationBtn = createMenuButton("◈  SELECT LOCATION",   UIColors.NEON_BLUE,   e -> sceneManager.showLocationSelect());
        Button modesBtn    = createMenuButton("◉  GAME MODES",        UIColors.NEON_PURPLE, e -> sceneManager.showModeSelect());
        Button garageBtn   = createMenuButton("⚙  GARAGE",            UIColors.NEON_ORANGE, e -> sceneManager.showGarage());
        Button achievBtn   = createMenuButton("★  ACHIEVEMENTS",      UIColors.NEON_BLUE,   e -> sceneManager.showAchievements());
        Button settingsBtn = createMenuButton("⚙  SETTINGS",          UIColors.TEXT_DIM,    e -> sceneManager.showSettings());

        // ── Coin display ──
        Label coins = new Label("🪙 " + profile.getCoins() + " COINS");
        coins.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        coins.setTextFill(UIColors.NEON_ORANGE);

        // ── Level / XP hint ──
        Label levelLabel = new Label("LEVEL " + profile.getLevel()
                + "  |  XP " + profile.getXp() + " / " + (profile.getLevel() * 1000));
        levelLabel.setFont(Font.font("Monospace", 13));
        levelLabel.setTextFill(UIColors.TEXT_DIM);

        VBox box = new VBox(12, startBtn, locationBtn, modesBtn, garageBtn, achievBtn, settingsBtn,
                new Separator(), coins, levelLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    private Button createMenuButton(String text, Color color, EventHandler<ActionEvent> action) {
        Button btn = new Button(text);
        btn.setPrefWidth(360);
        btn.setPrefHeight(52);
        btn.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        btn.setStyle(UIColors.buttonStyle(color));
        btn.setOnAction(action);
        btn.setOnMouseEntered(e -> btn.setStyle(UIColors.buttonHoverStyle(color)));
        btn.setOnMouseExited(e ->  btn.setStyle(UIColors.buttonStyle(color)));
        return btn;
    }

    /** Thin separator line used between buttons and stats. */
    private static class Separator extends Region {
        Separator() {
            setMinHeight(1);
            setPrefHeight(1);
            setMaxHeight(1);
            setPrefWidth(360);
            setStyle("-fx-background-color: #333366;");
        }
    }

    private HBox createBottomBar() {
        Label controls = new Label(
                "W/A/S/D or Arrow Keys  |  SPACE: Nitro  |  R: Rewind  |  ESC: Pause");
        controls.setFont(Font.font("Monospace", 12));
        controls.setTextFill(UIColors.TEXT_DIM);

        Label version = new Label("v1.0  |  Java/JavaFX Racing  |  Bengaluru, India  🏙");
        version.setFont(Font.font("Monospace", 12));
        version.setTextFill(UIColors.TEXT_DIM);

        HBox bar = new HBox(60, controls, version);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    private Node createMainLayout(VBox title, VBox buttons, HBox bottom) {
        BorderPane layout = new BorderPane();
        VBox center = new VBox(30, title, buttons);
        center.setAlignment(Pos.CENTER);
        layout.setCenter(center);
        layout.setBottom(bottom);
        layout.setPadding(new Insets(60, 0, 0, 0));
        return layout;
    }

    // ── Background Animation ────────────────────────────────────────────────────

    private void startBackgroundAnimation() {
        bgAnimator = new AnimationTimer() {
            private long lastTime = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) { lastTime = now; return; }
                double dt = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
                bgScrollX -= 30 * dt;
                drawBackground(bgScrollX);
            }
        };
        bgAnimator.start();
    }

    private void drawBackground(double scrollX) {
        GraphicsContext gc = bgCanvas.getGraphicsContext2D();
        double w = bgCanvas.getWidth();
        double h = bgCanvas.getHeight();

        // ── Sky gradient ──
        LinearGradient sky = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#01010F")),
                new Stop(1, Color.web("#050520")));
        gc.setFill(sky);
        gc.fillRect(0, 0, w, h);

        // ── Stars (deterministic positions) ──
        gc.setFill(Color.web("#AAAACC", 0.5));
        for (int i = 0; i < 80; i++) {
            double sx = ((i * 97 + 13) % (int) w);
            double sy = ((i * 53 +  7) % (int) (h * 0.5));
            double sz = (i % 3 == 0) ? 2 : 1;
            gc.fillRect(sx, sy, sz, sz);
        }

        // ── Distant purple nebula smear ──
        gc.setFill(Color.web("#330066", 0.08));
        gc.fillOval(w * 0.6, 30, 400, 200);
        gc.setFill(Color.web("#003355", 0.08));
        gc.fillOval(w * 0.1, 20, 320, 160);

        // ── Building skyline ──
        drawBuildingSkyline(gc, scrollX, w, h);

        // ── Ground strip ──
        LinearGradient ground = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1A1A2E")),
                new Stop(1, Color.web("#0D0D1A")));
        gc.setFill(ground);
        gc.fillRect(0, h * 0.78, w, h * 0.22);

        // ── Road lane markings ──
        gc.setFill(Color.web("#FFCC00", 0.3));
        for (int i = 0; i < 20; i++) {
            double rx = ((i * 120 + scrollX * 0.5) % (w + 120)) - 120;
            gc.fillRect(rx, h * 0.82, 80, 4);
        }

        // ── Neon road edge strips ──
        gc.setFill(Color.web("#00CCFF", 0.25));
        gc.fillRect(0, h * 0.78, w, 2);
        gc.setFill(Color.web("#CC00FF", 0.2));
        gc.fillRect(0, h * 0.78 + 4, w, 1);
    }

    private void drawBuildingSkyline(GraphicsContext gc, double scrollX, double w, double h) {
        int[]    heights = {120, 180, 90, 220, 160, 100, 200, 140, 80, 190, 110, 170};
        int[]    widths  = { 60,  50, 70,  45,  55,  80,  50,  65, 75,  48,  60,  55};
        String[] colors  = {
            "#1A1A3E", "#1E1A40", "#161630", "#201E44", "#181836", "#1C1C3A"
        };
        String[] windowColors = {"#FF8800", "#FFCC00", "#00CCFF", "#CC00FF", "#FF4400"};

        double totalWidth = 0;
        for (int bw : widths) totalWidth += bw + 8;

        double x = (scrollX % totalWidth) - totalWidth;
        // Draw two repetitions to cover any canvas width
        for (int rep = 0; rep < 3; rep++) {
            for (int i = 0; i < heights.length; i++) {
                double bh = heights[i];
                double bw = widths[i];
                double by = h * 0.78 - bh;
                gc.setFill(Color.web(colors[i % colors.length]));
                gc.fillRect(x, by, bw, bh);

                // Windows
                for (int wy = 0; wy < (int) (bh / 16) - 1; wy++) {
                    for (int wx = 0; wx < (int) (bw / 14); wx++) {
                        boolean lit = ((i + wy + wx) % 3 != 0);
                        if (lit) {
                            gc.setFill(Color.web(windowColors[(i + wy) % windowColors.length], 0.7));
                            gc.fillRect(x + wx * 14 + 4, by + wy * 16 + 4, 6, 8);
                        }
                    }
                }

                // Roof antenna on taller buildings
                if (bh > 150) {
                    gc.setFill(Color.web("#FF2244", 0.9));
                    gc.fillRect(x + bw / 2 - 1, by - 10, 2, 10);
                    gc.setFill(Color.web("#FF2244"));
                    gc.fillOval(x + bw / 2 - 3, by - 13, 6, 6);
                }

                x += bw + 8;
            }
        }
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    /** Stop the background animation (call when navigating away). */
    public void stop() {
        if (bgAnimator != null) bgAnimator.stop();
    }

    /** Restart the animation (call when navigating back). */
    public void start() {
        if (bgAnimator != null) bgAnimator.start();
    }
}
