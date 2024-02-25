package alphamovement;

import battlecode.common.Clock;
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
}