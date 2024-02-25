package checky;

import battlecode.common.*;

import java.util.Random;

public abstract class Duck {
    static RobotController rc;

    static CursedRandom rng;
    static Movement movement;
    static Action action;

    int creationRound;

    public abstract void run() throws GameActionException;

    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        movement = new Movement(rc);
        action = new Action(rng, rc);
    }
}
