package why;

import battlecode.common.*;

enum microState {
    ATTACKING,
    DEFENDING,
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
    static microState duckState = microState.NEUTRAL;
    
    public void run() throws GameActionException {

        // try global upgrade, update bfs and baselocations
        indicator = "";
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

        // sense nearby info
        sense();
        getMoveTarget();

        if (targetEnemyFlag != null && rc.canPickupFlag(targetEnemyFlag)) {
            rc.pickupFlag(targetEnemyFlag);
        }

        // decide microstate based on enemies and stuff
        if (enemies.length == 0) {
            duckState = microState.NEUTRAL;
            if (friends.length < Constants.MIN_TEAM_SIZE || teamHealth / (friends.length+1) < Constants.LOW_HEALTH_RETREAT) {
                duckState = microState.RETREATING;
            }
        } else {
            if (friends.length < Constants.MIN_TEAM_SIZE || (enemyTeamDamage != 0 && enemyTeamHealth/teamDamage > teamHealth/enemyTeamDamage+2)) {
                // enemy has more power than us, either defend or retreat
                duckState = microState.RETREATING;
                if (friendWithFlag != null || rc.getLocation().distanceSquaredTo(nearestFriendlyBase) <= Constants.DEFEND_RADIUS) {
                    duckState = microState.DEFENDING;
                }
            } else {
                // we have more power than the enemy, attack
                duckState = microState.ATTACKING;
                if (enemyWithFlag != null) duckState = microState.CHASING;
            }
        }

        if (rc.hasFlag()) duckState = microState.RETREATING;

        indicator += duckState + ", ";

        // build - build traps or fill water
        if (nearestEnemy != null && (duckState == microState.DEFENDING || duckState == microState.RETREATING) && minDistToTrap > 8) {
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

        // attack
        RobotInfo targetEnemy = getAttackableTarget();
        while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            indicator += "attacked, ";
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
        }

        // move: kiting, retreating, defensive, neutral, chasing
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
        } else if (duckState == microState.DEFENDING) {
            // keep distance to enemy but don't back away
            //defend();
            retreat();
        } else if (duckState == microState.NEUTRAL) {
            neutralMove();
        }

        // attack
        targetEnemy = getAttackableTarget();
        while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
            indicator += "attacked, ";
        }

        // heal
        RobotInfo healFriend = getHealableFriend();
        if (healFriend != null) {
            rc.heal(healFriend.getLocation());
            indicator += "healed";
        }

        rc.setIndicatorString(indicator);
    }

    public void neutralMove() throws GameActionException {
        targetLocation = targetEnemyFlag;
        if (targetLocation == null) {
            //targetLocation = targetEnemyBase;
            movement.spreadOut();
            return;
        } 
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
        
        if (bestie == null) {
            indicator += String.format("moving to (%d, %d), ", targetLocation.x, targetLocation.y);
            movement.moveTo(targetLocation, true);
        }
        else {
            movement.moveTo(bestie.getLocation(), true);
            indicator += String.format("moving to bestie at (%d, %d), ", bestie.getLocation().x, bestie.getLocation().y);
        }
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
                if (friend.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack -= 80;
            }
            int score = attack + Math.abs(5-closest) * 10000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            }
        }
        if (bestDir != null) {
            rc.move(bestDir);
        }
    }

    public void retreat() throws GameActionException {
        if (enemies.length != 0 && minDistToEnemy < Constants.RETREAT_ENEMY_RADIUS) {
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
        movement.fastRetreat(nearestFriendlyBase);
    }

    public void defend() throws GameActionException {

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
        }

        minDistToTrap = 1000000;
        MapInfo[] surroundings = rc.senseNearbyMapInfos();
        for (MapInfo info : surroundings) {
            if (info.getTrapType() != TrapType.NONE) {
                minDistToTrap = Math.min(minDistToTrap, rc.getLocation().distanceSquaredTo(info.getMapLocation()));
            }
        }
    }

    public void getMoveTarget() throws GameActionException {
        targetLocation = null;
        targetEnemyFlag = findNearestEnemyFlag();
        targetEnemyBase = findNearestEnemyBase();
        nearestFriendlyBase = findNearestFriendlyBase();
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }

    
}
