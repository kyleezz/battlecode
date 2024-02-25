package bready;

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

    public void run() throws GameActionException {

        if (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
            return;
        }

        MapLocation[] crumbs = rc.senseNearbyCrumbs(-1);
        if (target == null || target.equals(rc.getLocation())) {
            if (crumbs.length > 0) {
                target = crumbs[0];
                rc.setIndicatorString("crumbs");
                movement.moveTo(target);
            } else {
                target = null;
                movement.spreadOut();
            }
        } else {
            movement.moveTo(target);
        }
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
  
}
