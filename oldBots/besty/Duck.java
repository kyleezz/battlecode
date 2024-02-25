package besty;

import battlecode.common.*;

public abstract class Duck {

    static RobotController rc;

    static CursedRandom rng;
    static Movement movement;
    static Action action;
    static Symmetry symmetry;
    static Micro micro;

    int creationRound;

    public abstract void run() throws GameActionException;

    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        action = new Action(rng, rc);
        symmetry = new Symmetry(rc);
        movement = new Movement(symmetry, rc);
        Constants.mapHeight = rc.getMapHeight();
        Constants.mapWidth = rc.getMapWidth();
    }

    MapLocation findCrumb(int radius) throws GameActionException {
        MapLocation[] crumbs = rc.senseNearbyCrumbs(radius);
        int closest = 1000000;
        MapLocation crumb = null;
        for (int i = 0; i < crumbs.length; i++) {
            int dist = rc.getLocation().distanceSquaredTo(crumbs[i]);
            if (dist < closest) {
                closest = dist;
                crumb = crumbs[i];
            }
        }
        return crumb;
    }

    MapLocation findNearestBroadcast() {
        int closest = 1000000;
        MapLocation closestBroadcast = null;
        MapLocation[] broadcasts = rc.senseBroadcastFlagLocations();

        for (int i = 0; i < broadcasts.length; i++) {
            int dist = rc.getLocation().distanceSquaredTo(broadcasts[i]);
            if (dist < closest) {
                closest = dist;
                closestBroadcast = broadcasts[i];
            }
        }

        return closestBroadcast;
    }

    MapLocation getClosestBase() throws GameActionException {
        MapLocation closestBase = null;
        int closest = 1000000;
        for (int i = 0; i < 3; i++) {
            MapLocation base = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[i]));
            int dist = rc.getLocation().distanceSquaredTo(base);
            if (dist < closest) {
                closest = dist;
                closestBase = base;
            }
        }
        return closestBase;
    }

    void recordBaseLoc() throws GameActionException {
        if (rc.senseNearbyRobots(30).length == 0) {
            FlagInfo[] nearFlags = rc.senseNearbyFlags(34, rc.getTeam());
            for (int i = 0; i < 3; i++) {
                if (rc.readSharedArray(Constants.BASELOCINDEX[i]) == 0) {
                    rc.writeSharedArray(Constants.BASELOCINDEX[i], Util.serializeLoc(nearFlags[0].getLocation()));
                    break;
                }
            }
        }
    }

    MapLocation findSpawnLocation() throws GameActionException {
        int randomStart = rng.nextInt() % rc.getID();
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        for (int i = 0; i < 27; i++) {
            if (rc.canSpawn(spawnLocs[(randomStart + i) % 27])) return spawnLocs[(randomStart + i) % 27];
        }
        return null;
    }
}
