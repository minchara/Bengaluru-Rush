package com.nexusdrive.world;

import java.util.ArrayList;
import java.util.List;

/**
 * BengaluruMap manages pseudo-3D road track segments for each Bengaluru location.
 * Each location has ~1600 segments with distinct curve/hill/landmark patterns.
 */
public class BengaluruMap {

    public static final int TOTAL_SEGMENTS = 1600;

    private final List<RoadSegment> segments = new ArrayList<>();
    private LocationType currentLocation;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void loadLocation(LocationType location) {
        currentLocation = location;
        segments.clear();

        switch (location) {
            case MG_ROAD         -> buildMGRoad();
            case SILK_BOARD      -> buildSilkBoard();
            case OUTER_RING_ROAD -> buildOuterRingRoad();
            case VIDHANA_SOUDHA  -> buildVidhanaSoudha();
            case NICE_ROAD       -> buildNiceRoad();
            case LALBAGH         -> buildLalbagh();
        }

        // Pad to exactly TOTAL_SEGMENTS by cycling existing segments
        int base = segments.size();
        while (segments.size() < TOTAL_SEGMENTS) {
            segments.add(cloneSegment(segments.get(segments.size() % Math.max(1, base))));
        }
        // Trim if over
        while (segments.size() > TOTAL_SEGMENTS) {
            segments.remove(segments.size() - 1);
        }
    }

    public RoadSegment getSegment(int index) {
        int size = segments.size();
        return segments.get(((index % size) + size) % size);
    }

    public int getSegmentCount() {
        return segments.size();
    }

