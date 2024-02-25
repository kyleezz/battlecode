package plswork;

import battlecode.common.*;

public class NormalDuck extends Duck {

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    MapLocation target = null;
    MapLocation mySpawnLoc = null;

    RobotInfo[] friends;
    RobotInfo[] enemies;

    Communication communication;

    public void run() throws GameActionException {
        //if (rc.getRoundNum() >= 500) rc.resign();

        if (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();

            communication.findOurSpawns();

            int startSpawnLoc = rng.nextInt();
            for (int i = 0; i < 27; i++) {
                MapLocation randomLoc = spawnLocs[(startSpawnLoc+i) % 27];
                if (rc.canSpawn(randomLoc)) {
                    rc.spawn(randomLoc);
                    mySpawnLoc = randomLoc;
                    break;
                }
            }
        }

        if (!rc.isSpawned()) return;

        if (rc.getRoundNum() <= Constants.STAGEONE) {
            if (target == null || target.equals(rc.getLocation())) {
                MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
                if (crumbs.length > 0) {

                    int closest = -1;
                    int smolDist = 10000000;
                    for (int i = 0; i < crumbs.length; i++) {
                        if (rc.getLocation().distanceSquaredTo(crumbs[i]) < smolDist) {
                            smolDist = rc.getLocation().distanceSquaredTo(crumbs[i]);
                            closest = i;
                        }
                    }

                    target = crumbs[closest];
                    rc.setIndicatorString("crumbs");
                    movement.moveTo(target, true);
                } else {
                    target = null;
                    movement.spreadOut();
                }
            } else {
                movement.moveTo(target, true);
            }
            symmetry.findSymmetry();

            return;
        }
        else if (rc.getRoundNum() <= Constants.STAGETWO) {
            movement.moveTo(mySpawnLoc, true);
            if (rc.getLocation().distanceSquaredTo(mySpawnLoc) < 4)
            action.tryPlaceTrap(TrapType.STUN, rc.getLocation());
            symmetry.findSymmetry();

            return;
            // movement.moveToBroadcast();
            // if (rc.isActionReady()) {
            //     for (Direction direction : movement.directions) {
            //         MapLocation newLoc = rc.getLocation().add(direction);
            //         if (rc.canSenseLocation(newLoc) && Util.isDam(rc.senseMapInfo(newLoc))) {
            //             Action.tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation());
            //             return;
            //         }
            //     }
            // }
            // return;

        }

        // After Round 200

        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        action.friends = friends;
        action.enemies = enemies;
        
        if (target == null || target.equals(rc.getLocation())) {
            // moving to opposite wall?
            target = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
        }

        MapLocation bestie = null;
        int mini = rc.getID();
        int lowerCount = 0;

        for (RobotInfo friend : friends) {
            if (friend.hasFlag) {
                int val = rc.getLocation().distanceSquaredTo(friend.location) - 1000000;
                if (val < mini) {
                    bestie = friend.location;
                    mini = val;
                }
            }
            if (friend.ID < rc.getID()) lowerCount++;
            if (friend.ID < mini) {
                bestie = friend.location;
                mini = friend.ID;
            }
        }

        FlagInfo[] flags = rc.senseNearbyFlags(2, rc.getTeam().opponent());
        for (FlagInfo flag : flags) {
            if (!flag.isPickedUp() && rc.canPickupFlag(flag.getLocation())) {
                rc.pickupFlag(flag.getLocation());
                break;
            }  
        }

        if (rc.hasFlag()) {
            MapLocation nearestspawn = action.findRetreat();
            if (rc.getLocation().distanceSquaredTo(nearestspawn) <= 2 && rc.canDropFlag(nearestspawn)) rc.dropFlag(nearestspawn);
            else {
                movement.moveTo(nearestspawn, true);
            }
        }

        action.attackHeal();

        if (bestie != null && lowerCount < Constants.SWARMSIZE && rc.getLocation().distanceSquaredTo(bestie) > 2) {
            movement.moveTo(bestie, true);
        }
        else {
            MapLocation[] locs = communication.HQs;
            MapLocation[] enemylocs = new MapLocation[3];
            for (int i = 1; i < 4; i++) {
                if (symmetry.failedSymmetry[i] != 0) {
                    if (i == 1) {
                        for (int j = 0; j < 3; j++) enemylocs[j] = getVSym(locs[j]);
                    } else if (i == 2) {
                        for (int j = 0; j < 3; j++) enemylocs[j] = getHSym(locs[j]);
                    } else {
                        for (int j = 0; j < 3; j++) enemylocs[j] = getRSym(locs[j]);
                    }
                    break;
                }
            }

            movement.moveTo(enemylocs[0], true);
//            if (rc.getRoundNum() < 300) {
//
//                movement.moveToBroadcast();
//            } else {
//                movement.moveTo(movement.saved, true);
//
//            }

//            MapLocation tmp = action.chaseOrRetreat();
//            if (tmp != null) movement.moveTo(tmp, true);
//            else {
//                movement.moveTo(movement.saved, true);
//                movement.moveToBroadcast();
//                if (lowerCount < Constants.SWARMSIZE && friends.length >= Constants.SWARMSIZE - 3) movement.moveToBroadcast();
//                else movement.moveTo(action.findRetreat(), true);
                //else movement.moveRandom();

        }

        //action.attackHealFill();
        action.attackHeal();

        symmetry.findSymmetry();
    }

    MapLocation getHSym(MapLocation a) {
        return new MapLocation(a.x, rc.getMapHeight() - a.y - 1);
    }

    MapLocation getVSym(MapLocation a) {
        return new MapLocation(rc.getMapWidth() - a.x - 1, a.y);
    }

    MapLocation getRSym(MapLocation a) {
        return new MapLocation(rc.getMapWidth() - a.x - 1, rc.getMapHeight() - a.y - 1);
    }
    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);

        communication = new Communication(rc);
    }
}
