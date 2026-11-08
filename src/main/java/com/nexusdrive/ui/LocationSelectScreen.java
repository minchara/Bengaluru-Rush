package com.nexusdrive.ui;

import com.nexusdrive.world.LocationType;
import com.nexusdrive.data.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Location selection screen – shows six Bengaluru locations as interactive
 * cards arranged in a 2 × 3 {@link GridPane}. Locked locations display their
 * unlock cost and require sufficient coins in the player profile before the
 * game can be started.
 */
public class LocationSelectScreen extends BorderPane {

    // ── Location data record ───────────────────────────────────────────────────
    private record LocationEntry(
            LocationType type,
            String englishName,
            String kannadaName,
            String description,
            int    trafficDensity,   // 1–5
            int    unlockCost        // 0 = unlocked by default
    ) {}

    private static final LocationEntry[] LOCATIONS = {
        new LocationEntry(LocationType.MG_ROAD,
                "MG Road", "ಎಂ.ಜಿ. ರಸ್ತೆ",
                "Urban commercial district with Namma Metro", 3, 0),
        new LocationEntry(LocationType.SILK_BOARD,
                "Silk Board", "ಸಿಲ್ಕ್ ಬೋರ್ಡ್",
                "Notorious traffic junction — survive if you can!", 5, 0),
        new LocationEntry(LocationType.OUTER_RING_ROAD,
                "Outer Ring Road", "ಔಟರ್ ರಿಂಗ್ ರಸ್ತೆ",
                "Tech park corridor highway", 4, 500),
        new LocationEntry(LocationType.VIDHANA_SOUDHA,
                "Vidhana Soudha", "ವಿಧಾನ ಸೌಧ",
                "Night racing past iconic landmarks", 2, 1000),
        new LocationEntry(LocationType.NICE_ROAD,
                "NICE Road", "ನೈಸ್ ರಸ್ತೆ",
                "High-speed highway racing", 1, 2000),
        new LocationEntry(LocationType.LALBAGH,
                "Lal Bagh", "ಲಾಲ್ ಬಾಗ್",
                "Scenic green racing through the botanical garden", 2, 3000),
    };

    private final SceneManager sceneManager;
    private final PlayerProfile profile;

    public LocationSelectScreen(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        buildUI();
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        setTop(buildTopBar());
        setCenter(buildCardGrid());
        setBottom(buildBottomHint());
    }

