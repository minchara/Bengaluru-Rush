package com.nexusdrive.world;

/**
 * A single logical segment of the procedurally-generated road.
 *
 * <p>The road is modelled as a list (or ring-buffer) of {@code RoadSegment}
 * instances.  Each segment represents a fixed physical length of road
 * (typically 200 virtual units).  The pseudo-3D road renderer interpolates
 * between consecutive segments to produce curves, hills, and colour stripes.</p>
 *
 * <h3>Coordinate conventions</h3>
 * <ul>
 *   <li>{@link #curve} — lateral curvature: negative = bends left, positive = right.
 *       Typical range is −2.0 to +2.0; values beyond ±3.0 are very sharp hairpins.</li>
 *   <li>{@link #hill}  — signed elevation delta per segment. Positive = uphill,
 *       negative = downhill.  Values are in the same virtual-unit space as the
 *       road position and accumulated into camera y-offset by the renderer.</li>
 * </ul>
 *
 * <h3>Color striping</h3>
 * {@link #colorIndex} alternates between 0 and 1 to produce the classic
 * checker-stripe road pattern.  The renderer maps index 0 → primary road colour
 * and index 1 → alternate road colour (slightly lighter or darker).
 *
 * <h3>Overlay flags</h3>
 * Optional fields ({@link #landmark}, {@link #hasSignal}, {@link #isMetroSection})
 * trigger additional rendering layers and gameplay events:
 * <ul>
 *   <li>A non-null {@code landmark} string causes the HUD to display a
 *       location pop-up and awards bonus points in Landmark Challenge mode.</li>
 *   <li>{@code hasSignal == true} spawns a traffic signal sprite and may force
 *       NPC vehicles to stop / slow on red.</li>
 *   <li>{@code isMetroSection == true} renders the Namma Metro viaduct overhead
 *       and allows NAMMA_METRO vehicles to appear in the background layer.</li>
 * </ul>
 */
public class RoadSegment {

    // =========================================================================
    // Fields
    // =========================================================================

    /**
     * Lateral curvature of this segment in virtual-unit / segment units.
     * Negative values curve the road to the left, positive to the right.
     * Range: typically −2.0 to +2.0.
     */
    public double curve;

    /**
     * Elevation change contributed by this segment (virtual units).
     * The renderer accumulates these values to compute camera pitch.
     * Positive = rising terrain, negative = descending.
     */
    public double hill;

    /**
     * Alternating stripe colour index (0 or 1).
     * Flips every N segments to create the classic road-stripe illusion.
     */
    public int colorIndex;

    /**
     * Optional landmark label (e.g., {@code "Vidhana Soudha"}, {@code "Lalbagh Gate"}).
     * {@code null} when no landmark is associated with this segment.
     * When non-null, the HUD overlay renderer shows a pop-up banner.
     */
    public String landmark;

    /**
     * Whether a traffic signal is present at the start of this segment.
     * When {@code true}, NPC vehicles may stop at the signal and the road
     * renderer draws a signal pole and lights.
     */
    public boolean hasSignal;

    /**
     * Whether the Namma Metro viaduct runs overhead through this segment.
     * Enables the purple elevated-rail structure in the skybox layer.
     */
    public boolean isMetroSection;

    /**
     * Number of driveable lanes in this segment (2–6).
     * Normally inherited from the active {@link LocationType#roadLanes} but can
     * vary per-segment for lane-merge / diverge effects (e.g., near Silk Board).
     */
    public int laneCount;

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Creates a default straight, flat segment with 4 lanes and no overlays. */
    public RoadSegment() {
        this.curve          = 0.0;
        this.hill           = 0.0;
        this.colorIndex     = 0;
        this.landmark       = null;
        this.hasSignal      = false;
        this.isMetroSection = false;
        this.laneCount      = 4;
    }

    /**
     * Full constructor used by road-generation code.
     *
     * @param curve          lateral curvature (−2.0 to +2.0)
     * @param hill           elevation delta
     * @param colorIndex     stripe colour (0 or 1)
     * @param landmark       landmark label or {@code null}
     * @param hasSignal      {@code true} if a traffic signal is present
     * @param isMetroSection {@code true} if the metro viaduct is overhead
     * @param laneCount      number of road lanes (2–6)
     */
    public RoadSegment(double curve, double hill, int colorIndex,
                       String landmark, boolean hasSignal,
                       boolean isMetroSection, int laneCount) {
        this.curve          = curve;
        this.hill           = hill;
        this.colorIndex     = colorIndex;
        this.landmark       = landmark;
        this.hasSignal      = hasSignal;
        this.isMetroSection = isMetroSection;
        this.laneCount      = laneCount;
    }

    // =========================================================================
    // Helper factories
    // =========================================================================

    /**
     * Creates a plain straight segment with alternating colour.
     *
     * @param colorIndex stripe index (0 or 1)
     * @param laneCount  number of lanes
     * @return a new straight {@code RoadSegment}
     */
    public static RoadSegment straight(int colorIndex, int laneCount) {
        return new RoadSegment(0.0, 0.0, colorIndex, null, false, false, laneCount);
    }

    /**
     * Creates a curved segment.
     *
     * @param curve      curvature (negative = left, positive = right)
     * @param colorIndex stripe index
     * @param laneCount  number of lanes
     * @return a new curved {@code RoadSegment}
     */
    public static RoadSegment curve(double curve, int colorIndex, int laneCount) {
        return new RoadSegment(curve, 0.0, colorIndex, null, false, false, laneCount);
    }

    /**
     * Creates a hilly segment with optional curve.
     *
     * @param hill       elevation delta
     * @param curve      curvature
     * @param colorIndex stripe index
     * @param laneCount  number of lanes
     * @return a new hilly {@code RoadSegment}
     */
    public static RoadSegment hill(double hill, double curve, int colorIndex, int laneCount) {
        return new RoadSegment(curve, hill, colorIndex, null, false, false, laneCount);
    }

    /**
     * Creates a landmark segment (triggers HUD banner, bonus points).
     *
     * @param landmark   the name of the landmark
     * @param curve      curvature
     * @param colorIndex stripe index
     * @param laneCount  number of lanes
     * @return a new landmark {@code RoadSegment}
     */
    public static RoadSegment atLandmark(String landmark, double curve,
                                         int colorIndex, int laneCount) {
        return new RoadSegment(curve, 0.0, colorIndex, landmark, false, false, laneCount);
    }

    // =========================================================================
    // Object overrides
    // =========================================================================

    @Override
    public String toString() {
        return "RoadSegment{curve=" + curve
                + ", hill=" + hill
                + ", colorIndex=" + colorIndex
                + ", lanes=" + laneCount
                + (landmark       != null  ? ", landmark='" + landmark + "'" : "")
                + (hasSignal              ? ", SIGNAL"  : "")
                + (isMetroSection         ? ", METRO"   : "")
                + "}";
    }
}
