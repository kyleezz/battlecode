package sussy;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {

    static int turnCount;

    @SuppressWarnings("unused")
    public static void run (RobotController rc) throws GameActionException {
        Duck duck = new NormalDuck(rc);

        while (true) {
            try {
                int startRound = rc.getRoundNum();
                duck.run();

                if (startRound != rc.getRoundNum()) {
                    System.out.printf("overran turn from %d to %d at %d, %d\n", startRound, rc.getRoundNum(), rc.getLocation().x, rc.getLocation().y);
                }
            } catch
            (GameActionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                turnCount += 1;
                Clock.yield();
            }
        }
    }
}
