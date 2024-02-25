package speedy;

import battlecode.common.*;

public strictfp class RobotPlayer {

    static int turnCount;

    @SuppressWarnings("unused")
    public static void run (RobotController rc) throws GameActionException {
        Duck duck = null;
        if (rc.readSharedArray(Constants.BUILDERCOUNT) < 3) {
            duck = new BuilderDuck(rc);
        } else {
            duck = new NormalDuck(rc);
        }

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
                    System.out.printf("overran turn from %d to %d\n", startRound, rc.getRoundNum());
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
