package com.nexusdrive.ui;

import com.nexusdrive.audio.SoundManager;
import com.nexusdrive.data.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Settings screen for NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Controls:
 * <ul>
 *   <li>Master volume slider (0–100) → {@link SoundManager#setMasterVolume}</li>
 *   <li>SFX volume slider (0–100) → {@link SoundManager#setSfxVolume}</li>
 *   <li>Music volume slider (0–100) → {@link SoundManager#setMusicVolume}</li>
 *   <li>Player stats summary (car, races, distance, coins)</li>
 *   <li>Reset save data button (with two-step confirmation)</li>
 * </ul>
 * Settings are written back to the profile / SoundManager when "Save & Back" is clicked.
 * </p>
 */
public class SettingsScreen extends BorderPane {

    private final SceneManager  sceneManager;
    private final PlayerProfile profile;

    // Sliders – kept as fields so we can read them on save
    private Slider masterSlider;
    private Slider sfxSlider;
    private Slider musicSlider;

    // Confirmation state for reset
    private boolean awaitingResetConfirm = false;

    public SettingsScreen(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        buildUI();
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        setTop(buildTopBar());
        setCenter(buildContent());
        setBottom(buildBottomBar());
    }

    private HBox buildTopBar() {
        Label title = new Label("⚙  SETTINGS");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        title.setTextFill(UIColors.NEON_BLUE);
        title.setEffect(new DropShadow(14, UIColors.NEON_BLUE));

        Label kannada = new Label("ಸೆಟ್ಟಿಂಗ್ಸ್");
        kannada.setFont(Font.font("SansSerif", 15));
        kannada.setTextFill(UIColors.TEXT_DIM);

        VBox titleBox = new VBox(2, title, kannada);
        titleBox.setAlignment(Pos.CENTER);

        Region sL = new Region(); HBox.setHgrow(sL, Priority.ALWAYS);
        Region sR = new Region(); HBox.setHgrow(sR, Priority.ALWAYS);

        HBox bar = new HBox(new Region(), sL, titleBox, sR, new Region());
        bar.setPadding(new Insets(16, 24, 12, 24));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(36, 120, 36, 120));
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(900);

        // ── Audio Section ──
        content.getChildren().add(sectionHeader("🔊  AUDIO", UIColors.NEON_BLUE));

        SoundManager sm = SoundManager.getInstance();
        masterSlider = buildVolumeSlider("Master Volume", (int)(sm.getMasterVolume() * 100));
        sfxSlider    = buildVolumeSlider("SFX Volume",    (int)(sm.getSfxVolume()    * 100));
        musicSlider  = buildVolumeSlider("Music Volume",  (int)(sm.getMusicVolume()  * 100));

        // Live-update SoundManager as sliders move
        masterSlider.valueProperty().addListener((obs, o, n) ->
                sm.setMasterVolume(n.doubleValue() / 100.0));
        sfxSlider.valueProperty().addListener((obs, o, n) ->
                sm.setSfxVolume(n.doubleValue() / 100.0));
        musicSlider.valueProperty().addListener((obs, o, n) ->
                sm.setMusicVolume(n.doubleValue() / 100.0));

        content.getChildren().addAll(
                buildSliderRow("Master Volume",    masterSlider),
                buildSliderRow("SFX Volume",       sfxSlider),
                buildSliderRow("Music Volume",     musicSlider));

        content.getChildren().add(separator());

        // ── Player Info Section ──
        content.getChildren().add(sectionHeader("👤  PLAYER INFO", UIColors.NEON_ORANGE));
        content.getChildren().add(buildPlayerInfo());
        content.getChildren().add(separator());

        // ── Controls Reference ──
        content.getChildren().add(sectionHeader("🎮  CONTROLS", UIColors.NEON_PURPLE));
        content.getChildren().add(buildControlsTable());
        content.getChildren().add(separator());

        // ── Danger Zone ──
        content.getChildren().add(sectionHeader("⚠  DANGER ZONE", UIColors.NEON_RED));
        content.getChildren().add(buildResetSection());

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    // ── Slider ────────────────────────────────────────────────────────────────

    private Slider buildVolumeSlider(String name, int initialValue) {
        Slider slider = new Slider(0, 100, initialValue);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setBlockIncrement(5);
        slider.setPrefWidth(400);
        slider.setStyle(
            "-fx-control-inner-background: #111128; " +
            "-fx-background-color: #0A0A1A; " +
            "-fx-tick-label-fill: #8888BB;");
        return slider;
    }

    private HBox buildSliderRow(String label, Slider slider) {
        Label nameLbl = new Label(label);
        nameLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        nameLbl.setTextFill(UIColors.TEXT_PRIMARY);
        nameLbl.setPrefWidth(200);

        // Live percentage readout
        Label valueLbl = new Label(String.format("%d%%", (int) slider.getValue()));
        valueLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        valueLbl.setTextFill(UIColors.NEON_BLUE);
        valueLbl.setPrefWidth(55);
        slider.valueProperty().addListener((obs, o, n) ->
                valueLbl.setText(String.format("%d%%", n.intValue())));

        HBox row = new HBox(16, nameLbl, slider, valueLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Player info ───────────────────────────────────────────────────────────

    private GridPane buildPlayerInfo() {
        GridPane grid = new GridPane();
        grid.setHgap(32);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER_LEFT);

        addInfoRow(grid, 0, "Active Car",       profile.getSelectedCarId());
        addInfoRow(grid, 1, "Total Races",      String.valueOf(profile.getTotalRaces()));
        addInfoRow(grid, 2, "Total Distance",   String.format("%.2f KM", profile.getTotalDistanceKm()));
        addInfoRow(grid, 3, "Total Coins",      String.valueOf(profile.getTotalCoinsEarned()));
        addInfoRow(grid, 4, "Current Coins",    String.valueOf(profile.getCoins()));
        addInfoRow(grid, 5, "Level",            String.valueOf(profile.getLevel()));
        addInfoRow(grid, 6, "XP",               profile.getXp() + " / " + (profile.getLevel() * 1000));

        return grid;
    }

    private void addInfoRow(GridPane grid, int row, String key, String value) {
        Label keyLbl = new Label(key + ":");
        keyLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        keyLbl.setTextFill(UIColors.TEXT_DIM);

        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Monospace", 13));
        valLbl.setTextFill(UIColors.TEXT_PRIMARY);

        grid.add(keyLbl, 0, row);
        grid.add(valLbl, 1, row);
    }

    // ── Controls table ────────────────────────────────────────────────────────

    private GridPane buildControlsTable() {
        GridPane grid = new GridPane();
        grid.setHgap(32);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER_LEFT);

        String[][] controls = {
            {"W / ↑",        "Accelerate"},
            {"S / ↓",        "Brake / Reverse"},
            {"A / ←",        "Steer left"},
            {"D / →",        "Steer right"},
            {"SPACE",        "Activate Nitro"},
            {"R",            "Activate Rewind"},
            {"ESC",          "Pause game"},
            {"M (paused)",   "Return to menu"},
            {"SPACE (over)", "Retry after game over"},
        };

        for (int i = 0; i < controls.length; i++) {
            Label keyLbl = new Label(controls[i][0]);
            keyLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
            keyLbl.setTextFill(UIColors.NEON_BLUE);
            keyLbl.setPrefWidth(140);
            keyLbl.setStyle("-fx-background-color: #111128; -fx-border-color: #333366; "
                    + "-fx-border-width: 1; -fx-padding: 2 6 2 6; -fx-border-radius: 3;");

            Label descLbl = new Label(controls[i][1]);
            descLbl.setFont(Font.font("Monospace", 13));
            descLbl.setTextFill(UIColors.TEXT_PRIMARY);

            grid.add(keyLbl,  0, i);
            grid.add(descLbl, 1, i);
        }
        return grid;
    }

    // ── Reset section ─────────────────────────────────────────────────────────

    private VBox buildResetSection() {
        Label warning = new Label(
                "Resetting will erase all progress, coins, unlocked cars and achievements.\n"
              + "This action CANNOT be undone.");
        warning.setFont(Font.font("Monospace", 13));
        warning.setTextFill(UIColors.NEON_RED);
        warning.setWrapText(true);

        Button resetBtn = buildDangerButton("⚠  RESET ALL SAVE DATA");

        Label confirmLabel = new Label();
        confirmLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        confirmLabel.setTextFill(UIColors.NEON_RED);

        resetBtn.setOnAction(e -> {
            if (!awaitingResetConfirm) {
                awaitingResetConfirm = true;
                resetBtn.setText("⚠  CONFIRM RESET – CLICK AGAIN");
                confirmLabel.setText("All data will be permanently deleted!");
            } else {
                profile.resetAll();
                profile.saveToFile();
                awaitingResetConfirm = false;
                resetBtn.setText("⚠  RESET ALL SAVE DATA");
                confirmLabel.setText("✓  Save data has been reset.");
                confirmLabel.setTextFill(UIColors.NEON_GREEN);
                // Cancel confirm state after 3 seconds
                javafx.animation.PauseTransition pt =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
                pt.setOnFinished(ev -> confirmLabel.setText(""));
                pt.play();
            }
        });

        VBox box = new VBox(10, warning, resetBtn, confirmLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Button buildDangerButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        btn.setPrefWidth(360);
        btn.setPrefHeight(44);
        btn.setStyle(UIColors.buttonStyle(UIColors.NEON_RED));
        btn.setOnMouseEntered(e -> btn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_RED)));
        btn.setOnMouseExited(e ->  btn.setStyle(UIColors.buttonStyle(UIColors.NEON_RED)));
        return btn;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────

    private HBox buildBottomBar() {
        Button saveBack = new Button("💾  SAVE & BACK");
        saveBack.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        saveBack.setPrefWidth(200);
        saveBack.setPrefHeight(44);
        saveBack.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN));
        saveBack.setOnMouseEntered(e -> saveBack.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_GREEN)));
        saveBack.setOnMouseExited(e ->  saveBack.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN)));
        saveBack.setOnAction(e -> {
            saveSettings();
            sceneManager.showMainMenu();
        });

        Button discardBack = new Button("✗  DISCARD & BACK");
        discardBack.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        discardBack.setPrefWidth(200);
        discardBack.setPrefHeight(44);
        discardBack.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM));
        discardBack.setOnMouseEntered(e -> discardBack.setStyle(UIColors.buttonHoverStyle(UIColors.TEXT_DIM)));
        discardBack.setOnMouseExited(e ->  discardBack.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM)));
        discardBack.setOnAction(e -> sceneManager.showMainMenu());

        HBox bar = new HBox(16, discardBack, saveBack);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    // ── Save logic ────────────────────────────────────────────────────────────

    private void saveSettings() {
        SoundManager sm = SoundManager.getInstance();
        sm.setMasterVolume(masterSlider.getValue() / 100.0);
        sm.setSfxVolume(sfxSlider.getValue() / 100.0);
        sm.setMusicVolume(musicSlider.getValue() / 100.0);

        profile.setMasterVolume((int) masterSlider.getValue());
        profile.setSfxVolume((int) sfxSlider.getValue());
        profile.setMusicVolume((int) musicSlider.getValue());
        profile.saveToFile();
    }

    // ── Utility widgets ───────────────────────────────────────────────────────

    private Label sectionHeader(String text, Color color) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        l.setTextFill(color);
        l.setEffect(new DropShadow(10, color));
        return l;
    }

    private static Region separator() {
        Region r = new Region();
        r.setMinHeight(1);
        r.setPrefHeight(1);
        r.setMaxHeight(1);
        r.setPrefWidth(Double.MAX_VALUE);
        r.setStyle("-fx-background-color: #333366;");
        return r;
    }
}
