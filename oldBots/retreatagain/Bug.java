package retreatagain;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Bug {

    public static RobotController rc;
    public Bug(CursedRandom rng, RobotController rc) throws GameActionException {
        this.rc = rc;
    }
    public static MapLocation dest;

    public enum BugState {
        DIRECT, BUG
    }

    public enum WallSide {
        LEFT, RIGHT
    }

    public static BugState bugState;
    public static WallSide bugWallSide = WallSide.LEFT;
    private static int bugStartDistSq;
    private static Direction bugLastMoveDir;
    public static Direction bugLookStartDir;
    private static int bugRotationCount;
    private static int bugMovesSinceSeenObstacle = 0;

    private static boolean fill = true;

    private static boolean move(Direction dir) throws GameActionException {
        if (fill && rc.senseMapInfo(rc.getLocation().add(dir)).isWater() && rc.canFill(rc.getLocation().add(dir))) {
            rc.fill(rc.getLocation().add(dir));
        }
        //if (rc.getMovementCooldownTurns() < GameConstants.COOLDOWN_LIMIT) rc.move(dir);
        if (rc.canMove(dir)) rc.move(dir);
        return true;
    }

    private static boolean goodCurrent(Direction adjDir) throws GameActionException{
        return true;
    }

    private static boolean canMove(Direction dir) throws GameActionException {
        MapLocation test = rc.getLocation().add(dir);
        if (!rc.onTheMap(test)) return false;
        if (fill) {
            if (rc.senseMapInfo(rc.getLocation().add(dir)).isWater()) {
                return rc.canFill(rc.getLocation().add(dir));
            } else return rc.canMove(dir);
        } else return rc.canMove(dir);
    }

    private static boolean tryMoveDirect() throws GameActionException {
        Direction dirAhead = rc.getLocation().directionTo(dest);
        if(canMove(dirAhead)) {
            move(dirAhead);
            return true;
        }

        Direction dirLeft = dirAhead.rotateLeft();
        Direction dirRight = dirAhead.rotateRight();

        Direction[] dirs = new Direction[3];
        dirs[0] = dirAhead;
        if (rc.getLocation().add(dirLeft).distanceSquaredTo(dest) < rc.getLocation().add(dirRight).distanceSquaredTo(dest)) {
            dirs[1] = dirLeft;
            dirs[2] = dirRight;
        } else {
            dirs[1] = dirRight;
            dirs[2] = dirLeft;
        }

        for(Direction dir : dirs) {
            if(canMove(dir)) {
                move(dir);
                return true;
            }
        }

        return false;
    }

    private static void startBug() throws GameActionException {
        bugStartDistSq = rc.getLocation().distanceSquaredTo(dest);
        bugLastMoveDir = rc.getLocation().directionTo(dest);
        bugLookStartDir = rc.getLocation().directionTo(dest);
        bugRotationCount = 0;
        bugMovesSinceSeenObstacle = 0;

        // try to intelligently choose on which side we will keep the wall
        Direction leftTryDir = bugLastMoveDir.rotateLeft();
        for (int i = 0; i < 3; i++) {
            if (canMove(leftTryDir)) leftTryDir = leftTryDir.rotateLeft();
            else break;
        }
        Direction rightTryDir = bugLastMoveDir.rotateRight();
        for (int i = 0; i < 3; i++) {
            if (canMove(rightTryDir)) rightTryDir = rightTryDir.rotateRight();
            else break;
        }
        if (dest.distanceSquaredTo(rc.getLocation().add(leftTryDir)) < dest.distanceSquaredTo(rc.getLocation().add(rightTryDir))) {
            bugWallSide = WallSide.RIGHT;
        } else {
            bugWallSide = WallSide.LEFT;
        }
    }

    private static Direction findBugMoveDir() throws GameActionException {
        bugMovesSinceSeenObstacle++;
        Direction dir = bugLookStartDir;
        for (int i = 8; i-- > 0;) {
            if (canMove(dir)){
                return dir;
            }
            dir = (bugWallSide == WallSide.LEFT ? dir.rotateRight() : dir.rotateLeft());
            bugMovesSinceSeenObstacle = 0;
        }
        return null;
    }

    private static int numRightRotations(Direction start, Direction end) {
        return (end.ordinal() - start.ordinal() + 8) % 8;
    }

    private static int numLeftRotations(Direction start, Direction end) {
        return (-end.ordinal() + start.ordinal() + 8) % 8;
    }

    private static int calculateBugRotation(Direction moveDir) {
        if (bugWallSide == WallSide.LEFT) {
            return numRightRotations(bugLookStartDir, moveDir) - numRightRotations(bugLookStartDir, bugLastMoveDir);
        } else {
            return numLeftRotations(bugLookStartDir, moveDir) - numLeftRotations(bugLookStartDir, bugLastMoveDir);
        }
    }

    private static void bugMove(Direction dir) throws GameActionException {
        if (move(dir)) {
            bugRotationCount += calculateBugRotation(dir);
            bugLastMoveDir = dir;
            if (bugWallSide == WallSide.LEFT)
                bugLookStartDir = dir.rotateLeft().rotateLeft();
            else
                bugLookStartDir = dir.rotateRight().rotateRight();
        }
    }

    private static boolean detectBugIntoEdge() throws GameActionException{
        if (bugWallSide == WallSide.LEFT) {
            return !rc.onTheMap(rc.getLocation().add(bugLastMoveDir.rotateLeft()));
        } else {
            return !rc.onTheMap(rc.getLocation().add(bugLastMoveDir.rotateRight()));
        }
    }

    private static void reverseBugWallFollowDir() throws GameActionException {
        bugWallSide = (bugWallSide == WallSide.LEFT ? WallSide.RIGHT : WallSide.LEFT);
        startBug();
    }

    private static void bugTurn() throws GameActionException {
        if (detectBugIntoEdge()) {
            reverseBugWallFollowDir();
        }
        Direction dir = findBugMoveDir();
        if (dir != null && canMove(dir)) {
            bugMove(dir);
        }
    }

    private static boolean canEndBug() {
        if (bugMovesSinceSeenObstacle >= 4) return true;
        return (bugRotationCount <= 0 || bugRotationCount >= 8); //Stricter condition -> && rc.getLocation().distanceSquaredTo(dest) <= bugStartDistSq;
    }

    private static void bugMove() throws GameActionException {
        // Debug.clear("nav");
        // Debug.indicate("nav", 0, "bugMovesSinceSeenObstacle = " + bugMovesSinceSeenObstacle + "; bugRotatoinCount = " + bugRotationCount);

        // Check if we can stop bugging at the *beginning* of the turn
        if (bugState == BugState.BUG) {
            if (canEndBug()) {
                // Debug.indicateAppend("nav", 1, "ending bug; ");
                bugState = BugState.DIRECT;
            }
        }

        // If DIRECT mode, try to go directly to target
        if (bugState == BugState.DIRECT) {
            if (!tryMoveDirect()) {
                // Debug.indicateAppend("nav", 1, "starting to bug; ");
                bugState = BugState.BUG;
                startBug();
            } else {
                // Debug.indicateAppend("nav", 1, "successful direct move; ");
            }
        }

        // If that failed, or if bugging, bug
        if (bugState == BugState.BUG) {
            // Debug.indicateAppend("nav", 1, "bugging; ");
            bugTurn();
        }
    }

    public static boolean goTo(MapLocation theDest, boolean newFill) throws GameActionException {
        if (theDest == null) return false;
        if (!theDest.equals(dest) || newFill != fill) {
            dest = theDest;
            bugState = BugState.DIRECT;
            fill = newFill;
        }

        if (rc.getLocation().equals(dest)) return false;
        bugMove();
        return !rc.isActionReady();
    }
}