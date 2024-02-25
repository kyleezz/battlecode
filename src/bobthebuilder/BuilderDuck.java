package bobthebuilder;

import battlecode.common.*;

public class BuilderDuck extends Duck {

    static String indicator = "";

    static RobotInfo[] enemies;
    static RobotInfo[] friends;
    static FlagInfo[] friendlyFlags;
    static FlagInfo closestFriendlyFlag;

    static int parity = -1;
    static int CHECKERBOARD = 160;

    static RobotInfo nearestFriend = null;
    static int minDistToFriend = 1000000;
    static RobotInfo nearestEnemy = null;
    static int minDistToEnemy = 1000000;
    static MapLocation nearbyTrapLocs[];
    static int nearbyTraps;
    MapInfo[] surroundings;


    int builderNum = 0;


    public void run() throws GameActionException {

        indicator = "Builder, ";

        int vibes = -1;
        if (rc.isSpawned()) {
            sense();
        }
        comms.updateComms(vibes);
        globalbfs.updateGrid();
        tryUpgrade();
        if (rc.getRoundNum() == 2) getBaseLocs();

        if (!rc.isSpawned()) {
            MapLocation spawnLoc = findSpawnLocation(rc.getRoundNum() > 100);
            if (spawnLoc == null) return;
            rc.spawn(spawnLoc);
            if (rc.getRoundNum() == 1) recordBaseLoc();
            if (parity == -1) {
                FlagInfo flag = rc.senseNearbyFlags(-1)[0];
                parity = (flag.getLocation().x + flag.getLocation().y) % 2;
            }
            sense();
        }        

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
        
        indicator += "rollout, ";

        if (enemies.length == 0) {
            neutralMove();
        } else {

            // we see enemy

            if (rc.getCrumbs() > 400) {
                tryTrap(2, 4);
            } else if (rc.senseNearbyFlags(-1, rc.getTeam()).length > 0) {
                tryTrap(1, 3);
            }
            
            if (minDistToEnemy < 10 || friends.length < enemies.length) kite();
            else moveAttack();

            if (rc.getCrumbs() > 400) {
                tryTrap(2, 4);
            } else if (rc.senseNearbyFlags(-1, rc.getTeam()).length > 0) {
                tryTrap(1, 3);
            }

        }
        
        symmetry.findSymmetry();
        rc.setIndicatorString(indicator);

    }

    public void moveAttack() throws GameActionException {
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
            }
            int score = attack - closest * 100000;
            if (Math.abs(Constants.directions[i].dx) + Math.abs(Constants.directions[i].dy) == 1) score -= 50000;
            if (closest < 4) score += 1000000;
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

    public void neutralMove() throws GameActionException {
        MapLocation targetLocation = findNearestEnemyFlag();

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
        } else {
            indicator += "moving to " + targetLocation;
            movement.moveTo(targetLocation, true);
        }
    }

    public void sense() throws GameActionException {
        friends = rc.senseNearbyRobots(-1, rc.getTeam());
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        surroundings = rc.senseNearbyMapInfos();

        nearbyTrapLocs = new MapLocation[30];
        nearbyTraps = 0;

        for (MapInfo m : surroundings) {
            if (m.getTrapType() != TrapType.NONE) {
                nearbyTrapLocs[nearbyTraps] = m.getMapLocation();
                nearbyTraps = Math.min(29, nearbyTraps + 1);
            }
        }

        if (nearbyTraps == 29) nearbyTraps = 30;

        int minDistToEnemy = 1000000;
        int minDistToFriend = 1000000;
        nearestEnemy = null;
        nearestFriend = null;

        friendlyFlags = rc.senseNearbyFlags(-1, rc.getTeam());
        int closestFriendyFlagDist = 1000000;
        closestFriendlyFlag = null;
        for (FlagInfo flag : friendlyFlags) {
            int dist = flag.getLocation().distanceSquaredTo(rc.getLocation());
            if (dist < closestFriendyFlagDist) {
                closestFriendyFlagDist = dist;
                closestFriendlyFlag = flag;
            }
        }


        for (RobotInfo e : enemies) {
            int dist = rc.getLocation().distanceSquaredTo(e.getLocation());
            if (dist < minDistToEnemy) {
                minDistToEnemy = dist;
                nearestEnemy = e;
            }
        }

        for (RobotInfo f : friends) {
            int dist = rc.getLocation().distanceSquaredTo(f.getLocation());
            if (dist < minDistToFriend) {
                minDistToFriend = dist;
                nearestFriend = f;
            }
        }
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
                if (rc.getLocation().distanceSquaredTo(rc.getLocation().add(trapDirs[i])) < 13) nearbyEnemies ++;
                closest = Math.min(closest, rc.getLocation().add(trapDirs[i]).distanceSquaredTo(rc.getLocation()));
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

    public void kite() throws GameActionException {

        int bestScore = 1000000;
        Direction bestDir = null;
        // enemies may have changed after we attacked, friends stay the same
        enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
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
            int score = attack - closest * 10000;
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

    public void checkerBoard() throws GameActionException {
        for (Direction dir : Constants.directions) {
            MapLocation digLoc = rc.getLocation().add(dir);
            if (!rc.canDig(digLoc) || (digLoc.x + digLoc.y) % 2 != parity) continue;
            rc.dig(digLoc);
        }
    }

    public void findFarmSpot() throws GameActionException {
        if (rc.getRoundNum() == 1) return;
        int bestScore = 0;
        Direction bestDir = null;
        for (Direction dir : Constants.directions) {
            if (!rc.canMove(dir)) continue;
            int score = 0;
            for (MapLocation baseLoc : baseLocs) {
                score += baseLoc.distanceSquaredTo(rc.getLocation().add(dir));
            }

            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }

        if (bestDir != null && rc.canMove(bestDir)) rc.move(bestDir);
    }

    public BuilderDuck(RobotController rc) throws GameActionException {
        super(rc);
        builderNum = rc.readSharedArray(Constants.BUILDERCOUNT);
    }
    
}
