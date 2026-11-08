package com.nexusdrive.ui;

import com.nexusdrive.data.CarDefinition;
import com.nexusdrive.data.PlayerProfile;
import com.nexusdrive.entity.PixelSprites;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Garage screen – car selection, colour picker, stat upgrades, and car preview.
 *
 * <p>Layout:
 * <pre>
 *  ┌─ Top Bar (title + back) ────────────────────────────────────────┐
 *  │ Left panel      │  Center canvas (preview) │  Right panel       │
 *  │  5 car cards    │  pixel-art sprite 6×     │  stats + upgrades  │
 *  └─ Bottom bar (save / cost) ──────────────────────────────────────┘
 * </pre>
 * </p>
 */
public class GarageScreen extends BorderPane {

    // Upgrade costs per level (index 0 = upgrading to level 1, etc.)
    private static final int[] UPGRADE_COSTS = {200, 400, 700, 1100, 1600};
    private static final int MAX_UPGRADE_LEVEL = 5;

    // 8 selectable car tint colours
    private static final Color[] TINT_COLORS = {
        Color.web("#00CCFF"), // neon blue
        Color.web("#CC00FF"), // neon purple
        Color.web("#FF8800"), // orange
        Color.web("#00FF88"), // neon green
        Color.web("#FF2244"), // red
        Color.web("#FFCC00"), // yellow
        Color.web("#FFFFFF"), // white
        Color.web("#888888"), // grey
    };

    private final SceneManager  sceneManager;
    private final PlayerProfile profile;
    private final List<CarDefinition> cars;

    private CarDefinition selectedCar;
    private Color         selectedTint;

    // Upgrade levels for the currently selected car [engine, brakes, handling, nitro]
    private int[] upgradeLevels = {0, 0, 0, 0};

    // Preview canvas & bob animation
    private Canvas        previewCanvas;
    private AnimationTimer previewAnimator;
    private double        bobTime = 0;

    // Right-panel rebuild references
    private VBox rightPanel;
    private Label totalCostLabel;

    public GarageScreen(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        this.cars = CarDefinition.getDefaultCars();
        this.selectedCar = findProfileCar();
        String savedColor = profile.getCarColors() != null ? profile.getCarColors().get(selectedCar.getId()) : null;
        this.selectedTint = savedColor != null ? Color.web(savedColor) : TINT_COLORS[0];
        loadUpgradeLevels();
        buildUI();
        startPreviewAnimation();
    }

    // ── Init helpers ───────────────────────────────────────────────────────────

    private CarDefinition findProfileCar() {
        String id = profile.getSelectedCarId();
        return cars.stream()
                   .filter(c -> c.getId().equals(id))
                   .findFirst()
                   .orElse(cars.get(0));
    }

