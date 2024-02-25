package attacktryagain;

import battlecode.common.*;

public class NormalDuck extends Duck {

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    MapLocation target = null;
    MapLocation mySpawnLoc = null;

    RobotInfo[] friends;
    RobotInfo[] enemies;

    Communication communication;
    MapLocation bestToGo;
    boolean hasEnemiesNear = false;
    int hostileTurns = 0;


    enum State {
        WAIT,
        ADVANCE,
        CHASE,
        ATTACK,
        IMPROVE_VISION,
        HUNT,
        HUNT_SPAWN,
        HEAL,
        HARASS,
        STAGE_ONE,
        STAGE_TWO,
    }
    public void run() throws GameActionException {
        //if (rc.getRoundNum() >= 500) rc.resign();

        if (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();

            communication.findOurSpawns();

            int startSpawnLoc = rng.nextInt();
            for (int i = 0; i < 27; i++) {
                MapLocation randomLoc = spawnLocs[(startSpawnLoc+i) % 27];
                if (rc.canSpawn(randomLoc)) {
                    rc.spawn(randomLoc);
                    mySpawnLoc = randomLoc;
                    break;
                }
            }

            return;
        }

        hasEnemiesNear = false;
        updateNeighbours();
        if (hostileTurns > 0) hostileTurns--;
        State state = determineState();
        tryAttack(true);


        FlagInfo[] flags = rc.senseNearbyFlags(2, rc.getTeam().opponent());
        for (FlagInfo flag : flags) {
            if (!flag.isPickedUp() && rc.canPickupFlag(flag.getLocation())) {
                rc.pickupFlag(flag.getLocation());
                break;
            }
        }

        if (rc.hasFlag()) {
            MapLocation nearestspawn = action.findRetreat();
            if (rc.getLocation().distanceSquaredTo(nearestspawn) <= 2 && rc.canDropFlag(nearestspawn)) rc.dropFlag(nearestspawn);
            else movement.moveTo(nearestspawn, true);
        }

        if (rc.getID() % 50 != 0) {
            switch (state) {
                case STAGE_ONE: stage_one(); break;
                case STAGE_TWO: stage_two(); break;
                case ADVANCE: advance(); break;
                case ATTACK: maneuver(); break;
                case HUNT_SPAWN: hunt_spawn(); break;
            }
        }

        tryAttack(false);
        action.heal();

        symmetry.findSymmetry();
    }


    MapLocation getHSym(MapLocation a) {
        return new MapLocation(a.x, rc.getMapHeight() - a.y - 1);
    }

    MapLocation getVSym(MapLocation a) {
        return new MapLocation(rc.getMapWidth() - a.x - 1, a.y);
    }

    MapLocation getRSym(MapLocation a) {
        return new MapLocation(rc.getMapWidth() - a.x - 1, rc.getMapHeight() - a.y - 1);
    }

    void hunt_spawn() throws GameActionException {
        MapLocation[] locs = communication.HQs;
        MapLocation[] enemylocs = new MapLocation[3];
        for (int i = 1; i < 4; i++) {
            if (symmetry.failedSymmetry[i] != 0) {
                if (i == 1) {
                    for (int j = 0; j < 3; j++) enemylocs[j] = getVSym(locs[j]);
                } else if (i == 2) {
                    for (int j = 0; j < 3; j++) enemylocs[j] = getHSym(locs[j]);
                } else {
                    for (int j = 0; j < 3; j++) enemylocs[j] = getRSym(locs[j]);
                }
                break;
            }
        }

        if (rc.getID() % 4 == 0) movement.moveTo(enemylocs[1], true);
        else movement.moveTo(enemylocs[0], true);
    }
    void advance() throws GameActionException {
        movement.moveTo(bestToGo, true);
    }

    void tryAttack(boolean first) throws GameActionException {
        if (rc.getActionCooldownTurns() >= 10) return;
        RobotInfo r = getBestAttackTarget();

        if (r == null && first) return;
        else if (r == null && !first) {
            return;
        }
        if (rc.canAttack(r.location)) {
            rc.attack(r.location);
        }
    }

