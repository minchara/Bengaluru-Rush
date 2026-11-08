package com.nexusdrive.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * BackgroundRenderer draws parallax scrolling background elements – skylines,
 * buildings, landmarks, trees – for each Bengaluru location.
 *
 * <p>All drawing uses solid-colour rectangles, polygons, arcs and ovals on the
 * JavaFX GraphicsContext; no images are loaded.  Anti-aliasing is not disabled
 * at the GC level (JavaFX does not expose a simple flag), but we use integer
 * coordinates and block-shapes to maintain a pixel-art aesthetic.
 *
 * <p>Parallax: the background layer scrolls at a fraction of the road curve
 * accumulation.  Pass {@code bgScrollX} as the horizontal offset (can be
 * negative or positive).
 */
public class BackgroundRenderer {

    // -----------------------------------------------------------------------
    // Public entry point
    // -----------------------------------------------------------------------

    /**
     * Draw the background for the current frame.
     *
     * @param gc        target GraphicsContext
     * @param location  which Bengaluru location to render
     * @param weather   weather state (rain / clear)
     * @param bgScrollX parallax horizontal offset (world-curve accumulation × factor)
     * @param screenW   canvas width in pixels
     * @param screenH   canvas height in pixels
     */
    public void draw(GraphicsContext gc,
                     LocationType location,
                     WeatherSystem weather,
                     double bgScrollX,
                     double screenW,
                     double screenH) {

        // The background sits in the upper ~42 % of the screen (above the road).
        double horizon = screenH * 0.42;
        boolean isNight = location == LocationType.VIDHANA_SOUDHA;
        boolean isRaining = weather != null && weather.isRaining();

        switch (location) {
            case MG_ROAD         -> drawMGRoad(gc, bgScrollX, screenW, horizon, isRaining);
            case SILK_BOARD      -> drawSilkBoard(gc, bgScrollX, screenW, horizon, isRaining);
            case OUTER_RING_ROAD -> drawOuterRingRoad(gc, bgScrollX, screenW, horizon, isRaining);
            case VIDHANA_SOUDHA  -> drawVidhanaSoudha(gc, bgScrollX, screenW, horizon, isRaining);
            case NICE_ROAD       -> drawNiceRoad(gc, bgScrollX, screenW, horizon, isRaining);
            case LALBAGH         -> drawLalbagh(gc, bgScrollX, screenW, horizon, isRaining);
        }

        // Rain overlay: vertical streaks
        if (isRaining) {
            drawRainOverlay(gc, bgScrollX, screenW, horizon);
        }
    }

    // -----------------------------------------------------------------------
    // Location backgrounds
    // -----------------------------------------------------------------------

    /** MG Road: amber-lit commercial buildings, metro viaduct, neon signage. */
    private void drawMGRoad(GraphicsContext gc, double scrollX,
                             double screenW, double horizon, boolean rain) {

        double off = scrollOffset(scrollX, 0.25);

        // ---- Far-distance buildings (layer 1, slow parallax) ----
        double off1 = scrollOffset(scrollX, 0.15);
        Color far = Color.web("#1a1a28");
        int[] farWidths  = {55, 40, 70, 45, 60, 50, 65};
        int[] farHeights = {60, 80, 50, 90, 65, 75, 55};
        double fx = -60 + off1 % (screenW + 120);
        for (int i = 0; i < farWidths.length * 2; i++) {
            int idx = i % farWidths.length;
            double bw = farWidths[idx], bh = farHeights[idx];
            drawBuilding(gc, fx, horizon - bh, bw, bh, far, false);
            // Faint orange windows
            addWindowGrid(gc, fx, horizon - bh, bw, bh,
                           Color.web("#FF8800"), Color.web("#110a00"), 4, 5, rain);
            fx += bw + 8;
            if (fx > screenW + 60) fx -= (screenW + 180);
        }

        // ---- Mid-ground buildings (layer 2) ----
        Color mid = Color.web("#22222e");
        int[] mw = {48, 36, 60, 42, 50, 44};
        int[] mh = {90, 110, 75, 100, 85, 95};
        double mx = -40 + off % (screenW + 100);
        for (int i = 0; i < mw.length * 2; i++) {
            int idx = i % mw.length;
            double bw = mw[idx], bh = mh[idx];
            drawBuilding(gc, mx, horizon - bh, bw, bh, mid, false);
            addWindowGrid(gc, mx, horizon - bh, bw, bh,
                           Color.web("#FF9900"), Color.web("#440000"), 3, 6, rain);
            // Neon sign strip on some buildings
            if (i % 3 == 0) {
                gc.setFill(Color.web("#FF3366"));
                gc.fillRect(mx, horizon - bh - 6, bw, 5);
            }
            mx += bw + 12;
            if (mx > screenW + 60) mx -= (screenW + 220);
        }

        // ---- Metro viaduct ----
        double metroOff = scrollOffset(scrollX, 0.35);
        drawMetroStation(gc, screenW * 0.3 + metroOff * 0.5, horizon - 20, screenW);
    }

