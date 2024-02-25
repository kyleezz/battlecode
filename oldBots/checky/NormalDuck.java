package checky;

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

    public void run() throws GameActionException {
        //if (rc.getRoundNum() >= 500) rc.resign();

        if (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
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
                    movement.moveTo(target);
                } else {
                    target = null;
                    movement.spreadOut();
                }
            } else {
                movement.moveTo(target);
            }
            return;
        }
        else if (rc.getRoundNum() <= Constants.STAGETWO) {
            if (rc.getLocation().distanceSquaredTo(mySpawnLoc) > Constants.DEFENDRADIUS) movement.moveTo(mySpawnLoc);
            else movement.moveRandom();
        }
        
        for (Direction direction : Direction.allDirections()) {
            MapLocation newLoc = rc.getLocation().add(direction);
            if (!rc.canBuild(TrapType.STUN, newLoc)) continue;
            if ((newLoc.x + newLoc.y) % 2 == 0) continue;
            if (newLoc.distanceSquaredTo(mySpawnLoc) > Constants.DEFENDRADIUS) continue;
            rc.build(TrapType.STUN, newLoc);
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
            else movement.moveTo(nearestspawn);
        }

        action.attack();

        if (bestie != null && lowerCount < Constants.SWARMSIZE && rc.getLocation().distanceSquaredTo(bestie) > 2) {
            movement.moveTo(bestie);
        }
        else {
            MapLocation tmp = action.chaseOrRetreat();
            if (tmp != null) movement.moveTo(tmp);
            else {
                if (lowerCount < Constants.SWARMSIZE && friends.length >= Constants.RETREATSIZE) movement.moveToBroadcast();
                else movement.moveTo(action.findRetreat());
            }
        }

        action.attack();
        if (enemies.length == 0) action.heal();
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
