package com.nexusdrive.world;

import com.nexusdrive.entity.AITraffic;
import com.nexusdrive.entity.PixelSprites;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoadRenderer – pseudo-3D Outrun-style road renderer using JavaFX Canvas.
 *
 * <p>Coordinate convention:
 * <ul>
 *   <li>World X : lateral position (0 = centre, negative = left)
 *   <li>World Y : vertical / hill height
 *   <li>World Z : depth along the road (increases as player drives forward)
 * </ul>
 *
 * <p>Algorithm summary (per frame):
 * <ol>
 *   <li>Draw sky / background gradient.
 *   <li>Walk DRAW_DISTANCE segments from the player forward, projecting each
 *       3-D position to screen space.
 *   <li>Render each trapezoid from farthest → nearest (painter's algorithm),
 *       clipping to the bottom of whatever has already been drawn.
 *   <li>Overlay traffic sprites at their projected positions.
 * </ol>
 */
public class RoadRenderer {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Length of a single road segment in world units. */
    public static final double SEGMENT_LENGTH = 200.0;

    /** Camera height above the road surface (world units). */
    private static final double CAMERA_HEIGHT = 1500.0;

    /**
     * Depth of the camera's projection plane.
     * fieldOfView = 2 * atan(1 / CAMERA_DEPTH)  ≈ 100 ° when 0.84.
     */
    private static final double CAMERA_DEPTH = 0.84;

    /** Number of segments drawn ahead of the player. */
    private static final int DRAW_DISTANCE = 200;

    /** Half-width of the road in world units (full road = 2 × ROAD_WIDTH). */
    private static final double ROAD_WIDTH = 2000.0;

    /** Minimum perspective scale before a segment is considered invisible. */
    private static final double MIN_SCALE = 0.0001;

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    private double screenW;
    private double screenH;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public RoadRenderer(double screenW, double screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    public void setScreenSize(double w, double h) {
        this.screenW = w;
        this.screenH = h;
    }

    /**
     * Main render entry point. Called every frame.
     *
     * @param gc          target GraphicsContext
     * @param playerZ     player's absolute Z position along the track (world units)
     * @param playerX     player's lateral offset (−1 = far left, 0 = centre, +1 = far right),
     *                    normalised to road half-width
     * @param map         the loaded road map
     * @param trafficList AI traffic entities (any order – we sort during render)
     * @param weather     current weather state
     * @param location    current map location (for theming)
     */
    public void render(GraphicsContext gc,
                       double playerZ,
                       double playerX,
                       BengaluruMap map,
                       List<AITraffic> trafficList,
                       WeatherSystem weather,
                       LocationType location) {

        // --- 1. Sky / background ---
        drawSky(gc, location, weather);

        // --- 2. Project & draw road segments ---

        // Which segment contains the player
        int startSegment = (int) (playerZ / SEGMENT_LENGTH);

        // Lateral accumulator for curves (how far the road has drifted in X)
        double x  = 0.0;
        double dx = 0.0;

        // Vertical accumulator for hills (screen Y of the horizon shifts)
        double y  = 0.0;
        double dy = 0.0;

        // maxY = bottom clip line. We draw road trapezoids only while
        // screenY is ABOVE (< ) this value. Starts at screen bottom.
        double maxY = screenH;

        // Store projections keyed by segment index so traffic can query them.
        Map<Integer, double[]> projMap = new HashMap<>();

        // We collect {x1,y1,w1, x2,y2,w2, colorIndex, hasSignal, isMetro, landmark}
        // tuples and render them back-to-front by drawing from far to near,
        // which is naturally the order n=DRAW_DISTANCE-1 .. 0.
        // We first project ALL segments, then render front-to-back tracking maxY.

        double[] projX     = new double[DRAW_DISTANCE];
        double[] projY     = new double[DRAW_DISTANCE];
        double[] projW     = new double[DRAW_DISTANCE];
        double[] projScale = new double[DRAW_DISTANCE];
        int[]    segIdxArr = new int[DRAW_DISTANCE];

        double curX  = 0.0;
        double curDX = 0.0;
        double curY  = 0.0;

        for (int n = 0; n < DRAW_DISTANCE; n++) {
            int segIndex = (startSegment + n) % map.getSegmentCount();
            segIdxArr[n] = segIndex;
            RoadSegment seg = map.getSegment(segIndex);

            // Perspective scale: closer segments (n small) → large scale
            // Guard against n=0 (which would be z=0, behind camera)
            double segZ = (n + 1) * SEGMENT_LENGTH; // distance from camera
            double scale = CAMERA_DEPTH / segZ;

            // Screen coordinates of this segment's centre point
            double camX = -playerX * ROAD_WIDTH; // camera lateral pos in world
            double worldX = camX + curX;         // road centre X at this segment

            double sx = screenW / 2.0 + scale * worldX * screenW / 2.0;
            double sy = screenH / 2.0 - scale * (CAMERA_HEIGHT + curY);
            double sw = scale * ROAD_WIDTH * screenW / 2.0;

            projX[n]     = sx;
            projY[n]     = sy;
            projW[n]     = sw;
            projScale[n] = scale;

            // Store for traffic rendering (use segment index → screen coords)
            projMap.put(segIndex, new double[]{sx, sy, sw, scale});

            // Accumulate curve & hill
            curDX += seg.curve * 0.3;
            curX  += curDX;
            curY  += seg.hill * 50.0;
        }

        // Now render from FAR (n = DRAW_DISTANCE-1) to NEAR (n = 0)
        // We track maxY to implement the "clip" that hides segments behind hills.
        maxY = screenH;

        for (int n = DRAW_DISTANCE - 1; n >= 1; n--) {
            int segIndex = segIdxArr[n];
            RoadSegment seg = map.getSegment(segIndex);

            double x2 = projX[n],     y2 = projY[n],     w2 = projW[n];
            double x1 = projX[n - 1], y1 = projY[n - 1], w1 = projW[n - 1];

            // Near edge (n-1) is farther down the screen (larger y).
            // Only draw if the near edge is above the current clip line.
            if (y1 >= maxY) continue; // segment is hidden below previously drawn segs

            // Extend grass strips fully to screen edges before road trapezoid
            drawGrassStrip(gc, y2, y1, seg.colorIndex, location);

            drawSegment(gc,
                        x1, y1, w1,
                        x2, y2, w2,
                        seg.colorIndex, location,
                        seg.hasSignal, seg.isMetroSection,
                        weather);

            if (seg.landmark != null && y1 < maxY) {
                double midX = (x1 + x2) / 2.0;
                double midY = (y1 + y2) / 2.0 - 20;
                drawLandmarkLabel(gc, seg.landmark, midX, midY);
            }

            maxY = Math.min(maxY, y1);
        }

        // --- 3. Draw traffic sprites ---
        for (AITraffic vehicle : trafficList) {
            double[] proj = projMap.get(vehicle.getSegmentIndex() % map.getSegmentCount());
            if (proj == null) continue;
            double sx    = proj[0];
            double sy    = proj[1];
            double sw    = proj[2];
            double scale = proj[3];
            // Lateral offset within road (-1..1)
            double offsetX = sx + vehicle.getLateralOffset() * sw;
            drawTrafficAt(gc, vehicle, offsetX, sy, scale * 25.0);
        }
    }

    // -----------------------------------------------------------------------
    // Projection helper
    // -----------------------------------------------------------------------

    /**
     * Project a world-space point to screen space.
     *
     * @return double[3] = { screenX, screenY, screenRoadHalfWidth }
     */
    private double[] project(double worldX, double worldY, double worldZ,
                              double cameraX, double cameraY, double cameraZ) {
        if (worldZ - cameraZ <= 0) return new double[]{screenW / 2, screenH / 2, 0};
        double scale   = CAMERA_DEPTH / (worldZ - cameraZ);
        double screenX = (1.0 + scale * (worldX - cameraX)) * screenW / 2.0;
        double sy      = (1.0 - scale * (worldY - cameraY)) * screenH / 2.0;
        double sw      = scale * ROAD_WIDTH * screenW / 2.0;
        return new double[]{screenX, sy, sw};
    }

    // -----------------------------------------------------------------------
    // Sky / Background
    // -----------------------------------------------------------------------

    private void drawSky(GraphicsContext gc, LocationType location, WeatherSystem weather) {
        boolean isRaining = weather != null && weather.isRaining();

        // Choose sky gradient colours per location
        Color skyTop, skyBottom;

        if (isRaining) {
            skyTop    = Color.web("#2a2a35");
            skyBottom = Color.web("#4a4a55");
        } else {
            switch (location) {
                case MG_ROAD -> {
                    skyTop    = Color.web("#0d1b4a");
                    skyBottom = Color.web("#1a3a7a");
                }
                case VIDHANA_SOUDHA -> {
                    skyTop    = Color.web("#04050f");
                    skyBottom = Color.web("#0a0e2a");
                }
                case LALBAGH -> {
                    skyTop    = Color.web("#1a7abf");
                    skyBottom = Color.web("#5ab8f5");
                }
                case OUTER_RING_ROAD -> {
                    skyTop    = Color.web("#4a6a8a");
                    skyBottom = Color.web("#b0c8d8");
                }
                case NICE_ROAD -> {
                    skyTop    = Color.web("#1e6abf");
                    skyBottom = Color.web("#78c8f0");
                }
                case SILK_BOARD -> {
                    skyTop    = Color.web("#666677");
                    skyBottom = Color.web("#aaaaaa");
                }
                default -> {
                    skyTop    = Color.web("#1a3a7a");
                    skyBottom = Color.web("#4a7abf");
                }
            }
        }

        // Draw banded gradient manually (pixel-art style: 8-pixel bands)
        double skyHeight = screenH * 0.42;
        int bands = (int)(skyHeight / 8);
        for (int b = 0; b <= bands; b++) {
            double t = (double) b / (double) Math.max(1, bands);
            Color c = skyTop.interpolate(skyBottom, t);
            gc.setFill(c);
            gc.fillRect(0, b * 8, screenW, 9);
        }

        // Draw some stars for night locations
        if (location == LocationType.VIDHANA_SOUDHA && !isRaining) {
            gc.setFill(Color.WHITE);
            long seed = 42L;
            for (int s = 0; s < 80; s++) {
                seed = seed * 1664525L + 1013904223L;
                double sx = Math.abs(seed % (long) screenW);
                seed = seed * 1664525L + 1013904223L;
                double sy = Math.abs(seed % (long)(screenH * 0.35));
                gc.fillRect(sx, sy, 1, 1);
            }
        }

        // Moon for night
        if (location == LocationType.VIDHANA_SOUDHA && !isRaining) {
            gc.setFill(Color.web("#fffde0"));
            gc.fillOval(screenW * 0.78, screenH * 0.04, 24, 24);
        }

        // Sun for daytime locations
        if (location == LocationType.LALBAGH || location == LocationType.NICE_ROAD) {
            gc.setFill(Color.web("#ffe680"));
            gc.fillOval(screenW * 0.82, screenH * 0.03, 32, 32);
            // Simple sun rays (4-pixel lines)
            gc.setStroke(Color.web("#ffe680"));
            gc.setLineWidth(2);
            double cx = screenW * 0.82 + 16;
            double cy = screenH * 0.03 + 16;
            for (int a = 0; a < 360; a += 45) {
                double rad = Math.toRadians(a);
                gc.strokeLine(cx + 20 * Math.cos(rad), cy + 20 * Math.sin(rad),
                              cx + 30 * Math.cos(rad), cy + 30 * Math.sin(rad));
            }
        }
    }

    // -----------------------------------------------------------------------
    // Grass Strip
    // -----------------------------------------------------------------------

    private void drawGrassStrip(GraphicsContext gc, double y2, double y1,
                                int colorIndex, LocationType location) {
        Color grassA, grassB;
        switch (location) {
            case LALBAGH -> {
                grassA = Color.web("#1a7a28");
                grassB = Color.web("#22952e");
            }
            case NICE_ROAD -> {
                grassA = Color.web("#3a7a1a");
                grassB = Color.web("#4a8a2a");
            }
            case VIDHANA_SOUDHA -> {
                grassA = Color.web("#0a2a0a");
                grassB = Color.web("#133018");
            }
            default -> {
                grassA = Color.web("#1a5a1a");
                grassB = Color.web("#226622");
            }
        }
        gc.setFill(colorIndex == 0 ? grassA : grassB);
        double h = Math.max(1, y1 - y2);
        gc.fillRect(0, y2, screenW, h);
    }

    // -----------------------------------------------------------------------
    // Segment Trapezoid Renderer
    // -----------------------------------------------------------------------

    private void drawSegment(GraphicsContext gc,
                             double x1, double y1, double w1,
                             double x2, double y2, double w2,
                             int colorIndex, LocationType location,
                             boolean hasSignal, boolean isMetro,
                             WeatherSystem weather) {

        boolean wet = weather != null && weather.isRaining();

        // ---- Road surface colour ----
        Color roadA, roadB;
        switch (location) {
            case NICE_ROAD -> {
                roadA = Color.web("#2a2a2a");
                roadB = Color.web("#333333");
            }
            case OUTER_RING_ROAD -> {
                roadA = Color.web("#252530");
                roadB = Color.web("#2e2e3a");
            }
            case VIDHANA_SOUDHA -> {
                roadA = Color.web("#1a1a28");
                roadB = Color.web("#222232");
            }
            case LALBAGH -> {
                roadA = Color.web("#2a2a20");
                roadB = Color.web("#333328");
            }
            default -> {
                roadA = Color.web("#282828");
                roadB = Color.web("#313131");
            }
        }

        if (wet) {
            roadA = roadA.interpolate(Color.web("#1a2a3a"), 0.35);
            roadB = roadB.interpolate(Color.web("#1a2a3a"), 0.35);
        }

        Color roadColor = colorIndex == 0 ? roadA : roadB;

        // ---- Draw road trapezoid ----
        double h = Math.max(1, y1 - y2);
        gc.setFill(roadColor);
        gc.fillPolygon(
            new double[]{x1 - w1, x1 + w1, x2 + w2, x2 - w2},
            new double[]{y1,       y1,       y2,       y2},
            4);

        // ---- Rumble strips (alternating red/white at road edges) ----
        double rumbleW = w1 * 0.08;
        Color rumble = colorIndex == 0 ? Color.web("#DD2222") : Color.WHITE;
        gc.setFill(rumble);
        // Left rumble
        gc.fillPolygon(
            new double[]{x1 - w1,            x1 - w1 + rumbleW,
                         x2 - w2 + rumbleW,  x2 - w2},
            new double[]{y1, y1, y2, y2},
            4);
        // Right rumble
        gc.fillPolygon(
            new double[]{x1 + w1 - rumbleW,  x1 + w1,
                         x2 + w2,            x2 + w2 - rumbleW},
            new double[]{y1, y1, y2, y2},
            4);

        // ---- Lane markings (dashed white) ----
        if (colorIndex == 0) {
            gc.setFill(Color.web("#EEEECC"));
            // Draw lane dividers based on assumed lane count (not stored per segment here)
            // Use thirds of road width as dividers for a 3-lane look
            double[] fracs = {-0.33, 0.0, 0.33};
            double dashH = Math.max(1, h * 0.5);
            double dashY = y2 + (h - dashH) / 2.0;
            for (double frac : fracs) {
                double lx1 = x1 + frac * w1;
                double lx2 = x2 + frac * w2;
                double ly1 = y1 - h * 0.1;
                double ly2 = y2 + h * 0.1;
                double lw  = Math.max(1, w1 * 0.012);
                gc.fillPolygon(
                    new double[]{lx1 - lw, lx1 + lw, lx2 + lw, lx2 - lw},
                    new double[]{ly1,       ly1,       ly2,       ly2},
                    4);
            }
        }

        // ---- Metro overhead bar ----
        if (isMetro) {
            gc.setFill(Color.web("#7733CC"));
            double barH = Math.max(2, h * 0.15);
            gc.fillPolygon(
                new double[]{x1 - w1 * 0.5, x1 + w1 * 0.5, x2 + w2 * 0.5, x2 - w2 * 0.5},
                new double[]{y1 - barH, y1 - barH, y2 - barH, y2 - barH},
                4);
        }

        // ---- Traffic signal (red rectangle at top of near edge) ----
        if (hasSignal) {
            drawTrafficSignal(gc, x1, y2, w2 * 0.06);
        }
    }

    // -----------------------------------------------------------------------
    // Traffic Signal
    // -----------------------------------------------------------------------

    private void drawTrafficSignal(GraphicsContext gc, double x, double y, double size) {
        double s = Math.max(4, size);
        // Post
        gc.setFill(Color.web("#888888"));
        gc.fillRect(x - s * 0.15, y - s * 3, s * 0.3, s * 3);
        // Box
        gc.setFill(Color.web("#222222"));
        gc.fillRect(x - s * 0.6, y - s * 3.8, s * 1.2, s * 2.5);
        // Red light (top)
        gc.setFill(Color.web("#FF2222"));
        gc.fillOval(x - s * 0.35, y - s * 3.7, s * 0.7, s * 0.7);
        // Yellow
        gc.setFill(Color.web("#FFCC00"));
        gc.fillOval(x - s * 0.35, y - s * 2.9, s * 0.7, s * 0.7);
        // Green
        gc.setFill(Color.web("#22BB22"));
        gc.fillOval(x - s * 0.35, y - s * 2.1, s * 0.7, s * 0.7);
    }

    // -----------------------------------------------------------------------
    // Traffic drawing
    // -----------------------------------------------------------------------

    /**
     * Draw an AI traffic vehicle as a pixel-art sprite at screen position.
     *
     * @param scale pixels-per-sprite-pixel (larger = closer)
     */
    public void drawTrafficAt(GraphicsContext gc, AITraffic vehicle,
                               double screenX, double screenY, double scale) {
        double clampedScale = Math.max(0.3, scale);
        int[][] sprite = PixelSprites.getSpriteForType(vehicle.getVehicleType());
        PixelSprites.drawSprite(gc, sprite, screenX, screenY, clampedScale, null);
    }

    // -----------------------------------------------------------------------
    // Landmark Label
    // -----------------------------------------------------------------------

    private void drawLandmarkLabel(GraphicsContext gc, String text,
                                    double screenX, double screenY) {
        gc.save();
        double padding = 6;
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        gc.setTextAlign(TextAlignment.CENTER);

        // Approximate text width
        double textW = text.length() * 7.0;
        double boxW  = textW + padding * 2;
        double boxH  = 18;

        // Neon blue glow box
        gc.setFill(Color.web("#001a33CC"));
        gc.fillRect(screenX - boxW / 2, screenY - boxH, boxW, boxH);
        gc.setStroke(Color.web("#00AAFF"));
        gc.setLineWidth(1.5);
        gc.strokeRect(screenX - boxW / 2, screenY - boxH, boxW, boxH);

        // Text
        gc.setFill(Color.web("#00DDFF"));
        gc.fillText(text, screenX, screenY - 5);

        gc.restore();
    }
}
