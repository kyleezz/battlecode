package lockier;

import battlecode.common.*;

public class BuilderDuck extends Duck {
    
    static int FINDFARMSPOT = 10;
    static int STARTATTACK = 160;
    
    static RobotInfo[] friends;
    static RobotInfo[] enemies;

    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;
    static MapInfo[] surroundings;

    MapLocation target = null;

    static int builderID;
    static int roundNum;
    static int placedTraps = 0;

    Direction[] dirs = {
        Direction.NORTHEAST,
        Direction.SOUTHEAST,
        Direction.SOUTHWEST,
        Direction.NORTHWEST,
    };

    public void run() throws GameActionException {
        if (!rc.isSpawned()) {
            if (rc.getRoundNum() > 100) {
                MapLocation spawnLoc = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[builderID]));
                if (rc.canSpawn(spawnLoc)) {
                    rc.spawn(spawnLoc);
                    if (rc.senseNearbyFlags(3, rc.getTeam()).length != 0) {
                        for (int i = 0; i < 4; i++) {
                            if (action.tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation().add(dirs[i]))) {
                                placedTraps += 1;
                            }
                        }
                    }
                }
            }
            MapLocation spawnLoc = findSpawnLocation();
            if (spawnLoc == null) return;
            rc.spawn(spawnLoc);
        }

        roundNum = rc.getRoundNum();
        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        action.friends = friends;
        action.enemies = enemies;
        micro.friends = friends;
        micro.enemies = enemies;
        enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        friendlyFlags = rc.senseNearbyFlags(-1, rc.getTeam());
        micro.enemyFlags = enemyFlags;
        micro.friendlyFlags = friendlyFlags;
        micro.nearestSpawn = getClosestBase();
        target = findNearestBroadcast();
        micro.flagTarget = target;
        surroundings = rc.senseNearbyMapInfos(-1);
        micro.surroundings = surroundings;

        if (rc.getRoundNum() == 1) recordBaseLoc();

        if (roundNum < FINDFARMSPOT) {
            movement.spreadOut();
            return;
        }

        if (roundNum < STARTATTACK && rc.getExperience(SkillType.BUILD) < 25) {
            if (roundNum%2 == 0) {
                // try fill
                action.dig();
            } else {
                // try dig
                action.fill();
            }
            return;
        } 

        if (roundNum > STARTATTACK && placedTraps >= 4) {
//            action.fill();
            if(!micro.play()) {
                if (target == null) {
                    // move to nearest duck with flag
                } else {
//                    movement.moveTo(target, true);
//                    if (roundNum % ((int)Math.max(rc.getMapHeight(), rc.getMapWidth())/6) == 0) {
//                        action.tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation());
//                    }
                }
            }
            return;
        }

        target = Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[builderID]));
        movement.moveTo(target, true);
        if (rc.getLocation().equals(target)) {
            for (int i = 0; i < 4; i++) {
                if (action.tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation().add(dirs[i]))) {
                    placedTraps += 1;
                }
            }
        }        
    }

    public void bestieMove() throws GameActionException {
        MapLocation bestie = null;
        int mini = rc.getID();
        int lowerCount = 0;

        for (RobotInfo friend : friends) {
            if (friend.hasFlag) {
                bestie = friend.location;
                mini = -100000;
                continue;
            }
            if (friend.ID < rc.getID()) lowerCount++;
            if (friend.ID < mini) {
                bestie = friend.location;
                mini = friend.ID;
            }
        }

        if (bestie != null && rc.getLocation().distanceSquaredTo(bestie) > 2 && lowerCount < Constants.SWARMSIZE) {
            movement.moveTo(bestie, true);
        }
        else {
            movement.moveToBroadcast();
        }
    }


    public BuilderDuck(RobotController rc) throws GameActionException {
        super(rc);
        builderID = rc.readSharedArray(Constants.BUILDERCOUNT);
        rc.writeSharedArray(Constants.BUILDERCOUNT, builderID+1);
        micro = new Micro(movement, rc, true);
    }
}
