package kity;

import battlecode.common.*;

public class Constants {

    static int mapWidth = -1;
    static int mapHeight = -1;

    static int stageOne = 50;
    static int stageTwo = 180;

    static int SWARMSIZE = 15;
    static int RETREATSIZE = 3;
    
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
    
}