    private HBox buildTopBar() {
        // Back button
        Button back = new Button("◀  BACK");
        back.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM));
        back.setOnMouseEntered(e -> back.setStyle(UIColors.buttonHoverStyle(UIColors.TEXT_DIM)));
        back.setOnMouseExited(e ->  back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM)));
        back.setOnAction(e -> sceneManager.showMainMenu());

        // Title
        Label title = new Label("SELECT LOCATION");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        title.setTextFill(UIColors.NEON_BLUE);
        title.setEffect(new DropShadow(16, UIColors.NEON_BLUE));

        Label kannadaTitle = new Label("ಸ್ಥಳ ಆಯ್ಕೆ ಮಾಡಿ");
        kannadaTitle.setFont(Font.font("SansSerif", 16));
        kannadaTitle.setTextFill(UIColors.TEXT_DIM);

        VBox titleBox = new VBox(2, title, kannadaTitle);
        titleBox.setAlignment(Pos.CENTER);

        // Coins display
        Label coins = new Label("🪙 " + profile.getCoins() + " COINS");
        coins.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        coins.setTextFill(UIColors.NEON_ORANGE);

        HBox bar = new HBox();
        bar.setPadding(new Insets(16, 24, 12, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #0A0A2A;");

        Region spacerL = new Region();
        Region spacerR = new Region();
        HBox.setHgrow(spacerL, Priority.ALWAYS);
        HBox.setHgrow(spacerR, Priority.ALWAYS);

        bar.getChildren().addAll(back, spacerL, titleBox, spacerR, coins);
        return bar;
    }

    private GridPane buildCardGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(24);
        grid.setPadding(new Insets(32));
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < LOCATIONS.length; i++) {
            int col = i % 2;
            int row = i / 2;
            grid.add(buildLocationCard(LOCATIONS[i]), col, row);
        }
        return grid;
    }

    private Pane buildLocationCard(LocationEntry loc) {
        boolean unlocked = loc.unlockCost() == 0 || profile.isLocationUnlocked(loc.type());

        // Card container
        VBox card = new VBox(8);
        card.setPrefWidth(560);
        card.setPrefHeight(170);
        card.setPadding(new Insets(16));

        Color borderColor = unlocked ? UIColors.NEON_BLUE : UIColors.LOCKED_GRAY;
        card.setStyle(UIColors.cardStyle(borderColor));

        // ── Row 1: English name + lock badge ──
        Label nameLabel = new Label(loc.englishName());
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 22));
        nameLabel.setTextFill(unlocked ? UIColors.TEXT_PRIMARY : UIColors.LOCKED_GRAY);

        Label lockBadge = new Label(unlocked ? "✓ UNLOCKED" : "🔒  " + loc.unlockCost() + " COINS");
        lockBadge.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        lockBadge.setTextFill(unlocked ? UIColors.NEON_GREEN : UIColors.NEON_ORANGE);

        Region rowSpacer = new Region();
        HBox.setHgrow(rowSpacer, Priority.ALWAYS);
        HBox row1 = new HBox(nameLabel, rowSpacer, lockBadge);
        row1.setAlignment(Pos.CENTER_LEFT);

        // ── Row 2: Kannada name ──
        Label kannadaLabel = new Label(loc.kannadaName());
        kannadaLabel.setFont(Font.font("SansSerif", 15));
        kannadaLabel.setTextFill(UIColors.TEXT_DIM);

        // ── Row 3: Description ──
        Label desc = new Label(loc.description());
        desc.setFont(Font.font("Monospace", 13));
        desc.setTextFill(UIColors.TEXT_DIM);
        desc.setWrapText(true);

        // ── Row 4: Traffic density dots + action button ──
        HBox trafficBox = buildTrafficIndicator(loc.trafficDensity());

        Button actionBtn;
        if (unlocked) {
            actionBtn = new Button("RACE HERE  ▶");
            actionBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
            actionBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN));
            actionBtn.setOnMouseEntered(e -> actionBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_GREEN)));
            actionBtn.setOnMouseExited(e ->  actionBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_GREEN)));
            actionBtn.setOnAction(e -> {
                profile.setSelectedLocation(loc.type().name());
                sceneManager.showModeSelect();
            });
        } else {
            actionBtn = new Button("UNLOCK  🔒  " + loc.unlockCost() + "c");
            actionBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
            actionBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE));
            actionBtn.setOnMouseEntered(e -> actionBtn.setStyle(UIColors.buttonHoverStyle(UIColors.NEON_ORANGE)));
            actionBtn.setOnMouseExited(e ->  actionBtn.setStyle(UIColors.buttonStyle(UIColors.NEON_ORANGE)));
            actionBtn.setOnAction(e -> attemptUnlock(loc));
        }

        Region btnSpacer = new Region();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);
        HBox row4 = new HBox(trafficBox, btnSpacer, actionBtn);
        row4.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(row1, kannadaLabel, desc, row4);

        // Hover effect only for unlocked cards
        if (unlocked) {
            card.setOnMouseEntered(e -> card.setStyle(UIColors.cardHoverStyle(UIColors.NEON_BLUE)));
            card.setOnMouseExited(e ->  card.setStyle(UIColors.cardStyle(UIColors.NEON_BLUE)));
        }

        return card;
    }

    private HBox buildTrafficIndicator(int density) {
        Label trafficLabel = new Label("TRAFFIC: ");
        trafficLabel.setFont(Font.font("Monospace", 12));
        trafficLabel.setTextFill(UIColors.TEXT_DIM);

        HBox dots = new HBox(4);
        dots.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            Canvas dot = new Canvas(12, 12);
            GraphicsContext gc = dot.getGraphicsContext2D();
            Color dotColor;
            if (i <= density) {
                dotColor = density >= 4 ? UIColors.NEON_RED
                         : density >= 3 ? UIColors.NEON_ORANGE
                         :                UIColors.NEON_GREEN;
            } else {
                dotColor = UIColors.LOCKED_GRAY;
            }
            gc.setFill(dotColor);
            gc.fillOval(0, 0, 12, 12);
            dots.getChildren().add(dot);
        }

        HBox row = new HBox(4, trafficLabel, dots);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void attemptUnlock(LocationEntry loc) {
        if (profile.getCoins() >= loc.unlockCost()) {
            profile.spendCoins(loc.unlockCost());
            profile.unlockLocation(loc.type());
            // Rebuild UI to reflect the change
            setCenter(buildCardGrid());
        } else {
            // Show insufficient-funds flash (replace bottom hint temporarily)
            Label msg = new Label("⚠  NOT ENOUGH COINS!  Need " + loc.unlockCost()
                    + "c, have " + profile.getCoins() + "c");
            msg.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
            msg.setTextFill(UIColors.NEON_RED);
            msg.setPadding(new Insets(10));
            HBox errBar = new HBox(msg);
            errBar.setAlignment(Pos.CENTER);
            errBar.setStyle("-fx-background-color: #1A0A0A;");
            setBottom(errBar);
            // Restore after 2 seconds
            javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            pause.setOnFinished(ev -> setBottom(buildBottomHint()));
            pause.play();
        }
    }

    private HBox buildBottomHint() {
        Label hint = new Label("Click a location card to select it. Locked locations require coins to unlock.");
        hint.setFont(Font.font("Monospace", 12));
        hint.setTextFill(UIColors.TEXT_DIM);
        HBox bar = new HBox(hint);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }
}
