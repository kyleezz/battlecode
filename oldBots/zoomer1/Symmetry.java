package zoomer1;

import battlecode.common.*;

public class Symmetry {
    static RobotController rc;
    static CursedRandom rng;

    public Symmetry(RobotController rc) throws GameActionException{
        this.rc = rc;
        rng = new CursedRandom(rc);
    }

    int encode(int x, int y) {
        return x * 60 + y;
    }

    //0 <- unknown
    //1 <- not a wall
    //2 <- wall
    //3 <- not a wall and dam
    //4 <- wall and dam
    //5 <- team A spawnzone
    //6 <= team B spawnzone
    //3 bytes per value
    //20 values stored per long

    long[] map = new long[180];

    void write(int loc, int val) {
        map[loc / 20] |= ((long) val << ((loc % 20) * 3));
    }

    int read(int loc) {
        return (int) ((map[loc / 20] >> ((loc % 20) * 3)) % 8);
    }

    int symmetry = 0;
    int failedSymmetry[] = {0, 0, 0, 0};
    //unknonwn, flipX, flipY, flipBoth
    //unknown, vertical, horizontal, rotational

    MapLocation getSymmetricLoc(MapLocation loc) {
        if (failedSymmetry[3] == 0) return new MapLocation(Constants.mapWidth - loc.x - 1, Constants.mapHeight - loc.y - 1);
        if (failedSymmetry[2] == 0) return new MapLocation(loc.x, Constants.mapHeight - loc.y - 1);
        if (failedSymmetry[1] == 0) return new MapLocation(Constants.mapWidth - loc.x - 1, loc.y);
        return new MapLocation(Constants.mapWidth - loc.x - 1, Constants.mapHeight - loc.y - 1); //should never happen
    }
    
    void findSymmetry() throws GameActionException {
        if (symmetry != 0) return;
        rng.reset();

        int sharedSymmetry = rc.readSharedArray(Constants.SYMLOC);
        if ((sharedSymmetry & 1) != 0) failedSymmetry[1] = 1;
        if ((sharedSymmetry & 2) != 0) failedSymmetry[2] = 1;
        if ((sharedSymmetry & 4) != 0) failedSymmetry[3] = 1;

        MapLocation toCheck[] = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 20);
        MapLocation loc;
        MapInfo curInfo;
        int x, y, oppX, oppY;
        int curVal, flipX, flipY, flipBoth;

        while (Clock.getBytecodesLeft() >= 500) { //TODO: 300 should work but we can make sure in a later version
            loc = toCheck[rng.nextInt(toCheck.length)];

            x = loc.x;
            y = loc.y;
            if (read(encode(x, y)) != 0) continue;

            oppX = rc.getMapWidth() - 1 - x;
            oppY = rc.getMapHeight() - 1 - y;

            curInfo = rc.senseMapInfo(loc);
            if (curInfo.isSpawnZone()) {
                curVal = curInfo.getSpawnZoneTeam() + 4;
            }
            else {
                if (curInfo.isWall()) curVal = 2;
                else curVal = 1;

                if (curInfo.isDam()) curVal += 2;
            }

            write(encode(x, y), curVal);

            flipX = read(encode(oppX, y));
            flipY = read(encode(x, oppY));
            flipBoth = read(encode(oppX, oppY));

            if (curVal >= 5) {
                if (flipX != 0 && flipX + curVal != 11) failedSymmetry[1] = 1;
                if (flipY != 0 && flipY + curVal != 11) failedSymmetry[2] = 1;
                if (flipBoth != 0 && flipBoth + curVal != 11) failedSymmetry[3] = 1;
            }
            else {
                if (rc.getRoundNum() >= 200) {
                    if (curVal == 3 || curVal == 4) curVal -= 2; //just in case we went over a turn
                    if (flipX == 3 || flipX == 4) flipX -= 2;
                    if (flipY == 3 || flipY == 4) flipY -= 2;
                    if (flipBoth == 3 || flipBoth == 4) flipBoth -= 2;
                }

                if (flipX != 0 && flipX != curVal) failedSymmetry[1] = 1;
                if (flipY != 0 && flipY != curVal) failedSymmetry[2] = 1;
                if (flipBoth != 0 && flipBoth != curVal) failedSymmetry[3] = 1;
            }
        }

        sharedSymmetry = failedSymmetry[1] + (failedSymmetry[2] << 1) + (failedSymmetry[3] << 2);
        rc.writeSharedArray(Constants.SYMLOC, sharedSymmetry);

        if (failedSymmetry[1] + failedSymmetry[2] + failedSymmetry[3] == 2) {
            if (failedSymmetry[1] == 0) symmetry = 1;
            if (failedSymmetry[2] == 0) symmetry = 2;
            if (failedSymmetry[3] == 0) symmetry = 3;
        }
    }
}