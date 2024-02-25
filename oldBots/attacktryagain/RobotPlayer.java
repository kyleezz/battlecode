package attacktryagain;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GlobalUpgrade;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        Duck duck = null;
        duck = new NormalDuck(rc);

        while (true) {
            try {
                int startRound = rc.getRoundNum();
                duck.run();

                if (rc.canBuyGlobal(GlobalUpgrade.ACTION)) {
                    rc.buyGlobal(GlobalUpgrade.ACTION);
                } 
                if (rc.canBuyGlobal(GlobalUpgrade.HEALING)) {
                    rc.buyGlobal(GlobalUpgrade.HEALING);
                } 

                if (startRound != rc.getRoundNum()) {
                    System.out.printf("overran turn from %d to %d at ", startRound, rc.getRoundNum());
                    System.out.printf("I'm at (%d, %d)\n", rc.getLocation().x, rc.getLocation().y);
                }
            } catch (GameActionException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}