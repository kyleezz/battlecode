package kittyjuniorplus;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public abstract class Duck {
    static RobotController rc;

    static CursedRandom rng;
    static Movement movement;
    static Action action;
    static Symmetry symmetry;

    int creationRound;

    public abstract void run() throws GameActionException;

    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        movement = new Movement(rc);
        action = new Action(rng, rc);
        symmetry = new Symmetry(rc);
        Constants.mapHeight = rc.getMapHeight();
        Constants.mapWidth = rc.getMapWidth();
    }
}
