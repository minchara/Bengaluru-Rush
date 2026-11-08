package com.nexusdrive.ui;

import javafx.scene.paint.Color;

/**
 * Central color palette and CSS style helpers for NEXUS DRIVE – BENGALURU RUSH.
 * All UI components reference these constants to maintain a consistent
 * futuristic dark-neon design language.
 */
public final class UIColors {

    private UIColors() {}

    // ── Core Backgrounds ───────────────────────────────────────────────────────
    public static final Color BG_DARK    = Color.web("#0A0A1A");
    public static final Color BG_PANEL   = Color.web("#111128");

    // ── Neon Accents ───────────────────────────────────────────────────────────
    public static final Color NEON_BLUE   = Color.web("#00CCFF");
    public static final Color NEON_PURPLE = Color.web("#CC00FF");
    public static final Color NEON_ORANGE = Color.web("#FF8800");
    public static final Color NEON_GREEN  = Color.web("#00FF88");
    public static final Color NEON_RED    = Color.web("#FF2244");

    // ── Text ───────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY = Color.web("#E8E8FF");
    public static final Color TEXT_DIM     = Color.web("#8888BB");
    public static final Color LOCKED_GRAY  = Color.web("#444466");

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Converts a JavaFX {@link Color} to a CSS-compatible hex string (#RRGGBB).
     */
    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed()   * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue()  * 255));
    }

    /**
     * Base button CSS – dark panel background, neon border, monospace bold text.
     */
    public static String buttonStyle(Color borderColor) {
        return "-fx-background-color: #111128; "
             + "-fx-border-color: "   + toHex(borderColor) + "; "
             + "-fx-border-width: 2; "
             + "-fx-text-fill: "      + toHex(TEXT_PRIMARY) + "; "
             + "-fx-font-family: Monospace; "
             + "-fx-font-weight: bold; "
             + "-fx-cursor: hand; "
             + "-fx-border-radius: 4; "
             + "-fx-background-radius: 4;";
    }

    /**
     * Hover button CSS – same as {@link #buttonStyle} plus a coloured glow drop-shadow.
     */
    public static String buttonHoverStyle(Color borderColor) {
        return buttonStyle(borderColor)
             + "-fx-effect: dropshadow(gaussian, " + toHex(borderColor) + ", 10, 0.5, 0, 0);";
    }

    /**
     * Convenience: a full inline card/panel style.
     */
    public static String cardStyle(Color borderColor) {
        return "-fx-background-color: #111128; "
             + "-fx-border-color: " + toHex(borderColor) + "; "
             + "-fx-border-width: 1.5; "
             + "-fx-border-radius: 6; "
             + "-fx-background-radius: 6; "
             + "-fx-padding: 12;";
    }

    public static String cardHoverStyle(Color borderColor) {
        return cardStyle(borderColor)
             + "-fx-effect: dropshadow(gaussian, " + toHex(borderColor) + ", 14, 0.4, 0, 0);";
    }
}
