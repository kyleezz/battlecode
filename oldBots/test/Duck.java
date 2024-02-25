package testt;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public abstract class Duck {
    static RobotController rc;

    static CursedRandom rng;
    static Movement movement;

    int creationRound;

    public abstract void run() throws GameActionException;

    public Duck(RobotController rc) throws GameActionException {
        creationRound = rc.getRoundNum();
        rng = new CursedRandom(rc);
        this.rc = rc;
        Util.rc = rc;
        movement = new Movement(rng, rc);
    }
}
