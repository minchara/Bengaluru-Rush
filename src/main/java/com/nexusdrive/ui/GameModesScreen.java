package com.nexusdrive.ui;

import com.nexusdrive.GameMode;
import com.nexusdrive.world.LocationType;
import com.nexusdrive.data.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Game mode selection screen for NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Presents seven game modes as interactive cards inside a {@link ScrollPane}.
 * Selecting a mode stores it on the player profile and starts the game via
 * {@link SceneManager#showGame}.</p>
 */
public class GameModesScreen extends BorderPane {

    // ── Mode data record ───────────────────────────────────────────────────────
    private record ModeEntry(
            GameMode mode,
            String   displayName,
            String   kannadaName,
            String   description,
            String   objective,
            Color    accentColor,
            String   icon
    ) {}

    private static final ModeEntry[] MODES = {
        new ModeEntry(
            GameMode.BENGALURU_ENDLESS_RUSH,
            "Bengaluru Endless Rush",
            "ಬೆಂಗಳೂರು ಎಂಡ್ಲೆಸ್ ರಷ್",
            "Dodge infinite traffic on the streets of Bengaluru. The road never ends!",
            "Survive as long as possible. Score multiplies over time.",
            UIColors.NEON_BLUE, "∞"
        ),
        new ModeEntry(
            GameMode.SILK_BOARD_CHALLENGE,
            "Silk Board Challenge",
            "ಸಿಲ್ಕ್ ಬೋರ್ಡ್ ಸವಾಲು",
            "Navigate the world's most infamous junction at peak hour.",
            "Survive 5 minutes at Silk Board without crashing.",
            UIColors.NEON_RED, "⚠"
        ),
        new ModeEntry(
            GameMode.MG_ROAD_NIGHT_RACE,
            "MG Road Night Race",
            "ಎಂ.ಜಿ. ರಸ್ತೆ ರಾತ್ರಿ ರೇಸ್",
            "Blaze down MG Road at midnight – lights, action, neon signs!",
            "Reach 5 KM within the time limit.",
            UIColors.NEON_PURPLE, "🌙"
        ),
        new ModeEntry(
            GameMode.NICE_ROAD_HIGHWAY,
            "NICE Road Highway Blitz",
            "ನೈಸ್ ರಸ್ತೆ ಹೈವೇ ಬ್ಲಿಟ್ಜ್",
            "Open highway, full throttle – how fast can you go?",
            "Reach 200 KM/H and maintain it for 30 seconds.",
            UIColors.NEON_ORANGE, "🏁"
        ),
        new ModeEntry(
            GameMode.MONSOON_SURVIVAL,
            "Monsoon Survival",
            "ಮಳೆಗಾಲ ಸರ್ವೈವಲ್",
            "Bengaluru monsoon is brutal. Slippery roads, low visibility, heavy rain.",
            "Complete 3 KM in monsoon conditions without crashing.",
            UIColors.NEON_BLUE, "🌧"
        ),
        new ModeEntry(
            GameMode.TIME_TRIAL,
            "Time Trial",
            "ಸಮಯ ಪ್ರಯೋಗ",
            "Race against the clock. Every second counts.",
            "Complete the course in the shortest time possible.",
            UIColors.NEON_GREEN, "⏱"
        ),
        new ModeEntry(
            GameMode.LANDMARK_CHALLENGE,
            "Landmark Challenge",
            "ಹೆಗ್ಗುರುತು ಸವಾಲು",
            "Race through iconic Bengaluru landmarks – Vidhana Soudha, Lal Bagh and more.",
            "Collect all 10 landmark checkpoints before time runs out.",
            UIColors.NEON_ORANGE, "★"
        ),
    };

    private final SceneManager sceneManager;
    private final PlayerProfile profile;

    public GameModesScreen(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        buildUI();
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        setTop(buildTopBar());
        setCenter(buildScrollableCards());
        setBottom(buildBottomHint());
    }

    private HBox buildTopBar() {
        Button back = new Button("◀  BACK");
        back.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM));
        back.setOnMouseEntered(e -> back.setStyle(UIColors.buttonHoverStyle(UIColors.TEXT_DIM)));
        back.setOnMouseExited(e ->  back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM)));
        back.setOnAction(e -> sceneManager.showMainMenu());

        Label title = new Label("GAME MODES");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        title.setTextFill(UIColors.NEON_PURPLE);
        title.setEffect(new DropShadow(16, UIColors.NEON_PURPLE));

        Label kannadaTitle = new Label("ಆಟದ ಮೋಡ್ ಆಯ್ಕೆ ಮಾಡಿ");
        kannadaTitle.setFont(Font.font("SansSerif", 15));
        kannadaTitle.setTextFill(UIColors.TEXT_DIM);

        VBox titleBox = new VBox(2, title, kannadaTitle);
        titleBox.setAlignment(Pos.CENTER);

        Region spacerL = new Region();
        Region spacerR = new Region();
        HBox.setHgrow(spacerL, Priority.ALWAYS);
        HBox.setHgrow(spacerR, Priority.ALWAYS);

        HBox bar = new HBox(back, spacerL, titleBox, spacerR, new Region());
        bar.setPadding(new Insets(16, 24, 12, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    private ScrollPane buildScrollableCards() {
        VBox cardList = new VBox(20);
        cardList.setPadding(new Insets(28, 60, 28, 60));
        cardList.setAlignment(Pos.TOP_CENTER);

        // Two-column flow for the 7 mode cards
        // We use a TilePane-like arrangement via a FlowPane
        FlowPane flow = new FlowPane(24, 20);
        flow.setAlignment(Pos.CENTER);
        flow.setPadding(new Insets(10));

        for (ModeEntry me : MODES) {
            flow.getChildren().add(buildModeCard(me));
        }

        cardList.getChildren().add(flow);

        ScrollPane sp = new ScrollPane(cardList);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private Pane buildModeCard(ModeEntry me) {
        VBox card = new VBox(10);
        card.setPrefWidth(540);
        card.setPadding(new Insets(18));
        card.setStyle(UIColors.cardStyle(me.accentColor()));

        // ── Header: icon + mode name ──
        Label iconLabel = new Label(me.icon());
        iconLabel.setFont(Font.font("SansSerif", 28));
        iconLabel.setTextFill(me.accentColor());

        Label nameLabel = new Label(me.displayName());
        nameLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 20));
        nameLabel.setTextFill(UIColors.TEXT_PRIMARY);
        nameLabel.setWrapText(true);

        HBox header = new HBox(12, iconLabel, nameLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        // ── Kannada subtitle ──
        Label kannadaLabel = new Label(me.kannadaName());
        kannadaLabel.setFont(Font.font("SansSerif", 14));
        kannadaLabel.setTextFill(UIColors.TEXT_DIM);

        // ── Description ──
        Label descLabel = new Label(me.description());
        descLabel.setFont(Font.font("Monospace", 13));
        descLabel.setTextFill(UIColors.TEXT_DIM);
        descLabel.setWrapText(true);

        // ── Objective ──
        Label objHeader = new Label("OBJECTIVE:");
        objHeader.setFont(Font.font("Monospace", FontWeight.BOLD, 12));
        objHeader.setTextFill(me.accentColor());

        Label objLabel = new Label(me.objective());
        objLabel.setFont(Font.font("Monospace", 12));
        objLabel.setTextFill(UIColors.TEXT_PRIMARY);
        objLabel.setWrapText(true);

        // ── Action button ──
        Button selectBtn = new Button("SELECT MODE  ▶");
        selectBtn.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        selectBtn.setStyle(UIColors.buttonStyle(me.accentColor()));
        selectBtn.setOnMouseEntered(ev -> selectBtn.setStyle(UIColors.buttonHoverStyle(me.accentColor())));
        selectBtn.setOnMouseExited(ev ->  selectBtn.setStyle(UIColors.buttonStyle(me.accentColor())));
        selectBtn.setOnAction(ev -> {
            profile.setSelectedMode(me.mode().name());
            LocationType loc = LocationType.MG_ROAD;
            if (profile.getSelectedLocation() != null) {
                try {
                    loc = LocationType.valueOf(profile.getSelectedLocation());
                } catch (Exception ignored) {}
            }
            sceneManager.showGame(loc, me.mode());
        });

        HBox btnRow = new HBox(selectBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(header, kannadaLabel, descLabel, objHeader, objLabel, btnRow);

        // Card hover glow
        card.setOnMouseEntered(e -> card.setStyle(UIColors.cardHoverStyle(me.accentColor())));
        card.setOnMouseExited(e ->  card.setStyle(UIColors.cardStyle(me.accentColor())));

        return card;
    }

    private HBox buildBottomHint() {
        Label hint = new Label(
                "Select a mode to start racing immediately. The last-selected location will be used.");
        hint.setFont(Font.font("Monospace", 12));
        hint.setTextFill(UIColors.TEXT_DIM);
        HBox bar = new HBox(hint);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }
}
