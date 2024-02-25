package diagonallybuilder;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GlobalUpgrade;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {

    static int turnCount;
    static int builderDuck = 0;
    @SuppressWarnings("unused")
    public static void run (RobotController rc) throws GameActionException {
        Duck duck = null;

        int builderDuck = rc.readSharedArray(Constants.BUILDERCOUNT);
        if (builderDuck < 3) {
            duck = new BuilderDuck(rc);
            builderDuck++;
            rc.writeSharedArray(Constants.BUILDERCOUNT, builderDuck);
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