    void stage_one() throws GameActionException {
        if (target == null || target.equals(rc.getLocation())) {
            MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
            if (crumbs.length > 0) {

                int closest = -1;
                int smolDist = 10000000;
                for (int i = 0; i < crumbs.length; i++) {
                    if (rc.getLocation().distanceSquaredTo(crumbs[i]) < smolDist) {
                        smolDist = rc.getLocation().distanceSquaredTo(crumbs[i]);
                        closest = i;
                    }
                }

                target = crumbs[closest];
                rc.setIndicatorString("crumbs");
                movement.moveTo(target, true);
            } else {
                target = null;
                movement.spreadOut();
            }
        } else {
            movement.moveTo(target, true);
        }
    }

    void stage_two() throws GameActionException {
        movement.moveTo(mySpawnLoc, true);
        if (rc.getLocation().distanceSquaredTo(mySpawnLoc) < 4)
            Action.tryPlaceTrap(TrapType.WATER, rc.getLocation());
    }
    State determineState() throws GameActionException {
        if (rc.getRoundNum() < 150) {
            return State.STAGE_ONE;
        }

        if (hasEnemiesNear) {
            hostileTurns = 5;
            return State.ATTACK;
        }

        if (hostileTurns > 0) {
            return State.ADVANCE;
        }

        return State.HUNT_SPAWN;
    }

    void updateNeighbours() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(-1);

        double x = 0;
        double y = 0;
        double w = 0;

        for (RobotInfo r : robots) {
            if (r.getTeam() == rc.getTeam()) {
                x += r.getLocation().x;
                y += r.getLocation().y;
                w += 1;
            } else {
                hasEnemiesNear = true;
            }
        }

