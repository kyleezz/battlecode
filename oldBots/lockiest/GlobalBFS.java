package lockiest;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Collections;

public class GlobalBFS extends Util {
    RobotController rc;
    int[][] grid;

    void clean(int x) {
        if (grid[x] == null) grid[x] = new int[rc.getMapHeight() + 2];
    }

    GlobalBFS(RobotController rc) throws GameActionException {
        this.rc = rc;
        grid = new int[rc.getMapWidth() + 2][];

        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        int x, y;
        for (MapLocation spawnLoc : spawnLocs) {
            x = spawnLoc.x + 1; y = spawnLoc.y + 1;
            clean(x);
            grid[x][y] = -1000000;
        }
    }

    int updateVal(int x, int y) {
        clean(x - 1); clean(x); clean(x + 1);
        int mini = grid[x][y] - 1;

        if (grid[x - 1][y - 1] < mini) mini = grid[x - 1][y - 1];
        if (grid[x - 1][y] < mini) mini = grid[x - 1][y];
        if (grid[x - 1][y + 1] < mini) mini = grid[x - 1][y + 1];

        if (grid[x][y - 1] < mini) mini = grid[x][y - 1];
        if (grid[x][y + 1] < mini) mini = grid[x][y + 1];

        if (grid[x + 1][y - 1] < mini) mini = grid[x + 1][y - 1];
        if (grid[x + 1][y] < mini) mini = grid[x + 1][y];
        if (grid[x + 1][y + 1] < mini) mini = grid[x + 1][y + 1];

        return mini + 1;
    }

    int toRemove = -1;

    void updateGrid() throws GameActionException { //should only be run once a turn
        if (toRemove != -1) {
            rc.writeSharedArray(toRemove, 0);
            toRemove = -1;
        }

        MapLocation toCheck;
        int x, y;
        int toWrite = -1;

        for (int i : Constants.BFSINDEX) {
            if (rc.readSharedArray(i) == 0) {
                toWrite = i;
                continue;
            }

            toCheck = Util.deserializeLoc(rc.readSharedArray(i));
            x = toCheck.x + 1; y = toCheck.y + 1;

            clean(x);
            grid[x][y] = updateVal(x, y);
        }

        if (toWrite != -1) {
            if (!rc.isSpawned()) return;
            x = rc.getLocation().x + 1; y = rc.getLocation().y + 1;

            clean(x);
            int tmp = updateVal(x, y);
            if (grid[x][y] != tmp) {
                grid[x][y] = tmp;
                rc.writeSharedArray(toWrite, Util.serializeLoc(rc.getLocation()));
                toRemove = toWrite;
            }
        }

        if (rc.getRoundNum() < 200) {
            for (int i : Constants.BFSINDEX) {
                if (rc.readSharedArray(i) == 0) {
                    continue;
                }

                toCheck = Util.deserializeLoc(rc.readSharedArray(i));
                x = toCheck.x + 1; y = toCheck.y + 1;

                clean(x);
                grid[x][y] = updateVal(x, y);
            }
        }
    }

    int getVal(MapLocation loc) {
        int x = loc.x + 1, y = loc.y + 1;
        clean(x);
        return grid[x][y];
    }
}