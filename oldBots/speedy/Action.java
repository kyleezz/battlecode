package speedy;

import battlecode.common.*;

public class Action {
    static RobotController rc;
    static CursedRandom rng;

    RobotInfo[] friends;
    RobotInfo[] enemies;

    public Action(CursedRandom rng, RobotController rc) throws GameActionException {
        this.rc = rc;
        this.rng = rng;
    }

    MapLocation getWorstEnemy() throws GameActionException {
        int worst = 10000000;
        MapLocation worstEnemy = null;
        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.location)) continue;
            int enemyRating = enemy.health * Constants.HEALTHWEIGHT - enemy.attackLevel * Constants.ATTACKWEIGHT;
            if (enemyRating < worst) {
                worst = enemyRating;
                worstEnemy = enemy.location;
            }
        }
        return worstEnemy;
    }

    MapLocation getBestFriend() throws GameActionException {
        int best = 10000000;
        MapLocation bestFriend = null;
        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.location)) continue;
            if (friend.getHealth() < best) {
                best = friend.getHealth();
                bestFriend = friend.getLocation();
            }
        }
        return bestFriend;
    }

    void attack() throws GameActionException {
        MapLocation worstEnemy = getWorstEnemy();
        if (worstEnemy != null) {
            rc.attack(worstEnemy);
            return;
        }
    }

    void heal() throws GameActionException {
        MapLocation bestFriend = getBestFriend();
        if (bestFriend != null) {
            rc.heal(bestFriend);
            return;
        }
    }

    void fill() throws GameActionException {
        int x = rc.getLocation().x;
        int y = rc.getLocation().y;

        if (rc.canFill(new MapLocation(x - 1, y - 1))) rc.fill(new MapLocation(x - 1, y - 1));
        if (rc.canFill(new MapLocation(x - 1, y))) rc.fill(new MapLocation(x - 1, y));
        if (rc.canFill(new MapLocation(x - 1, y + 1))) rc.fill(new MapLocation(x - 1, y + 1));

        if (rc.canFill(new MapLocation(x, y - 1))) rc.fill(new MapLocation(x, y - 1));
        if (rc.canFill(new MapLocation(x, y + 1))) rc.fill(new MapLocation(x, y + 1));

        if (rc.canFill(new MapLocation(x + 1, y - 1))) rc.fill(new MapLocation(x + 1, y - 1));
        if (rc.canFill(new MapLocation(x + 1, y))) rc.fill(new MapLocation(x + 1, y));
        if (rc.canFill(new MapLocation(x + 1, y + 1))) rc.fill(new MapLocation(x + 1, y + 1));
    }

    void dig() throws GameActionException {
        int x = rc.getLocation().x;
        int y = rc.getLocation().y;

        if (rc.canDig(new MapLocation(x - 1, y - 1))) rc.dig(new MapLocation(x - 1, y - 1));
        if (rc.canDig(new MapLocation(x - 1, y))) rc.dig(new MapLocation(x - 1, y));
        if (rc.canDig(new MapLocation(x - 1, y + 1))) rc.dig(new MapLocation(x - 1, y + 1));

        if (rc.canDig(new MapLocation(x, y - 1))) rc.dig(new MapLocation(x, y - 1));
        if (rc.canDig(new MapLocation(x, y + 1))) rc.dig(new MapLocation(x, y + 1));

        if (rc.canDig(new MapLocation(x + 1, y - 1))) rc.dig(new MapLocation(x + 1, y - 1));
        if (rc.canDig(new MapLocation(x + 1, y))) rc.dig(new MapLocation(x + 1, y));
        if (rc.canDig(new MapLocation(x + 1, y + 1))) rc.dig(new MapLocation(x + 1, y + 1));
    }

    MapLocation findRetreat() throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        int mini = 1000000;
        MapLocation closeLoc = null;
        for (MapLocation spawnLoc : spawnLocs) {
            if (rc.getLocation().distanceSquaredTo(spawnLoc) < mini) {
                mini = rc.getLocation().distanceSquaredTo(spawnLoc);
                closeLoc = spawnLoc;
            }
        }
        return closeLoc;
    }

    static boolean tryPlaceTrap(TrapType trap, MapLocation loc) throws GameActionException {
        if (rc.canBuild(trap, loc)) {
            rc.build(trap, loc);
            return true;
        }
        return false;
    }

    MapLocation chaseOrRetreat() throws GameActionException {
        if (enemies.length == 0) return null;

        int friendsHealth = 0;
        int enemiesHealth = 0;
        for (RobotInfo friend : friends) friendsHealth += friend.health;
        for (RobotInfo enemy : enemies) enemiesHealth += enemy.health;

        if (friendsHealth > enemiesHealth * 1.2) {
            return getWorstEnemy();
        }
        else {
            //retreat
            tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation());
            return findRetreat();
            //I'm worried this is pretty bytecode heavy
        }
    }
}