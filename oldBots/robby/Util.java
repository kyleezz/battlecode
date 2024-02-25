package robby;

import battlecode.common.*;

public class Util {
    static RobotController rc;

    static boolean isDam(MapInfo locInfo) throws GameActionException{
        return !locInfo.isPassable() && !locInfo.isWall() && !locInfo.isWater();
    }
}