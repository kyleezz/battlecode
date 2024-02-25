package alpha;

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
            /*if (rc.readSharedArray(0) == 1) {
                return;
            }
            rc.writeSharedArray(0, 1);*/
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            // Pick a random spawn location to attempt spawning in.
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
            return;
        }
        
        if (target == null || target.equals(rc.getLocation())) {
            target = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
        }

        movement.moveTo(target);
    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
