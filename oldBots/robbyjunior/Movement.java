package robbyjunior;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Collections;

public class Movement {
    static RobotController rc;
    static CursedRandom rng;

    public Movement(CursedRandom rng, RobotController rc) throws GameActionException {
        this.rc = rc;
        this.rng = rng;
    }

    public static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    boolean followRight = true;
    MapLocation lastObstacle = null;
    int minDist = 1000000;
    MapLocation prevTarget = null;
    int bugLength = 0;

    void calcTurn(MapLocation target) {
        int ax = rc.getMapWidth() / 2 - rc.getLocation().x;
        int ay = rc.getMapHeight() / 2 - rc.getLocation().y;
        int bx = rc.getMapWidth() / 2 - target.x;
        int by = rc.getMapHeight() / 2 - target.y;
        int cprod = ax * by - bx * ay;
        followRight = (cprod > 0);
    }

    void reset(MapLocation target) {
        lastObstacle = null;
        minDist = 1000000;
        bugLength = 0;
        if (prevTarget == null || !prevTarget.equals(target)) calcTurn(target);
        if (rng.nextInt(50) == 0) followRight = !followRight;
    }

    void moveTo(MapLocation target) throws GameActionException {
        if (target == null) return;
        if (!rc.isMovementReady()) return;
        moveCount = 4;

        if (prevTarget == null || !prevTarget.equals(target)) reset(target);
        prevTarget = target;

        MapLocation myLoc = rc.getLocation();
        int d = myLoc.distanceSquaredTo(target);
        if (d <= minDist) reset(target);
        minDist = Math.min(d, minDist);
        
        Direction dir = myLoc.directionTo(target);
        if (lastObstacle != null) dir = myLoc.directionTo(lastObstacle);

        if (rc.canMove(dir)) reset(target);
        /*if (bugLength > Math.max(rc.getMapHeight(), rc.getMapWidth()) / 1.5) {
            reset(target);
            followRight = !followRight;
        }*/

        bugLength++;
        rc.setIndicatorString(String.format("length: %d, followRight %b", bugLength, followRight));
        rc.setIndicatorLine(rc.getLocation(), target, 0, 255, 0);

        for (int i = 0; i < 16; i++) {
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
            MapLocation newLoc = myLoc.add(dir);
            if (!rc.onTheMap(newLoc)) followRight = !followRight;
            else lastObstacle = newLoc;
            if (followRight) dir = dir.rotateRight();
            else dir = dir.rotateLeft();
        }

        if (rc.canMove(dir)) rc.move(dir);
    }

    static int moveCount = 0;
    static Direction curDir = Direction.CENTER;

    static void moveRandom() throws GameActionException {
        if (curDir == Direction.CENTER) curDir = Direction.values()[rng.nextInt(8)+1];
        if (rc.isMovementReady()) {
            if (moveCount == 0 || !rc.canMove(curDir)) {
                moveCount = 4;
                Direction newDirects[] = {
                        curDir,
                        curDir.rotateRight(),
                        curDir.rotateLeft(),
                        curDir.rotateRight().rotateRight(),
                        curDir.rotateLeft().rotateLeft(),
                };
                for (int i=0; i<20; i++) {
                    curDir = newDirects[rng.nextInt(newDirects.length)];
                    if (rc.canMove(curDir)) {
                        rc.move(curDir);
                        return;
                    }
                }
                while (true) {
                    curDir = directions[rng.nextInt(directions.length)];
                    if (rc.canMove(curDir)) {
                        rc.move(curDir);
                        return;
                    }
                }
            }
            moveCount--;
            rc.move(curDir);
        }
    }

    void moveToBroadcast() throws GameActionException {
        MapLocation[] flagLocs = rc.senseBroadcastFlagLocations();
        int mini = 1000000;
        MapLocation closeLoc = null;

        for (MapLocation flagLoc : flagLocs) {
            if (rc.getLocation().distanceSquaredTo(flagLoc) < mini) {
                mini = rc.getLocation().distanceSquaredTo(flagLoc);
                closeLoc = flagLoc;
            }
        }
        
        if (closeLoc == null) moveRandom();
        else moveTo(closeLoc);
    }

    public void spreadOut() throws GameActionException {

        int count[] = new int[9];

        RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());

        for (RobotInfo ri : friends) {
            Direction dir = rc.getLocation().directionTo(ri.getLocation());
            count[dir.ordinal()] ++;
        }

        int least = 100000;
        int best = 0;
        for (int i = 0; i < 8; i++) {
            if (count[i] < least) {
                least = count[i];
                best = i;
            }
        }

        ArrayList<Direction> dirs = new ArrayList<Direction>();
        for (int i = 0; i < 8; i++)
        {
            if (count[i] == least)
            {
                dirs.add(directions[i]);
            }
        }
        Collections.shuffle(dirs);
        for (Direction dir : dirs)
        {
            if (rc.canMove(dir))
            {
                rc.move(dir);
                return;
            }
        }

    }
}