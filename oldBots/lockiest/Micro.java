package lockiest;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.min;

public class Micro {

    static RobotController rc;
    static boolean isBuilder;
    static RobotInfo[] enemies;
    static RobotInfo[] friends;
    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;
    static MapInfo[] surroundings;

    static MapLocation flagTarget = null;
    static MapLocation nearestSpawn;

    static int attackPower;
    static int healPower;

    static final Direction[] dirs = {
            Direction.CENTER,
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.NORTHEAST,
            Direction.SOUTHEAST,
            Direction.SOUTHWEST,
            Direction.NORTHWEST,
    };

    class Pair {
        int x;
        int y;

        // Constructor
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "(" + x +
                    ", " + y +
                    ')';
        }
    }

    static Movement movement;

    public Micro(Movement movement, RobotController rc, boolean isBuilder) throws GameActionException {
        this.rc = rc;
        this.isBuilder = isBuilder;
        this.movement = movement;
    }

    void retreat() throws GameActionException {
        if (rc.getCrumbs() > 500) {
            // rc.getCrumbs() > Constants.MINCRUMBS &&
            boolean canBuild = false;
            for (int i = 0; i < 9; i++) {
                MapLocation loc = rc.getLocation().add(dirs[i]);
                if (rc.canBuild(TrapType.EXPLOSIVE, loc)) canBuild = true;
            }
            if (canBuild) {
                Pair[] arr = new Pair[9];
                for (int i = 0; i < 9; i++) {
                    int dist = 0;
                    MapLocation loc = rc.getLocation().add(dirs[i]);
                    for (int j = 0; j < enemies.length; j++) {
                        dist += loc.distanceSquaredTo(enemies[j].getLocation());
                    }
                    arr[i] = new Pair(dist, i);
                }

                Arrays.sort(arr, new Comparator<Pair>() {
                    @Override
                    public int compare(Pair p1, Pair p2) {
                        return p1.x - p2.x;
                    }
                });

    //                boolean built = false;
                for (int i = 0; i < 9; i++) {
                    if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation().add(dirs[arr[i].y]))) {
                        rc.build(TrapType.EXPLOSIVE, rc.getLocation().add(dirs[arr[i].y]));
                        break;
                    }
                }
            }
        }

        if (rc.getHealth() > 900) {
            int tx = 0, ty = 0, cnt = 0;
            for (MapInfo loc : surroundings) {
                if (!loc.isPassable()) continue;
                tx += loc.getMapLocation().x;
                ty += loc.getMapLocation().y;
                cnt++;
            }

            if (cnt != 0) {
                MapLocation target = new MapLocation(tx / cnt, ty / cnt);
                if (target != rc.getLocation()) {
                    movement.moveTo(target, true);
                }
                else {
                    movement.moveToBroadcast();
                }
            }
            return;
        }

        if (friends.length == 0) {
            movement.fastRetreat(nearestSpawn);
            return;
        }
        // move to center of friends
        int tx = 0, ty = 0;
        for (RobotInfo friend : friends) {
            tx += friend.location.x;
            ty += friend.location.y;
        }

        MapLocation target = new MapLocation(tx / friends.length, ty / friends.length);

        if (target != rc.getLocation()) {
            movement.moveTo(target, true);
        }
        else {
            movement.fastRetreat(nearestSpawn);
        }
    }

    void tryAttack() throws GameActionException{
        int minPriority = 10000;
        MapLocation minLoc = null;
        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.location)) return;
            if (enemy.health < minPriority) {
                minPriority = enemy.health;
                minLoc = enemy.location;
            }
        }
        if (minLoc != null) rc.attack(minLoc);
    }

    boolean play() throws GameActionException {
        // No enemies in sight, no need for micro attack
        if (enemies.length == 0) return false;

        int minPriority = 100000;
        RobotInfo minDude = null;
        for (RobotInfo enemy : enemies) {
            int priority = enemy.health;
            if (!enemy.hasFlag) priority += 10000;
            int dist = rc.getLocation().distanceSquaredTo(enemy.location);
            if (dist > 2) priority += 10000;
            if (dist > 8) priority += 10000;
            if (priority < minPriority) {
                minPriority = priority;
                minDude = enemy;
            }
        }

        if (minPriority <= 10000) {
            //has the flag :/
            if (rc.canAttack(minDude.location)) rc.attack(minDude.location);
            movement.moveTo(minDude.location, false);
            if (rc.canAttack(minDude.location)) rc.attack(minDude.location);
            return true;
        }

        if (minPriority <= 20000) {
            if (rc.canAttack(minDude.location)) rc.attack(minDude.location);
            retreat();
            return true;
        }

        if (minPriority <= 30000) {
            if (!rc.isActionReady()) {
                retreat();
                return true;
            }

            movement.moveTo(minDude.location, false);
            if (rc.canAttack(minDude.location)) rc.attack(minDude.location); 
            else tryAttack();

            return true;
        }

        int healPriority = 100000;
        MapLocation healDude = null;
        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.location)) continue;
            int priority = friend.health;
            if (!friend.hasFlag) priority += 10000;
            if (priority < healPriority) {
                healPriority = priority;
                healDude = friend.location;
            }
        }
        if (healDude != null) rc.heal(healDude);

        if (rc.getHealth() > 900) movement.moveTo(minDude.location, true);
        else retreat();

        tryAttack(); //shouldn't happen

        return true;
    }
}