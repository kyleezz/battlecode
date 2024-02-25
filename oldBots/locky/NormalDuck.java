package locky;

import battlecode.common.*;

public class NormalDuck extends Duck {

    static int roundNum;
    static MapLocation target = null;
    static MapLocation nearestSpawn = null;
    static int FINDCRUMBS = 140;

    static RobotInfo[] friends;
    static RobotInfo[] enemies;
    static MapInfo[] surroundings;

    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;   
    
    public void run() throws GameActionException {
        globalbfs.updateGrid();

        if (!rc.isSpawned()) {
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
        surroundings = rc.senseNearbyMapInfos(-1);
        micro.surroundings = surroundings;

        if (rc.getRoundNum() == 1) recordBaseLoc();

        if (roundNum < FINDCRUMBS) {
            target = findCrumb(-1);
            if (target == null) {
                movement.spreadOut();
            } else {
                movement.moveTo(target, true);
            }
            symmetry.findSymmetry();
            return;
        }

        target = findNearestBroadcast();
        micro.flagTarget = target;
        nearestSpawn = getClosestBase();
        micro.nearestSpawn = nearestSpawn;

//        action.fill();

        if (rc.hasFlag()) {
            movement.fastRetreat(nearestSpawn);
        }

        int closest = 1000000;
        MapLocation closestEnemyFlag = null;
        for (int i = 0; i < enemyFlags.length; i++) {
            if (enemyFlags[i].isPickedUp()) continue;
            int dist = rc.getLocation().distanceSquaredTo(enemyFlags[i].getLocation());
            if (dist < closest) {
                closest = dist;
                closestEnemyFlag = enemyFlags[i].getLocation();
            }
        }

        if (closestEnemyFlag != null) {
            if (rc.canPickupFlag(closestEnemyFlag)) {
                rc.pickupFlag(closestEnemyFlag);
                Movement.resetRetreat();
            } else {
                movement.moveTo(closestEnemyFlag, true);
            }
        }

        if(!micro.play()) {
            if (rc.hasFlag()) {
                // woweee we have the flag!
                movement.fastRetreat(nearestSpawn);
                symmetry.findSymmetry();
                return;
            }

            bestieMove();
            action.attack();
            action.heal();

            // } else {
            //     if (target == null) {
            //         // move to nearest duck with flag
            //     } else {
            //         movement.moveTo(target, true);
            //     }
            //     action.heal();                
            // }
        }   

        symmetry.findSymmetry();
    }

    MapLocation getClosestTrap() {
        int closest = 1000000;
        MapLocation closestTrap = null;
        for (int i = 0; i < surroundings.length; i++) {
            if (surroundings[i].getTrapType() != TrapType.NONE) {
                MapLocation trap = surroundings[i].getMapLocation();
                int dist = trap.distanceSquaredTo(rc.getLocation());
                if (dist < closest) {
                    closest = dist;
                    closestTrap = trap;
                }
            }
        }
        return closestTrap;
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
            // target = findNearestBroadcast();
            // if (target != null) {
            //     // what do we do if there is no nearby broadcast
            //     // rn its just erroring lol havent pushed this one
            //     // ohhhhhhhhhhhhhhhhhhhhhhhhhhhhh ok yeah
            // }
            // movement.moveTo(target, true); ohohohohoh
            // oh did u know you can open the console up
        }
    }


    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
        micro = new Micro(movement, rc, false);
    }

    
}
