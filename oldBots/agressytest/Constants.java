package agressytest;

import battlecode.common.Direction;

public class Constants {
    
    static int mapWidth = -1;
    static int mapHeight = -1;

    static int stageOne = 130;
    static int stageTwo = 200;

    static int SWARMSIZE = 3;
    static int RETREATSIZE = -1;

    static int HEALTHWEIGHT = 1;
    static int ATTACKWEIGHT = 0;

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

    static int[] BASELOCINDEX = {0, 1, 2};
    static int[] TARGETENEMYBASEINDEX = {3, 4};
    static int[] GOTBREADINDEX = {5, 6, 7};
    static int[] HELPINDEX = {8, 9, 10};
    //Stored array index locations
    static int SYMLOC = 11;
    static int FLAGLOC = 12;
}
