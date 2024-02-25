package kittyjunior;

import battlecode.common.*;

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

    static int serializeLoc(MapLocation loc) throws GameActionException {
        return (loc.x+1) * 1000 + (loc.y+1);
    }

    static MapLocation deserializeLoc(int serialized) throws GameActionException {
        return new MapLocation((int) serialized/1000 - 1, serialized%1000 - 1);
    }

    static MapLocation getSymmetricLoc(MapLocation loc) {
        return new MapLocation(Constants.mapWidth - loc.x - 1, Constants.mapHeight - loc.y - 1);
    }

    
}