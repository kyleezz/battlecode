package boomyfix;

import battlecode.common.*;
import com.sun.tools.internal.jxc.ap.Const;
import scala.collection.immutable.Stream;

enum microState {
    ATTACKING,
    RETREATING,
    CHASING,
    NEUTRAL
};

public class NormalDuck extends Duck {

    // macro variables
    static String indicator = "";
    static MapLocation targetLocation;
    static MapLocation targetEnemyFlag;
    static MapLocation targetEnemyBase;
    static MapLocation nearestFriendlyBase;
    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;
    static MapLocation nearestFriendlyFlag;


    // micro variables
    static RobotInfo[] enemies;
    static RobotInfo[] friends;
    static RobotInfo enemyWithFlag;
    static RobotInfo friendWithFlag;
    static RobotInfo nearestEnemy;
    static int minDistToEnemy;
    static int enemyTeamHealth;
    static int enemyTeamDamage;
    static int teamHealth;
    static int teamDamage;
    static int minDistToTrap;
    static int closeFriends;
    static int lotsaFriends = 0;
    static microState duckState = microState.NEUTRAL;

    static MapLocation defendLoc = null;

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void run() throws GameActionException {

        // try global upgrade, update bfs and baselocations
        indicator = "";

        int vibes = -1;
        if (rc.isSpawned()) {
            sense();
            vibes = Math.min(enemies.length - friends.length, 10);
            if (rc.hasFlag()) {
                vibes += 15;
                vibes = Math.min(vibes, 17);
            }

            if (vibes < 0) vibes = 0;

            if (rc.getRoundNum() > 200 && rc.getLocation().distanceSquaredTo(nearestFriendlyBase) <= 20) {
                int found = 0;
                for (int i = 0; i < 3; i++) {
                    if (baseLocs[i].equals(nearestFriendlyBase)) {
                        found = i;
                        break;
                    }
                }
                if (friendlyFlags.length != 0 && enemies.length > friends.length) {
                    rc.writeSharedArray(Constants.BASELOCINDEX[found], rc.getRoundNum());
                } else if (rc.readSharedArray(Constants.BASELOCINDEX[found]) != rc.getRoundNum() || rc.getLocation().distanceSquaredTo(nearestFriendlyBase) <= 12) {
                    rc.writeSharedArray(Constants.BASELOCINDEX[found], 0);
                }
            }
        }

        comms.updateComms(vibes);
        globalbfs.updateGrid();

        tryUpgrade();
        if (rc.getRoundNum() == 2) getBaseLocs();

        // spawn in
        if (!rc.isSpawned()) {
            // if round > 100, see if any base needs help
            MapLocation spawnLoc = findSpawnLocation(rc.getRoundNum() > 100);
            if (spawnLoc == null) return;
            rc.spawn(spawnLoc);
            if (rc.getRoundNum() == 1) recordBaseLoc();
            sense();
        }

        //
        if (rc.getRoundNum() > 2 && rc.getRoundNum() < 150 && nearestFriendlyFlag != null && rc.canPickupFlag(nearestFriendlyFlag))
            rc.pickupFlag(nearestFriendlyFlag);


        if (rc.hasFlag() && rc.getRoundNum() <= 200) {
            if (rc.readSharedArray(Constants.offset) == 0) {
                rc.writeSharedArray(Constants.offset, Comms.encode(rc.getLocation(), 0));
            } else if (rc.readSharedArray(Constants.offset + 1) == 0) {
                rc.writeSharedArray(Constants.offset + 1, Comms.encode(rc.getLocation(), 0));
            } else if (rc.readSharedArray(Constants.offset + 2) == 0) {
                rc.writeSharedArray(Constants.offset + 2, Comms.encode(rc.getLocation(), 0));
            } else {

                int closest = 10000000;
                int idx = -1;
                for (int i = 0; i < 3; i++) {
                    MapLocation loc = Comms.decodeLoc(rc.readSharedArray(Constants.offset + i));
                    if (rc.getLocation().distanceSquaredTo(loc) < closest) {
                        closest = rc.getLocation().distanceSquaredTo(loc);
                        idx = i;
                    }
                }

                rc.writeSharedArray(Constants.offset + idx, Comms.encode(rc.getLocation(), 0));
            }
        }

        if (rc.hasFlag() && rc.getRoundNum() <= 200) {
            if (!rc.isMovementReady()) {
                symmetry.findSymmetry();
                return;
            }

            MapLocation flag1 = null, flag2 = null;


            if (rc.getRoundNum() >= 2) {

                for (int i = 0; i < 3; i++) {
                    MapLocation loc = Comms.decodeLoc(rc.readSharedArray(Constants.offset + i));
                    if (rc.getLocation().distanceSquaredTo(loc) > 1) {
                        if (flag1 == null) flag1 = loc;
                        else flag2 = loc;
                    }
                }
            }

            if (rc.getRoundNum() > 150) {
                if (rc.getLocation().distanceSquaredTo(flag1) >= 36 && rc.getLocation().distanceSquaredTo(flag2) >= 36) {
                    rc.dropFlag(rc.getLocation());
                }
            }

            int best = -1000000;
            Direction bestDir = null;
            for (Direction dir : Constants.directions) {
                if (!rc.canMove(dir)) continue;
                int score = 0;//rc.getLocation().add(dir).distanceSquaredTo(findNearestEnemyBase());
                for (int i = 0; i < 3; i++) {
                    score += rc.getLocation().add(dir).distanceSquaredTo(symmetry.getSymmetricLoc(baseLocs[i]));
                    score -= rc.getLocation().add(dir).distanceSquaredTo(baseLocs[i]);
                }
//                if (nearestFriendlyFlag != null && rc.getLocation().add(dir).distanceSquaredTo(nearestFriendlyFlag) <= 40) {
//                    score -= 1000 * (36 - rc.getLocation().add(dir).distanceSquaredTo(nearestFriendlyFlag));
//                }
                if (flag1 != null && rc.getLocation().add(dir).distanceSquaredTo(flag1) < 49)
                    score -= 1000 * (49 - rc.getLocation().add(dir).distanceSquaredTo(flag1));
                if (flag2 != null && rc.getLocation().add(dir).distanceSquaredTo(flag2) < 49)
                    score -= 1000 * (49 - rc.getLocation().add(dir).distanceSquaredTo(flag2));

                if (score > best) {
                    best = score;
                    bestDir = dir;
                }
            }

            if (bestDir != null) rc.move(bestDir);
            symmetry.findSymmetry();
            return;
        }

            // prep rounds, spread out and collect crumbs
            if (rc.getRoundNum() < Constants.FIND_CRUMBS) {
                MapLocation target = findNearestCrumb(-1);
                if (target == null) {
                    movement.spreadOut();
                } else {
                    movement.moveTo(target, true);
                }
                symmetry.findSymmetry();
                rc.setIndicatorString(indicator);
                return;
            }

            if (rc.getRoundNum() <= 200) {
                Direction[] tmpDirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                for (Direction dir : tmpDirs) {
                    MapLocation tmp = rc.adjacentLocation(dir);
                    if (rc.onTheMap(tmp) && rc.senseMapInfo(tmp).isDam()) {
                        symmetry.findSymmetry();
                        rc.setIndicatorString(indicator);
                        return;
                    }
                }

                MapLocation target = findNearestEnemyFlag();
                if (target == null) target = findNearestEnemyBase();
                movement.moveTo(target, true);
                symmetry.findSymmetry();
                rc.setIndicatorString(indicator);
                return;
            }

            if (targetEnemyFlag != null && rc.canPickupFlag(targetEnemyFlag)) {
                rc.pickupFlag(targetEnemyFlag);
                movement.resetRetreat();
            } else if (enemyFlags.length != 0 && targetEnemyFlag != null && targetEnemyFlag.distanceSquaredTo(rc.getLocation()) < 9 && rc.isActionReady()) {
                for (Direction flagDir : Constants.directions) {
                    if (!rc.canMove(flagDir)) continue;
                    if (rc.getLocation().add(flagDir).distanceSquaredTo(targetEnemyFlag) <= 2) {
                        rc.move(flagDir);
                        if (rc.canPickupFlag(targetEnemyFlag)) {
                            rc.pickupFlag(targetEnemyFlag);
                            movement.resetRetreat();
                        }
                    }
                }
            }

            // decide microstate based on enemies and stuff
            if (enemies.length == 0) {
                duckState = microState.NEUTRAL;
            } else {
                duckState = microState.ATTACKING;
                if (enemyWithFlag != null) duckState = microState.CHASING;
            }

            if (rc.hasFlag()) duckState = microState.RETREATING;

            indicator += duckState + ", ";

            if (minDistToEnemy > 4 || friends.length > enemies.length) {
                Direction[] waterBreak = {
                        Direction.NORTH,
                        Direction.SOUTH,
                        Direction.EAST,
                        Direction.WEST,
                };

                for (Direction fillDir : waterBreak) {
                    if (rc.canMove(fillDir)) continue;
                    if (rc.canFill(rc.getLocation().add(fillDir.rotateLeft()))) {
                        rc.fill(rc.getLocation().add(fillDir.rotateLeft()));
                    }
                    if (rc.canFill(rc.getLocation().add(fillDir.rotateRight()))) {
                        rc.fill(rc.getLocation().add(fillDir.rotateRight()));
                    }
                }
            }

            // build - build traps or fill water
            if (nearestEnemy != null && duckState != microState.CHASING && minDistToTrap > 6) {

                if (enemies.length > 6 || rc.getCrumbs() > 2000) {
                    Direction enemyDir = rc.getLocation().directionTo(nearestEnemy.getLocation());
                    Direction[] trapDirs = {
                            enemyDir,
                            enemyDir.rotateLeft(),
                            enemyDir.rotateRight(),
                            Direction.CENTER,
                    };
                    for (Direction trapDir : trapDirs) {
                        if (tryPlaceTrap(TrapType.STUN, rc.getLocation().add(trapDir))) {
                            indicator += "built trap, ";
                            break;
                        }
                    }
                }
            }

            // attack
            RobotInfo targetEnemy = getAttackableTarget();
            while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
                indicator += "attacked, ";
                rc.attack(targetEnemy.getLocation());
                targetEnemy = getAttackableTarget();
            }

