package lockier;

import battlecode.common.Direction;

public class Constants {
    static int SWARMSIZE = 3;
    static int RETREATSIZE = -1;

    static int HEALTHWEIGHT = 1;
    static int ATTACKWEIGHT = 0;

    static int ATTACK_RADIUS = 4;
    static int HEAL_RADIUS = 4;
    static int VISION_RADIUS = 20;
    static int MIN_HEALED_HP = 150;

    static int mapWidth = -1;
    static int mapHeight = -1;

    static double ATTACKENEMYRATIO = 1.4;
    static int DEFENSIVESTATERADIUS = 100;
    static int MINCRUMBS = 500;

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    //Stored array index locations
    static int BUILDERCOUNT = 0;
    static int[] BASELOCINDEX = {1, 2, 3};
    static int[] GOTENEMYFLAGLOC = {4, 5, 6};
    static int SYMLOC = 7;
    static int FLAGLOC = 8;
    static int[] BFSINDEX = {12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
    //delete ones that you need, this should be more than enough

    static final int[] attackLevels = {
        150,
        158,
        165,
        173,
        180,
        195,
        225
    };

    static final int[] healLevels = {
        80,
        83,
        84,
        86,
        88,
        92,
        100
    };
}
