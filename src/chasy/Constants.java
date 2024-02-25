package chasy;

import battlecode.common.Direction;

public class Constants {

    static int FIND_CRUMBS = 140;
    static int DEFEND_RADIUS = 100;
    static int ATTACK_RADIUS = 4;
    static int LOW_HEALTH_RETREAT = 400;
    static int RETREAT_ENEMY_RADIUS = 16;
    static int IDEAL_HEALTH = 800;
    static int MIN_TEAM_SIZE = 0;
    static int ESCORT_SIZE = 3;
    static int ESCORT_DISTANCE = 10;
    static int CLOSE_FRIEND_RADIUS = 10;
    static int CLOSE_FRIEND_SIZE = 5;

    static int mapWidth = -1;
    static int mapHeight = -1;

    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
            Direction.CENTER
    };

    //Stored array index locations
    static int BUILDERCOUNT = 0;
    static int[] BASELOCINDEX = {1, 2, 3};
    static int SYMLOC = 4;
    static int FLAGLOC = 5;

    static int[] ENEMYLOCS = {6, 7, 8, 9, 10, 11};
    static int[] DUCKLOCS = {12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
    static int[] MAINLOCS =  {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
    //if you need locations, remove 2 from DUCKLOCS and MAINLOCS

    static final int[] ATTACK_POWER = {
            150,
            158,
            161,
            165,
            195,
            203,
            240
    };

    static final int[] UPGRADED_ATTACK_POWER = {
        210,
        221,
        225,
        231,
        273,
        284,
        336
    };

    static final int[] BUILD_LEVEL_COOLDOWNS = {
        5,
        5,
        5,
        4,
        4,
        4,
        3
    };
}
