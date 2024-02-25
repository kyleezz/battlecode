package dippy;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.min;

enum STATE {
    AGGRESSIVE,
    DEFENSIVE,
    EVASIVE,
    GOTFLAG,
};

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

    static STATE microState = STATE.AGGRESSIVE;

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

    public Micro(RobotController rc, boolean isBuilder) throws GameActionException {
        this.rc = rc;
        this.isBuilder = isBuilder;
    }

    static RobotInfo currDuck;
    static MapInfo currTrap;

    boolean play() throws GameActionException {
        // No enemies in sight, no need for micro attack
        if (enemies.length == 0) return false;

        if (flagTarget != null && rc.canPickupFlag(flagTarget)) {
            rc.pickupFlag(flagTarget);
            Movement.resetRetreat();
        }

        /* Check State:
         * AGGRESSIVE:  friends to enemy ratio is good enough, charge!
         * EVASIVE:     ratio is not good enough, run away!
         * DEFENDING:   uh oh, ratio is not good but they are too close to base, stall
         * GOTFLAG:     yay! got flag, run away!
         */

        if (friends.length * Constants.ATTACKENEMYRATIO >= enemies.length ) {
            // if more friends than enemies, AGGRO
            microState = STATE.AGGRESSIVE;
        } else {
            // less friends, run
            microState = STATE.EVASIVE;
            // hold ground if near base
            if (rc.getLocation().distanceSquaredTo(nearestSpawn) <= Constants.DEFENSIVESTATERADIUS) microState = STATE.AGGRESSIVE;
        }

        if (isBuilder) microState = STATE.EVASIVE;

        // has flag
        if (rc.hasFlag()) microState = STATE.GOTFLAG;

        // get info
        surroundings = rc.senseNearbyMapInfos();
        attackPower = Constants.attackLevels[rc.getLevel(SkillType.ATTACK)];
        healPower = Constants.healLevels[rc.getLevel(SkillType.HEAL)];

        // see if can enemy within attack range before moving
        RobotInfo bestEnemy = null;
        for (int i = 0; i < enemies.length; i++) {
            bestEnemy = bestEnemyToAttack(bestEnemy, enemies[i], rc.getLocation());
        }

        if (rc.canAttack(bestEnemy.getLocation())) rc.attack(bestEnemy.getLocation());

        // find best spot to move to
        MicroInfo[] microInfos = new MicroInfo[9];
        for (int i = 0; i < 9; i++) microInfos[i] = new MicroInfo(dirs[i]);

        // process all enemies
        for (int i = 0; i < enemies.length; i++) {
            currDuck = enemies[i];
            for (int m = 0; m < 9; m++) {
                microInfos[m].updateEnemy();
            }
        }

        // process all frens
        RobotInfo bestHeal = rc.senseRobotAtLocation(rc.getLocation());

        for (int i = 0; i < friends.length; i++) {
            currDuck = friends[i];
            if (rc.getLocation().distanceSquaredTo(currDuck.getLocation()) <= Constants.HEAL_RADIUS) bestHeal = bestFriendToHeal(bestHeal, currDuck);
            for (int m = 0; m < 9; m++) {
                microInfos[m].updateFriends();
            }
        }

        // process all traps
        boolean closeTrap = false;

        for (int i = 0; i < surroundings.length; i++) {
            if (surroundings[i].getTrapType() != TrapType.NONE) {
                currTrap = surroundings[i];
                if (rc.getLocation().distanceSquaredTo(currTrap.getMapLocation()) <= 18) closeTrap = true;
                for (int m = 0; m < 9; m++) {
                    microInfos[m].updateTraps();
                }
            }
        }

        // get best spot
        MicroInfo bestLocation = microInfos[1];
        for (int i = 2; i < 9; i++) {
            if (!bestLocation.isBetter(microInfos[i])) {
                bestLocation = microInfos[i];
            }
        }

        rc.setIndicatorLine(rc.getLocation(), bestLocation.loc, 255, 0, 255);

        MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();

        int closestDist = 100000;
        for (int i = 0; i < broadcastLocs.length; i++) {
            closestDist = min(closestDist, rc.getLocation().distanceSquaredTo(broadcastLocs[i]));
        }

        // try place traps if evading
        if (!closeTrap && enemies.length >= 8 && (microState == STATE.DEFENSIVE || microState == STATE.EVASIVE || microState == STATE.AGGRESSIVE)) {
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
        } else if (closestDist < 100 && rc.getCrumbs() > 1000) {
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



        if (isBuilder) {
            if (flagTarget != null && rc.getLocation().distanceSquaredTo(flagTarget) < 30) {
                if (rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
                    rc.build(TrapType.EXPLOSIVE, rc.getLocation());
                }
            }
            if (enemies.length >= 5 && rc.canBuild(TrapType.EXPLOSIVE, rc.getLocation())) {
                rc.build(TrapType.EXPLOSIVE, rc.getLocation());
            }
        }

        if (rc.canMove(bestLocation.dir)) {
            rc.move(bestLocation.dir);
        }

        if (bestLocation.target != null && rc.canAttack(bestLocation.target.getLocation())) {
            rc.attack(bestLocation.target.getLocation());
        }
        if (bestHeal != null && rc.canHeal(bestHeal.getLocation())) {
            rc.heal(bestHeal.getLocation());
            //rc.setIndicatorString("Healed: " + bestHeal.getLocation().x + " " + bestHeal.getLocation().y);
        }

        return true;
    }

    RobotInfo bestEnemyToAttack(RobotInfo a, RobotInfo b, MapLocation location) throws GameActionException {

        /* Targetting enemies:
         * Go for enemies that are closer to us, as further enemies can run away
         * Prioritise enemies with flags!!
         * Go for enemies that are one hit or low health     ] test which one is
         * Prioritise higher attack level enemies            ] more important
         */

        if (a == null) return b;
        if (b == null) return a;

        if (rc.canAttack(a.location) && !rc.canAttack(b.location)) return a;
        if (!rc.canAttack(a.location) && rc.canAttack(b.location)) return b;

        // get one hit ones
        if (a.getHealth() < attackPower) return a;
        if (b.getHealth() < attackPower) return b;

        // get closer one
        int distA = location.distanceSquaredTo(a.getLocation());
        int distB = location.distanceSquaredTo(b.getLocation());
        if (distA < distB) return a;
        if (distA > distB) return b;

        // get one with flag
        if (a.hasFlag()) return a;
        if (b.hasFlag()) return b;

        // get weaker HP one
        if (a.getHealth() > b.getHealth()) return a;
        if (a.getHealth() < b.getHealth()) return b;

        // get one with higher attack level
        if (a.getAttackLevel() > b.getAttackLevel()) return a;
        else return b;
    }

    RobotInfo bestFriendToHeal(RobotInfo a, RobotInfo b) throws GameActionException {

        // Similar to bestenemy, but not prioritising distance

        if (a == null) return b;

        // get one with flag
        if (a.hasFlag()) return a;
        if (b.hasFlag()) return b;

        // prioritise builders
        if (a.getBuildLevel() > b.getBuildLevel()) return a;
        if (b.getBuildLevel() > a.getBuildLevel()) return a;

        // if friend dies in one hit even with heal, theres not really a point...
        if (a.getHealth() + healPower < Constants.MIN_HEALED_HP) return b;
        if (b.getHealth() + healPower < Constants.MIN_HEALED_HP) return a;

        // get weaker HP one
        if (a.getHealth() > b.getHealth()) return a;
        if (a.getHealth() < b.getHealth()) return b;

        // get one with higher attack level
        if (a.getAttackLevel() > b.getAttackLevel()) return a;
        else return b;
    }

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
    class MicroInfo {

        // tile info
        Direction dir;
        MapLocation loc;
        boolean canMove = true;
        int canAttack = 0;

        // enemy info
        int attackableEnemyUnits = 0;
        int visibleEnemyUnits = 0;
        int minDistToEnemy = 1000000;
        int damageReceived = 0;
        RobotInfo closestEnemy = null;
        RobotInfo target = null;

        // friend info
        int healableFriends = 0;
        int visibleFriends = 0;
        int minDistToFriend = 1000000;

        // trap info
        int minDistToTrap = 1000000;

        public MicroInfo(Direction dir) throws GameActionException {
            this.dir = dir;
            this.loc = rc.getLocation().add(dir);
            canMove = true;
            if (dir != Direction.CENTER && !rc.canMove(dir)) canMove = false;
        }

        void updateEnemy() throws GameActionException {

            if (!canMove) return;
            int dist = currDuck.getLocation().distanceSquaredTo(loc);
            if (dist < minDistToEnemy) {
                minDistToEnemy = dist;
                closestEnemy = currDuck;
            }
            if (dist < Constants.ATTACK_RADIUS) {
                attackableEnemyUnits += 1;
                damageReceived += Constants.attackLevels[currDuck.getAttackLevel()];
                if (rc.isActionReady()) canAttack = 1;
                target = bestEnemyToAttack(target, currDuck, loc);
            }
            if (dist < Constants.VISION_RADIUS) visibleEnemyUnits += 1;
        }

        void updateFriends() throws GameActionException {
            if (!canMove) return;
            int dist = currDuck.getLocation().distanceSquaredTo(loc);
            if (dist < minDistToEnemy) {
                minDistToFriend = dist;
            }
            if (dist < Constants.HEAL_RADIUS) healableFriends += 1;
            if (dist < Constants.VISION_RADIUS) visibleFriends += 1;
        }

        void updateTraps() throws GameActionException {
            if (!canMove) return;
            int dist = currTrap.getMapLocation().distanceSquaredTo(loc);
            if (dist < minDistToTrap) minDistToTrap = dist;
        }

        boolean isBetter(MicroInfo m) {

            if (canMove && !m.canMove) return true;
            if (m.canMove && !canMove) return false;

            if (microState == STATE.AGGRESSIVE) {
                // AGGRESSIVE:
                // prioritise squares with enemies in range, try get close to flag
                if (canAttack > m.canAttack) return true;
                if (canAttack < m.canAttack) return false;
                if (flagTarget != null) {
                    int distA = loc.distanceSquaredTo(flagTarget);
                    int distB = m.loc.distanceSquaredTo(flagTarget);
                    if (distA < distB) return true;
                    if (distB < distA) return false;
                }
            }

            // EVASIVE:
            // prioritise squares with less enemies and less damage received, more
            // friendly units nearby, try move out of view range of enemies
            // avoid going over water

            if (attackableEnemyUnits - canAttack < m.attackableEnemyUnits - m.canAttack) return true;
            if (attackableEnemyUnits - canAttack > m.attackableEnemyUnits - m.canAttack) return false;

            if (isBuilder && flagTarget != null) {
                int distA = loc.distanceSquaredTo(flagTarget);
                int distB = m.loc.distanceSquaredTo(flagTarget);
                if (distA < distB) return true;
                if (distB < distA) return false;
            }

            if (microState == STATE.GOTFLAG) {
                // GOTFLAG:
                // try move closer back to base
                int distA = loc.distanceSquaredTo(nearestSpawn);
                int distB = m.loc.distanceSquaredTo(nearestSpawn);
                if (distA < distB) return true;
                if (distB < distA) return false;
            }

            if (visibleEnemyUnits < m.visibleEnemyUnits) return true;
            if (visibleEnemyUnits > m.visibleEnemyUnits) return false;

            if (microState == STATE.GOTFLAG || microState == STATE.EVASIVE) {
                if (minDistToTrap < m.minDistToTrap) return true;
                if (minDistToTrap > m.minDistToTrap) return false;
            }

            if (healableFriends > m.healableFriends) return true;
            if (healableFriends < m.healableFriends) return false;

            if (canAttack > m.canAttack) return true;
            if (canAttack < m.canAttack) return false;

            if (minDistToFriend < m.minDistToFriend) return true;
            if (minDistToFriend > m.minDistToFriend) return false;

            if (canAttack == 0|| minDistToEnemy <= Constants.ATTACK_RADIUS) return minDistToEnemy >= m.minDistToEnemy;
            else return minDistToEnemy <= m.minDistToEnemy;
        }

    }
}

// place trap first
// then attack
// then run
// try not to fill, since both action and movement have cooldowns

/* Retreat:
 * Try place traps as cover behind
 * Try to attack before moving
 * Then heal
 */