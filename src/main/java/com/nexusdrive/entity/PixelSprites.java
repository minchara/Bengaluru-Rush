package com.nexusdrive.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * PixelSprites – static repository of all pixel-art sprite data for NEXUS DRIVE.
 *
 * <p>All sprites are int[][] arrays viewed TOP-DOWN (rear-angle for player car).
 * <ul>
 *   <li>0x00000000 = fully transparent (skip drawing)</li>
 *   <li>0xFFRRGGBB = fully opaque colour</li>
 * </ul>
 *
 * <p>Sprites are drawn via {@link #drawSprite} which scales each "pixel" to a
 * {@code scale × scale} rectangle on the JavaFX Canvas.
 */
public class PixelSprites {

    // -----------------------------------------------------------------------
    // Common colour constants  (0xFFRRGGBB)
    // -----------------------------------------------------------------------

    private static final int T   = 0x00000000; // transparent

    private static final int BLK = 0xFF111111; // near-black outline
    private static final int WHT = 0xFFFFFFFF; // white
    private static final int DGY = 0xFF444444; // dark gray
    private static final int MGY = 0xFF666666; // mid gray
    private static final int LGY = 0xFF999999; // light gray
    private static final int SGY = 0xFFCCCCCC; // silver gray
    private static final int CYN = 0xFF44CCEE; // window cyan
    private static final int DCY = 0xFF227799; // dark cyan (window tint)
    private static final int YLW = 0xFFFFDD00; // headlight / auto yellow
    private static final int DYL = 0xFFCC9900; // dark yellow
    private static final int ORG = 0xFFFF8800; // orange
    private static final int RED = 0xFFDD2222; // red (BMTC, tail-light)
    private static final int DRD = 0xFF881111; // dark red
    private static final int CRM = 0xFFF5E6C8; // cream
    private static final int BLU = 0xFF3366FF; // blue
    private static final int DBL = 0xFF1133AA; // dark blue
    private static final int GRN = 0xFF22AA44; // green
    private static final int PRP = 0xFF8822CC; // purple (metro)
    private static final int LPR = 0xFFBB66FF; // light purple
    private static final int GLD = 0xFFFFCC00; // gold (coin)
    private static final int NIT = 0xFF00FFCC; // neon cyan (nitro)
    private static final int OLV = 0xFF55AA22; // olive green (delivery box)

    // -----------------------------------------------------------------------
    // AUTO RICKSHAW  (14 wide × 20 tall, top-down view)
    // Yellow-black body, three-wheel shape (front narrow, rear wide)
    // -----------------------------------------------------------------------

    public static final int[][] AUTO_RICKSHAW = {
        // col: 0    1    2    3    4    5    6    7    8    9   10   11   12   13
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T  }, // 0  front tip
        { T,   T,   BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T,   T,   T  }, // 1
        { T,   BLK, YLW, YLW, CYN, CYN, CYN, CYN, CYN, YLW, YLW, BLK, T,   T  }, // 2 windshield
        { T,   BLK, YLW, BLK, CYN, CYN, CYN, CYN, CYN, BLK, YLW, BLK, T,   T  }, // 3
        { T,   BLK, YLW, BLK, CYN, CYN, CYN, CYN, CYN, BLK, YLW, BLK, T,   T  }, // 4
        { BLK, YLW, YLW, BLK, BLK, BLK, BLK, BLK, BLK, BLK, YLW, YLW, BLK, T  }, // 5 canopy start
        { BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T  }, // 6
        { BLK, BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, BLK, T  }, // 7
        { T,   BLK, YLW, YLW, YLW, BLK, BLK, BLK, YLW, YLW, YLW, BLK, T,   T  }, // 8 seat/passenger
        { T,   BLK, YLW, YLW, BLK, DGY, DGY, BLK, YLW, YLW, YLW, BLK, T,   T  }, // 9
        { T,   BLK, YLW, YLW, BLK, DGY, DGY, BLK, YLW, YLW, YLW, BLK, T,   T  }, // 10
        { T,   BLK, YLW, YLW, YLW, BLK, BLK, BLK, YLW, YLW, YLW, BLK, T,   T  }, // 11
        { T,   BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T,   T  }, // 12
        { T,   BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T,   T  }, // 13
        { BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T  }, // 14 body widens
        { BLK, YLW, BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, YLW, BLK, T  }, // 15 rear wheel gaps
        { BLK, YLW, BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, YLW, BLK, T  }, // 16
        { BLK, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, YLW, BLK, T  }, // 17
        { T,   BLK, YLW, DGY, DGY, YLW, YLW, YLW, DGY, DGY, YLW, BLK, T,   T  }, // 18 rear engine
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T  }, // 19 rear bumper
    };

    // -----------------------------------------------------------------------
    // BMTC BUS  (22 wide × 36 tall, top-down)
    // Red body with cream/white stripe band, large rectangular
    // -----------------------------------------------------------------------

    public static final int[][] BMTC_BUS = {
        // 22 columns
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 0
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 1
        { BLK, RED, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, RED, BLK }, // 2 windshield
        { BLK, RED, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, RED, BLK }, // 3
        { BLK, RED, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, RED, BLK }, // 4
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 5
        { BLK, RED, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, RED, BLK }, // 6 cream stripe
        { BLK, RED, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, RED, BLK }, // 7
        { BLK, RED, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, RED, BLK }, // 8
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 9
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 10 windows
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 11
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 12
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 13
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 14
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 15
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 16
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 17
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 18
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 19
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 20
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 21
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 22
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 23
        { BLK, RED, RED, CYN, BLK, RED, RED, CYN, BLK, RED, RED, RED, BLK, CYN, RED, RED, BLK, CYN, RED, RED, RED, BLK }, // 24
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 25
        { BLK, RED, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, RED, BLK }, // 26 cream stripe rear
        { BLK, RED, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, CRM, RED, BLK }, // 27
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 28
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 29
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 30
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 31
        { BLK, RED, RED, DRD, DRD, RED, RED, DRD, DRD, RED, RED, RED, DRD, DRD, RED, RED, DRD, DRD, RED, RED, RED, BLK }, // 32 rear
        { BLK, RED, DRD, DRD, DRD, DRD, RED, DRD, DRD, DRD, DRD, DRD, DRD, DRD, DRD, RED, DRD, DRD, DRD, DRD, RED, BLK }, // 33
        { BLK, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, RED, BLK }, // 34
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 35
    };

    // -----------------------------------------------------------------------
    // DELIVERY BIKE  (10 wide × 18 tall, top-down)
    // Gray bike + orange delivery box on rear
    // -----------------------------------------------------------------------

    public static final int[][] DELIVERY_BIKE = {
        // 10 cols
        { T,   T,   BLK, MGY, MGY, MGY, MGY, BLK, T,   T   }, // 0
        { T,   BLK, MGY, MGY, CYN, CYN, MGY, MGY, BLK, T   }, // 1 visor
        { T,   BLK, MGY, BLK, CYN, CYN, BLK, MGY, BLK, T   }, // 2
        { T,   BLK, MGY, MGY, MGY, MGY, MGY, MGY, BLK, T   }, // 3
        { T,   BLK, MGY, MGY, MGY, MGY, MGY, MGY, BLK, T   }, // 4 body
        { BLK, DGY, DGY, MGY, MGY, MGY, MGY, DGY, DGY, BLK }, // 5 handlebar
        { T,   BLK, MGY, MGY, MGY, MGY, MGY, MGY, BLK, T   }, // 6
        { T,   BLK, MGY, MGY, MGY, MGY, MGY, MGY, BLK, T   }, // 7
        { T,   T,   BLK, MGY, MGY, MGY, MGY, BLK, T,   T   }, // 8
        { T,   T,   BLK, ORG, ORG, ORG, ORG, BLK, T,   T   }, // 9 delivery box start
        { T,   BLK, ORG, ORG, ORG, ORG, ORG, ORG, BLK, T   }, // 10
        { T,   BLK, ORG, BLK, ORG, ORG, BLK, ORG, BLK, T   }, // 11 logo on box
        { T,   BLK, ORG, ORG, ORG, ORG, ORG, ORG, BLK, T   }, // 12
        { T,   BLK, ORG, ORG, ORG, ORG, ORG, ORG, BLK, T   }, // 13
        { T,   BLK, ORG, ORG, ORG, ORG, ORG, ORG, BLK, T   }, // 14
        { T,   BLK, ORG, ORG, ORG, ORG, ORG, ORG, BLK, T   }, // 15
        { T,   T,   BLK, ORG, ORG, ORG, ORG, BLK, T,   T   }, // 16
        { T,   T,   T,   BLK, BLK, BLK, BLK, T,   T,   T   }, // 17
    };

    // -----------------------------------------------------------------------
    // MOTORCYCLE  (10 wide × 18 tall, top-down)
    // Sporty, dark gray / black
    // -----------------------------------------------------------------------

    public static final int[][] MOTORCYCLE = {
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 0
        { T,   BLK, DGY, DGY, CYN, CYN, DGY, DGY, BLK, T   }, // 1 visor
        { T,   BLK, DGY, BLK, CYN, CYN, BLK, DGY, BLK, T   }, // 2
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 3
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 4 handlebar
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 5
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 6
        { T,   T,   BLK, DGY, BLK, BLK, DGY, BLK, T,   T   }, // 7 tank
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 8
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 9
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 10
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 11
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 12 rear seat
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 13
        { T,   T,   BLK, DGY, DGY, DGY, DGY, BLK, T,   T   }, // 14
        { T,   T,   T,   BLK, DGY, DGY, BLK, T,   T,   T   }, // 15
        { T,   T,   T,   BLK, RED, RED, BLK, T,   T,   T   }, // 16 tail-light
        { T,   T,   T,   T,   BLK, BLK, T,   T,   T,   T   }, // 17
    };

    // -----------------------------------------------------------------------
    // CAR  (16 wide × 24 tall, top-down)
    // Generic white sedan with dark windows
    // -----------------------------------------------------------------------

    public static final int[][] CAR = {
        // 16 cols
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK, T   }, // 1
        { BLK, SGY, YLW, YLW, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, YLW, YLW, SGY, BLK }, // 2 headlights
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 3
        { BLK, SGY, SGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, SGY, SGY, BLK }, // 4 windshield
        { BLK, SGY, SGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, SGY, SGY, BLK }, // 5
        { BLK, SGY, SGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, SGY, SGY, BLK }, // 6
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 7 A-pillar
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 8 side windows
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 9
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 10
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 11
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 12 B-pillar
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 13
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 14
        { BLK, SGY, DCY, DCY, DCY, BLK, SGY, SGY, SGY, BLK, DCY, DCY, DCY, DCY, SGY, BLK }, // 15
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 16 C-pillar
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 17 trunk
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 18
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 19
        { BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK }, // 20
        { BLK, SGY, RED, RED, SGY, SGY, SGY, SGY, SGY, SGY, SGY, RED, RED, SGY, SGY, BLK }, // 21 tail lights
        { T,   BLK, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, SGY, BLK, T   }, // 22
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 23
    };

    // -----------------------------------------------------------------------
    // SUV  (18 wide × 26 tall, top-down)
    // Dark gray SUV, slightly wider than the CAR
    // -----------------------------------------------------------------------

    public static final int[][] SUV = {
        // 18 cols
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 1
        { BLK, DGY, YLW, YLW, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, YLW, YLW, DGY, BLK }, // 2 headlights
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 3
        { BLK, DGY, DGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DGY, DGY, BLK }, // 4 windshield
        { BLK, DGY, DGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DGY, DGY, BLK }, // 5
        { BLK, DGY, DGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DGY, DGY, BLK }, // 6
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 7
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 8 side windows
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 9
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 10
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 11
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 12
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 13
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 14
        { BLK, DGY, DCY, DCY, DCY, DCY, BLK, DGY, DGY, BLK, DCY, DCY, DCY, DCY, DCY, DCY, DGY, BLK }, // 15
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 16
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 17 rear body
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 18
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 19
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 20
        { BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK }, // 21
        { BLK, DGY, RED, RED, RED, DGY, DGY, DGY, DGY, DGY, DGY, DGY, RED, RED, RED, DGY, DGY, BLK }, // 22 tail lights
        { BLK, DGY, DRD, DRD, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DRD, DRD, DGY, DGY, BLK }, // 23
        { T,   BLK, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, DGY, BLK, T   }, // 24
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 25
    };

    // -----------------------------------------------------------------------
    // TRUCK  (22 wide × 40 tall, top-down)
    // Large goods truck – dark blue cab, light gray cargo box
    // -----------------------------------------------------------------------

    public static final int[][] TRUCK = {
        // 22 cols
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK, T   }, // 1
        { BLK, DBL, YLW, YLW, YLW, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, YLW, YLW, YLW, DBL, BLK }, // 2 headlights
        { BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK }, // 3
        { BLK, DBL, DBL, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DBL, DBL, BLK }, // 4 cab window
        { BLK, DBL, DBL, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DBL, DBL, BLK }, // 5
        { BLK, DBL, DBL, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, DBL, DBL, BLK }, // 6
        { BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK }, // 7
        { BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK }, // 8 cab body
        { BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK }, // 9
        { T,   BLK, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, DBL, BLK, T   }, // 10
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 11 cab/cargo divider
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 12 cargo box
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 13
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 14
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 15
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 16
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 17
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 18
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 19
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 20
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 21
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 22
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 23
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 24
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 25
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 26
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 27
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 28
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 29
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 30
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 31
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 32
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 33
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 34
        { T,   BLK, LGY, RED, RED, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, RED, RED, LGY, LGY, BLK, T   }, // 35 tail-lights
        { T,   BLK, LGY, DRD, DRD, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, DRD, DRD, LGY, LGY, BLK, T   }, // 36
        { T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 37
        { T,   T,   BLK, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, LGY, BLK, T   }, // 38
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T   }, // 39
    };

    // -----------------------------------------------------------------------
    // OLA TAXI  (16 wide × 24 tall, top-down)
    // White + neon green Ola branding, same silhouette as CAR
    // -----------------------------------------------------------------------

    private static final int OGN = 0xFF00CC44; // Ola green

    public static final int[][] OLA_TAXI = {
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK, T   }, // 1
        { BLK, WHT, YLW, YLW, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, YLW, YLW, WHT, BLK }, // 2 headlights
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 3
        { BLK, WHT, WHT, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, WHT, WHT, BLK }, // 4 windshield
        { BLK, WHT, WHT, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, WHT, WHT, BLK }, // 5
        { BLK, WHT, WHT, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, WHT, WHT, BLK }, // 6
        { BLK, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, BLK }, // 7 green stripe
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 8 side windows
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 9
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 10
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 11
        { BLK, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, OGN, BLK }, // 12 green band
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 13
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 14
        { BLK, WHT, DCY, DCY, DCY, BLK, WHT, WHT, WHT, BLK, DCY, DCY, DCY, DCY, WHT, BLK }, // 15
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 16
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 17
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 18
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 19
        { BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK }, // 20
        { BLK, WHT, RED, RED, WHT, WHT, WHT, WHT, WHT, WHT, WHT, RED, RED, WHT, WHT, BLK }, // 21 tail lights
        { T,   BLK, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, WHT, BLK, T   }, // 22
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 23
    };

    // -----------------------------------------------------------------------
    // NAMMA METRO  (28 wide × 48 tall, top-down)
    // Purple body with silver/white panel accents, long train
    // -----------------------------------------------------------------------

    public static final int[][] NAMMA_METRO = {
        // 28 cols
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK, T   }, // 1
        { BLK, PRP, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, PRP, BLK }, // 2 front stripe
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 3
        { BLK, PRP, PRP, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, PRP, PRP, BLK }, // 4 cab window
        { BLK, PRP, PRP, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, PRP, PRP, BLK }, // 5
        { BLK, PRP, PRP, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, PRP, PRP, BLK }, // 6
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 7
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 8 door pillars silver
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 9
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 10
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 11
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 12
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 13
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 14
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 15
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 16
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 17
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 18
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 19
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 20
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 21
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 22
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 23
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 24
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 25
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 26
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 27
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 28
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 29
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 30
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 31
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 32
        { BLK, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, DCY, DCY, SGY, PRP, PRP, SGY, BLK }, // 33
        { BLK, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, PRP, PRP, SGY, BLK }, // 34
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 35
        { BLK, PRP, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, LPR, PRP, BLK }, // 36 rear stripe
        { BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK }, // 37
        { BLK, PRP, RED, RED, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, RED, RED, PRP, BLK }, // 38 tail lights
        { T,   BLK, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, PRP, BLK, T   }, // 39
        { T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T   }, // 40
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 41
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 42
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 43
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 44
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 45
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 46
        { T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T,   T   }, // 47
    };

    // -----------------------------------------------------------------------
    // PLAYER CAR: STREET RACER  (18 wide × 28 tall, top-down rear-angle)
    // Red sporty car, aerodynamic
    // -----------------------------------------------------------------------

    private static final int PRD = 0xFFCC1111; // player red
    private static final int LRD = 0xFFEE3333; // lighter red highlight

    public static final int[][] PLAYER_STREET_RACER = {
        // 18 cols
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T   }, // 0
        { T,   T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T,   T   }, // 1
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 2
        { BLK, PRD, PRD, PRD, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, PRD, PRD, PRD, BLK, T   }, // 3 windshield
        { BLK, PRD, PRD, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, PRD, PRD, BLK, T   }, // 4
        { BLK, PRD, PRD, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, PRD, PRD, BLK, T   }, // 5
        { BLK, PRD, PRD, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, PRD, PRD, BLK, T   }, // 6
        { BLK, PRD, LRD, LRD, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, LRD, LRD, PRD, BLK, T   }, // 7 roof
        { BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T   }, // 8
        { T,   BLK, PRD, DCY, DCY, BLK, PRD, PRD, PRD, PRD, PRD, BLK, DCY, DCY, PRD, BLK, T,   T   }, // 9 side windows
        { T,   BLK, PRD, DCY, DCY, BLK, PRD, PRD, PRD, PRD, PRD, BLK, DCY, DCY, PRD, BLK, T,   T   }, // 10
        { T,   BLK, PRD, DCY, DCY, BLK, PRD, PRD, PRD, PRD, PRD, BLK, DCY, DCY, PRD, BLK, T,   T   }, // 11
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 12
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 13
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 14
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 15
        { BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T   }, // 16
        { BLK, PRD, LRD, LRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, LRD, LRD, PRD, BLK, T   }, // 17 trunk stripe
        { BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T   }, // 18
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 19
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 20
        { T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T   }, // 21
        { T,   BLK, PRD, YLW, YLW, PRD, PRD, PRD, PRD, PRD, PRD, PRD, YLW, YLW, PRD, BLK, T,   T   }, // 22 tail-lights
        { T,   BLK, PRD, RED, RED, PRD, PRD, PRD, PRD, PRD, PRD, PRD, RED, RED, PRD, BLK, T,   T   }, // 23
        { T,   T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T,   T   }, // 24
        { T,   T,   T,   BLK, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, PRD, BLK, T,   T,   T,   T   }, // 25
        { T,   T,   T,   T,   BLK, BLK, PRD, PRD, PRD, PRD, BLK, BLK, T,   T,   T,   T,   T,   T   }, // 26 rear diffuser
        { T,   T,   T,   T,   T,   BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T,   T,   T,   T,   T   }, // 27
    };

    // -----------------------------------------------------------------------
    // PLAYER CAR: NEON PHANTOM  (18 × 28) – dark body, neon blue trim
    // -----------------------------------------------------------------------

    private static final int NPB = 0xFF0A0A1A; // neon phantom body (near-black)
    private static final int NTR = 0xFF00AAFF; // neon trim blue

    public static final int[][] PLAYER_NEON_PHANTOM = {
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T   },
        { T,   T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T,   T   },
        { T,   BLK, NPB, NPB, NTR, NTR, NPB, NPB, NPB, NPB, NPB, NPB, NTR, NTR, NPB, BLK, T,   T   },
        { BLK, NPB, NPB, NPB, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, NPB, NPB, NPB, BLK, T   },
        { BLK, NPB, NPB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, NPB, NPB, BLK, T   },
        { BLK, NPB, NPB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, NPB, NPB, BLK, T   },
        { BLK, NPB, NPB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, NPB, NPB, BLK, T   },
        { BLK, NTR, NTR, NTR, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, NTR, NTR, NTR, BLK, T   },
        { BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T   },
        { T,   BLK, NPB, DCY, DCY, BLK, NPB, NPB, NPB, NPB, NPB, BLK, DCY, DCY, NPB, BLK, T,   T   },
        { T,   BLK, NPB, DCY, DCY, BLK, NPB, NPB, NPB, NPB, NPB, BLK, DCY, DCY, NPB, BLK, T,   T   },
        { T,   BLK, NPB, DCY, DCY, BLK, NPB, NPB, NPB, NPB, NPB, BLK, DCY, DCY, NPB, BLK, T,   T   },
        { T,   BLK, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, BLK, T,   T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T   },
        { BLK, NPB, NTR, NTR, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NTR, NTR, NPB, BLK, T   },
        { BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T   },
        { T,   BLK, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, NTR, BLK, T,   T   },
        { T,   BLK, NPB, RED, RED, NPB, NPB, NPB, NPB, NPB, NPB, NPB, RED, RED, NPB, BLK, T,   T   },
        { T,   T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T,   T   },
        { T,   T,   T,   BLK, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, NPB, BLK, T,   T,   T,   T   },
        { T,   T,   T,   T,   BLK, BLK, NPB, NPB, NPB, NPB, BLK, BLK, T,   T,   T,   T,   T,   T   },
        { T,   T,   T,   T,   T,   BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T,   T,   T,   T,   T   },
    };

    // -----------------------------------------------------------------------
    // PLAYER CAR: TURBO GT  (18 × 28) – yellow/black racing stripes
    // -----------------------------------------------------------------------

    private static final int TGY = 0xFFEECC00; // turbo yellow
    private static final int TGB = 0xFF111111; // turbo black

    public static final int[][] PLAYER_TURBO_GT = {
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T   },
        { T,   T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T,   T   },
        { T,   BLK, TGY, TGY, TGB, TGB, TGY, TGY, TGY, TGY, TGY, TGB, TGB, TGY, TGY, BLK, T,   T   },
        { BLK, TGY, TGY, TGY, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, TGY, TGY, TGY, BLK, T   },
        { BLK, TGY, TGY, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TGY, TGY, BLK, T   },
        { BLK, TGY, TGY, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TGY, TGY, BLK, T   },
        { BLK, TGY, TGY, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TGY, TGY, BLK, T   },
        { BLK, TGY, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGY, BLK, T   },
        { BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T   },
        { T,   BLK, TGY, DCY, DCY, BLK, TGY, TGY, TGY, TGY, TGY, BLK, DCY, DCY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, DCY, DCY, BLK, TGY, TGY, TGY, TGY, TGY, BLK, DCY, DCY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, DCY, DCY, BLK, TGY, TGY, TGY, TGY, TGY, BLK, DCY, DCY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGB, TGB, TGY, TGY, TGY, TGY, TGY, TGB, TGB, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGB, TGB, TGY, TGY, TGY, TGY, TGY, TGB, TGB, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGB, TGB, TGY, TGY, TGY, TGY, TGY, TGB, TGB, TGY, TGY, BLK, T,   T   },
        { BLK, TGY, TGY, TGY, TGB, TGB, TGY, TGY, TGY, TGY, TGY, TGB, TGB, TGY, TGY, TGY, BLK, T   },
        { BLK, TGY, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGB, TGY, BLK, T   },
        { BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T   },
        { T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T   },
        { T,   BLK, TGY, YLW, YLW, TGY, TGY, TGY, TGY, TGY, TGY, TGY, YLW, YLW, TGY, BLK, T,   T   },
        { T,   BLK, TGY, RED, RED, TGY, TGY, TGY, TGY, TGY, TGY, TGY, RED, RED, TGY, BLK, T,   T   },
        { T,   T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T,   T   },
        { T,   T,   T,   BLK, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, TGY, BLK, T,   T,   T,   T   },
        { T,   T,   T,   T,   BLK, BLK, TGY, TGY, TGY, TGY, BLK, BLK, T,   T,   T,   T,   T,   T   },
        { T,   T,   T,   T,   T,   BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T,   T,   T,   T,   T   },
    };

    // -----------------------------------------------------------------------
    // PLAYER CAR: CYBER BEAST  (18 × 28) – green circuit-board style
    // -----------------------------------------------------------------------

    private static final int CBG = 0xFF0A2A0A; // cyber dark green
    private static final int CBL = 0xFF00FF44; // cyber neon lime

    public static final int[][] PLAYER_CYBER_BEAST = {
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T   },
        { T,   T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T,   T   },
        { T,   BLK, CBG, CBG, CBL, CBL, CBG, CBG, CBG, CBG, CBG, CBL, CBL, CBG, CBG, BLK, T,   T   },
        { BLK, CBG, CBG, CBG, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CBG, CBG, CBG, BLK, T   },
        { BLK, CBG, CBG, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, CBG, CBG, BLK, T   },
        { BLK, CBG, CBG, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, CBG, CBG, BLK, T   },
        { BLK, CBG, CBG, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, CBG, CBG, BLK, T   },
        { BLK, CBL, CBL, CBL, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, CBL, CBL, CBL, BLK, T   },
        { BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T   },
        { T,   BLK, CBG, DCY, DCY, BLK, CBG, CBG, CBG, CBG, CBG, BLK, DCY, DCY, CBG, BLK, T,   T   },
        { T,   BLK, CBG, DCY, DCY, BLK, CBG, CBG, CBG, CBG, CBG, BLK, DCY, DCY, CBG, BLK, T,   T   },
        { T,   BLK, CBG, DCY, DCY, BLK, CBG, CBG, CBG, CBG, CBG, BLK, DCY, DCY, CBG, BLK, T,   T   },
        { T,   BLK, CBL, CBL, CBL, CBL, CBL, CBG, CBG, CBL, CBL, CBL, CBL, CBL, CBL, BLK, T,   T   },
        { T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T   },
        { T,   BLK, CBG, CBG, CBL, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBL, CBG, CBG, BLK, T,   T   },
        { T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T   },
        { BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T   },
        { BLK, CBG, CBL, CBL, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBL, CBL, CBG, BLK, T   },
        { BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T   },
        { T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T   },
        { T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T   },
        { T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T   },
        { T,   BLK, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, CBL, BLK, T,   T   },
        { T,   BLK, CBG, RED, RED, CBG, CBG, CBG, CBG, CBG, CBG, CBG, RED, RED, CBG, BLK, T,   T   },
        { T,   T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T,   T   },
        { T,   T,   T,   BLK, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, CBG, BLK, T,   T,   T,   T   },
        { T,   T,   T,   T,   BLK, BLK, CBG, CBG, CBG, CBG, BLK, BLK, T,   T,   T,   T,   T,   T   },
        { T,   T,   T,   T,   T,   BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T,   T,   T,   T,   T   },
    };

    // -----------------------------------------------------------------------
    // PLAYER CAR: THUNDER X  (18 × 28) – electric blue, wide body
    // -----------------------------------------------------------------------

    private static final int TXB = 0xFF0033AA; // thunder blue
    private static final int TXL = 0xFF44AAFF; // thunder light blue

    public static final int[][] PLAYER_THUNDER_X = {
        { T,   T,   T,   BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T   },
        { T,   T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T,   T   },
        { T,   BLK, TXB, TXB, TXL, TXL, TXB, TXB, TXB, TXB, TXB, TXL, TXL, TXB, TXB, BLK, T,   T   },
        { BLK, TXB, TXB, TXB, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, TXB, TXB, TXB, BLK, T   },
        { BLK, TXB, TXB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TXB, TXB, BLK, T   },
        { BLK, TXB, TXB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TXB, TXB, BLK, T   },
        { BLK, TXB, TXB, BLK, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, CYN, BLK, TXB, TXB, BLK, T   },
        { BLK, TXL, TXL, TXL, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, BLK, TXL, TXL, TXL, BLK, T   },
        { BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T   },
        { T,   BLK, TXB, DCY, DCY, BLK, TXB, TXB, TXB, TXB, TXB, BLK, DCY, DCY, TXB, BLK, T,   T   },
        { T,   BLK, TXB, DCY, DCY, BLK, TXB, TXB, TXB, TXB, TXB, BLK, DCY, DCY, TXB, BLK, T,   T   },
        { T,   BLK, TXB, DCY, DCY, BLK, TXB, TXB, TXB, TXB, TXB, BLK, DCY, DCY, TXB, BLK, T,   T   },
        { T,   BLK, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, BLK, T,   T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T   },
        { BLK, TXB, TXL, TXL, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXL, TXL, TXB, BLK, T   },
        { BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T   },
        { T,   BLK, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, TXL, BLK, T,   T   },
        { T,   BLK, TXB, RED, RED, TXB, TXB, TXB, TXB, TXB, TXB, TXB, RED, RED, TXB, BLK, T,   T   },
        { T,   T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T,   T   },
        { T,   T,   T,   BLK, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, TXB, BLK, T,   T,   T,   T   },
        { T,   T,   T,   T,   BLK, BLK, TXB, TXB, TXB, TXB, BLK, BLK, T,   T,   T,   T,   T,   T   },
        { T,   T,   T,   T,   T,   BLK, BLK, BLK, BLK, BLK, T,   T,   T,   T,   T,   T,   T,   T   },
    };

    // -----------------------------------------------------------------------
    // TRAFFIC SIGNAL  (8 wide × 16 tall)
    // -----------------------------------------------------------------------

    public static final int[][] TRAFFIC_SIGNAL = {
        // 8 cols
        { T,   T,   BLK, BLK, BLK, BLK, T,   T   }, // 0
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 1 housing top
        { T,   BLK, RED, RED, RED, RED, BLK, T   }, // 2 red
        { T,   BLK, RED, RED, RED, RED, BLK, T   }, // 3
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 4
        { T,   BLK, YLW, YLW, YLW, YLW, BLK, T   }, // 5 yellow
        { T,   BLK, YLW, YLW, YLW, YLW, BLK, T   }, // 6
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 7
        { T,   BLK, GRN, GRN, GRN, GRN, BLK, T   }, // 8 green
        { T,   BLK, GRN, GRN, GRN, GRN, BLK, T   }, // 9
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 10 housing bottom
        { T,   T,   BLK, MGY, MGY, BLK, T,   T   }, // 11 post
        { T,   T,   BLK, MGY, MGY, BLK, T,   T   }, // 12
        { T,   T,   BLK, MGY, MGY, BLK, T,   T   }, // 13
        { T,   T,   BLK, MGY, MGY, BLK, T,   T   }, // 14
        { T,   BLK, BLK, BLK, BLK, BLK, BLK, T   }, // 15 base
    };

    // -----------------------------------------------------------------------
    // COIN PICKUP  (10 wide × 10 tall)
    // Gold coin with C (rupee-inspired)
    // -----------------------------------------------------------------------

    public static final int[][] COIN = {
        { T,   T,   GLD, GLD, GLD, GLD, GLD, GLD, T,   T   }, // 0
        { T,   GLD, GLD, GLD, GLD, GLD, GLD, GLD, GLD, T   }, // 1
        { GLD, GLD, GLD, DYL, DYL, DYL, DYL, GLD, GLD, GLD }, // 2
        { GLD, GLD, DYL, GLD, GLD, GLD, GLD, GLD, GLD, GLD }, // 3
        { GLD, GLD, DYL, GLD, GLD, GLD, GLD, GLD, GLD, GLD }, // 4
        { GLD, GLD, DYL, GLD, GLD, GLD, GLD, GLD, GLD, GLD }, // 5
        { GLD, GLD, DYL, GLD, GLD, GLD, GLD, GLD, GLD, GLD }, // 6
        { GLD, GLD, GLD, DYL, DYL, DYL, DYL, GLD, GLD, GLD }, // 7
        { T,   GLD, GLD, GLD, GLD, GLD, GLD, GLD, GLD, T   }, // 8
        { T,   T,   GLD, GLD, GLD, GLD, GLD, GLD, T,   T   }, // 9
    };

    // -----------------------------------------------------------------------
    // NITRO PICKUP  (12 wide × 12 tall)
    // Neon cyan lightning-bolt shape
    // -----------------------------------------------------------------------

    public static final int[][] NITRO_PICKUP = {
        { T,   T,   T,   T,   NIT, NIT, NIT, NIT, NIT, T,   T,   T   }, // 0
        { T,   T,   T,   NIT, NIT, NIT, NIT, NIT, T,   T,   T,   T   }, // 1
        { T,   T,   NIT, NIT, NIT, NIT, NIT, T,   T,   T,   T,   T   }, // 2
        { T,   NIT, NIT, NIT, NIT, NIT, T,   T,   T,   T,   T,   T   }, // 3
        { NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, T   }, // 4
        { NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT, NIT }, // 5
        { T,   T,   T,   T,   T,   NIT, NIT, NIT, NIT, NIT, T,   T   }, // 6
        { T,   T,   T,   T,   T,   T,   NIT, NIT, NIT, T,   T,   T   }, // 7
        { T,   T,   T,   T,   T,   T,   NIT, NIT, NIT, T,   T,   T   }, // 8
        { T,   T,   T,   T,   T,   T,   T,   NIT, NIT, T,   T,   T   }, // 9
        { T,   T,   T,   T,   T,   T,   T,   NIT, NIT, T,   T,   T   }, // 10
        { T,   T,   T,   T,   T,   T,   T,   T,   NIT, T,   T,   T   }, // 11
    };

    // -----------------------------------------------------------------------
    // Static rendering API
    // -----------------------------------------------------------------------

    /**
     * Draw a sprite centred at ({@code x}, {@code y}), scaled so that each
     * sprite-pixel occupies {@code scale × scale} screen pixels.
     *
     * @param sprite    pixel array (row-major, 0xFFRRGGBB or 0x00000000)
     * @param x         screen X of the sprite centre
     * @param y         screen Y of the sprite centre
     * @param scale     pixels per sprite-pixel (e.g. 2.0 → double-size)
     * @param tintColor if non-null, apply a 50 % colour tint to body pixels
     */
    public static void drawSprite(GraphicsContext gc,
                                   int[][] sprite,
                                   double x, double y,
                                   double scale,
                                   Color tintColor) {
        if (sprite == null || sprite.length == 0) return;
        int rows = sprite.length;
        int cols = sprite[0].length;

        double startX = x - (cols * scale) / 2.0;
        double startY = y - (rows * scale) / 2.0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int pixel = sprite[row][col];

                // Skip fully transparent pixels
                if (((pixel >> 24) & 0xFF) == 0) continue;

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8)  & 0xFF;
                int b =  pixel        & 0xFF;

                Color color = Color.rgb(r, g, b);

                // Apply tint to body pixels only (not near-black outlines, not
                // white/silver, not windows)
                if (tintColor != null) {
                    boolean isOutline  = r < 60 && g < 60 && b < 60;
                    boolean isWhite    = r > 200 && g > 200 && b > 200;
                    boolean isWindow   = b > 150 && r < 100;
                    if (!isOutline && !isWhite && !isWindow) {
                        color = color.interpolate(tintColor, 0.5);
                    }
                }

                gc.setFill(color);
                gc.fillRect(
                    startX + col * scale,
                    startY + row * scale,
                    Math.max(1, scale),
                    Math.max(1, scale));
            }
        }
    }

    // -----------------------------------------------------------------------
    // Sprite look-up helpers
    // -----------------------------------------------------------------------

    /**
     * Return the sprite array for a given traffic vehicle type.
     */
    public static int[][] getSpriteForType(VehicleType type) {
        return switch (type) {
            case AUTO_RICKSHAW -> AUTO_RICKSHAW;
            case BMTC_BUS      -> BMTC_BUS;
            case DELIVERY_BIKE -> DELIVERY_BIKE;
            case MOTORCYCLE    -> MOTORCYCLE;
            case CAR           -> CAR;
            case SUV           -> SUV;
            case TRUCK         -> TRUCK;
            case OLA_TAXI      -> OLA_TAXI;
            case NAMMA_METRO   -> NAMMA_METRO;
        };
    }

    /**
     * Return the player sprite for the given car ID string.
     * Falls back to {@link #PLAYER_STREET_RACER} for unknown IDs.
     */
    public static int[][] getPlayerSprite(String carId) {
        if (carId == null) return PLAYER_STREET_RACER;
        return switch (carId) {
            case "neon_phantom" -> PLAYER_NEON_PHANTOM;
            case "turbo_gt"     -> PLAYER_TURBO_GT;
            case "cyber_beast"  -> PLAYER_CYBER_BEAST;
            case "thunder_x"    -> PLAYER_THUNDER_X;
            default             -> PLAYER_STREET_RACER;
        };
    }
}
