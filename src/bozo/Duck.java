package bozo;

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
    static MapLocation goToHelpLoc;

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
        if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
            rc.buyGlobal(GlobalUpgrade.HEALING);
        } 
        if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        } 
    }

    public MapLocation findNearestCrumb(int radius) throws GameActionException {
        MapLocation[] crumbs = rc.senseNearbyCrumbs(radius);
        int closest = 1000000;
        MapLocation crumb = null;
        for (int i = 0; i < crumbs.length; i++) {
            if (rc.senseMapInfo(crumbs[i]).isWall()) continue;
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
                if (baseLocs[j] == null) continue;
                MapLocation enemyBase = symmetry.getSymmetricLoc(baseLocs[j]);
                if (rc.getLocation().distanceSquaredTo(enemyBase) <= 20) continue;
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

    public MapLocation getRandomSpawn() throws GameActionException {
        int randomStart = rng.nextInt() % rc.getID();
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        for (int i = 0; i < 27; i++) {
            MapLocation spawnLoc = spawnLocs[(randomStart + i) % 27];
            if (!rc.canSpawn(spawnLoc)) continue;
            return spawnLoc;
        }
        return null;
    }

    public MapLocation findSpawnLocation(boolean goHelp) throws GameActionException {

        if (!goHelp) return getRandomSpawn();

        //for (MapLocation m :baseLocs) if (rc.canSpawn(m)) return m;

        int randomStart = rng.nextInt() % rc.getID();
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        int bestHelp = 1000000;
        MapLocation bestSpawn = null;
        
        if (rc.readSharedArray(Constants.BASELOCINDEX[0]) + rc.readSharedArray(Constants.BASELOCINDEX[1]) + rc.readSharedArray(Constants.BASELOCINDEX[1]) != 0) {
            for (int i = 0; i < 27; i++) {
                MapLocation spawnLoc = spawnLocs[(randomStart + i) % 27];
                if (!rc.canSpawn(spawnLoc)) continue;
                for (int j = 0; j < 3; j++) {
                    if (rc.readSharedArray(Constants.BASELOCINDEX[j]) == 0) continue;
                    int dist = spawnLoc.distanceSquaredTo(baseLocs[j]);
                    if (dist < bestHelp) {
                        bestHelp = dist;
                        bestSpawn = spawnLoc;
                    }
                }
            }
        }
        if (bestSpawn == null) {
            MapLocation spawnNear = findBestHelpLocation();
            if (spawnNear != null) {
                if (rc.canSpawn(spawnNear)) return spawnNear;
                for (Direction dir : Constants.directions) {
                    if (rc.canSpawn(spawnNear.add(dir))) return spawnNear.add(dir);
                }
            }
        } else {
            return bestSpawn;
        }
        return null;
    }

    public MapLocation findBestHelpLocation() throws GameActionException{
        float bestHelpVal = -1;
        MapLocation bestSpawnPoint = null;

        for (int duckIndex : Constants.DUCKLOCS) {
            if (rc.readSharedArray(duckIndex) == 0) continue;
            MapLocation helpLoc = Comms.decodeLoc(rc.readSharedArray(duckIndex));
            float helpVal = Comms.decodeVal(rc.readSharedArray(duckIndex));
            int closest = baseLocs[0].distanceSquaredTo(helpLoc);
            MapLocation closestBase = baseLocs[0];
            if (baseLocs[1].distanceSquaredTo(helpLoc) < closest) {
                closest = baseLocs[1].distanceSquaredTo(helpLoc);
                closestBase = baseLocs[1];
            }
            if (baseLocs[2].distanceSquaredTo(helpLoc) < closest) {
                closest = baseLocs[2].distanceSquaredTo(helpLoc);
                closestBase = baseLocs[2];
            }
            if (helpVal/closest > bestHelpVal) {
                bestSpawnPoint = closestBase;
                bestHelpVal = helpVal/closest;
                goToHelpLoc = helpLoc;
            }
        }

        return bestSpawnPoint;
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
