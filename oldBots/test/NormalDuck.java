package testt;

import battlecode.common.*;

import java.util.Map;

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

    boolean build = false;
    public void run() throws GameActionException {
        if (!rc.isSpawned()) {
            if (rc.readSharedArray(0) == 1) {
                return;
            }
            rc.writeSharedArray(0, 1);
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            // Pick a random spawn location to attempt spawning in.
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
            return;
        }

        if (build) {
            movement.moveTo(new MapLocation(0, 0));
            return;
        }

        if (rc.getLocation().equals(new MapLocation(35, 15))) {
//            System.out.println("test");
            if (rc.canBuild(TrapType.STUN, new MapLocation(36, 14))) {
                rc.build(TrapType.STUN, new MapLocation(36, 14));
                build = true;
            }
        }else movement.moveTo(new MapLocation(35, 15));

    }

    public NormalDuck(RobotController rc) throws GameActionException {
        super(rc);
    }
}
