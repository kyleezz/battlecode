package boomybeforesurgery;

import battlecode.common.*;

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

    static int getRobotAttackDamage(RobotInfo robot) throws GameActionException {
        GlobalUpgrade[] upgrades = rc.getGlobalUpgrades(robot.getTeam());
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == GlobalUpgrade.ATTACK || upgrades[i] == GlobalUpgrade.ACTION) return Constants.UPGRADED_ATTACK_POWER[robot.getAttackLevel()];
        }
        return Constants.ATTACK_POWER[robot.getAttackLevel()];
    }
}