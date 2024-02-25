package kity2fortress;

import battlecode.common.*;

public class BuilderDuck extends Duck {

    // macro variables
    static String indicator = "";
    static MapLocation targetLocation;
    static MapLocation targetEnemyFlag;
    static MapLocation targetEnemyBase;
    static MapLocation nearestFriendlyBase;
    static FlagInfo[] enemyFlags;
    static FlagInfo[] friendlyFlags;
    static FlagInfo closestFriendlyFlag;

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
    static boolean movedWithFlag = false;

    static int parity = -1;
    static int CHECKERBOARD = 160;

    public void run() throws GameActionException {

        // try global upgrade, update bfs and baselocations
        indicator = "";
        tryUpgrade();
        if (rc.getRoundNum() == 2) getBaseLocs();

        int vibes = -1;
        if (rc.isSpawned()) {
            goToHelpLoc = null;
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

        comms.updateEnemies(enemyWithFlag == null ? null : enemyWithFlag.location);
        comms.updateComms(vibes);
        globalbfs.updateGrid();

        // spawn in
        if (!rc.isSpawned()) {
            // if round > 100, see if any base needs help
            MapLocation spawnLoc = findSpawnLocation(rc.getRoundNum() > 100);
            if (spawnLoc == null) {
                return;
            }
            rc.spawn(spawnLoc);
            if (parity == -1) {
                FlagInfo flag = rc.senseNearbyFlags(-1)[0];
                parity = (flag.getLocation().x + flag.getLocation().y) % 2;
            }
            if (rc.getRoundNum() == 1) recordBaseLoc();
            sense();            
            if (closestFriendlyFlag != null) {
                tryTrap(4, 0);
            }
        }

        // prep rounds, spread out and collect crumbs
        if (rc.getRoundNum() < CHECKERBOARD && (rc.getLevel(SkillType.BUILD) < 4 || (rc.getLevel(SkillType.BUILD) < 6 && rc.getCrumbs() > 1000))) {
            // start digging
            checkerBoard();
            indicator += "farming build lvl, ";
            if (rc.isActionReady()) {
                movement.spreadOut();
            }
            checkerBoard();
            movement.spreadOut();
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

        // decide microstate based on enemies and stuff
        if (enemies.length == 0) {
            duckState = microState.NEUTRAL;
        } else {
            duckState = microState.ATTACKING;
        }

        indicator += duckState + ", ";

        if (enemies.length != 0 && (minDistToEnemy > 4 || friends.length > enemies.length)) {
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

            //8000, 4000, 2000

            if (rc.getCrumbs() > 8000 || rc.getRoundNum() < 210) {
                tryTrap(4, 0);
                tryTrap(2, 0);
            } else if (rc.senseNearbyFlags(-1).length > 0) {
                tryTrap(2, 3);
            }

            else if (rc.getCrumbs() > 3000 && rc.getRoundNum() > 300) {
                tryTrap(2, 4);
            } else if (rc.getCrumbs() > 400) {
                tryTrap(4, 4);
            } 
        } else if (rc.getCrumbs() > 4000 || rc.getRoundNum() < 210) {
            tryTrap(4, 0);
        }


        // attack
        RobotInfo targetEnemy = getAttackableTarget();
        while (targetEnemy != null && rc.canAttack(targetEnemy.getLocation())) {
            indicator += "attacked, ";
            rc.attack(targetEnemy.getLocation());
            targetEnemy = getAttackableTarget();
        }

        // heal
        if (minDistToEnemy > 10) {
            RobotInfo healFriend = getHealableFriend();
            if (healFriend != null && rc.canHeal(healFriend.getLocation())) {
                rc.heal(healFriend.getLocation());
                indicator += "healed";
            }
        }
        
        // move: kiting, retreating, neutral
        if (duckState == microState.ATTACKING) {
            if (rc.isActionReady()) {
                if (rc.getHealth() < 600) retreat();
                else if (minDistToEnemy > 12) moveAttack();
                else kite();
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
        if (healFriend != null && rc.canHeal(healFriend.getLocation())) {
            rc.heal(healFriend.getLocation());
            indicator += "healed";
        }

        rc.setIndicatorString(indicator);
        symmetry.findSymmetry();
    }

    public void checkerBoard() throws GameActionException {
        for (Direction dir : Constants.directions) {
            MapLocation digLoc = rc.getLocation().add(dir);
            if (!rc.canDig(digLoc) || (digLoc.x + digLoc.y) % 2 != parity) continue;
            rc.dig(digLoc);
        }
    }

    public void flagStuff() throws GameActionException {

        if (movedWithFlag) return;

        long bestScore = 1000000000000L;
        Direction bestDir = null;
        for (Direction dir : Constants.directions) {
            MapLocation newFlagLoc = rc.getLocation().add(dir);
            if (!rc.canMove(dir) && !rc.canSenseRobotAtLocation(newFlagLoc)) continue;
            long score = 0;
            
            MapInfo surroundings[] = rc.senseNearbyMapInfos(newFlagLoc, 2);
            for (MapInfo m : surroundings) {
                if (m.isSpawnZone()) score += 1000000000L;
                else if (!m.isWall()) score += 10000000000L;
            }
            score = Math.min(score, 23500000000L);
            for (MapLocation friendBase : baseLocs) if (friendBase != rc.getLocation()) {
                score -= newFlagLoc.distanceSquaredTo(friendBase);
            }
            score -= 10000L * newFlagLoc.distanceSquaredTo(targetEnemyBase);
            if (score < bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        if (bestDir == null || !rc.canMove(bestDir)) return;
        movedWithFlag = true;
        rc.move(bestDir);
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

        int maxIndex = -1;
        int maxEnemies = 0;
        int minDist = 0;

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
            int closest = 1000000;
            for (RobotInfo r : enemies) {
                if (r.getLocation().distanceSquaredTo(rc.getLocation().add(trapDirs[i])) < 13) nearbyEnemies ++;
                closest = Math.min(closest, rc.getLocation().add(trapDirs[i]).distanceSquaredTo(r.getLocation()));
            }
            if (nearbyEnemies > maxEnemies) {
                maxEnemies = nearbyEnemies;
                maxIndex = i;
                minDist = closest;
            } else if (nearbyEnemies == maxEnemies && minDist > closest) {
                maxIndex = i;
                minDist = closest;
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
        if (goToHelpLoc != null) {
            goToHelp = goToHelpLoc;
        } else {
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

        // look for sentinel
        RobotInfo sentinel = null;
        int sentinelDist = 100000000;
        if (rc.getLocation().distanceSquaredTo(nearestFriendlyBase) <= 20 && friendlyFlags.length != 0){// && Clock.getBytecodesLeft() > 5000) {
            indicator += "check for sentinel, ";
            for (RobotInfo friend : friends) {
                int dist = friend.getLocation().distanceSquaredTo(closestFriendlyFlag.getLocation()) * 100000 + friend.getID();
                if (dist < sentinelDist) {
                    sentinelDist = dist;
                    sentinel = friend;
                }
            }
            if (sentinel == null) {
                // ur the sentinel
                indicator += "am sentinel, ";
                if (rc.getLocation().distanceSquaredTo(closestFriendlyFlag.getLocation()) == 0) {
                    tryPlaceTrap(TrapType.STUN, rc.getLocation().add(Direction.NORTHEAST));
                    tryPlaceTrap(TrapType.STUN, rc.getLocation().add(Direction.SOUTHEAST));
                    tryPlaceTrap(TrapType.STUN, rc.getLocation().add(Direction.NORTHWEST));
                    tryPlaceTrap(TrapType.STUN, rc.getLocation().add(Direction.SOUTHWEST));
                    return;
                }
                movement.moveTo(closestFriendlyFlag.getLocation(), true);
                return;
            } else {
                indicator += "found sentinel " + sentinel.getID() + ",";
            }
        }

        // look for bestie
        RobotInfo bestie = null;
        int bestieScore = rc.getLocation().distanceSquaredTo(targetLocation) * 1000 - rc.getHealth();
        for (RobotInfo friend: friends) {
            if (friend.hasFlag()) continue;
            if (sentinel != null && friend.getID() == sentinel.getID()) continue;
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
            int score = attack + -closest * 100000;
            if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) == 1) score -= 50000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
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
            if (rc.getActionCooldownTurns() >= 20 || rc.getHealth() < 400) score = attack + -closest * 10000;
            if (score < bestScore) {
                bestScore = score;
                bestDir = Constants.directions[i];
            } else if (score == bestScore) {
                if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) > Math.abs(bestDir.dx) + Math.abs(bestDir.dy)) {
                    bestDir = Constants.directions[i];
                }
            }
        }
        if (bestDir != null && rc.canMove(bestDir)) {
            rc.move(bestDir);
        }
    }

    public void retreat() throws GameActionException {
        if (rc.hasFlag()) {
            movement.fastRetreat(nearestFriendlyBase);
            return;
        }
        if (enemies.length != 0 && minDistToEnemy < Constants.RETREAT_ENEMY_RADIUS && !rc.hasFlag()) {
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
        movement.moveTo(nearestFriendlyBase, true);
    }

    public RobotInfo getHealableFriend() throws GameActionException {

        if (!rc.isActionReady()) return null;
        int bestFriendScore = 0;
        RobotInfo bestFriend = null;

        for (RobotInfo friend : friends) {
            if (!rc.canHeal(friend.getLocation())) continue;
            int score = Math.abs(Constants.IDEAL_HEALTH - friend.getHealth());
            score += Util.getRobotAttackDamage(friend) * 10000;
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

        int closestFriendyFlagDist = 1000000;
        closestFriendlyFlag = null;

        for (FlagInfo flag : friendlyFlags) {
            int dist = flag.getLocation().distanceSquaredTo(rc.getLocation());
            if (dist < closestFriendyFlagDist) {
                closestFriendyFlagDist = dist;
                closestFriendlyFlag = flag;
            }
        }

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

    public BuilderDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
