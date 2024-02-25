package kity;

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

    MapLocation gotBread = null;
    
    int isSentinel = -1;
    int hasBread = -1;
    

    public void run() throws GameActionException {

        // spawn in duck
        if (!rc.isSpawned()) {
            if (isSentinel != -1) {
                rc.writeSharedArray(Constants.HELPINDEX[isSentinel],100000);
                isSentinel = -1;
            }
            if (hasBread != -1) {
                rc.writeSharedArray(Constants.GOTBREADINDEX[hasBread], 0);
                hasBread = -1;
            }
            MapLocation spawnLoc = findSpawn();
            if (spawnLoc == null) {
                return;
            }
            rc.spawn(spawnLoc);
        }

        int roundNum = rc.getRoundNum();
        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        action.friends = friends;
        action.enemies = enemies;

        // sentinel ducks stay on base and watch for enemies
        int onBase = onBaseCentre();
        if (onBase != -1 && rc.senseNearbyFlags(3).length != 0) {
            rc.writeSharedArray(Constants.HELPINDEX[onBase], Math.max(0, enemies.length - friends.length));
            action.tryPlaceTrap(TrapType.STUN, rc.getLocation());
            if (enemies.length > 1) {
                MapLocation enemyLoc = null;
                int dist = 1000000;
                for (int i = 0; i < enemies.length; i++) {
                    int enemyDist = rc.getLocation().distanceSquaredTo(enemies[i].getLocation());
                    if (enemyDist < dist) {
                        dist = enemyDist;
                        enemyLoc = enemies[i].getLocation();
                    }
                }

                Direction enemyDir = rc.getLocation().directionTo(enemyLoc);

                action.tryPlaceTrap(TrapType.STUN, rc.getLocation().add(enemyDir));
                action.tryPlaceTrap(TrapType.STUN, rc.getLocation().add(enemyDir.rotateRight().rotateRight()));
                action.tryPlaceTrap(TrapType.STUN, rc.getLocation().add(enemyDir.rotateLeft().rotateLeft()));


            } 
            action.attack();
            if (rc.getHealth() < 1000 && rc.isActionReady()) {
                rc.heal(rc.getLocation());
            }
            rc.setIndicatorString(rc.readSharedArray(Constants.HELPINDEX[0]) + " " + rc.readSharedArray(Constants.HELPINDEX[1]) + " " + rc.readSharedArray(Constants.HELPINDEX[2]) + " ");
            return;
        }

        // record base centres
        if (roundNum == 1) {
            
            if (rc.senseNearbyRobots(10).length == 0) {
                FlagInfo[] nearFlags = rc.senseNearbyFlags(2, rc.getTeam());
                System.out.println("starting...");
                for (int i = 0; i < 3; i++) {
                    if (rc.readSharedArray(Constants.BASELOCINDEX[i]) == 0) {
                        rc.writeSharedArray(Constants.BASELOCINDEX[i], Util.serializeLoc(nearFlags[0].getLocation()));
                        break;
                    }
                }
            }
        }

        // stage one: collect crumbs
        if (roundNum < Constants.stageOne) {
            
            if (onBaseCentre() != -1) {
                return;
            }

            if (target == null || target.equals(rc.getLocation())) {
                target = findCrumbWithinRadius(-1);
                if (target == null) {
                    movement.spreadOut();
                }
            } else {
                movement.moveTo(target);
            }
            return;
        }

        // stage two: start swarming
        if (roundNum < Constants.stageTwo) {
            // just go to the closest bro
            // TODO: don't
            target = getClosestEnemyBase();
            movement.moveTo(target);
            return;
        }

        // stage three: 
        // aim for enemy bases

        // if duck is closer to enemy base, aim to get attack base
        // if duck is closer to a help zone, aim to defend instead

        target = getClosestEnemyBase();
        rc.setIndicatorString("travelling towards enemy base" + target.x + " " + target.y);
        int closest = rc.getLocation().distanceSquaredTo(target);
        rc.setIndicatorString("Heading for enemy base (" + target.x + ", " + target.y + ")");

        for (int i = 0; i < 3; i++) {
            if (rc.readSharedArray(Constants.HELPINDEX[i]) != 0) {
                MapLocation helpLoc = Util.deserializeLoc(rc.readSharedArray(i));
                int dist = rc.getLocation().distanceSquaredTo(helpLoc);
                if (dist < closest) {
                    closest = dist;
                    target = helpLoc;
                    rc.setIndicatorString("Heading to help out (" + target.x + ", " + target.y + ")");
                }
            }
        }

        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);

        shellieAttack();
        if (enemies.length != 0) {    
            rc.setIndicatorString("attacking since theres enemies near");            
            
            // int closest = 1000000;
            // MapLocation closeEnemy = null;
            // for (int i = 0; i < enemies.length; i++) {
            //     int dist = rc.getLocation().distanceSquaredTo(enemies[i].getLocation());
            //     if (dist < closest) {
            //         closest = dist;
            //         closeEnemy = enemies[i].getLocation();
            //     }
            // }   

            // if (closest > 20) {
            //     // further away, has time to place a trap but hold formation
            //     if (rc.getID()%4 == 0) {
            //         action.tryPlaceTrap(TrapType.STUN, rc.getLocation().add(rc.getLocation().directionTo(closeEnemy)));
            //     }
            // } else {
                
            //     MapLocation hitEnemy = action.attack();
            //     if (hitEnemy != null) {
            //         // if i can hit the enemy, attack and try move away
            //         movement.moveTo(rc.getLocation().add(rc.getLocation().directionTo(hitEnemy)));
            //     } else {
            //         // try move closer, and hit
            //         movement.moveTo(rc.getLocation().add(rc.getLocation().directionTo(closeEnemy)));
            //         action.attack();
            //         action.heal();
            //     }
            // }
        } else {
            // travel        
            
            FlagInfo[] flags = rc.senseNearbyFlags(2, rc.getTeam().opponent());
            for (FlagInfo flag : flags) {
                if (!flag.isPickedUp() && rc.canPickupFlag(flag.getLocation())) {
                    MapLocation flagLoc = flag.getLocation();
                    rc.pickupFlag(flagLoc);
                    for (int i = 0; i < 3; i++) {
                        if (rc.readSharedArray(Constants.GOTBREADINDEX[i]) == 0) {
                            rc.writeSharedArray(Constants.GOTBREADINDEX[i], Util.serializeLoc(flagLoc));
                        }
                    }
                    
                    break;
                }  
            }

            if (rc.hasFlag()) {
                MapLocation nearestspawn = action.findRetreat();
                if (rc.getLocation().distanceSquaredTo(nearestspawn) <= 2 && rc.canDropFlag(nearestspawn)) rc.dropFlag(nearestspawn);
                else movement.moveTo(nearestspawn);
            }

            movement.moveTo(target);
            if (rc.isActionReady()) {
                action.heal();
                if (rc.getHealth() < 1000 && rc.canHeal(rc.getLocation())) rc.heal(rc.getLocation());
            }
        }

    }

    public MapLocation getClosestEnemyBase() throws GameActionException {

        int closest = 10000000;
        MapLocation baseLoc = null;

        for (int i = 0; i < 3; i++) {
            MapLocation base = Util.getSymmetricLoc(Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[i])));
            if (Util.deserializeLoc(rc.readSharedArray(Constants.GOTBREADINDEX[i])) == base) continue;
            int dist = rc.getLocation().distanceSquaredTo(base);
            if (dist < closest) {
                closest = dist;
                baseLoc = base;
            }
        }
        if (baseLoc != null) return baseLoc;
        return Util.getSymmetricLoc(Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[rng.nextInt(3)])));
    }

    public void shellieAttack() throws GameActionException {
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

    public MapLocation findSpawn() throws GameActionException {

        for (int i = 0; i < 3; i++) {
            MapLocation randomLoc = Util.deserializeLoc(rc.readSharedArray(i));
            if (rc.canSpawn(randomLoc)) {
                return randomLoc;
            }
        }

        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        int startSpawnLoc = rng.nextInt();
        int closest = 10000000;
        MapLocation bestLoc = null;

        MapLocation helpCall = null;

        for (int i = 0; i < 27; i++) {

            MapLocation randomLoc = spawnLocs[(startSpawnLoc+i) % 27];
            if (!rc.canSpawn(randomLoc)) continue;
            return randomLoc;
            
        }

        return bestLoc;
    }

    public MapLocation findCrumbWithinRadius(int radius) throws GameActionException {
        MapLocation[] crumbs = rc.senseNearbyCrumbs(radius);
        if (crumbs.length > 0) {
            int closest = -1;
            int smolDist = 10000000;
            for (int i = 0; i < crumbs.length; i++) {
                if (rc.getLocation(). distanceSquaredTo(crumbs[i]) < smolDist) {
                    smolDist = rc.getLocation().distanceSquaredTo(crumbs[i]);
                    closest = i;
                }
            }
            return crumbs[closest];
        } 
        return null;
    }

    public int onBaseCentre() throws GameActionException {
        int base = -1;
        for (int i = 0; i < 3; i++) {
            MapLocation baseCentre = Util.deserializeLoc(rc.readSharedArray(i));
            if (rc.getLocation().equals(baseCentre)) return i;
        }
        return base;
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