    /** Silk Board: dense flyover, packed gray buildings, hoardings. */
    private void drawSilkBoard(GraphicsContext gc, double scrollX,
                                double screenW, double horizon, boolean rain) {

        double off = scrollOffset(scrollX, 0.2);

        // Packed mid-rise gray buildings
        Color bldg = Color.web("#3a3a3a");
        int[] bw = {40, 55, 35, 50, 45, 60, 38};
        int[] bh = {70, 55, 80, 65, 75, 60, 85};
        double bx = off % (screenW + 50) - 50;
        for (int i = 0; i < bw.length * 3; i++) {
            int idx = i % bw.length;
            double w = bw[idx], h = bh[idx];
            drawBuilding(gc, bx, horizon - h, w, h, bldg, false);
            addWindowGrid(gc, bx, horizon - h, w, h,
                           Color.web("#CCCC88"), Color.web("#333322"), 3, 5, rain);
            bx += w + 4;
            if (bx > screenW + 50) bx -= (screenW + 300);
        }

        // Flyover pillars (thick gray columns)
        double pillarOff = scrollOffset(scrollX, 0.5);
        for (int p = 0; p < 8; p++) {
            double px = -20 + (p * 110 + pillarOff) % (screenW + 50);
            gc.setFill(Color.web("#555555"));
            gc.fillRect(px, horizon - 50, 18, 55);
            // Flyover deck
            gc.setFill(Color.web("#666666"));
            gc.fillRect(px - 20, horizon - 55, 60, 10);
        }

        // Hoardings (colorful billboards)
        double hoOff = scrollOffset(scrollX, 0.3);
        Color[] hoColors = {Color.web("#CC2244"), Color.web("#2244CC"),
                            Color.web("#22AA44"), Color.web("#CCAA00")};
        for (int h = 0; h < 4; h++) {
            double hx = -30 + (h * 180 + hoOff) % (screenW + 60) - 60;
            double hy = horizon - 95;
            gc.setFill(hoColors[h % hoColors.length]);
            gc.fillRect(hx, hy, 70, 28);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospace", FontWeight.BOLD, 8));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("HOSUR RD", hx + 35, hy + 16);
        }
    }

    /** Outer Ring Road: glass tech-park towers, manicured lawns, wide sky. */
    private void drawOuterRingRoad(GraphicsContext gc, double scrollX,
                                    double screenW, double horizon, boolean rain) {

        double off1 = scrollOffset(scrollX, 0.1);
        double off2 = scrollOffset(scrollX, 0.25);

        // ---- Far hills / horizon haze ----
        gc.setFill(Color.web("#7a9aaa44"));
        gc.fillRect(0, horizon - 20, screenW, 25);

        // ---- Far-layer sparse trees ----
        Color treeFar = Color.web("#224422");
        for (int t = 0; t < 12; t++) {
            double tx = (t * 95 + off1 * 0.5) % (screenW + 20) - 10;
            drawTree(gc, tx, horizon, 18, Color.web("#3a2a10"), treeFar);
        }

        // ---- Tech park towers ----
        double[] tpX   = {0.1, 0.3, 0.55, 0.72, 0.88};
        double[] tpW   = {50,  38,  60,   44,   52};
        double[] tpH   = {110, 90,  130,  100,  118};
        for (int i = 0; i < tpX.length; i++) {
            double tx = screenW * tpX[i] + off2 * (0.2 + i * 0.04);
            tx = wrapX(tx, screenW + 100, -100);
            drawTechParkTower(gc, tx, horizon - tpH[i], tpW[i], tpH[i]);
        }

        // ---- Manicured lawn strip ----
        gc.setFill(Color.web("#2a7a2a"));
        gc.fillRect(0, horizon - 10, screenW, 14);
    }

