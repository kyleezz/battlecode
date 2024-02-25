package healy;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Collections;

public class Movement extends Util {
    BFS bfs;
    BFSNoFill bfsNoFill;
    MapLocation destination = null;
    static final int BYTECODE_REMAINING = 4500;
    int[] tracker = new int[113];

    int fuzzyMovesLeft = 0;
    int maxFuzzyMoves = 3;

    boolean fill = true;

    Bug bug;
    RobotController rc;
    CursedRandom rng;
    GlobalBFS globalbfs;
    Symmetry symmetry;

    Movement(GlobalBFS globalbfs, Symmetry symmetry, RobotController rc) throws GameActionException {
        this.globalbfs = globalbfs;
        this.symmetry = symmetry;
        this.rc = rc;
        rng = new CursedRandom(rc);
        bug = new Bug(rng, rc);
        bfs = new BFS(rc);
        bfsNoFill = new BFSNoFill(rc);
    }
    // Set path to new destination
    public void setNewDestination(MapLocation newDest, boolean newFill) {
        if (destination == null || destination.distanceSquaredTo(newDest) != 0 || fill != newFill) {
            destination = newDest;
            fill = newFill; 
            resetTracker();
            addVisited(rc.getLocation());
        }
    }

    // Get current destination
    public MapLocation getCurrentDestination(){
        return destination;
    }

    // Turn by turn move to destination
    public void moveToDestination() throws GameActionException {
        if (destination != null) {
            pathTo(destination);
        }
    }

    public void moveTo(MapLocation dest, boolean fill) throws GameActionException{
//        if (rc.getRoundNum() == 180) {
//            bug.bugState = Bug.BugState.DIRECT;
//        }
        setNewDestination(dest, fill);
        moveToDestination();
    }

    // Computer next direction to move to
    public void pathTo(MapLocation target) throws GameActionException {
        if (!rc.isMovementReady()) return;

        if (fuzzyMovesLeft > 0) {
            fuzzyMove(target);
            return;
        }

        if (rc.getLocation().distanceSquaredTo(target) <= 2) {
            moveToDir(rc.getLocation().directionTo(target));
            return;
        }
        Util.bytecodeCheck("PreBugNavCheck");
        int nearbyRobotcount = rc.senseNearbyRobots().length;
//        if (rc.getRoundNum() - BIRTH_ROUND == 3){
//            bug.goTo(rc.getLocation());
//        }
        if (nearbyRobotcount > 15 || Clock.getBytecodesLeft() < BYTECODE_REMAINING || (bug.bugState == Bug.BugState.BUG && target.equals(bug.dest))) {
            bug.goTo(target, fill);
            destinationFlag+="$Bu1$";
            Util.bytecodeCheck("PBN1 T:" + target+ "|Side "+ Bug.bugWallSide + "|StartDir " + Bug.bugLookStartDir);
            return;
        }
        Util.bytecodeCheck("PreBFS");
        Direction dir = null;
        if (fill) dir = bfs.bestDir(target);
        else dir = bfsNoFill.bestDir(target);
//        bug.goTo(target);
        Util.bytecodeCheck("PostBFS T:" + target+ " " + dir);
        if (dir == null || (!rc.canMove(dir) && !rc.senseMapInfo(rc.getLocation().add(dir)).isWater()) || (rc.senseMapInfo(rc.getLocation().add(dir)).isWater() && !rc.canFill(rc.getLocation().add(dir)))|| isVisited(rc.getLocation().add(dir)) || !rc.sensePassability(rc.getLocation().add(dir)) ||
                rc.isLocationOccupied(rc.getLocation().add(dir))) {
            bug.goTo(target, fill);
            destinationFlag+="$Bu2$";
            Util.bytecodeCheck("PBN2 T:" + target + "|Side "+ bug.bugWallSide + "|StartDir " + bug.bugLookStartDir);
            addVisited(rc.getLocation());
        } else {
            moveToDir(dir);
            destinationFlag+="$BF1$";
        }
    }

    // Try to move to next cell in computed path
    public void moveToDir(Direction dir) throws GameActionException {
        if (rc.canMove(dir)){
            rc.move(dir);
        } else if (rc.canFill(rc.getLocation().add(dir))) {
            rc.fill(rc.getLocation().add(dir));
            return;
        }
        if (fuzzyMovesLeft > 0) {
            fuzzyMovesLeft--;
        }
        addVisited(rc.getLocation());
    }

    // If in a loop, do fuzzy move
    public void fuzzyMove(MapLocation target) throws GameActionException {
        if (!rc.isMovementReady()) return;

        // Don't move if adjacent to destination and something is blocking it
        if (rc.getLocation().distanceSquaredTo(target) <= 2 && !rc.canMove(rc.getLocation().directionTo(target))) {
            return;
        }

        Direction toDest = rc.getLocation().directionTo(target);
        Direction[] dirs = {toDest, toDest.rotateLeft(), toDest.rotateRight(), toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};
        int cost = 99999;
        Direction optimalDir = null;
        for (int i = 0; i < dirs.length; i++) {
            if (i > 2 && cost > 0) {
                break;
            }
            Direction dir = dirs[i];
            if (rc.canMove(dir)) {
                // TODO: Have some kind of cost function
                int newCost = 10;
                if (dir == toDest) {
                    newCost -= 1;
                }
                if (newCost < cost) {
                    cost = newCost;
                    optimalDir = dir;
                }
            }
        }
        if (optimalDir != null) {
            moveToDir(optimalDir);
        }
    }

    private void addVisited(MapLocation loc) {
        int bit = loc.x + 60*loc.y;
        tracker[bit >>> 5] |= 1 << (31 - bit & 31);
    }