            // move: kiting, retreating, neutral, chasing
            if (duckState == microState.CHASING) {
                movement.moveTo(enemyWithFlag.getLocation(), false);
            } else if (duckState == microState.ATTACKING) {
                if (rc.isActionReady()) {
                    // can attack, move towards enemy
                    moveAttack();
                } else {
                    // already attacked, kite away from enemy
                    kite();
                }
            } else if (duckState == microState.RETREATING) {
                // move away from enemy, towards base
                retreat();
            } else if (duckState == microState.NEUTRAL) {
                if (defendLoc != null) movement.moveTo(defendLoc, true);
                else neutralMove();
            }

            // attack
            targetEnemy = getAttackableTarget();
            while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
                rc.attack(targetEnemy.getLocation());
                targetEnemy = getAttackableTarget();
                indicator += "attacked, ";
            }

            // heal
            // sus
            RobotInfo healFriend = getHealableFriend();
            if (healFriend != null) {
                rc.heal(healFriend.getLocation());
                indicator += "healed";
            }

            rc.setIndicatorString(indicator);
            symmetry.findSymmetry();


    }


    public void neutralMove() throws GameActionException {
        targetLocation = targetEnemyFlag;

        float bestVal = 0;
        MapLocation goToHelp = null;
        for (int i : Constants.DUCKLOCS) {
            if (rc.readSharedArray(i) == 0) continue;
            MapLocation helpLoc = Comms.decodeLoc(rc.readSharedArray(i));
            float helpVal = Comms.decodeVal(rc.readSharedArray(i));
            if (helpLoc.equals(rc.getLocation()) || helpVal == 0) continue;
            float val = helpVal/(rc.getLocation().distanceSquaredTo(helpLoc));
            if (val > bestVal) {
                bestVal = val;
                goToHelp = helpLoc;
            }
        }

        if (targetLocation == null || (goToHelp != null && (bestVal > 0.1 && rc.getLocation().distanceSquaredTo(goToHelp) < 400))) {
            targetLocation = goToHelp;
        }

        if (targetLocation == null) {
            movement.spreadOut();
            return;
        }

        MapLocation nearbyCrumb = findNearestCrumb(10);
        if (nearbyCrumb != null) targetLocation = nearbyCrumb;
        if (friendWithFlag != null) {
            int closer = 0;
            for (RobotInfo friend: friends) {
                if (friend.hasFlag()) continue;
                if (friend.getLocation().distanceSquaredTo(friendWithFlag.getLocation()) < rc.getLocation().distanceSquaredTo(friendWithFlag.getLocation())) closer ++;
            }

            if (rc.getLocation().distanceSquaredTo(friendWithFlag.getLocation()) > Constants.ESCORT_DISTANCE) {
                if (closer < Constants.ESCORT_SIZE) {
                    movement.moveTo(friendWithFlag.getLocation(), true);
                    indicator += "escorting flag, ";
                    return;
                }
            } else {
                Direction optDir = friendWithFlag.getLocation().directionTo(rc.getLocation());
                Direction[] optDirs = {
                        optDir,
                        optDir.rotateLeft(),
                        optDir.rotateRight(),
                        optDir.rotateLeft().rotateLeft(),
                        optDir.rotateRight().rotateRight(),
                        optDir.rotateLeft().rotateLeft().rotateLeft(),
                        optDir.rotateRight().rotateRight().rotateRight(),
                };
                for (Direction dir : optDirs) {
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                        break;
                    }
                }
                indicator += "escorting flag, ";
                return;
            }
        }
        RobotInfo bestie = null;
        int bestieScore = rc.getLocation().distanceSquaredTo(targetLocation) * 1000 - rc.getHealth();
        for (RobotInfo friend: friends) {
            if (friend.hasFlag()) continue;
            int score = friend.getLocation().distanceSquaredTo(targetLocation) * 1000;
            score -= friend.getHealth();
            if (score < bestieScore) {
                bestie = friend;
                bestieScore = score;
            }
        }

        if (closeFriends > Constants.CLOSE_FRIEND_SIZE && enemies.length < friends.length) { //sus
            lotsaFriends = rc.getRoundNum();
        }

        if (rc.getRoundNum() - lotsaFriends < 10 || bestie == null || closeFriends > Constants.CLOSE_FRIEND_SIZE) {
            indicator += String.format("moving to (%d, %d), ", targetLocation.x, targetLocation.y);
            movement.moveTo(targetLocation, true);
        }
        else {
            movement.moveTo(bestie.getLocation(), true);
            indicator += String.format("moving to bestie at (%d, %d), ", bestie.getLocation().x, bestie.getLocation().y);
        }
    }

    public void moveAttack() throws GameActionException { //omega sus
        // find enemy we can hit and move towards it, minise amount of enemies that can hit us
        int bestScore = 1000000000;
        Direction bestDir = null;
        // enemies may have changed after we attacked, friends stay the same
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (int i = 0; i < 8; i++) {
            if (!rc.canMove(Constants.directions[i])) continue;
            int attack = 0;
            int closest = 1000000;
            MapLocation newLoc = rc.getLocation().add(Constants.directions[i]);
            for (RobotInfo enemy : enemies) {
                if (enemy.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack += Util.getRobotAttackDamage(enemy);
                closest = Math.min(closest, enemy.getLocation().distanceSquaredTo(newLoc));
            }
            int score = attack - (4 - closest) * 100000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            } else if (score == bestScore) { //more more sus
                if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) < Math.abs(bestDir.dx) + Math.abs(bestDir.dy)) {
                    bestDir = Constants.directions[i];
                }
            }
        }
        indicator += "moveAttack" + bestScore;
        if (bestDir != null) {
            rc.move(bestDir);
        }

    }

    public void kite() throws GameActionException {
        // try to find an enemy that hasn't had its turn yet, and kite away
        // but, try to keep within move distance so we don't have to move much to attack
        // again
        int bestScore = 1000000;
        Direction bestDir = null;
        // enemies may have changed after we attacked, friends stay the same
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        for (int i = 0; i < 8; i++) {
            int attack = 0;
            int closest = 1000000;
            if (!rc.canMove(Constants.directions[i])) continue;
            MapLocation newLoc = rc.getLocation().add(Constants.directions[i]);
            for (RobotInfo enemy : enemies) {
                if (enemy.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack += Util.getRobotAttackDamage(enemy);
                closest = Math.min(closest, enemy.getLocation().distanceSquaredTo(newLoc));
            }
            for (RobotInfo friend : friends) {
                if (friend.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack -= 80; //slightly sus
            }
            int score = attack + Math.abs(10-closest) * 10000; // check if better without abs
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            } else if (score == bestScore) {
                if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) < Math.abs(bestDir.dx) + Math.abs(bestDir.dy)) {
                    bestDir = Constants.directions[i];
                }
            }
        }
        if (bestDir != null) {
            rc.move(bestDir);
        }
    }

    public void retreat() throws GameActionException {
        /*if (enemies.length != 0 && minDistToEnemy < Constants.RETREAT_ENEMY_RADIUS && !rc.hasFlag()) {
            Direction away = rc.getLocation().directionTo(nearestEnemy.getLocation()).opposite();
            Direction[] retreatDirs = {
                away,
                away.rotateLeft(),
                away.rotateRight(),
                away.rotateLeft().rotateLeft(),
                away.rotateRight().rotateRight()
            };
            for (int i = 0; i < 5; i++) {
                if (rc.canMove(retreatDirs[i])) {
                    rc.move(retreatDirs[i]);
                    return;
                }
            }
        }
        movement.moveTo(nearestFriendlyBase, true);*/
        movement.fastRetreat(nearestFriendlyBase);
    }

    public RobotInfo getHealableFriend() throws GameActionException {

        if (!rc.isActionReady()) return null;
        int bestFriendScore = 0;
        RobotInfo bestFriend = null;

        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.getLocation())) continue;
            int score = Math.abs(Constants.IDEAL_HEALTH - friend.getHealth());
            score += Util.getRobotAttackDamage(friend);
            if (friend.hasFlag()) score += 100; //omega sus
            if (friend.getHealth() + rc.getHealAmount() > 1000) score = 1;
            if (score > bestFriendScore) {
                bestFriendScore = score;
                bestFriend = friend;
            }
        }
        return bestFriend;
    }

    public RobotInfo getAttackableTarget() throws GameActionException {
        if (!rc.isActionReady()) return null;

        int bestEnemyScore = 1000000;
        RobotInfo bestEnemy = null;

        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.getLocation())) continue;
            if (enemy.getHealth() <= rc.getAttackDamage()) return enemy; //maybe sus
            int totalCanAttack = rc.getAttackDamage();
            for (RobotInfo friend : friends) {
                // ssusssss look at enemy id
                if (friend.getLocation().distanceSquaredTo(enemy.getLocation()) < Constants.ATTACK_RADIUS && friend.getID() > rc.getID()) totalCanAttack += Util.getRobotAttackDamage(friend);
            }
            if (enemy.getHealth() <= totalCanAttack) return enemy;
            int score = rc.getLocation().distanceSquaredTo(enemy.getLocation()) * 100; //sus
            score += enemy.getHealth();
            score -= enemy.getAttackLevel();
            if (rc.hasFlag()) score -= 1000;
            if (score < bestEnemyScore) {
                bestEnemyScore = score;
                bestEnemy = enemy;
            }
        }

        return bestEnemy;
    }

    public boolean tryPlaceTrap(TrapType trap, MapLocation loc) throws GameActionException {
        if (rc.canBuild(trap, loc)) {
            rc.build(trap, loc);
            return true;
        }
        return false;
    }

    public void sense() throws GameActionException {
        // we need enemy health, friend health, enemy attack power, friend attack power
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemyWithFlag = null;
        friendWithFlag = null;
        nearestEnemy = null;
        minDistToEnemy = 1000000;

        enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
        friendlyFlags = rc.senseNearbyFlags(-1, rc.getTeam());

        enemyTeamHealth = 0;
        enemyTeamDamage = 0;
        teamHealth = rc.getHealth();
        teamDamage = rc.getAttackDamage();
        closeFriends = 0;

        for (RobotInfo enemy : enemies) {
            enemyTeamHealth += enemy.getHealth();
            if (enemy.hasFlag()) enemyWithFlag = enemy;
            else enemyTeamDamage += Util.getRobotAttackDamage(enemy);
            int dist = rc.getLocation().distanceSquaredTo(enemy.getLocation());
            if (dist < minDistToEnemy) {
                minDistToEnemy = dist;
                nearestEnemy = enemy;
            }
        }

        for (RobotInfo friend : friends) {
            teamHealth += friend.getHealth();
            if (friend.hasFlag()) friendWithFlag = friend;
            else teamDamage += Util.getRobotAttackDamage(friend);
            if (rc.getLocation().distanceSquaredTo(friend.getLocation()) <= Constants.CLOSE_FRIEND_RADIUS) closeFriends ++;
        }

        minDistToTrap = 1000000;
        MapInfo[] surroundings = rc.senseNearbyMapInfos();
        for (MapInfo info : surroundings) {
            if (info.getTrapType() != TrapType.NONE) {
                minDistToTrap = Math.min(minDistToTrap, rc.getLocation().distanceSquaredTo(info.getMapLocation()));
            }
        }


        int closeFlag = 1000000;
        nearestFriendlyFlag = null;
        for (FlagInfo flag : friendlyFlags) {
            int dist = rc.getLocation().distanceSquaredTo(flag.getLocation());
            if (dist < closeFlag && dist != 0) {
                closeFlag = dist;
                nearestFriendlyFlag = flag.getLocation();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (rc.readSharedArray(Constants.offset + i) != 0) {
                MapLocation loc = Comms.decodeLoc(rc.readSharedArray(Constants.offset + i));
                if (rc.getLocation().distanceSquaredTo(loc) < closeFlag) {
                    closeFlag = rc.getLocation().distanceSquaredTo(loc);
                    nearestFriendlyFlag = loc;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            int temp = rc.readSharedArray(Constants.defendPosOffset + i);
            if (temp == 0) continue;
            MapLocation defendPos = Comms.decodeLoc(temp);
            if (rc.canSenseLocation(defendPos)) {
                rc.writeSharedArray(Constants.defendPosOffset + i, 0);
            }
        }

        if (rc.getRoundNum() > 200) {
            FlagInfo[] nearbyFlags = rc.senseNearbyFlags(-1, rc.getTeam());
            for (int i = 0; i < nearbyFlags.length; i++) {
                RobotInfo temp = rc.senseRobotAtLocation(nearbyFlags[i].getLocation());
                boolean danger = false;
                if (temp != null && temp.getTeam() == rc.getTeam().opponent()) {
                    danger = true;
                }
//                if (nearbyFlags[i].getLocation().distanceSquaredTo(nearestFriendlyBase) > 3) {
//                    danger = true;
//                }
                if (danger) {
                    for (int j = 0; j < 3; j++) {
                        if (rc.readSharedArray(Constants.defendPosOffset + i) != 0) continue;
                        rc.writeSharedArray(Constants.defendPosOffset + i, Comms.encode(nearbyFlags[i].getLocation(), 0));
                    }
                }
            }

            defendLoc = null;

            for (int i = 0; i < 3; i++) {
                int temp = rc.readSharedArray(Constants.defendPosOffset + i);
                if (temp == 0) continue;
                if (defendLoc == null) defendLoc = Comms.decodeLoc(temp);
                else {
                    if (rc.getLocation().distanceSquaredTo(defendLoc) > rc.getLocation().distanceSquaredTo(Comms.decodeLoc(temp))) {
                        defendLoc = Comms.decodeLoc(temp);
                    }
                }
            }
        }




        getMoveTarget();
    }

    public void getMoveTarget() throws GameActionException {
        if (rc.getRoundNum() < 3) return;
        targetLocation = null;
        targetEnemyFlag = findNearestEnemyFlag();
        targetEnemyBase = findNearestEnemyBase();
        nearestFriendlyBase = findNearestFriendlyBase();
    }

}