    private void loadUpgradeLevels() {
        int[] upg = profile.getCarUpgrades() != null ? profile.getCarUpgrades().get(selectedCar.getId()) : null;
        upgradeLevels = upg != null ? upg.clone() : new int[4];
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        setTop(buildTopBar());
        setLeft(buildCarList());
        setCenter(buildPreviewPane());
        setRight(buildRightPanel());
        setBottom(buildBottomBar());
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        Button back = new Button("◀  BACK");
        back.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM));
        back.setOnMouseEntered(e -> back.setStyle(UIColors.buttonHoverStyle(UIColors.TEXT_DIM)));
        back.setOnMouseExited(e ->  back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM)));
        back.setOnAction(e -> {
            stopPreviewAnimation();
            sceneManager.showMainMenu();
        });

        Label title = new Label("⚙  GARAGE");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 34));
        title.setTextFill(UIColors.NEON_ORANGE);
        title.setEffect(new DropShadow(14, UIColors.NEON_ORANGE));

        Label kannada = new Label("ಗ್ಯಾರೇಜ್");
        kannada.setFont(Font.font("SansSerif", 15));
        kannada.setTextFill(UIColors.TEXT_DIM);

        VBox titleBox = new VBox(2, title, kannada);
        titleBox.setAlignment(Pos.CENTER);

        Label coins = new Label("🪙 " + profile.getCoins() + " COINS");
        coins.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        coins.setTextFill(UIColors.NEON_ORANGE);

        Region sL = new Region(); HBox.setHgrow(sL, Priority.ALWAYS);
        Region sR = new Region(); HBox.setHgrow(sR, Priority.ALWAYS);

        HBox bar = new HBox(back, sL, titleBox, sR, coins);
        bar.setPadding(new Insets(14, 24, 10, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    // ── Left: car list ────────────────────────────────────────────────────────

    private ScrollPane buildCarList() {
        VBox list = new VBox(10);
        list.setPadding(new Insets(16, 10, 16, 16));
        list.setAlignment(Pos.TOP_CENTER);

        Label header = new Label("VEHICLES");
        header.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        header.setTextFill(UIColors.TEXT_DIM);
        list.getChildren().add(header);

        for (CarDefinition car : cars) {
            list.getChildren().add(buildCarCard(car));
        }

        ScrollPane sp = new ScrollPane(list);
        sp.setPrefWidth(220);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private Pane buildCarCard(CarDefinition car) {
        boolean owned    = profile.isCarOwned(car.getId());
        boolean selected = car.getId().equals(selectedCar.getId());

        VBox card = new VBox(4);
        card.setPadding(new Insets(10));
        card.setPrefWidth(190);

        Color border = selected ? UIColors.NEON_ORANGE : (owned ? UIColors.NEON_BLUE : UIColors.LOCKED_GRAY);
        card.setStyle(UIColors.cardStyle(border));

        Label nameLbl = new Label(car.getName());
        nameLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        nameLbl.setTextFill(selected ? UIColors.NEON_ORANGE : UIColors.TEXT_PRIMARY);
        nameLbl.setWrapText(true);

        String statusText = selected ? "ACTIVE" : (owned ? "OWNED" : "🔒 " + car.getUnlockCost() + "c");
        Label statusLbl = new Label(statusText);
        statusLbl.setFont(Font.font("Monospace", 11));
        statusLbl.setTextFill(selected ? UIColors.NEON_ORANGE : (owned ? UIColors.NEON_GREEN : UIColors.NEON_ORANGE));

        card.getChildren().addAll(nameLbl, statusLbl);

        card.setOnMouseClicked(e -> selectCar(car));
        card.setOnMouseEntered(e -> card.setStyle(UIColors.cardHoverStyle(border)));
        card.setOnMouseExited(e ->  card.setStyle(UIColors.cardStyle(border)));

        return card;
    }

    // ── Center: preview canvas ────────────────────────────────────────────────

    private StackPane buildPreviewPane() {
        previewCanvas = new Canvas(420, 420);
        drawCarPreview(0);

        Label previewLabel = new Label("PREVIEW");
        previewLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        previewLabel.setTextFill(UIColors.TEXT_DIM);
        StackPane.setAlignment(previewLabel, Pos.BOTTOM_CENTER);

        StackPane pane = new StackPane(previewCanvas, previewLabel);
        pane.setStyle("-fx-background-color: #0D0D20;");
        pane.setPadding(new Insets(16));
        return pane;
    }

    private void drawCarPreview(double bob) {
        GraphicsContext gc = previewCanvas.getGraphicsContext2D();
        double w = previewCanvas.getWidth();
        double h = previewCanvas.getHeight();

        gc.setFill(Color.web("#0D0D20"));
        gc.fillRect(0, 0, w, h);

        // Ground shadow
        gc.setFill(Color.web("#000000", 0.4));
        gc.fillOval(w / 2 - 60, h / 2 + 55 + bob, 120, 20);

        // Draw the car's pixel sprite at 6× scale
        int[][] sprite = PixelSprites.getPlayerSprite(selectedCar.getId());
        if (sprite == null || sprite.length == 0) {
            drawPlaceholderCar(gc, w, h, bob);
            return;
        }
        PixelSprites.drawSprite(gc, sprite, w / 2.0, h / 2.0 + bob, 6.0, selectedTint);

        // Name overlay at bottom of preview
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 18));
        gc.setFill(UIColors.TEXT_PRIMARY);
        gc.fillText(selectedCar.getName(), 20, h - 24);

        // Neon underlighting strip
        int rows = sprite.length;
        int cols = sprite[0].length;
        double scale = 6.0;
        double startX = w / 2.0 - (cols * scale) / 2.0;
        double startY = h / 2.0 - (rows * scale) / 2.0 + bob;
        gc.setFill(selectedTint.deriveColor(0, 1, 1, 0.5));
        gc.fillRect(startX - 10, startY + rows * scale + 2, cols * scale + 20, 3);
    }

    /** Simple placeholder when no sprite data is set. */
    private void drawPlaceholderCar(GraphicsContext gc, double w, double h, double bob) {
        int scale = 6;
        // 10×20 simple car shape
        int[][] shape = {
            {0,0,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,1,1,1,1,1,1,1,1},
        };
        double sx = w / 2.0 - (shape[0].length * scale) / 2.0;
        double sy = h / 2.0 - (shape.length * scale) / 2.0 + bob;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    gc.setFill(selectedTint);
                    gc.fillRect(sx + c * scale, sy + r * scale, scale, scale);
                }
            }
        }
    }

    private Color tintBlend(Color base, Color tint) {
        // Multiply blend – preserves shading while applying tint
        return new Color(
                base.getRed()   * tint.getRed(),
                base.getGreen() * tint.getGreen(),
                base.getBlue()  * tint.getBlue(),
                base.getOpacity());
    }

    // ── Right: stats + upgrades ───────────────────────────────────────────────

    private ScrollPane buildRightPanel() {
        rightPanel = buildRightPanelContent();
        ScrollPane sp = new ScrollPane(rightPanel);
        sp.setPrefWidth(360);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private VBox buildRightPanelContent() {
        VBox panel = new VBox(16);
        panel.setPadding(new Insets(16, 16, 16, 10));

        // ── Car name + base stats ──
        Label carName = new Label(selectedCar.getName());
        carName.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        carName.setTextFill(UIColors.NEON_ORANGE);

        Label carDesc = new Label(selectedCar.getDescription());
        carDesc.setFont(Font.font("Monospace", 12));
        carDesc.setTextFill(UIColors.TEXT_DIM);
        carDesc.setWrapText(true);

        panel.getChildren().addAll(carName, carDesc, separator());
        panel.getChildren().add(sectionLabel("BASE STATS"));
        panel.getChildren().add(buildStatBar("Top Speed",  (int) Math.min(10, selectedCar.getBaseTopSpeed() / 26.0),  10));
        panel.getChildren().add(buildStatBar("Accel",      (int) Math.min(10, selectedCar.getBaseAccel()),             10));
        panel.getChildren().add(buildStatBar("Handling",   (int) Math.min(10, selectedCar.getBaseHandling() * 10.0),    10));
        panel.getChildren().add(buildStatBar("Nitro Cap",  (int) Math.min(10, selectedCar.getBaseNitro() / 10.0),      10));
        panel.getChildren().add(separator());

        // ── Colour picker ──
        panel.getChildren().add(sectionLabel("COLOUR"));
        panel.getChildren().add(buildColorPicker());
        panel.getChildren().add(separator());

        // ── Upgrades ──
        panel.getChildren().add(sectionLabel("UPGRADES"));
        panel.getChildren().add(buildUpgradeRow("ENGINE",   upgradeLevels[0], 0));
        panel.getChildren().add(buildUpgradeRow("BRAKES",   upgradeLevels[1], 1));
        panel.getChildren().add(buildUpgradeRow("HANDLING", upgradeLevels[2], 2));
        panel.getChildren().add(buildUpgradeRow("NITRO",    upgradeLevels[3], 3));
        panel.getChildren().add(separator());

        // ── Unlock / Select ──
        boolean owned = profile.isCarOwned(selectedCar.getId());
        if (!owned) {
            Button unlockBtn = new Button("UNLOCK  🔒  " + selectedCar.getUnlockCost() + " COINS");
            unlockBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
            unlockBtn.setPrefWidth(300);
            unlockBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE));
            unlockBtn.setOnMouseEntered(e -> unlockBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_ORANGE)));
            unlockBtn.setOnMouseExited(e ->  unlockBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE)));
            unlockBtn.setOnAction(e -> attemptUnlockCar());
            panel.getChildren().add(unlockBtn);
        } else {
            Button selectBtn = new Button("✓  SET AS ACTIVE CAR");
            selectBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
            selectBtn.setPrefWidth(300);
            selectBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN));
            selectBtn.setOnMouseEntered(e -> selectBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_GREEN)));
            selectBtn.setOnMouseExited(e ->  selectBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN)));
            selectBtn.setOnAction(e -> {
                profile.setSelectedCarId(selectedCar.getId());
                refreshRight();
            });
            panel.getChildren().add(selectBtn);
        }

        return panel;
    }

    private HBox buildColorPicker() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        for (Color tint : TINT_COLORS) {
            Rectangle circle = new Rectangle(28, 28);
            circle.setArcWidth(28);
            circle.setArcHeight(28);
            circle.setFill(tint);
            circle.setStroke(tint.equals(selectedTint) ? Color.WHITE : Color.TRANSPARENT);
            circle.setStrokeWidth(2.5);
            circle.setOnMouseClicked(e -> {
                selectedTint = tint;
                drawCarPreview(0);
                refreshRight();
            });
            circle.setOnMouseEntered(e -> circle.setEffect(new DropShadow(10, tint)));
            circle.setOnMouseExited(e ->  circle.setEffect(null));
            row.getChildren().add(circle);
        }
        return row;
    }

    private HBox buildStatBar(String name, int value, int maxValue) {
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Monospace", 12));
        nameLbl.setTextFill(UIColors.TEXT_DIM);
        nameLbl.setPrefWidth(80);

        HBox blocks = new HBox(3);
        for (int i = 0; i < maxValue; i++) {
            Rectangle block = new Rectangle(16, 10);
            block.setFill(i < value ? UIColors.NEON_BLUE : UIColors.LOCKED_GRAY.darker());
            block.setArcWidth(2);
            block.setArcHeight(2);
            blocks.getChildren().add(block);
        }

        Label valueLbl = new Label(value + "/" + maxValue);
        valueLbl.setFont(Font.font("Monospace", 11));
        valueLbl.setTextFill(UIColors.TEXT_DIM);

        HBox row = new HBox(8, nameLbl, blocks, valueLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node buildUpgradeRow(String statName, int currentLevel, int upgradeIndex) {
        Label nameLbl = new Label(statName);
        nameLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        nameLbl.setTextFill(UIColors.TEXT_PRIMARY);
        nameLbl.setPrefWidth(90);

        // 5 blocks
        HBox blocks = new HBox(3);
        for (int i = 0; i < MAX_UPGRADE_LEVEL; i++) {
            Rectangle block = new Rectangle(18, 12);
            block.setFill(i < currentLevel ? UIColors.NEON_BLUE : Color.web("#222244"));
            block.setArcWidth(2);
            block.setArcHeight(2);
            blocks.getChildren().add(block);
        }

        Label lvlLbl = new Label("Lv" + currentLevel);
        lvlLbl.setFont(Font.font("Monospace", 11));
        lvlLbl.setTextFill(UIColors.NEON_BLUE);

        Button upgradeBtn;
        if (currentLevel >= MAX_UPGRADE_LEVEL) {
            upgradeBtn = new Button("MAX");
            upgradeBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
            upgradeBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN));
            upgradeBtn.setDisable(true);
        } else {
            int cost = UPGRADE_COSTS[currentLevel];
            upgradeBtn = new Button("+" + cost + "c");
            upgradeBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
            upgradeBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE));
            upgradeBtn.setOnMouseEntered(e -> upgradeBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_ORANGE)));
            upgradeBtn.setOnMouseExited(e ->  upgradeBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE)));
            upgradeBtn.setOnAction(e -> attemptUpgrade(upgradeIndex, cost));
        }

        HBox row = new HBox(8, nameLbl, blocks, lvlLbl, upgradeBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Bottom bar ────────────────────────────────────────────────────────────

    private HBox buildBottomBar() {
        Button saveBtn = new Button("💾  SAVE CHANGES");
        saveBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        saveBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN));
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_GREEN)));
        saveBtn.setOnMouseExited(e ->  saveBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN)));
        saveBtn.setOnAction(e -> saveChanges());

        totalCostLabel = new Label("Total upgrade cost: 0c");
        totalCostLabel.setFont(Font.font("Monospace", 13));
        totalCostLabel.setTextFill(UIColors.TEXT_DIM);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(16, totalCostLabel, spacer, saveBtn);
        bar.setPadding(new Insets(10, 24, 10, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void selectCar(CarDefinition car) {
        selectedCar = car;
        loadUpgradeLevels();
        refreshRight();
        drawCarPreview(0);
        setLeft(buildCarList()); // refresh selected highlight
    }

    private void attemptUnlockCar() {
        int cost = selectedCar.getUnlockCost();
        if (profile.getCoins() >= cost) {
            profile.spendCoins(cost);
            profile.unlockCar(selectedCar.getId());
            profile.setSelectedCarId(selectedCar.getId());
            refreshRight();
            setLeft(buildCarList());
        } else {
            flashError("Not enough coins! Need " + cost + "c, have " + profile.getCoins() + "c");
        }
    }

    private void attemptUpgrade(int upgradeIndex, int cost) {
        if (upgradeLevels[upgradeIndex] >= MAX_UPGRADE_LEVEL) return;
        if (profile.getCoins() >= cost) {
            profile.spendCoins(cost);
            upgradeLevels[upgradeIndex]++;
            profile.setCarUpgrades(selectedCar.getId(), upgradeLevels.clone());
            refreshRight();
        } else {
            flashError("Need " + cost + "c to upgrade!");
        }
    }

    private void flashError(String msg) {
        Label err = new Label("⚠  " + msg);
        err.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        err.setTextFill(UIColors.NEON_RED);
        err.setPadding(new Insets(10));
        HBox errBar = new HBox(err);
        errBar.setAlignment(Pos.CENTER);
        errBar.setStyle("-fx-background-color: #1A0A0A;");
        setBottom(errBar);
        javafx.animation.PauseTransition pt =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pt.setOnFinished(e -> setBottom(buildBottomBar()));
        pt.play();
    }

    private void saveChanges() {
        profile.setCarColor(selectedTint);
        profile.setSelectedCarId(selectedCar.getId());
        profile.saveToFile();
        flashSuccess("Changes saved!");
    }

    private void flashSuccess(String msg) {
        Label lbl = new Label("✓  " + msg);
        lbl.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        lbl.setTextFill(UIColors.NEON_GREEN);
        lbl.setPadding(new Insets(10));
        HBox bar = new HBox(lbl);
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #0A1A0A;");
        setBottom(bar);
        javafx.animation.PauseTransition pt =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pt.setOnFinished(e -> setBottom(buildBottomBar()));
        pt.play();
    }

    private void refreshRight() {
        rightPanel = buildRightPanelContent();
        ScrollPane sp = new ScrollPane(rightPanel);
        sp.setPrefWidth(360);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setRight(sp);
    }

    // ── Preview animation ─────────────────────────────────────────────────────

    private void startPreviewAnimation() {
        previewAnimator = new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) { last = now; return; }
                double dt = (now - last) / 1_000_000_000.0;
                last = now;
                bobTime += dt;
                double bob = Math.sin(bobTime * 2.0) * 5.0; // gentle bob ±5 px
                drawCarPreview(bob);
            }
        };
        previewAnimator.start();
    }

    public void stopPreviewAnimation() {
        if (previewAnimator != null) previewAnimator.stop();
    }

    // ── Utility widgets ───────────────────────────────────────────────────────

    private static Region separator() {
        Region r = new Region();
        r.setMinHeight(1);
        r.setPrefHeight(1);
        r.setMaxHeight(1);
        r.setStyle("-fx-background-color: #333366;");
        return r;
    }

    private static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        l.setTextFill(UIColors.TEXT_DIM);
        return l;
    }
}
