package defendyfix;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapInfo;
import battlecode.common.RobotController;

public class Util extends Globals {
    static RobotController rc;

    public static void bytecodeCheck(){
        int bytecodesLeft = Clock.getBytecodesLeft();
        rc.setIndicatorString("BC " + bytecodesLeft +"|SNo. " + bytecodeCounter + " " + destinationFlag);
        bytecodeCounter++;
    }

    public static void bytecodeCheck(String flag){
        int bytecodesLeft = Clock.getBytecodesLeft();
        rc.setIndicatorString("BC " + bytecodesLeft +"| " + flag + " " + destinationFlag);
    }

    static boolean isDam(MapInfo locInfo) throws GameActionException{
        return !locInfo.isPassable() && !locInfo.isWall() && !locInfo.isWater();
    }
}