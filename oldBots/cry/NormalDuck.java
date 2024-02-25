package cry;

import battlecode.common.*;

public class NormalDuck extends Duck {

    // macro variables
    static MapLocation targetLocation;
    static MapLocation targetEnemyFlag;
    static MapLocation targetEnemyBase;
    static MapLocation nearestFriendlyBase;
    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;

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
    static int closeFriends;
    static int teamDamage;
    static int minDistToTrap;

    static String indicator = "";
    

    public void run() throws GameActionException {

        indicator = "";

        comms.updateComms(0);
        globalbfs.updateGrid();
    
        // record base centres
        if (rc.getRoundNum() == 2) getBaseLocs();

        // spawn in
        if (!rc.isSpawned()) {
            // if round > 100, see if any base needs help
            MapLocation spawnLoc = findSpawnLocation(rc.getRoundNum() > 100);
            if (spawnLoc == null) return;
            rc.spawn(spawnLoc);
            if (rc.getRoundNum() == 1) recordBaseLoc();
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
            return;
        }

        if (rc.getRoundNum() < 201) {

            for (Direction dir : Constants.directions) {
                MapLocation tmp = rc.adjacentLocation(dir);
                if (rc.onTheMap(tmp) && rc.senseMapInfo(tmp).isDam()) {
                    symmetry.findSymmetry();
                    return;
                }
            }

            MapLocation target = findNearestEnemyFlag();
            if (target == null) target = findNearestEnemyBase();
            movement.moveTo(target, true);
            symmetry.findSymmetry();
            return;
        }

        if (rc.getRoundNum() < Constants.TEMP_RETREAT) {
            // do sumn later
        }

        // macro sense targets
        getMoveTarget();
        sense();

        // if flag is near, see if we can pick it up this turn
        if (targetEnemyFlag != null && rc.canPickupFlag(targetEnemyFlag)) {
            movement.resetRetreat();
            rc.pickupFlag(targetEnemyFlag);
        } else if (targetEnemyFlag != null && targetEnemyFlag.distanceSquaredTo(rc.getLocation()) < 9 && rc.isActionReady() && rc.isMovementReady()) {
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

        if (rc.hasFlag()) {
            movement.fastRetreat(nearestFriendlyBase);
            return;
        }

        // try build a trap
        if (nearestEnemy != null && rc.isActionReady()) {
            tryBuildTrap();
        }        

        // attack
        RobotInfo targetEnemy = getAttackableTarget();
        while (rc.isActionReady() && targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
        }

        // move: kiting, retreating, neutral, chasing
        if (enemies.length == 0 || 
        (nearestEnemy != null && rc.senseMapInfo(rc.getLocation().add(rc.getLocation().directionTo(nearestEnemy.getLocation()))).isWall())) {
            neutralMove();
        }
        else {
            // special cases time baby
            if (rc.isActionReady()) moveAttack();
            else kite();
        }

        // attack
        targetEnemy = getAttackableTarget();
        while (rc.isActionReady() && targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
        }

        // heal
        RobotInfo healFriend = getHealableFriend();
        if (healFriend != null) {
            rc.heal(healFriend.getLocation());
        }

        rc.setIndicatorString(indicator);
        symmetry.findSymmetry();
    }

    public void moveAttack() throws GameActionException {
        // find enemy we can hit and move towards it, minise amount of enemies that can hit us
        int bestScore = 1000000;
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
            }
        }
        if (bestDir != null) {
            rc.move(bestDir);
        }

    }

    public void neutralMove() throws GameActionException {
        targetLocation = targetEnemyFlag;

        float bestVal = 0;
        MapLocation goToHelp = null;
        MapLocation closestFriend = null;
        int closestDist = 1000000;
        for (int i : Constants.DUCKLOCS) {
            if (rc.readSharedArray(i) == 0) continue;
            MapLocation helpLoc = Comms.decodeLoc(rc.readSharedArray(i));
            if (helpLoc.equals(rc.getLocation())) continue;
            if (rc.getLocation().distanceSquaredTo(helpLoc) < closestDist) {
                closestDist = rc.getLocation().distanceSquaredTo(helpLoc);
                closestFriend = helpLoc;
            }
            float helpVal = Comms.decodeVal(rc.readSharedArray(i));
            float val = helpVal/(rc.getLocation().distanceSquaredTo(helpLoc));
            if (val > bestVal) {
                bestVal = val;
                goToHelp = helpLoc;
            }
        }
        
        if (goToHelp != null && ((bestVal > 0.1 && rc.getLocation().distanceSquaredTo(goToHelp) < 400) || targetLocation == null)) {
            targetLocation = goToHelp;
        }

        MapLocation nearbyCrumb = findNearestCrumb(10);
        if (nearbyCrumb != null) targetLocation = nearbyCrumb;
        if (friendWithFlag != null) {
            int closer = 0;
            for (RobotInfo friend: friends) {
                if (friend.hasFlag()) continue;
                if (friend.getLocation().distanceSquaredTo(friendWithFlag.getLocation()) < rc.getLocation().distanceSquaredTo(friendWithFlag.getLocation())) closer ++;
            }
            if (closer < Constants.ESCORT_SIZE) {
                targetLocation = nearestFriendlyBase;
                if (rc.getLocation().distanceSquaredTo(friendWithFlag.getLocation()) > Constants.ESCORT_DISTANCE) {
                    movement.moveTo(friendWithFlag.getLocation(), true);
                } else {
                    movement.moveTo(friendWithFlag.getLocation().add(friendWithFlag.getLocation().directionTo(targetLocation).opposite()), false);
                }
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
        
        if (bestie == null && rc.getHealth() < Constants.IDEAL_HEALTH) {
            movement.moveTo(closestFriend, true);
            return;
        }

        if (bestie == null || closeFriends > Constants.MIN_TEAM_SIZE) {
            movement.moveTo(targetLocation, true);
        }
        else {
            movement.moveTo(bestie.getLocation(), true);
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
                if (friend.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack -= 80;
            }
            int score = attack + Math.abs(10-closest) * 10000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            }
        }
        if (bestDir != null) {
            rc.move(bestDir);
        }
    }

    public RobotInfo getAttackableTarget() throws GameActionException {
        if (!rc.isActionReady()) return null;

        int bestEnemyScore = 1000000;
        RobotInfo bestEnemy = null;

        for (RobotInfo enemy : enemies) {
            if (!rc.canAttack(enemy.getLocation())) continue;
            if (enemy.getHealth() <= rc.getAttackDamage()) return enemy;
            int totalCanAttack = rc.getAttackDamage();
            for (RobotInfo friend : friends) {
                if (friend.getLocation().distanceSquaredTo(enemy.getLocation()) < Constants.ATTACK_RADIUS && friend.getID() > rc.getID()) totalCanAttack += Util.getRobotAttackDamage(friend);
            }
            if (enemy.getHealth() <= totalCanAttack) return enemy;
            int score = rc.getLocation().distanceSquaredTo(enemy.getLocation()) * 100;
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

    public void tryBuildTrap() throws GameActionException {
        if (minDistToTrap < 6) return;
        if (enemies.length > 6) {
            Direction enemyDir = rc.getLocation().directionTo(nearestEnemy.getLocation());
            Direction[] trapDirs = {
                    enemyDir,
                    enemyDir.rotateLeft(),
                    enemyDir.rotateRight(),
                    Direction.CENTER,
            };
            for (Direction trapDir : trapDirs) {
                tryPlaceTrap(TrapType.STUN, rc.getLocation().add(trapDir));
            }
        } else if (rc.getCrumbs() > 2000) {
            Direction enemyDir = rc.getLocation().directionTo(nearestEnemy.getLocation());
            Direction[] trapDirs = {
                    enemyDir,
                    enemyDir.rotateLeft(),
                    enemyDir.rotateRight(),
                    Direction.CENTER,
            };
            for (Direction trapDir : trapDirs) {
                tryPlaceTrap(TrapType.STUN, rc.getLocation().add(trapDir));
            }
        }
    }

    public void sense() throws GameActionException {
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

        for (FlagInfo enemyFlag : enemyFlags) {
            tryWriteEnemyFlag(enemyFlag);
        }
    }

    public void getMoveTarget() throws GameActionException {
        targetLocation = null;
        targetEnemyFlag = findNearestEnemyFlag();
        targetEnemyBase = findNearestEnemyBase();
        nearestFriendlyBase = findNearestFriendlyBase();
    }

    public boolean tryPlaceTrap(TrapType trap, MapLocation loc) throws GameActionException {
        if (rc.canBuild(trap, loc)) {
            rc.build(trap, loc);
            return true;
        }
        return false;
    }

    public RobotInfo getHealableFriend() throws GameActionException {

        if (!rc.isActionReady()) return null;
        int bestFriendScore = 0;
        RobotInfo bestFriend = null;

        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.getLocation())) continue;
            int score = Math.abs(Constants.IDEAL_HEALTH - friend.getHealth());
            score += Util.getRobotAttackDamage(friend);
            if (friend.hasFlag()) score += 100;
            if (friend.getHealth() + rc.getHealAmount() > 1000) score = 1;
            if (score > bestFriendScore) {
                bestFriendScore = score;
                bestFriend = friend;
            }
        }
        return bestFriend;
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}