    public LocationType getCurrentLocation() {
        return currentLocation;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private RoadSegment cloneSegment(RoadSegment src) {
        RoadSegment s = new RoadSegment();
        s.curve         = src.curve;
        s.hill          = src.hill;
        s.colorIndex    = src.colorIndex;
        s.laneCount     = src.laneCount;
        s.landmark      = src.landmark;
        s.hasSignal     = src.hasSignal;
        s.isMetroSection = src.isMetroSection;
        return s;
    }

    /**
     * Add {@code count} straight segments.
     * @param landmark  placed at the midpoint segment; null for no label
     * @param hasSignal the FIRST segment of this run gets a traffic signal
     */
    private void addStraight(int count, int lanes, String landmark,
                              boolean hasSignal, boolean isMetro) {
        int colorStart = segments.size();
        for (int i = 0; i < count; i++) {
            RoadSegment seg = new RoadSegment();
            seg.curve         = 0.0;
            seg.hill          = 0.0;
            seg.colorIndex    = ((colorStart + i) / 8) % 2;
            seg.laneCount     = lanes;
            seg.landmark      = (i == count / 2) ? landmark : null;
            seg.hasSignal     = hasSignal && (i == 0);
            seg.isMetroSection = isMetro;
            segments.add(seg);
        }
    }

    private void addCurve(int count, double curvature, int lanes) {
        int colorStart = segments.size();
        for (int i = 0; i < count; i++) {
            RoadSegment seg = new RoadSegment();
            seg.curve         = curvature;
            seg.hill          = 0.0;
            seg.colorIndex    = ((colorStart + i) / 8) % 2;
            seg.laneCount     = lanes;
            seg.landmark      = null;
            seg.hasSignal     = false;
            seg.isMetroSection = false;
            segments.add(seg);
        }
    }

    private void addHillCurve(int count, double curvature, double hill, int lanes) {
        int colorStart = segments.size();
        for (int i = 0; i < count; i++) {
            RoadSegment seg = new RoadSegment();
            seg.curve         = curvature;
            seg.hill          = hill;
            seg.colorIndex    = ((colorStart + i) / 8) % 2;
            seg.laneCount     = lanes;
            seg.landmark      = null;
            seg.hasSignal     = false;
            seg.isMetroSection = false;
            segments.add(seg);
        }
    }

    // -------------------------------------------------------------------------
    // Location build methods
    // -------------------------------------------------------------------------

    /**
     * MG ROAD – urban corridor, 4 lanes, metro overhead, moderate curves,
     * frequent traffic signals, famous junctions as landmarks.
     * Target: ~1600 segments.
     */
    private void buildMGRoad() {
        // ---- Pass 1 (~800 segs) ----
        addStraight(80,  4, null,                    false, false);
        addStraight(50,  4, "Trinity Circle",        true,  false);
        addCurve(80, 1.5, 4);
        addStraight(60,  4, "MG Road Metro Station", false, true);
        addCurve(60, -1.2, 4);
        addStraight(80,  4, "Church Street",         true,  false);
        addCurve(100, 0.8, 4);
        addStraight(70,  4, "Brigade Road",          false, false);
        addCurve(80, -1.5, 4);
        addStraight(100, 4, "Residency Road",        true,  true);
        addCurve(60, 1.0, 4);
        addHillCurve(60, 0.5, 0.3, 4);
        addStraight(80,  4, "St. Mark's Road",       true,  false);
        addCurve(70, -0.9, 4);
        addStraight(60,  4, "Lavelle Road",          false, false);
        addHillCurve(50, 0.4, -0.2, 4);
        addStraight(70,  4, "Infantry Road",         true,  false);
        addCurve(90, 1.2, 4);
        addStraight(50,  4, "Cubbon Road",           false, true);

        // ---- Pass 2 (~800 segs) ----
        addStraight(80,  4, null,                    false, false);
        addCurve(80, -1.0, 4);
        addStraight(60,  4, "Trinity Circle",        true,  false);
        addCurve(60,  1.3, 4);
        addStraight(80,  4, "MG Road Metro Station", false, true);
        addCurve(100, -0.8, 4);
        addStraight(70,  4, "Church Street",         true,  false);
        addHillCurve(60, 0.6, 0.25, 4);
        addStraight(80,  4, "Brigade Road",          false, false);
        addCurve(80, 1.5, 4);
        addStraight(100, 4, "Residency Road",        true,  true);
        addCurve(60, -1.2, 4);
        addHillCurve(50, -0.5, -0.3, 4);
        addStraight(80,  4, "St. Mark's Road",       true,  false);
        addCurve(70, 0.9, 4);
        addStraight(60,  4, "Lavelle Road",          false, false);
        addStraight(70,  4, "Infantry Road",         true,  false);
        addCurve(90, -1.2, 4);
        addStraight(50,  4, "Cubbon Road",           false, true);
    }

    /**
     * SILK BOARD – notorious junction, mostly straight with sudden narrowings,
     * dense signals, flyover pillars, packed multi-lane chaos.
     * Target: ~1600 segments.
     */
    private void buildSilkBoard() {
        // ---- Pass 1 (~800 segs) ----
        addStraight(120, 6, null,                      false, false);
        addStraight(40,  6, "Silk Board Flyover",      true,  false);
        addCurve(30, 0.4, 6);   // slight correction
        addStraight(100, 6, "Hosur Road Junction",     true,  false);
        addCurve(20, -0.3, 5);
        addStraight(80,  5, null,                      true,  false);
        addCurve(25, 0.5, 5);
        addStraight(60,  5, "Electronic City Flyover", true,  false);
        addCurve(20, -0.4, 6);
        addStraight(90,  6, "Madiwala Junction",       true,  false);
        addCurve(30, 0.3, 6);
        addStraight(70,  6, "BTM Layout Signal",       true,  false);
        addCurve(20, -0.3, 6);
        addStraight(80,  6, "Bommanahalli Signal",     true,  false);
        addHillCurve(30, 0.2, 0.1, 6);
        addStraight(100, 6, null,                      false, false);

        // ---- Pass 2 (~800 segs) ----
        addStraight(120, 6, null,                      false, false);
        addStraight(40,  6, "Silk Board Flyover",      true,  false);
        addCurve(30, -0.4, 6);
        addStraight(100, 6, "Hosur Road Junction",     true,  false);
        addCurve(20,  0.3, 5);
        addStraight(80,  5, null,                      true,  false);
        addCurve(25, -0.5, 5);
        addStraight(60,  5, "Electronic City Flyover", true,  false);
        addCurve(20,  0.4, 6);
        addStraight(90,  6, "Madiwala Junction",       true,  false);
        addCurve(30, -0.3, 6);
        addStraight(70,  6, "BTM Layout Signal",       true,  false);
        addCurve(20,  0.3, 6);
        addStraight(80,  6, "Bommanahalli Signal",     true,  false);
        addHillCurve(30, -0.2, -0.1, 6);
        addStraight(100, 6, null,                      false, false);
    }

    /**
     * OUTER RING ROAD – high-speed, long straights, gentle sweeping curves,
     * tech park landmarks, 6 lanes, minimal signals.
     */
    private void buildOuterRingRoad() {
        // ---- Pass 1 ----
        addStraight(150, 6, null,                    false, false);
        addCurve(100, 0.5, 6);
        addStraight(120, 6, "Manyata Tech Park",     false, false);
        addCurve(80, -0.4, 6);
        addStraight(100, 6, "Embassy Golf Links",    false, false);
        addHillCurve(60, 0.3, 0.2, 6);
        addStraight(80,  6, "RMZ Infinity",          false, false);
        addCurve(90, -0.6, 6);
        addStraight(100, 6, "Karle Town Centre",     false, false);
        addCurve(70,  0.4, 6);

        // ---- Pass 2 ----
        addStraight(150, 6, null,                    false, false);
        addCurve(100, -0.5, 6);
        addStraight(120, 6, "Manyata Tech Park",     false, false);
        addCurve(80,  0.4, 6);
        addStraight(100, 6, "Embassy Golf Links",    false, false);
        addHillCurve(60, -0.3, -0.15, 6);
        addStraight(80,  6, "RMZ Infinity",          false, false);
        addCurve(90,  0.6, 6);
        addStraight(100, 6, "Karle Town Centre",     false, false);
        addCurve(70, -0.4, 6);
    }

    /**
     * VIDHANA SOUDHA – night setting, grand approach boulevard,
     * gentle S-curves, monumental landmarks.
     */
    private void buildVidhanaSoudha() {
        // ---- Pass 1 ----
        addStraight(100, 4, null,                    false, false);
        addCurve(60, 0.6, 4);
        addStraight(80,  4, "Vidhana Soudha",        false, false);
        addCurve(60, -0.6, 4);
        addStraight(100, 4, "Attara Kacheri",        false, false);
        addHillCurve(40, 0.3, 0.1, 4);
        addStraight(80,  4, "Cubbon Park",           false, false);
        addCurve(60, -0.5, 4);
        addStraight(80,  4, "High Court",            false, false);
        addCurve(60,  0.5, 4);
        addStraight(80,  4, "Raj Bhavan Road",       false, false);
        addHillCurve(40, -0.3, -0.1, 4);

        // ---- Pass 2 ----
        addStraight(100, 4, null,                    false, false);
        addCurve(60, -0.6, 4);
        addStraight(80,  4, "Vidhana Soudha",        false, false);
        addCurve(60,  0.6, 4);
        addStraight(100, 4, "Attara Kacheri",        false, false);
        addHillCurve(40, -0.3, 0.12, 4);
        addStraight(80,  4, "Cubbon Park",           false, false);
        addCurve(60,  0.5, 4);
        addStraight(80,  4, "High Court",            false, false);
        addCurve(60, -0.5, 4);
        addStraight(80,  4, "Raj Bhavan Road",       false, false);
        addHillCurve(40,  0.3,  0.1,  4);
        addStraight(80,  4, "Seshadri Road",         false, false);
        addCurve(60, -0.4, 4);
        addStraight(80,  4, "Bangalore Palace",      false, false);
    }

    /**
     * NICE ROAD – open elevated highway, very long straights, 2–3 big sweeping
     * curves, toll plazas, sparse and fast.
     */
    private void buildNiceRoad() {
        // ---- Pass 1 ----
        addStraight(200, 6, null,                    false, false);
        addStraight(50,  6, "NICE Road Toll",        true,  false);
        addCurve(120, 0.8, 6);
        addStraight(200, 6, "Kengeri",               false, false);
        addCurve(100, -0.9, 6);
        addHillCurve(80, 0.4, 0.4, 6);
        addStraight(50,  6, "Tumkur Road Junction",  true,  false);

        // ---- Pass 2 ----
        addStraight(200, 6, null,                    false, false);
        addStraight(50,  6, "NICE Road Toll",        true,  false);
        addCurve(120, -0.8, 6);
        addStraight(200, 6, "Kengeri",               false, false);
        addCurve(100,  0.9, 6);
        addHillCurve(80, -0.4, -0.4, 6);
        addStraight(50,  6, "Tumkur Road Junction",  true,  false);
        addStraight(100, 6, "Mysuru Road Junction",  false, false);
        addCurve(80, 0.6, 6);
        addStraight(100, 6, "Ramanagara Bypass",     false, false);
    }

    /**
     * LALBAGH – gentle winding garden roads, 2 lanes, lush green, slow pace,
     * botanical landmarks.
     */
    private void buildLalbagh() {
        // ---- Pass 1 ----
        addStraight(60,  2, null,                           false, false);
        addCurve(80, 1.0, 2);
        addStraight(60,  2, "Lalbagh Botanical Garden",     false, false);
        addCurve(70, -1.0, 2);
        addStraight(50,  2, "Glass House",                  false, false);
        addHillCurve(50, 0.5, 0.15, 2);
        addStraight(60,  2, "Rose Garden",                  false, false);
        addCurve(60, -0.8, 2);
        addStraight(70,  2, "Lotus Pond",                   false, false);
        addHillCurve(40, 0.4, -0.1, 2);
        addStraight(60,  2, "Kempegowda Tower",             false, false);
        addCurve(50, 1.2, 2);
        addStraight(50,  2, "Bull Temple Road",             false, false);
        addCurve(60, -1.0, 2);
        addStraight(80,  2, "Mavalli Circle",               false, false);
        addHillCurve(50, 0.6, 0.2, 2);

        // ---- Pass 2 ----
        addStraight(60,  2, null,                           false, false);
        addCurve(80, -1.0, 2);
        addStraight(60,  2, "Lalbagh Botanical Garden",     false, false);
        addCurve(70,  1.0, 2);
        addStraight(50,  2, "Glass House",                  false, false);
        addHillCurve(50, -0.5, -0.15, 2);
        addStraight(60,  2, "Rose Garden",                  false, false);
        addCurve(60,  0.8, 2);
        addStraight(70,  2, "Lotus Pond",                   false, false);
        addHillCurve(40, -0.4, 0.12, 2);
        addStraight(60,  2, "Kempegowda Tower",             false, false);
        addCurve(50, -1.2, 2);
        addStraight(50,  2, "Bull Temple Road",             false, false);
        addCurve(60,  1.0, 2);
        addStraight(80,  2, "Mavalli Circle",               false, false);
        addHillCurve(50, -0.6, -0.2, 2);
        addStraight(60,  2, "Basavanagudi",                 false, false);
        addCurve(60,  0.7, 2);
        addStraight(50,  2, "Sajjan Rao Circle",            false, false);
    }
}