    private boolean isVisited(MapLocation loc) {
        int bit = loc.x + 60*loc.y;
        return (tracker[bit >>> 5] & (1 << (31 - bit & 31))) != 0;
    }

    private void resetTracker() {
        tracker = new int[113];
    }

    static int moveCount = 0;
    static Direction curDir = Direction.CENTER;

    void moveRandom() throws GameActionException {
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
                return;
            }
            moveCount--;
            rc.move(curDir);
        }
    }

    MapLocation knownFlag() throws GameActionException {
        FlagInfo[] flagInfos = rc.senseNearbyFlags(20, rc.getTeam().opponent());

        int sharedFlag = rc.readSharedArray(Constants.FLAGLOC);
        if (sharedFlag != 0) {
            MapLocation curFlag = new MapLocation((sharedFlag - 1) / 60, (sharedFlag - 1) % 60);

            if (rc.canSenseLocation(curFlag)) {
                for (FlagInfo flagInfo : flagInfos) {
                    if (flagInfo.isPickedUp()) continue;
                    if (flagInfo.getLocation().equals(curFlag)) {
                        return curFlag;
                    }
                }
            }
            else {
                return curFlag;
            }
        }

        int mini = 1000000;
        MapLocation closeLoc = null;

        for (FlagInfo flagInfo : flagInfos) {
            if (flagInfo.isPickedUp()) continue;
            if (rc.getLocation().distanceSquaredTo(flagInfo.getLocation()) < mini) {
                mini = rc.getLocation().distanceSquaredTo(flagInfo.getLocation());
                closeLoc = flagInfo.getLocation();
            }
        }

        int val = 0;
        if (closeLoc != null) {
            val = closeLoc.x * 60 + closeLoc.y + 1;
        }
        if (val != sharedFlag) {
            rc.writeSharedArray(Constants.FLAGLOC, val);
        }
        return closeLoc;
    }

    void moveToBroadcast() throws GameActionException {
        
        FlagInfo[] flagInfos = rc.senseNearbyFlags(-1, rc.getTeam().opponent());

        int mini = 1000000;
        MapLocation closeLoc = null;

        for (FlagInfo flagInfo : flagInfos) {
            if (flagInfo.isPickedUp()) continue;
            if (rc.getLocation().distanceSquaredTo(flagInfo.getLocation()) < mini) {
                mini = rc.getLocation().distanceSquaredTo(flagInfo.getLocation());
                closeLoc = flagInfo.getLocation();
            }
        }
        if (closeLoc != null) moveTo(closeLoc, true);

        MapLocation[] flagLocs = {
            symmetry.getSymmetricLoc(Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[0]))),
            symmetry.getSymmetricLoc(Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[1]))),
            symmetry.getSymmetricLoc(Util.deserializeLoc(rc.readSharedArray(Constants.BASELOCINDEX[2]))),
        };
        //todo: change to symmetry.getSymmetricLoc

        MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();

        mini = 1000000;
        closeLoc = null;

        //MapLocation center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);

        for (MapLocation flagLoc : flagLocs) {
            if (rc.getLocation().distanceSquaredTo(flagLoc) < mini) {
                boolean inRange = false;
                if (rc.canSenseLocation(flagLoc)) continue;
                for (MapLocation broadcastLoc : broadcastLocs) {
                    rc.setIndicatorLine(flagLoc, broadcastLoc, 0, 255, 0);
                    if (broadcastLoc.distanceSquaredTo(flagLoc) <= 100) inRange = true;
                }
                if (!inRange) continue;

                mini = rc.getLocation().distanceSquaredTo(flagLoc);
                closeLoc = flagLoc;
            }
        }

        if (closeLoc == null) {
            mini = 1000000;
            for (MapLocation broadcastLoc : broadcastLocs) {
                if (rc.getLocation().distanceSquaredTo(broadcastLoc) < mini) {
                    mini = rc.getLocation().distanceSquaredTo(broadcastLoc);
                    closeLoc = broadcastLoc;
                }
            }
        }

        if (closeLoc == null) moveRandom();
        else {
            rc.setIndicatorLine(rc.getLocation(), closeLoc, 255, 255, 255);
            moveTo(closeLoc, true);
        }
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
                dirs.add(Constants.directions[i]);
            }
        }
        Collections.shuffle(dirs);
        for (Direction dir : dirs)
        {
            if (rc.canFill(rc.getLocation().add(dir))) rc.fill(rc.getLocation().add(dir));
            if (rc.canMove(dir))
            {
                rc.move(dir);
                return;
            }
        }
    }

    static int retreatBest = 0;

    static void resetRetreat() {
        retreatBest = 0;
    }

    void fastRetreat(MapLocation nearestSpawn) throws GameActionException {
        int cur = globalbfs.getVal(rc.getLocation());
        if (cur < retreatBest) retreatBest = cur;
        Direction bestDir = null;

        Direction toDest = rc.getLocation().directionTo(nearestSpawn);
        Direction[] dirs = {
            toDest,
            toDest.rotateLeft(),
            toDest.rotateRight(),
            toDest.rotateLeft().rotateLeft(),
            toDest.rotateRight().rotateRight(),
            toDest.rotateLeft().rotateLeft().rotateLeft(),
            toDest.rotateRight().rotateRight().rotateRight(),
            toDest.opposite()
        };

        for (Direction dir : dirs) {
            if (!rc.canMove(dir)) continue;

            cur = globalbfs.getVal(rc.getLocation().add(dir));
            if (cur < retreatBest) {
                retreatBest = cur;
                bestDir = dir;
            }
        }
        // Potential TODO: tiebreak by enemies around

        if (bestDir != null) {
            rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(bestDir), 0, 0, 255);
            rc.move(bestDir);
        }
        else moveTo(nearestSpawn, false);
    }
}