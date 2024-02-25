package shellyminortest;

import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Util {
    static RobotController rc;

    static boolean isDam(MapInfo locInfo) throws GameActionException{
        return !locInfo.isPassable() && !locInfo.isWall() && !locInfo.isWater();
    }

    static int serializeLoc(MapLocation loc) throws GameActionException {
        return (loc.x+1) * 1000 + (loc.y+1);
    }

    static MapLocation deserializeLoc(int serialized) throws GameActionException {
        return new MapLocation((int) serialized/1000 - 1, serialized%1000 - 1);
    }

    static MapLocation getSymmetricLoc(MapLocation loc) {
        return new MapLocation(rc.getMapWidth() - loc.x - 1, rc.getMapHeight() - loc.y - 1);
    }

}