        x /= w;
        y /= w;
        bestToGo = new MapLocation((int) x, (int) y);
    }

    RobotInfo getBestAttackTarget() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        AttackTarget[] targets = new AttackTarget[enemies.length];
        int ind = 0;
        for (RobotInfo e: enemies) {
            targets[ind++] = new AttackTarget(e);
        }
        AttackTarget best = null;
        for (AttackTarget t: targets)
            if (t.isBetterThan(best)) best = t;
        if (best == null) return null;
        return best.r;
    }

    class AttackTarget {
        MapLocation loc;
        int health;
        boolean canAttack;
        RobotInfo r;
        int d;

        AttackTarget(RobotInfo r) throws GameActionException {
            loc = r.location;
            health = r.health;
            this.r = r;
            canAttack = rc.canSenseLocation(loc) && rc.canAttack(loc) && rc.isLocationOccupied(loc);
            d = r.location.distanceSquaredTo(new MapLocation(0, 0));
        }

        boolean isBetterThan(AttackTarget at) {
            if (at == null) return true;
            if (at.canAttack && !canAttack) return false;
            if (!at.canAttack && canAttack) return true;
            if (at.health < health) return false;
            if (health < at.health) return true;

            return d <= at.d;
        }
    }
    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
        communication = new Communication(rc);
    }

    void maneuver() throws GameActionException {
        rc.setIndicatorString("Maneuvering");

        MicroTarget[] microtargets = new MicroTarget[9];
        microtargets[0] = new MicroTarget(directions[0]);
        microtargets[1] = new MicroTarget(directions[1]);
        microtargets[2] = new MicroTarget(directions[2]);
        microtargets[3] = new MicroTarget(directions[3]);
        microtargets[4] = new MicroTarget(directions[4]);
        microtargets[5] = new MicroTarget(directions[5]);
        microtargets[6] = new MicroTarget(directions[6]);
        microtargets[7] = new MicroTarget(directions[7]);
        microtargets[8] = new MicroTarget(Direction.CENTER);


        MapLocation m;
        Team myTeam = rc.getTeam();
        Team opponentTeam = myTeam.opponent();

        RobotInfo[] neighbours = rc.senseNearbyRobots(-1);
        int iters = 0;
        for (RobotInfo r: neighbours) {
            if (r == null) continue;
            if (Clock.getBytecodesLeft() < 1500) break;
            m = r.location;
            boolean canSense = rc.canSenseLocation(m);
            if (r.getTeam() == myTeam) {
                microtargets[0].addAlly(r);
                microtargets[1].addAlly(r);
                microtargets[2].addAlly(r);
                microtargets[3].addAlly(r);
                microtargets[4].addAlly(r);
                microtargets[5].addAlly(r);
                microtargets[6].addAlly(r);
                microtargets[7].addAlly(r);
                microtargets[8].addAlly(r);
            } else {
                microtargets[0].addEnemy(r);
                microtargets[1].addEnemy(r);
                microtargets[2].addEnemy(r);
                microtargets[3].addEnemy(r);
                microtargets[4].addEnemy(r);
                microtargets[5].addEnemy(r);
                microtargets[6].addEnemy(r);
                microtargets[7].addEnemy(r);
                microtargets[8].addEnemy(r);
            }
            iters++;
        }
        // Needs 1k Bytecode.
        MicroTarget best = microtargets[0];
        if (microtargets[0].isBetterThan(best)) best = microtargets[0];
        if (microtargets[1].isBetterThan(best)) best = microtargets[1];
        if (microtargets[2].isBetterThan(best)) best = microtargets[2];
        if (microtargets[3].isBetterThan(best)) best = microtargets[3];
        if (microtargets[4].isBetterThan(best)) best = microtargets[4];
        if (microtargets[5].isBetterThan(best)) best = microtargets[5];
        if (microtargets[6].isBetterThan(best)) best = microtargets[6];
        if (microtargets[7].isBetterThan(best)) best = microtargets[7];
        if (microtargets[8].isBetterThan(best)) best = microtargets[8];
        if (rc.senseMapInfo(rc.getLocation().add(best.dir)).isWater()) {
            if (rc.canFill(rc.getLocation().add(best.dir))) rc.fill(rc.getLocation().add(best.dir));
        }
        if (rc.canMove(best.dir)) rc.move(best.dir);

    }

    class MicroTarget {
        Direction dir;
        double dps_targetting = 0;
        double dps_defending = 0;
        double net_dps;
        int minDistToEnemy = 100000;
        int action;
        boolean canMove;
        MapLocation nloc;

        MicroTarget(Direction dir) throws GameActionException {
            this.dir = dir;
            nloc = rc.getLocation().add(dir);
            canMove = rc.canMove(dir) || (rc.onTheMap(rc.getLocation().add(dir)) && rc.senseMapInfo(rc.getLocation().add(dir)).isWater());
            if (!rc.onTheMap(rc.getLocation().add(dir))) canMove = false;
            if (rc.canSenseLocation(nloc)) {
                net_dps -= 150;
            }
            // minDistToEnemy = nloc.distanceSquaredTo(previousEnemy);
        }

        void addEnemy(RobotInfo r) throws GameActionException {
            //int start = Clock.getBytecodesLeft();
            if (!canMove) return;
            int d = nloc.distanceSquaredTo(r.location);
            if (d <= 20) {
                dps_targetting += 150;
                net_dps += 150;
            }
            if (d <= minDistToEnemy)
                minDistToEnemy = d;
        }


        void addAlly(RobotInfo r) throws GameActionException {
            if (!canMove) return;
            if (nloc.distanceSquaredTo(r.location) <= 150)
                dps_defending += 150;
        }

        int safe() {
            if (net_dps > 0) return 2;
            if (dps_defending < dps_targetting) return 3;
            return 4;
        }

        boolean inRange() {
            return minDistToEnemy <= action;
        }

        boolean isBetterThan(MicroTarget mt) {
            if (!canMove) return false;
            if (mt.safe() > safe()) return false;
            if (mt.safe() < safe()) return true;

            if (mt.safe() == 1 && safe() == 1) {
                if (mt.dps_targetting < dps_targetting) return false;
                if (mt.dps_targetting > dps_targetting) return true;
            }

            // the idea here is attack first, then move out of range.
            if (mt.inRange() && !inRange()) return false;
            if (!mt.inRange() && inRange()) return true;

            if (mt.inRange() && inRange()) return minDistToEnemy >= mt.minDistToEnemy;
            else return minDistToEnemy <= mt.minDistToEnemy;
        }
    }
}
