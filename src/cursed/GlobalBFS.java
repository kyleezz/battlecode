package cursed;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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

    void updateGrid() throws GameActionException {
        MapLocation toCheck;
        int x, y;

        for (int i : Constants.DUCKLOCS) {
            if (rc.readSharedArray(i) == 0) continue;

            toCheck = Comms.decodeLoc(rc.readSharedArray(i));
            x = toCheck.x + 1; y = toCheck.y + 1;

            clean(x);
            grid[x][y] = updateVal(x, y);
        }
    }

    int getVal(MapLocation loc) {
        int x = loc.x + 1, y = loc.y + 1;
        clean(x);
        return grid[x][y];
    }
}