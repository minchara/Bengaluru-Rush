package com.nexusdrive.ui;

import com.nexusdrive.data.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Achievements screen for NEXUS DRIVE – BENGALURU RUSH.
 *
 * <p>Displays all 10 in-game achievements with their unlock status, icons,
 * description and a progress hint. Unlocked achievements glow in neon blue;
 * locked ones are shown dimmed with a lock icon.</p>
 */
public class AchievementsScreen extends BorderPane {

    // ── Achievement data ───────────────────────────────────────────────────────
    private record AchievementEntry(
            String id,
            String icon,
            String title,
            String kannadaTitle,
            String description,
            String progressHint
    ) {}

    private static final AchievementEntry[] ACHIEVEMENTS = {
        new AchievementEntry("first_race",      "🏁",
            "First Race",          "ಮೊದಲ ರೇಸ್",
            "Complete your first race.",
            "Finish any race without quitting."),
        new AchievementEntry("speed_demon",     "⚡",
            "Speed Demon",         "ವೇಗದ ರಾಕ್ಷಸ",
            "Reach 200 KM/H.",
            "Hit top speed with a fully upgraded car or nitro boost."),
        new AchievementEntry("near_miss_master","✦",
            "Near Miss Master",    "ಮಿಸ್ ಮಾಸ್ಟರ್",
            "Accumulate 50 near misses total.",
            "Skirt past traffic without crashing – 50 times across all races."),
        new AchievementEntry("silk_board",      "⚠",
            "Silk Board Survivor", "ಸಿಲ್ಕ್ ಬೋರ್ಡ್ ಸರ್ವೈವರ್",
            "Survive 5 minutes at Silk Board.",
            "Stay alive for a full 5 minutes in Silk Board Challenge mode."),
        new AchievementEntry("monsoon_racer",   "🌧",
            "Monsoon Racer",       "ಮಳೆಗಾಲ ರೇಸರ್",
            "Complete a Monsoon Survival race.",
            "Finish 3 KM in Monsoon Survival mode without crashing."),
        new AchievementEntry("combo_king",      "★",
            "Combo King",          "ಕಾಂಬೋ ಕಿಂಗ್",
            "Reach a x10 near-miss combo.",
            "Chain 10 consecutive near misses without hitting anything."),
        new AchievementEntry("high_roller",     "🪙",
            "High Roller",         "ಹೈ ರೋಲರ್",
            "Collect 10,000 coins in total.",
            "Accumulate 10,000 coins across all sessions."),
        new AchievementEntry("rewind_master",   "◀",
            "Rewind Master",       "ರಿವೈಂಡ್ ಮಾಸ್ಟರ್",
            "Use the Rewind ability 100 times.",
            "Press R to rewind – 100 times total."),
        new AchievementEntry("night_rider",     "🌙",
            "Night Rider",         "ನೈಟ್ ರೈಡರ್",
            "Race 10 minutes at Vidhana Soudha.",
            "Accumulate 10 minutes of play-time on the Vidhana Soudha track."),
        new AchievementEntry("distance_champ",  "🛣",
            "Distance Champion",   "ಅಂತರ ಚಾಂಪಿಯನ್",
            "Drive 100 KM in total.",
            "Accumulate 100 KM of driven distance across all sessions."),
    };

    private final SceneManager  sceneManager;
    private final PlayerProfile profile;

    public AchievementsScreen(SceneManager sceneManager, PlayerProfile profile) {
        this.sceneManager = sceneManager;
        this.profile = profile;
        buildUI();
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    private void buildUI() {
        setBackground(new Background(new BackgroundFill(UIColors.BG_DARK, null, null)));
        setMinSize(1280, 720);

        setTop(buildTopBar());
        setCenter(buildAchievementList());
        setBottom(buildBottomBar());
    }

    private HBox buildTopBar() {
        Button back = new Button("◀  BACK");
        back.setFont(Font.font("Monospace", FontWeight.BOLD, 15));
        back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM));
        back.setOnMouseEntered(e -> back.setStyle(UIColors.buttonHoverStyle(UIColors.TEXT_DIM)));
        back.setOnMouseExited(e ->  back.setStyle(UIColors.buttonStyle(UIColors.TEXT_DIM)));
        back.setOnAction(e -> sceneManager.showMainMenu());

