package alphamovement;

import battlecode.common.*;
public class NormalDuck extends Duck {


    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
    final Direction[] directions = {
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

    public void run() throws GameActionException {

        if (!rc.isSpawned()) {
//            if (rc.readSharedArray(0) == 1) {
//                return;
//            }
//            rc.writeSharedArray(0, 1);
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            // Pick a random spawn location to attempt spawning in.
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
        } else {
            //moveTo(new MapLocation(49, 51));
            if (target == null || target.equals(rc.getLocation())) {
                target = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
            }
            moveTo(target);

//            if (rc.canPickupFlag(rc.getLocation())) {
//                rc.pickupFlag(rc.getLocation());
//                rc.setIndicatorString("Holding a flag!");
//            }
//            // If we are holding an enemy flag, singularly focus on moving towards
//            // an ally spawn zone to capture it! We use the check roundNum >= SETUP_ROUNDS
//            // to make sure setup phase has ended.
//            if (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
//                MapLocation[] spawnLocs = rc.getAllySpawnLocations();
//                MapLocation firstLoc = spawnLocs[0];
//                Direction dir = rc.getLocation().directionTo(firstLoc);
//                if (rc.canMove(dir)) rc.move(dir);
//            }
//            // Move and attack randomly if no objective.
//            Direction dir = directions[rng.nextInt(directions.length)];
//            MapLocation nextLoc = rc.getLocation().add(dir);
//            if (rc.canMove(dir)) {
//                rc.move(dir);
//            } else if (rc.canAttack(nextLoc)) {
//                rc.attack(nextLoc);
//                System.out.println("Take that! Damaged an enemy that was in our way!");
//            }
//
//            // Rarely attempt placing traps behind the robot.
//            MapLocation prevLoc = rc.getLocation().subtract(dir);
//            if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && rng.nextInt() % 37 == 1)
//                rc.build(TrapType.EXPLOSIVE, prevLoc);
//            // We can also move our code into different methods or classes to better organize it!
        }
    }

}
