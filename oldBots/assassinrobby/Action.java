package assassinrobby;

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

    void attackHeal() throws GameActionException {
        int mini = 1000000;
        MapLocation weakEnemy = null;
        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.location)) continue;
            int val = enemy.health - enemy.attackLevel * 10000;
            if (val < mini) {
                mini = val;
                weakEnemy = enemy.location;
            }
        }
        if (weakEnemy != null) {
            rc.attack(weakEnemy);
            return;
        }

        mini = 1000000;
        MapLocation weakFriend = null;
        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.location)) continue;
            if (friend.health < mini) {
                mini = friend.health;
                weakFriend = friend.location;
            }
        }
        if (weakFriend != null) {
            rc.heal(weakFriend);
            return;
        }
    }

    void attackHealFill() throws GameActionException {
        attackHeal();

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

    static void tryPlaceTrap(TrapType trap, MapLocation loc) throws GameActionException {
        if (rc.canBuild(trap, loc)) {
            rc.build(trap, loc);
        }
    }

    MapLocation chaseOrRetreat() throws GameActionException {
        if (enemies.length == 0) return null;

        int friendsHealth = 0;
        int enemiesHealth = 0;
        for (RobotInfo friend : friends) friendsHealth += friend.health;
        for (RobotInfo enemy : enemies) enemiesHealth += enemy.health;

        if (friendsHealth > enemiesHealth * 1.2) {
            //chase
            int mini = 1000000;
            MapLocation weakEnemy = null;
            for (RobotInfo enemy : enemies) {
                if (enemy.health < mini) {
                    mini = enemy.health;
                    weakEnemy = enemy.location;
                }
            }
            return weakEnemy;
        }
        else {
            //retreat
            tryPlaceTrap(TrapType.EXPLOSIVE, rc.getLocation());
            return findRetreat();
            //I'm worried this is pretty bytecode heavy
        }
    }
}