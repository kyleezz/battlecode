package stoppy;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Collections;

public class Comms extends Util {
    RobotController rc;

    Comms(RobotController rc) throws GameActionException {
        this.rc = rc;
    }

    static int encode(MapLocation loc, int val) {
        return val * 3600 + loc.x * 60 + loc.y + 1;
    }

    static MapLocation decodeLoc(int val) {
        val = (val - 1) % 3600;
        return new MapLocation(val / 60, val % 60);
    }
    
    static int decodeVal(int val) {
        return (val - 1) / 3600;
    }

    int toRemove = -1, enemyFlagRemove = -1;
    void updateComms(int x, MapLocation enemyFlag) throws GameActionException { //run once a term, preferably early on
        //x is between [0, 17]
        if (toRemove != -1) {
            rc.writeSharedArray(toRemove, 0);
            toRemove = -1;
        }
        if (enemyFlagRemove != -1) {
            rc.writeSharedArray(enemyFlagRemove, 0);
            enemyFlagRemove = -1;
        }

        if (x == -1) return;

        if (!rc.isSpawned()) return;
        int toWrite = -1;
        for (int i : Constants.DUCKLOCS) {
            if (rc.readSharedArray(i) == 0) {
                if (toRemove == -1) {
                    rc.writeSharedArray(i, encode(rc.getLocation(), x));
                    toRemove = i;
                    if (enemyFlag == null) return;
                }
                else toWrite = i;
            }
            else {
                if (enemyFlag != null && enemyFlag.equals(decodeLoc(rc.readSharedArray(i)))) {
                    enemyFlag = null;
                }
            }
        }
        if (enemyFlag != null && toWrite != -1) {
            //System.out.println("yippee");
            rc.writeSharedArray(toWrite, encode(enemyFlag, 17));
            enemyFlagRemove = toWrite;
        }
    }
}