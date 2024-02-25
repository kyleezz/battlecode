package attacktryagain;

import battlecode.common.*;

import java.util.ArrayList;

public class Communication {

    RobotController rc;

    MapLocation[] HQs = new MapLocation[3];
    boolean done = false;
    public Communication(RobotController rc) {
        this.rc = rc;
    }

    void findOurSpawns() throws GameActionException {
        if (HQs[0] != null) return;
        if (rc.readSharedArray(0) != 0) {
            if (HQs[0] != null) return;
            for (int i = 0; i < 3; i++) {
                int temp = rc.readSharedArray(i);
                HQs[i] = new MapLocation((temp >> 1) & (0b111111), (temp >> 7) & (0b111111));
            }
            return;
        }

        MapLocation[] spawnLocs = rc.getAllySpawnLocations();

        MapLocation spawn1 = null, spawn2 = null, spawn3 = null;
        int x1 = 0, x2 = 0, x3 = 0, y1 = 0, y2 = 0, y3 = 0;
        for (int i = 0; i < 27; i++) {
            if (spawn1 == null) {
                spawn1 = spawnLocs[i];
                x1 += spawnLocs[i].x;
                y1 += spawnLocs[i].y;
                continue;
            }
            if (spawnLocs[i].distanceSquaredTo(spawn1) <= 8) {
                x1 += spawnLocs[i].x;
                y1 += spawnLocs[i].y;
                continue;
            }

            if (spawn2 == null) {
                spawn2 = spawnLocs[i];
                x2 += spawnLocs[i].x;
                y2 += spawnLocs[i].y;
                continue;
            }
            if (spawnLocs[i].distanceSquaredTo(spawn2) <= 8) {
                x2 += spawnLocs[i].x;
                y2 += spawnLocs[i].y;
                continue;
            }

            if (spawn3 == null) {
                spawn3 = spawnLocs[i];
                x3 += spawnLocs[i].x;
                y3 += spawnLocs[i].y;
                continue;
            }
            if (spawnLocs[i].distanceSquaredTo(spawn3) <= 8) {
                x3 += spawnLocs[i].x;
                y3 += spawnLocs[i].y;
                continue;
            }
        }

        done = true;
        rc.writeSharedArray(0, 1 + (1 << 1) * x1 + (1 << 7) * y1);
        rc.writeSharedArray(1, 1 + (1 << 1) * x2 + (1 << 7) * y2);
        rc.writeSharedArray(2, 1 + (1 << 1) * x3 + (1 << 7) * y3);
        HQs[0] = new MapLocation(x1, y1);
        HQs[1] = new MapLocation(x2, y2);
        HQs[2] = new MapLocation(x3, y3);
    }
}
