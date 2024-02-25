package sleepyoptimisation;

import battlecode.common.*;

public abstract class Duck {

    static RobotController rc;

    static CursedRandom rng;
    static Movement movement;
    static Symmetry symmetry;
    static Comms comms;
    static GlobalBFS globalbfs;
    static int creationRound;
    static MapLocation[] baseLocs;
    static FlagInfo[] enemyFlags;

    public abstract void run() throws GameActionException;

    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        symmetry = new Symmetry(rc);
        comms = new Comms(rc);
        globalbfs = new GlobalBFS(rc);
        movement = new Movement(globalbfs, symmetry, rc);
        Constants.mapHeight = rc.getMapHeight();
        Constants.mapWidth = rc.getMapWidth();
    }

    void recordBaseLoc() throws GameActionException {
        if (rc.senseNearbyRobots(-1).length == 0) {
            FlagInfo[] nearFlags = rc.senseNearbyFlags(34, rc.getTeam());
            for (int i = 0; i < 3; i++) {
                if (rc.readSharedArray(Constants.BASELOCINDEX[i]) == 0) {
                    rc.writeSharedArray(Constants.BASELOCINDEX[i], Util.serializeLoc(nearFlags[0].getLocation()));
                    break;
                }
            }
        }
    }

    public void getBaseLocs() throws GameActionException {
        baseLocs = new MapLocation[3];
        baseLocs[0] = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[0]));
        baseLocs[1] = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[1]));
        baseLocs[2] = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[2]));
    }

    public static void tryUpgrade() throws GameActionException {
        if (rc.canBuyGlobal(GlobalUpgrade.ATTACK)) {
            rc.buyGlobal(GlobalUpgrade.ATTACK);
        } 
        if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        } 
        if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
            rc.buyGlobal(GlobalUpgrade.HEALING);
        } 
    }

    public MapLocation findNearestCrumb(int radius) throws GameActionException {
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

    public MapLocation findNearestEnemyFlag() throws GameActionException {
        int closest = 1000000;
        MapLocation closestFlag = null;

        FlagInfo[] seenFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());

        // check if there is flag in vision
        for (int i = 0; i < seenFlags.length; i++) {
            if (seenFlags[i].isPickedUp()) continue;
            int dist = seenFlags[i].getLocation().distanceSquaredTo(rc.getLocation());
            if(dist < closest) {
                closest = dist;
                closestFlag = seenFlags[i].getLocation();
            }
        }

        if (closestFlag != null) return closestFlag;
        
        // check broadcasts
        MapLocation[] broadcasts = rc.senseBroadcastFlagLocations();
        for (int i = 0; i < broadcasts.length; i++) {
            MapLocation broadcastLoc = broadcasts[i];
            int closestBase = 1000000;
            MapLocation base = null;
            for (int j = 0; j < 3; j++) {
                MapLocation enemyBase = symmetry.getSymmetricLoc(baseLocs[j]);
                int baseDist = enemyBase.distanceSquaredTo(broadcastLoc);
                if (baseDist < closestBase) {
                    closestBase = baseDist;
                    base = enemyBase;
                }
            }
            if (closestBase <= 100){
                broadcastLoc = base;
            }
            int dist = rc.getLocation().distanceSquaredTo(broadcastLoc);
            if (dist < closest) {
                closest = dist;
                closestFlag = broadcastLoc;
            }
        }

        return closestFlag;
    }

    MapLocation findNearestEnemyBase() {
        int closest = 1000000;
        MapLocation closestBase = null;
        for (int j = 0; j < 3; j++) {
            MapLocation enemyBase = symmetry.getSymmetricLoc(baseLocs[j]);
            int dist = enemyBase.distanceSquaredTo(rc.getLocation());
            if (dist < closest) {
                closest = dist;
                closestBase = enemyBase;
            }
        }

        return closestBase;
    }

    public MapLocation findSpawnLocation(boolean goHelp) throws GameActionException {
        int randomStart = rng.nextInt() % rc.getID();
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        int bestHelp = 1000000;
        MapLocation bestSpawn = null;
        MapLocation randomSpawn = null;
        for (int i = 0; i < 27; i++) {
            MapLocation spawnLoc = spawnLocs[(randomStart + i) % 27];
            if (!rc.canSpawn(spawnLoc)) continue;
            if (randomSpawn == null) randomSpawn = spawnLoc;
            if (goHelp) {
                for (int j = 0; j < 3; j++) {
                    if (rc.readSharedArray(Constants.BASELOCINDEX[j]) == 0) continue; 
                    int dist = spawnLoc.distanceSquaredTo(baseLocs[j]);
                    if (dist < bestHelp) {
                        bestHelp = dist;
                        bestSpawn = spawnLoc;
                    }
                }
            } else {
                return spawnLoc;
            }
        }
        if (bestSpawn == null) return randomSpawn;
        return bestSpawn;
    }

    public MapLocation findNearestFriendlyBase() throws GameActionException {
        MapLocation closestBase = null;
        int closest = 1000000;
        for (int i = 0; i < 3; i++) {
            int dist = rc.getLocation().distanceSquaredTo(baseLocs[i]);
            if (dist < closest) {
                closest = dist;
                closestBase = baseLocs[i];
            }
        }
        return closestBase;
    }
}
