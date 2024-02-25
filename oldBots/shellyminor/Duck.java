package shellyminor;

import battlecode.common.*;

import java.util.Random;

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
        movement = new Movement(rng, rc);
        action = new Action(rng, rc);
        symmetry = new Symmetry(rc);
    }
}
