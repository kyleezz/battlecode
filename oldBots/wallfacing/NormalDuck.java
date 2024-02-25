package wallfacing;

import battlecode.common.*;

enum microState {
    ATTACKING,
    RETREATING,
    NEUTRAL,
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
    static int closeFriends;
    static int lotsaFriends = 0;
    static microState duckState = microState.NEUTRAL;

    public void run() throws GameActionException {

        // try global upgrade, update bfs and baselocations
        indicator = "";
        tryUpgrade();
        if (rc.getRoundNum() == 2) getBaseLocs();

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

        // spawn in
        if (!rc.isSpawned()) {
            // if round > 100, see if any base needs help
            MapLocation spawnLoc = findSpawnLocation(rc.getRoundNum() > 100);
            if (spawnLoc == null) return;
            rc.spawn(spawnLoc);
            if (rc.getRoundNum() == 1) recordBaseLoc();
            sense();
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
            if (enemies.length * 2 <= (friends.length + 1) * 3) duckState = microState.ATTACKING;
            else duckState = microState.RETREATING;
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

        if (nearestEnemy != null) {

            if (minDistToEnemy > 4 && minDistToEnemy < 16 && rc.getCrumbs() > 500) {
                tryTrap(4, 6);
            } else if (rc.senseNearbyFlags(16, rc.getTeam()).length > 0) {
                tryTrap(2, 3);
            }
        }


        // attack
        RobotInfo targetEnemy = getAttackableTarget();
        while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            indicator += "attacked, ";
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
        }

        // move: kiting, retreating, neutral
        if (duckState == microState.ATTACKING) {
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
        symmetry.findSymmetry();
    }
    
    public void tryTrap(int trapRadius, int minEnemies) throws GameActionException {
        if (!rc.isActionReady()) return;

        Direction[] trapDirs = {
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

        int maxIndex = -1;
        int maxEnemies = 0;

        MapLocation[] nearbyTrapLocs = new MapLocation[20];
        int nearbyTraps = 0;
        MapInfo[] surroundings = rc.senseNearbyMapInfos(-1);

        for (MapInfo m : surroundings) {
            if (m.getTrapType() != TrapType.NONE) {
                nearbyTrapLocs[nearbyTraps] = m.getMapLocation();
                nearbyTraps = Math.min(19, nearbyTraps + 1);
            }
        }

        if (nearbyTraps == 19) nearbyTraps = 20;

        for (int i = 0; i < 9; i++) {
            if (!rc.canBuild(TrapType.STUN, rc.getLocation().add(trapDirs[i]))) continue;
            
            boolean nearTrap = false;
            for (int j = 0; j < nearbyTraps; j++) {
                if (nearbyTrapLocs[j].distanceSquaredTo(rc.getLocation().add(trapDirs[i])) <= trapRadius) {
                    nearTrap = true;
                    break;
                }
            }
            if (nearTrap) continue;
            int nearbyEnemies = 0;
            for (RobotInfo r : enemies) {
                if (r.getLocation().distanceSquaredTo(rc.getLocation().add(trapDirs[i])) < 13) nearbyEnemies ++;
            }
            if (nearbyEnemies > maxEnemies) {
                maxEnemies = nearbyEnemies;
                maxIndex = i;
            }
        }

        if (maxIndex != -1 && maxEnemies >= minEnemies) {
            tryPlaceTrap(TrapType.STUN, rc.getLocation().add(trapDirs[maxIndex]));
        }

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

        if (closeFriends > Constants.CLOSE_FRIEND_SIZE && enemies.length < friends.length) {
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
            int closest = 1000;
            MapLocation newLoc = rc.getLocation().add(Constants.directions[i]);
            for (RobotInfo enemy : enemies) {
                if (enemy.getLocation().distanceSquaredTo(newLoc) <= Constants.ATTACK_RADIUS) attack += Util.getRobotAttackDamage(enemy);
                closest = Math.min(closest, 2 * Util.attackRingDist(enemy.getLocation(), newLoc));
                if (enemy.hasFlag()) closest = Math.min(closest, 2 * Util.attackRingDist(enemy.getLocation(), newLoc) - 1);
            }
            int score = attack + closest * 100000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            } else if (score == bestScore) {
                if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) < Math.abs(bestDir.dx) + Math.abs(bestDir.dy)) {
                    bestDir = Constants.directions[i];
                }
            }
        }
        indicator += "moveAttack" + bestScore;
        if (bestDir != null && rc.canMove(bestDir)) {
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
        //System.out.println(rc.getActionCooldownTurns());
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
            if (rc.getActionCooldownTurns() >= 20) score = attack + -closest * 10000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            } else if (score == bestScore) {
                if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) < Math.abs(bestDir.dx) + Math.abs(bestDir.dy)) {
                    bestDir = Constants.directions[i];
                }
            }
        }
        if (bestDir != null && rc.canMove(bestDir)) {
            rc.move(bestDir);
        }
    }

    public void retreat() throws GameActionException {
        if (rc.hasFlag()) movement.fastRetreat(nearestFriendlyBase);
        else movement.moveTo(nearestFriendlyBase, true);
    }

    public RobotInfo getHealableFriend() throws GameActionException {

        if (!rc.isActionReady()) return null;
        int bestFriendScore = 0;
        RobotInfo bestFriend = null;

        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.getLocation())) continue;
            int score = Math.abs(Constants.IDEAL_HEALTH - friend.getHealth());
            score += Util.getRobotAttackDamage(friend);
            if (friend.hasFlag()) score += 1000;
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
                if (friend.getLocation().distanceSquaredTo(enemy.getLocation()) < Constants.ATTACK_RADIUS) totalCanAttack += Util.getRobotAttackDamage(friend);
            }
            if (enemy.getHealth() <= totalCanAttack) return enemy;
            int score = rc.getLocation().distanceSquaredTo(enemy.getLocation()) * 100;
            score += enemy.getHealth();
            score -= enemy.getAttackLevel();
            if (enemy.hasFlag()) score -= 100000;
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

        getMoveTarget();
    }

    public void getMoveTarget() throws GameActionException {
        if (rc.getRoundNum() < 3) return;
        targetLocation = null;
        targetEnemyFlag = findNearestEnemyFlag();
        targetEnemyBase = findNearestEnemyBase();
        nearestFriendlyBase = findNearestFriendlyBase();
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
