package spiky;

import battlecode.common.*;

public class Constants {
    static int SWARMSIZE = 1000;
    static int RETREATSIZE = -1;

    static int STAGEONE = 150;
    static int STAGETWO = 150;

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
}