        Label title = new Label("★  ACHIEVEMENTS");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 36));
        title.setTextFill(UIColors.NEON_BLUE);
        title.setEffect(new DropShadow(16, UIColors.NEON_BLUE));

        Label kannadaTitle = new Label("ಸಾಧನೆಗಳು");
        kannadaTitle.setFont(Font.font("SansSerif", 15));
        kannadaTitle.setTextFill(UIColors.TEXT_DIM);

        VBox titleBox = new VBox(2, title, kannadaTitle);
        titleBox.setAlignment(Pos.CENTER);

        // Progress counter
        long unlocked = java.util.Arrays.stream(ACHIEVEMENTS)
                .filter(a -> profile.hasAchievement(a.id())).count();
        Label progress = new Label(unlocked + " / " + ACHIEVEMENTS.length + "  UNLOCKED");
        progress.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        progress.setTextFill(UIColors.NEON_GREEN);

        Region sL = new Region(); HBox.setHgrow(sL, Priority.ALWAYS);
        Region sR = new Region(); HBox.setHgrow(sR, Priority.ALWAYS);

        HBox bar = new HBox(back, sL, titleBox, sR, progress);
        bar.setPadding(new Insets(16, 24, 12, 24));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }

    private ScrollPane buildAchievementList() {
        // Two-column grid
        int COLS = 2;
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);
        grid.setPadding(new Insets(28, 48, 28, 48));
        grid.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < ACHIEVEMENTS.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            grid.add(buildAchievementCard(ACHIEVEMENTS[i]), col, row);
        }

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0A0A1A; -fx-background-color: #0A0A1A;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private Pane buildAchievementCard(AchievementEntry ae) {
        boolean unlocked = profile.hasAchievement(ae.id());
        Color borderColor = unlocked ? UIColors.NEON_BLUE : UIColors.LOCKED_GRAY;

        HBox card = new HBox(16);
        card.setPrefWidth(560);
        card.setPadding(new Insets(14));
        card.setStyle(UIColors.cardStyle(borderColor));
        card.setAlignment(Pos.CENTER_LEFT);

        // ── Icon badge ──
        Canvas badge = new Canvas(54, 54);
        GraphicsContext gc = badge.getGraphicsContext2D();
        gc.setFill(unlocked ? Color.web("#00CCFF", 0.15) : Color.web("#444466", 0.2));
        gc.fillOval(0, 0, 54, 54);
        gc.setFont(Font.font("SansSerif", 24));
        gc.setFill(unlocked ? Color.web("#00CCFF") : Color.web("#555577"));
        gc.fillText(unlocked ? ae.icon() : "🔒", 12, 36);

        // ── Text content ──
        Label titleLbl = new Label(ae.title());
        titleLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 17));
        titleLbl.setTextFill(unlocked ? UIColors.TEXT_PRIMARY : UIColors.LOCKED_GRAY);
        if (unlocked) titleLbl.setEffect(new DropShadow(8, UIColors.NEON_BLUE));

        Label kannadaLbl = new Label(ae.kannadaTitle());
        kannadaLbl.setFont(Font.font("SansSerif", 13));
        kannadaLbl.setTextFill(UIColors.TEXT_DIM);

        Label descLbl = new Label(ae.description());
        descLbl.setFont(Font.font("Monospace", 12));
        descLbl.setTextFill(unlocked ? UIColors.TEXT_DIM : UIColors.LOCKED_GRAY.darker());
        descLbl.setWrapText(true);

        Label hintLbl = new Label(ae.progressHint());
        hintLbl.setFont(Font.font("Monospace", 11));
        hintLbl.setTextFill(Color.web("#555577"));
        hintLbl.setWrapText(true);

        Label statusLbl = new Label(unlocked ? "✓  UNLOCKED" : "LOCKED");
        statusLbl.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        statusLbl.setTextFill(unlocked ? UIColors.NEON_GREEN : UIColors.LOCKED_GRAY);

        VBox textBox = new VBox(3, titleLbl, kannadaLbl, descLbl, hintLbl, statusLbl);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(badge, textBox);

        // Hover glow for unlocked
        if (unlocked) {
            card.setOnMouseEntered(e -> card.setStyle(UIColors.cardHoverStyle(UIColors.NEON_BLUE)));
            card.setOnMouseExited(e ->  card.setStyle(UIColors.cardStyle(UIColors.NEON_BLUE)));
        }

        return card;
    }

    private HBox buildBottomBar() {
        long unlocked = java.util.Arrays.stream(ACHIEVEMENTS)
                .filter(a -> profile.hasAchievement(a.id())).count();
        double pct = ACHIEVEMENTS.length > 0 ? (unlocked * 100.0 / ACHIEVEMENTS.length) : 0;

        Label info = new Label(String.format(
                "Total progress: %d / %d achievements unlocked  (%.0f%%)",
                unlocked, ACHIEVEMENTS.length, pct));
        info.setFont(Font.font("Monospace", 12));
        info.setTextFill(UIColors.TEXT_DIM);

        HBox bar = new HBox(info);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #0A0A2A;");
        return bar;
    }
}
