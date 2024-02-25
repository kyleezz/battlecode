package besty;

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
        return (loc.x+1) * 1000 + (loc.y+1)*10;
    }

    static MapLocation deserializeLoc(int serialized) throws GameActionException {
        return new MapLocation((int) serialized/1000 - 1, (serialized%1000)/10 - 1);
    }

    static int serializeTimedLoc(MapLocation loc) throws GameActionException {
        return serializeLoc(loc) + (rc.getRoundNum() % 10);
    }

    static MapLocation deserializeTimedLoc(int serializeLoc) throws GameActionException {
        int num = serializeLoc % 10;
        if (serializeLoc == 0 || (rc.getRoundNum() - num) % 10 > 1) return null;
        return deserializeLoc(serializeLoc);
    }
}