    /** Vidhana Soudha: night, golden-lit dome, government buildings, trees. */
    private void drawVidhanaSoudha(GraphicsContext gc, double scrollX,
                                    double screenW, double horizon, boolean rain) {

        double off = scrollOffset(scrollX, 0.2);

        // Dark silhouette government buildings (flanking the main dome)
        Color bldgColor = Color.web("#0a0a18");
        int[] bw = {60, 45, 70, 50, 65};
        int[] bh = {70, 85, 60, 90, 75};
        double bx = -30 + off % (screenW + 100) - 100;
        for (int i = 0; i < bw.length * 2; i++) {
            int idx = i % bw.length;
            drawBuilding(gc, bx, horizon - bh[idx], bw[idx], bh[idx], bldgColor, false);
            // Golden lit arched windows
            int rows = bh[idx] / 15, cols = bw[idx] / 12;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    gc.setFill(Color.web("#AA6600"));
                    gc.fillOval(bx + 4 + c * 12, horizon - bh[idx] + 5 + r * 15, 7, 9);
                }
            }
            bx += bw[idx] + 20;
            if (bx > screenW + 60) bx -= (screenW + 280);
        }

        // ---- Vidhana Soudha central silhouette ----
        double centerX = screenW * 0.5 + off * 0.1;
        double vsScale = 1.0;
        drawVidhanaSoudhaSilhouette(gc, centerX, horizon, vsScale);

        // ---- Street lamp glow ----
        double lampOff = scrollOffset(scrollX, 0.6);
        for (int l = 0; l < 10; l++) {
            double lx = (l * 110 + lampOff) % (screenW + 20) - 10;
            gc.setFill(Color.web("#AA8800AA"));
            gc.fillOval(lx - 12, horizon - 55, 24, 24);
            gc.setFill(Color.web("#FFEE44"));
            gc.fillOval(lx - 3, horizon - 48, 6, 6);
            gc.setFill(Color.web("#888888"));
            gc.fillRect(lx - 1.5, horizon - 44, 3, 44);
        }
    }

    /** NICE Road: open highway, sparse dark-green hills, toll booth. */
    private void drawNiceRoad(GraphicsContext gc, double scrollX,
                               double screenW, double horizon, boolean rain) {

        double off = scrollOffset(scrollX, 0.08);

        // ---- Distant rolling hills ----
        gc.setFill(Color.web("#1a4a1a"));
        double[] hillX = new double[8];
        double[] hillY = new double[8];
        double hBase = horizon + 5;
        for (int i = 0; i < 4; i++) {
            double hx = screenW * i * 0.33 + off * 0.1;
            double hw = 220 + i * 30;
            double hh = 55 + i * 10;
            // Draw as rough trapezoid
            gc.fillOval(hx - hw / 2, hBase - hh, hw, hh * 2);
        }

        // ---- Highway overpass silhouette ----
        double ovOff = scrollOffset(scrollX, 0.3);
        double ovX   = (screenW * 0.4 + ovOff) % (screenW * 1.5) - screenW * 0.1;
        gc.setFill(Color.web("#444444"));
        gc.fillRect(ovX, horizon - 40, screenW * 0.3, 12);  // deck
        gc.fillRect(ovX + screenW * 0.08, horizon - 40, 10, 42); // left pier
        gc.fillRect(ovX + screenW * 0.22, horizon - 40, 10, 42); // right pier

        // ---- Toll booth ----
        double tollOff = scrollOffset(scrollX, 0.5);
        double tollX   = (screenW * 0.6 + tollOff * 0.3) % (screenW + 100) - 50;
        gc.setFill(Color.web("#dddddd"));
        gc.fillRect(tollX, horizon - 55, 30, 58);
        gc.setFill(Color.web("#2255aa"));
        gc.fillRect(tollX + 2, horizon - 52, 26, 10);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 7));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("TOLL", tollX + 15, horizon - 25);

        // ---- Sparse trees ----
        Color trunk = Color.web("#4a3010");
        Color leaf  = Color.web("#1a5a1a");
        for (int t = 0; t < 6; t++) {
            double tx = (t * 190 + off * 0.4) % (screenW + 40) - 20;
            drawTree(gc, tx, horizon, 24, trunk, leaf);
            drawTree(gc, tx + screenW * 0.9, horizon, 20, trunk, leaf);
        }
    }

    /** Lalbagh: lush tree canopy, flower patches, Glass House dome. */
    private void drawLalbagh(GraphicsContext gc, double scrollX,
                              double screenW, double horizon, boolean rain) {

        double off = scrollOffset(scrollX, 0.18);

        // ---- Dense tree canopy (background layer) ----
        Color darkGreen = Color.web("#0d3a10");
        Color midGreen  = Color.web("#1a5a1a");
        Color liteGreen = Color.web("#22882a");
        for (int t = 0; t < 20; t++) {
            double tx = (t * 65 + off * 0.6) % (screenW + 50) - 25;
            double th = 60 + (t % 4) * 15;
            // Large canopy oval
            gc.setFill(t % 3 == 0 ? darkGreen : (t % 3 == 1 ? midGreen : liteGreen));
            gc.fillOval(tx - 25, horizon - th, 55, th + 5);
            // Trunk
            gc.setFill(Color.web("#3a2008"));
            gc.fillRect(tx - 3, horizon - 15, 7, 18);
        }

        // ---- Flower beds ----
        Color[] flowerColors = {
            Color.web("#FF4488"), Color.web("#FFAA00"),
            Color.web("#FF2200"), Color.web("#FFFFFF"),
            Color.web("#AA00FF")
        };
        double flOff = scrollOffset(scrollX, 0.9);
        for (int f = 0; f < 12; f++) {
            double fx = (f * 80 + flOff) % (screenW + 20);
            Color fc = flowerColors[f % flowerColors.length];
            gc.setFill(fc);
            gc.fillOval(fx, horizon - 8, 18, 8);
        }

        // ---- Glass House dome ----
        double ghX = screenW * 0.5 + off * 0.1;
        drawGlassHouseDome(gc, ghX, horizon);

        // ---- Rock / boulders ----
        gc.setFill(Color.web("#888870"));
        for (int r = 0; r < 5; r++) {
            double rx = (r * 200 + off * 0.4) % (screenW + 20);
            gc.fillOval(rx, horizon - 12, 22, 12);
        }
    }

    // -----------------------------------------------------------------------
    // Drawing helpers
    // -----------------------------------------------------------------------

    /**
     * Draw a solid rectangular building with optional window grid.
     */
    private void drawBuilding(GraphicsContext gc,
                               double x, double y, double w, double h,
                               Color color, boolean hasWindows) {
        gc.setFill(color);
        gc.fillRect(x, y, w, h);
        // Outline
        gc.setStroke(color.darker());
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
    }

    /**
     * Add a grid of lit/unlit windows to a building rectangle.
     *
     * @param litColor   colour of lit windows
     * @param darkColor  colour of dark windows
     * @param cols       number of window columns
     * @param rows       number of window rows
     * @param rain       if true, most windows appear dark
     */
    private void addWindowGrid(GraphicsContext gc,
                                double bx, double by, double bw, double bh,
                                Color litColor, Color darkColor,
                                int cols, int rows, boolean rain) {
        double ww = (bw - (cols + 1) * 3.0) / cols;
        double wh = (bh - (rows + 1) * 4.0) / rows;
        if (ww < 2 || wh < 2) return;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double wx = bx + 3 + c * (ww + 3);
                double wy = by + 4 + r * (wh + 4);
                // Simple pseudo-random window state
                boolean lit = !rain && ((r * cols + c * 3 + (int) bx) % 3 != 0);
                gc.setFill(lit ? litColor : darkColor);
                gc.fillRect(wx, wy, ww, wh);
            }
        }
    }

    /**
     * Draw a simple pixel-art tree.
     */
    private void drawTree(GraphicsContext gc, double x, double y,
                          double size, Color trunkColor, Color leafColor) {
        // Trunk
        gc.setFill(trunkColor);
        gc.fillRect(x - size * 0.1, y - size * 0.4, size * 0.2, size * 0.4);
        // Canopy (two overlapping ovals)
        gc.setFill(leafColor);
        gc.fillOval(x - size * 0.5, y - size * 1.2, size, size * 0.8);
        gc.fillOval(x - size * 0.4, y - size * 1.5, size * 0.8, size * 0.7);
    }

    /**
     * Draw a simplified metro station arch / viaduct spanning the width.
     */
    private void drawMetroStation(GraphicsContext gc, double x, double y, double screenW) {
        gc.setFill(Color.web("#7733CC"));
        // Viaduct deck spanning screen
        gc.fillRect(0, y - 10, screenW, 8);
        // Station box
        gc.setFill(Color.web("#9955EE"));
        gc.fillRect(x, y - 40, 90, 32);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 8));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("NAMMA METRO", x + 45, y - 24);
        gc.fillText("ನಮ್ಮ ಮೆಟ್ರೋ", x + 45, y - 12);
        // Pillars
        gc.setFill(Color.web("#555566"));
        for (double px = x - 150; px < screenW + 50; px += 180) {
            gc.fillRect(px, y - 10, 12, 50);
        }
    }

    /**
     * Draw a glass tech-park tower (blue-green tinted, reflective look).
     */
    private void drawTechParkTower(GraphicsContext gc, double x, double y,
                                    double w, double h) {
        // Main body
        gc.setFill(Color.web("#1a2a3a"));
        gc.fillRect(x, y, w, h);

        // Glass panels (vertical bands of lighter blue-green)
        int panels = Math.max(2, (int)(w / 10));
        double pw = w / panels;
        for (int p = 0; p < panels; p++) {
            gc.setFill(p % 2 == 0 ? Color.web("#1e4a6a") : Color.web("#153a55"));
            gc.fillRect(x + p * pw, y, pw - 1, h);
        }

        // Horizontal floor lines
        gc.setStroke(Color.web("#224466"));
        gc.setLineWidth(1);
        for (double fy = y + 8; fy < y + h; fy += 8) {
            gc.strokeLine(x, fy, x + w, fy);
        }

        // Rooftop antenna
        gc.setFill(Color.web("#888888"));
        gc.fillRect(x + w / 2 - 1, y - 10, 2, 10);
        gc.setFill(Color.web("#FF2200"));
        gc.fillOval(x + w / 2 - 2, y - 13, 4, 4);
    }

    /**
     * Draw the iconic Vidhana Soudha silhouette:
     * central dome flanked by two smaller towers, Dravidian style.
     */
    private void drawVidhanaSoudhaSilhouette(GraphicsContext gc,
                                              double centerX, double horizon,
                                              double scale) {
        Color body = Color.web("#0d0d1a");
        Color glow = Color.web("#CC8800");

        double baseY = horizon;
        double baseH = 90 * scale;
        double baseW = 200 * scale;

        // ---- Main building body ----
        gc.setFill(body);
        gc.fillRect(centerX - baseW / 2, baseY - baseH, baseW, baseH);

        // ---- Columns (vertical strips) ----
        gc.setFill(Color.web("#111122"));
        for (int c = 0; c < 8; c++) {
            double cx = centerX - baseW / 2 + 10 + c * (baseW - 20) / 7;
            gc.fillRect(cx, baseY - baseH, 6 * scale, baseH);
        }

        // ---- Arched windows (ambient gold glow) ----
        gc.setFill(glow);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                double wx = centerX - 60 * scale + col * 25 * scale;
                double wy = baseY - baseH + 15 + row * 22 * scale;
                // Arch: filled rect + oval top
                gc.fillRect(wx, wy + 5 * scale, 10 * scale, 12 * scale);
                gc.fillOval(wx, wy, 10 * scale, 10 * scale);
            }
        }

        // ---- Left tower ----
        double towerW  = 36 * scale;
        double towerH  = 70 * scale;
        double ltX = centerX - baseW / 2 + 10;
        gc.setFill(body);
        gc.fillRect(ltX, baseY - baseH - towerH, towerW, towerH);
        // Tower dome top
        gc.setFill(Color.web("#0d0d20"));
        gc.fillOval(ltX, baseY - baseH - towerH - 15, towerW, 22);
        gc.setFill(glow);
        gc.fillOval(ltX + towerW * 0.25, baseY - baseH - towerH - 10, towerW * 0.5, 14);

        // ---- Right tower ----
        double rtX = centerX + baseW / 2 - 10 - towerW;
        gc.setFill(body);
        gc.fillRect(rtX, baseY - baseH - towerH, towerW, towerH);
        gc.setFill(Color.web("#0d0d20"));
        gc.fillOval(rtX, baseY - baseH - towerH - 15, towerW, 22);
        gc.setFill(glow);
        gc.fillOval(rtX + towerW * 0.25, baseY - baseH - towerH - 10, towerW * 0.5, 14);

        // ---- Central main dome ----
        double domeW = 80 * scale;
        double domeH = 60 * scale;
        double domeX = centerX - domeW / 2;
        double domeY = baseY - baseH - domeH;
        gc.setFill(body);
        gc.fillRect(domeX, domeY + domeH * 0.4, domeW, domeH * 0.6); // drum
        // Dome arc
        gc.setFill(Color.web("#0d0d20"));
        gc.fillOval(domeX, domeY, domeW, domeH * 0.9);
        // Golden highlight
        gc.setFill(glow);
        gc.fillOval(domeX + domeW * 0.2, domeY + domeH * 0.1,
                     domeW * 0.6, domeH * 0.5);

        // ---- Spire ----
        double spireX = centerX;
        gc.setFill(glow);
        gc.fillRect(spireX - 2, domeY - 20, 4, 20);
        gc.fillOval(spireX - 4, domeY - 24, 8, 8);

        // ---- Ground-level illumination glow ----
        gc.setFill(Color.web("#AA660033"));
        gc.fillRect(centerX - baseW * 0.6, baseY - 8, baseW * 1.2, 10);
    }

    /**
     * Draw the Lalbagh Glass House outline – a simple greenhouse dome.
     */
    private void drawGlassHouseDome(GraphicsContext gc, double centerX, double horizon) {
        double domeW = 100;
        double domeH = 50;
        double domeX = centerX - domeW / 2;
        double domeY = horizon - domeH;

        // Base walls
        gc.setFill(Color.web("#888870"));
        gc.fillRect(domeX, horizon - 20, domeW, 20);

        // Glass panels (pale cyan tint)
        gc.setFill(Color.web("#aae8cc44"));
        gc.fillOval(domeX, domeY, domeW, domeH * 1.2);

        // Structural ribs
        gc.setStroke(Color.web("#666650"));
        gc.setLineWidth(1.5);
        for (int r = 0; r < 6; r++) {
            double rx = domeX + r * (domeW / 5.0);
            gc.strokeLine(rx, horizon - 20, centerX, domeY + 10);
        }
        // Horizontal ring
        gc.strokeLine(domeX + 10, horizon - 30, domeX + domeW - 10, horizon - 30);

        gc.setFill(Color.web("#555544"));
        gc.setFont(Font.font("Monospace", FontWeight.NORMAL, 8));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GLASS HOUSE", centerX, horizon - 55);
    }

    // -----------------------------------------------------------------------
    // Rain overlay
    // -----------------------------------------------------------------------

    private void drawRainOverlay(GraphicsContext gc, double scrollX,
                                  double screenW, double horizon) {
        gc.setStroke(Color.web("#88AACCAA"));
        gc.setLineWidth(1);
        long seed = (long)(scrollX * 7 + 12345);
        for (int i = 0; i < 60; i++) {
            seed = seed * 1664525L + 1013904223L;
            double rx = Math.abs(seed % (long) screenW);
            seed = seed * 1664525L + 1013904223L;
            double ry = Math.abs(seed % (long) horizon);
            double len = 8 + (seed & 7);
            gc.strokeLine(rx, ry, rx - 2, ry + len);
        }
    }

    // -----------------------------------------------------------------------
    // Scroll offset helper
    // -----------------------------------------------------------------------

    /**
     * Convert a world-space curve accumulation value to a screen-space
     * parallax offset.
     *
     * @param scrollX raw curve accumulation from the road renderer
     * @param factor  parallax factor (0 = static, 1 = same as road)
     */
    private double scrollOffset(double scrollX, double factor) {
        return scrollX * factor;
    }

    /**
     * Wrap an X coordinate so it stays within [min, max).
     */
    private double wrapX(double x, double max, double min) {
        double range = max - min;
        while (x > max) x -= range;
        while (x < min) x += range;
        return x;
    }
}
