package besty;

import battlecode.common.Direction;

public class Constants {
    static int SWARMSIZE = 3;
    static int RETREATSIZE = -1;

    static int HEALTHWEIGHT = 1;
    static int ATTACKWEIGHT = 0;

    static int ATTACK_RADIUS = 8;
    static int HEAL_RADIUS = 8;
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
    static int[] FLAGHELPLOC = {9, 10, 11};

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
