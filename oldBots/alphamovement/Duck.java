package alphamovement;

import battlecode.common.*;

public abstract class Duck {
    RobotController rc;
    CursedRandom rng;
    Movement movement;
    int creationRound;


    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        movement = new Movement(rc);
    }

    public void moveTo(MapLocation loc) throws GameActionException {
        movement.setAndMoveToDestination(loc);
    }
    abstract void run() throws GameActionException;

    public void Test() {

    }
}
