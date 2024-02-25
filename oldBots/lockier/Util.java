package lockier;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Util extends Globals {
    static RobotController rc;

    static boolean disableCheck = true;

    public static void bytecodeCheck(){
        if (disableCheck) return;
        int bytecodesLeft = Clock.getBytecodesLeft();
        rc.setIndicatorString("BC " + bytecodesLeft +"|SNo. " + bytecodeCounter + " " + destinationFlag);
        bytecodeCounter++;
    }

    public static void bytecodeCheck(String flag){
        if (disableCheck) return;
        int bytecodesLeft = Clock.getBytecodesLeft();
        rc.setIndicatorString("BC " + bytecodesLeft +"| " + flag + " " + destinationFlag);
    }

    static int serializeLoc(MapLocation loc) throws GameActionException {
        return (loc.x+1) * 1000 + (loc.y+1)*10;
    }

    static MapLocation deserializeLoc(int serialized) throws GameActionException {
        return new MapLocation((int) serialized/1000 - 1, (serialized%1000)/10 - 1);
    }
}