package shelly;

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

    RobotInfo[] friends;
    RobotInfo[] enemies;

    MapLocation spawnLoc = null;

    public void run() throws GameActionException {

        if (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            int startSpawnLoc = rng.nextInt();
            for (int i = 0; i < 27; i++) {
                MapLocation randomLoc = spawnLocs[(startSpawnLoc+i) % 27];
                if (rc.canSpawn(randomLoc)) {
                    rc.spawn(randomLoc);
                    spawnLoc = randomLoc;
                    break;
                }
            }
        }

        if (!rc.isSpawned()) return;

        if (rc.getRoundNum() <= CONSTANTS.STAGEONE) {
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

        else if (rc.getRoundNum() <= CONSTANTS.STAGETWO) {
            movement.moveTo(spawnLoc);
            if (rc.getLocation().distanceSquaredTo(spawnLoc) < 4)
            Action.tryPlaceTrap(TrapType.WATER, rc.getLocation());
            return;
        }

        // After Round 200
        else if (rc.getRoundNum() <= CONSTANTS.STAGEFOUR) {
            friends = rc.senseNearbyRobots(-1, rc.getTeam());
            enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            action.friends = friends;
            action.enemies = enemies;

            Action.tryPlaceTrap(TrapType.WATER, rc.getLocation());
            
            if (target == null || target.equals(rc.getLocation())) {
                // moving to opposite wall?
                target = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
            }

            MapLocation bestie = null;
            int mini = rc.getID();
            int lowerCount = 0;

            for (RobotInfo friend : friends) {
                if (friend.ID < rc.getID()) lowerCount++;
                if (friend.ID < mini) {
                    bestie = friend.location;
                    mini = friend.ID;
                }
            }

            if (bestie == null) {
                movement.moveTo(spawnLoc);
                return;
            }

            action.attackHeal();

            if (bestie != null && lowerCount < CONSTANTS.SWARMSIZE && rc.getLocation().distanceSquaredTo(bestie) > 2) {
                movement.moveTo(bestie);
            }

            action.attackHeal();
            return;
        }

        if (!rc.isSpawned()) return;

        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        action.friends = friends;
        action.enemies = enemies;
        
        if (target == null || target.equals(rc.getLocation())) {
            target = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
        }

        MapLocation bestie = null;
        int mini = rc.getID();
        int lowerCount = 0;

        for (RobotInfo friend : friends) {
            if (friend.ID < rc.getID()) lowerCount++;
            if (friend.ID < mini) {
                bestie = friend.location;
                mini = friend.ID;
            }
        }

        action.attackHeal();

        if (bestie != null && lowerCount < 7 && rc.getLocation().distanceSquaredTo(bestie) > 2) {
            movement.moveTo(bestie);
        }
        else {
            MapLocation tmp = action.chaseOrRetreat();
            if (tmp != null) movement.moveTo(tmp);
            else {
                if (lowerCount < 7 && friends.length >= 7) movement.moveToBroadcast();
                else movement.moveRandom();
            }
        }

        //action.attackHealFill();
        action.attackHeal();
        
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
