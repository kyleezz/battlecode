package wallfacer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class BFS {
    RobotController rc;


    public BFS(RobotController rc) {
        this.rc = rc;
    }

    static MapLocation ml1; // (-4, -2)
    static int d1;
    static Direction dir1;

    static MapLocation ml2; // (-4, -1)
    static int d2;
    static Direction dir2;

    static MapLocation ml3; // (-4, 0)
    static int d3;
    static Direction dir3;

    static MapLocation ml4; // (-4, 1)
    static int d4;
    static Direction dir4;

    static MapLocation ml5; // (-4, 2)
    static int d5;
    static Direction dir5;

    static MapLocation ml6; // (-3, -3)
    static int d6;
    static Direction dir6;

    static MapLocation ml7; // (-3, -2)
    static int d7;
    static Direction dir7;

    static MapLocation ml8; // (-3, -1)
    static int d8;
    static Direction dir8;

    static MapLocation ml9; // (-3, 0)
    static int d9;
    static Direction dir9;

    static MapLocation ml10; // (-3, 1)
    static int d10;
    static Direction dir10;

    static MapLocation ml11; // (-3, 2)
    static int d11;
    static Direction dir11;

    static MapLocation ml12; // (-3, 3)
    static int d12;
    static Direction dir12;

    static MapLocation ml13; // (-2, -4)
    static int d13;
    static Direction dir13;

    static MapLocation ml14; // (-2, -3)
    static int d14;
    static Direction dir14;

    static MapLocation ml15; // (-2, -2)
    static int d15;
    static Direction dir15;

    static MapLocation ml16; // (-2, -1)
    static int d16;
    static Direction dir16;

    static MapLocation ml17; // (-2, 0)
    static int d17;
    static Direction dir17;

    static MapLocation ml18; // (-2, 1)
    static int d18;
    static Direction dir18;

    static MapLocation ml19; // (-2, 2)
    static int d19;
    static Direction dir19;

    static MapLocation ml20; // (-2, 3)
    static int d20;
    static Direction dir20;

    static MapLocation ml21; // (-2, 4)
    static int d21;
    static Direction dir21;

    static MapLocation ml22; // (-1, -4)
    static int d22;
    static Direction dir22;

    static MapLocation ml23; // (-1, -3)
    static int d23;
    static Direction dir23;

    static MapLocation ml24; // (-1, -2)
    static int d24;
    static Direction dir24;

    static MapLocation ml25; // (-1, -1)
    static int d25;
    static Direction dir25;

    static MapLocation ml26; // (-1, 0)
    static int d26;
    static Direction dir26;

    static MapLocation ml27; // (-1, 1)
    static int d27;
    static Direction dir27;

    static MapLocation ml28; // (-1, 2)
    static int d28;
    static Direction dir28;

    static MapLocation ml29; // (-1, 3)
    static int d29;
    static Direction dir29;

    static MapLocation ml30; // (-1, 4)
    static int d30;
    static Direction dir30;

    static MapLocation ml31; // (0, -4)
    static int d31;
    static Direction dir31;

    static MapLocation ml32; // (0, -3)
    static int d32;
    static Direction dir32;

    static MapLocation ml33; // (0, -2)
    static int d33;
    static Direction dir33;

    static MapLocation ml34; // (0, -1)
    static int d34;
    static Direction dir34;

    static MapLocation ml35; // (0, 0)
    static int d35;
    static Direction dir35;

    static MapLocation ml36; // (0, 1)
    static int d36;
    static Direction dir36;

    static MapLocation ml37; // (0, 2)
    static int d37;
    static Direction dir37;

    static MapLocation ml38; // (0, 3)
    static int d38;
    static Direction dir38;

    static MapLocation ml39; // (0, 4)
    static int d39;
    static Direction dir39;

    static MapLocation ml40; // (1, -4)
    static int d40;
    static Direction dir40;

    static MapLocation ml41; // (1, -3)
    static int d41;
    static Direction dir41;

    static MapLocation ml42; // (1, -2)
    static int d42;
    static Direction dir42;

    static MapLocation ml43; // (1, -1)
    static int d43;
    static Direction dir43;

    static MapLocation ml44; // (1, 0)
    static int d44;
    static Direction dir44;

    static MapLocation ml45; // (1, 1)
    static int d45;
    static Direction dir45;

    static MapLocation ml46; // (1, 2)
    static int d46;
    static Direction dir46;

    static MapLocation ml47; // (1, 3)
    static int d47;
    static Direction dir47;

    static MapLocation ml48; // (1, 4)
    static int d48;
    static Direction dir48;

    static MapLocation ml49; // (2, -4)
    static int d49;
    static Direction dir49;

    static MapLocation ml50; // (2, -3)
    static int d50;
    static Direction dir50;

    static MapLocation ml51; // (2, -2)
    static int d51;
    static Direction dir51;

    static MapLocation ml52; // (2, -1)
    static int d52;
    static Direction dir52;

    static MapLocation ml53; // (2, 0)
    static int d53;
    static Direction dir53;

    static MapLocation ml54; // (2, 1)
    static int d54;
    static Direction dir54;

    static MapLocation ml55; // (2, 2)
    static int d55;
    static Direction dir55;

    static MapLocation ml56; // (2, 3)
    static int d56;
    static Direction dir56;

    static MapLocation ml57; // (2, 4)
    static int d57;
    static Direction dir57;

    static MapLocation ml58; // (3, -3)
    static int d58;
    static Direction dir58;

    static MapLocation ml59; // (3, -2)
    static int d59;
    static Direction dir59;

    static MapLocation ml60; // (3, -1)
    static int d60;
    static Direction dir60;

    static MapLocation ml61; // (3, 0)
    static int d61;
    static Direction dir61;

    static MapLocation ml62; // (3, 1)
    static int d62;
    static Direction dir62;

    static MapLocation ml63; // (3, 2)
    static int d63;
    static Direction dir63;

    static MapLocation ml64; // (3, 3)
    static int d64;
    static Direction dir64;

    static MapLocation ml65; // (4, -2)
    static int d65;
    static Direction dir65;

    static MapLocation ml66; // (4, -1)
    static int d66;
    static Direction dir66;

    static MapLocation ml67; // (4, 0)
    static int d67;
    static Direction dir67;

    static MapLocation ml68; // (4, 1)
    static int d68;
    static Direction dir68;

    static MapLocation ml69; // (4, 2)
    static int d69;
    static Direction dir69;

    public Direction bestDir(MapLocation target) throws GameActionException {

        ml35 = rc.getLocation();
        d35 = 0;
        dir35 = Direction.CENTER;

        ml25 = ml35.add(Direction.SOUTHWEST); // (-1, -1) from (0, 0)
        d25 = 99999;
        dir25 = null;

        ml26 = ml35.add(Direction.WEST); // (-1, 0) from (0, 0)
        d26 = 99999;
        dir26 = null;

        ml27 = ml35.add(Direction.NORTHWEST); // (-1, 1) from (0, 0)
        d27 = 99999;
        dir27 = null;

        ml34 = ml35.add(Direction.SOUTH); // (0, -1) from (0, 0)
        d34 = 99999;
        dir34 = null;

        ml36 = ml35.add(Direction.NORTH); // (0, 1) from (0, 0)
        d36 = 99999;
        dir36 = null;

        ml43 = ml35.add(Direction.SOUTHEAST); // (1, -1) from (0, 0)
        d43 = 99999;
        dir43 = null;

        ml44 = ml35.add(Direction.EAST); // (1, 0) from (0, 0)
        d44 = 99999;
        dir44 = null;

        ml45 = ml35.add(Direction.NORTHEAST); // (1, 1) from (0, 0)
        d45 = 99999;
        dir45 = null;

        ml15 = ml25.add(Direction.SOUTHWEST); // (-2, -2) from (-1, -1)
        d15 = 99999;
        dir15 = null;

        ml16 = ml25.add(Direction.WEST); // (-2, -1) from (-1, -1)
        d16 = 99999;
        dir16 = null;

        ml17 = ml25.add(Direction.NORTHWEST); // (-2, 0) from (-1, -1)
        d17 = 99999;
        dir17 = null;

        ml24 = ml25.add(Direction.SOUTH); // (-1, -2) from (-1, -1)
        d24 = 99999;
        dir24 = null;

        ml33 = ml25.add(Direction.SOUTHEAST); // (0, -2) from (-1, -1)
        d33 = 99999;
        dir33 = null;

        ml18 = ml26.add(Direction.NORTHWEST); // (-2, 1) from (-1, 0)
        d18 = 99999;
        dir18 = null;

        ml19 = ml27.add(Direction.NORTHWEST); // (-2, 2) from (-1, 1)
        d19 = 99999;
        dir19 = null;

        ml28 = ml27.add(Direction.NORTH); // (-1, 2) from (-1, 1)
        d28 = 99999;
        dir28 = null;

        ml37 = ml27.add(Direction.NORTHEAST); // (0, 2) from (-1, 1)
        d37 = 99999;
        dir37 = null;

        ml42 = ml34.add(Direction.SOUTHEAST); // (1, -2) from (0, -1)
        d42 = 99999;
        dir42 = null;

        ml46 = ml36.add(Direction.NORTHEAST); // (1, 2) from (0, 1)
        d46 = 99999;
        dir46 = null;

        ml51 = ml43.add(Direction.SOUTHEAST); // (2, -2) from (1, -1)
        d51 = 99999;
        dir51 = null;

        ml52 = ml43.add(Direction.EAST); // (2, -1) from (1, -1)
        d52 = 99999;
        dir52 = null;

        ml53 = ml43.add(Direction.NORTHEAST); // (2, 0) from (1, -1)
        d53 = 99999;
        dir53 = null;

        ml54 = ml44.add(Direction.NORTHEAST); // (2, 1) from (1, 0)
        d54 = 99999;
        dir54 = null;

        ml55 = ml45.add(Direction.NORTHEAST); // (2, 2) from (1, 1)
        d55 = 99999;
        dir55 = null;

        ml6 = ml15.add(Direction.SOUTHWEST); // (-3, -3) from (-2, -2)
        d6 = 99999;
        dir6 = null;

        ml7 = ml15.add(Direction.WEST); // (-3, -2) from (-2, -2)
        d7 = 99999;
        dir7 = null;

        ml8 = ml15.add(Direction.NORTHWEST); // (-3, -1) from (-2, -2)
        d8 = 99999;
        dir8 = null;

        ml14 = ml15.add(Direction.SOUTH); // (-2, -3) from (-2, -2)
        d14 = 99999;
        dir14 = null;

        ml23 = ml15.add(Direction.SOUTHEAST); // (-1, -3) from (-2, -2)
        d23 = 99999;
        dir23 = null;

        ml9 = ml16.add(Direction.NORTHWEST); // (-3, 0) from (-2, -1)
        d9 = 99999;
        dir9 = null;

        ml10 = ml17.add(Direction.NORTHWEST); // (-3, 1) from (-2, 0)
        d10 = 99999;
        dir10 = null;

        ml32 = ml24.add(Direction.SOUTHEAST); // (0, -3) from (-1, -2)
        d32 = 99999;
        dir32 = null;

        ml41 = ml33.add(Direction.SOUTHEAST); // (1, -3) from (0, -2)
        d41 = 99999;
        dir41 = null;

        ml11 = ml18.add(Direction.NORTHWEST); // (-3, 2) from (-2, 1)
        d11 = 99999;
        dir11 = null;

        ml12 = ml19.add(Direction.NORTHWEST); // (-3, 3) from (-2, 2)
        d12 = 99999;
        dir12 = null;

        ml20 = ml19.add(Direction.NORTH); // (-2, 3) from (-2, 2)
        d20 = 99999;
        dir20 = null;

        ml29 = ml19.add(Direction.NORTHEAST); // (-1, 3) from (-2, 2)
        d29 = 99999;
        dir29 = null;

        ml38 = ml28.add(Direction.NORTHEAST); // (0, 3) from (-1, 2)
        d38 = 99999;
        dir38 = null;

        ml47 = ml37.add(Direction.NORTHEAST); // (1, 3) from (0, 2)
        d47 = 99999;
        dir47 = null;

        ml50 = ml42.add(Direction.SOUTHEAST); // (2, -3) from (1, -2)
        d50 = 99999;
        dir50 = null;

        ml56 = ml46.add(Direction.NORTHEAST); // (2, 3) from (1, 2)
        d56 = 99999;
        dir56 = null;

        ml58 = ml51.add(Direction.SOUTHEAST); // (3, -3) from (2, -2)
        d58 = 99999;
        dir58 = null;

        ml59 = ml51.add(Direction.EAST); // (3, -2) from (2, -2)
        d59 = 99999;
        dir59 = null;

        ml60 = ml51.add(Direction.NORTHEAST); // (3, -1) from (2, -2)
        d60 = 99999;
        dir60 = null;

        ml61 = ml52.add(Direction.NORTHEAST); // (3, 0) from (2, -1)
        d61 = 99999;
        dir61 = null;

        ml62 = ml53.add(Direction.NORTHEAST); // (3, 1) from (2, 0)
        d62 = 99999;
        dir62 = null;

        ml63 = ml54.add(Direction.NORTHEAST); // (3, 2) from (2, 1)
        d63 = 99999;
        dir63 = null;

        ml64 = ml55.add(Direction.NORTHEAST); // (3, 3) from (2, 2)
        d64 = 99999;
        dir64 = null;

        ml1 = ml6.add(Direction.NORTHWEST); // (-4, -2) from (-3, -3)
        d1 = 99999;
        dir1 = null;

        ml13 = ml6.add(Direction.SOUTHEAST); // (-2, -4) from (-3, -3)
        d13 = 99999;
        dir13 = null;

        ml2 = ml7.add(Direction.NORTHWEST); // (-4, -1) from (-3, -2)
        d2 = 99999;
        dir2 = null;

        ml3 = ml8.add(Direction.NORTHWEST); // (-4, 0) from (-3, -1)
        d3 = 99999;
        dir3 = null;

        ml22 = ml14.add(Direction.SOUTHEAST); // (-1, -4) from (-2, -3)
        d22 = 99999;
        dir22 = null;

        ml31 = ml23.add(Direction.SOUTHEAST); // (0, -4) from (-1, -3)
        d31 = 99999;
        dir31 = null;

        ml4 = ml9.add(Direction.NORTHWEST); // (-4, 1) from (-3, 0)
        d4 = 99999;
        dir4 = null;

        ml5 = ml10.add(Direction.NORTHWEST); // (-4, 2) from (-3, 1)
        d5 = 99999;
        dir5 = null;

        ml40 = ml32.add(Direction.SOUTHEAST); // (1, -4) from (0, -3)
        d40 = 99999;
        dir40 = null;

        ml49 = ml41.add(Direction.SOUTHEAST); // (2, -4) from (1, -3)
        d49 = 99999;
        dir49 = null;

        ml21 = ml12.add(Direction.NORTHEAST); // (-2, 4) from (-3, 3)
        d21 = 99999;
        dir21 = null;

        ml30 = ml20.add(Direction.NORTHEAST); // (-1, 4) from (-2, 3)
        d30 = 99999;
        dir30 = null;

        ml39 = ml29.add(Direction.NORTHEAST); // (0, 4) from (-1, 3)
        d39 = 99999;
        dir39 = null;

        ml48 = ml38.add(Direction.NORTHEAST); // (1, 4) from (0, 3)
        d48 = 99999;
        dir48 = null;

        ml57 = ml47.add(Direction.NORTHEAST); // (2, 4) from (1, 3)
        d57 = 99999;
        dir57 = null;

        ml65 = ml58.add(Direction.NORTHEAST); // (4, -2) from (3, -3)
        d65 = 99999;
        dir65 = null;

        ml66 = ml59.add(Direction.NORTHEAST); // (4, -1) from (3, -2)
        d66 = 99999;
        dir66 = null;

        ml67 = ml60.add(Direction.NORTHEAST); // (4, 0) from (3, -1)
        d67 = 99999;
        dir67 = null;

        ml68 = ml61.add(Direction.NORTHEAST); // (4, 1) from (3, 0)
        d68 = 99999;
        dir68 = null;

        ml69 = ml62.add(Direction.NORTHEAST); // (4, 2) from (3, 1)
        d69 = 99999;
        dir69 = null;

        if (rc.onTheMap(ml26)) {
            if (rc.senseMapInfo(ml26).isWater()) {
                if (d26 > d35) {
                    d26 = d35;
                    dir26 = dir35;
                }
                d26 += 12;
            }
            else if (rc.senseMapInfo(ml26).isPassable()) {
                if (d26 > d35) {
                    d26 = d35;
                    dir26 = dir35;
                }
                d26 += 8;
            }
        }
        if (rc.onTheMap(ml34)) {
            if (rc.senseMapInfo(ml34).isWater()) {
                if (d34 > d26) {
                    d34 = d26;
                    dir34 = dir26;
                }
                if (d34 > d35) {
                    d34 = d35;
                    dir34 = dir35;
                }
                d34 += 12;
            }
            else if (rc.senseMapInfo(ml34).isPassable()) {
                if (d34 > d26) {
                    d34 = d26;
                    dir34 = dir26;
                }
                if (d34 > d35) {
                    d34 = d35;
                    dir34 = dir35;
                }
                d34 += 8;
            }
        }
        if (rc.onTheMap(ml36)) {
            if (rc.senseMapInfo(ml36).isWater()) {
                if (d36 > d26) {
                    d36 = d26;
                    dir36 = dir26;
                }
                if (d36 > d35) {
                    d36 = d35;
                    dir36 = dir35;
                }
                d36 += 12;
            }
            else if (rc.senseMapInfo(ml36).isPassable()) {
                if (d36 > d26) {
                    d36 = d26;
                    dir36 = dir26;
                }
                if (d36 > d35) {
                    d36 = d35;
                    dir36 = dir35;
                }
                d36 += 8;
            }
        }
        if (rc.onTheMap(ml44)) {
            if (rc.senseMapInfo(ml44).isWater()) {
                if (d44 > d34) {
                    d44 = d34;
                    dir44 = dir34;
                }
                if (d44 > d35) {
                    d44 = d35;
                    dir44 = dir35;
                }
                if (d44 > d36) {
                    d44 = d36;
                    dir44 = dir36;
                }
                d44 += 12;
            }
            else if (rc.senseMapInfo(ml44).isPassable()) {
                if (d44 > d34) {
                    d44 = d34;
                    dir44 = dir34;
                }
                if (d44 > d35) {
                    d44 = d35;
                    dir44 = dir35;
                }
                if (d44 > d36) {
                    d44 = d36;
                    dir44 = dir36;
                }
                d44 += 8;
            }
        }
        if (rc.onTheMap(ml25)) {
            if (rc.senseMapInfo(ml25).isWater()) {
                if (d25 > d26) {
                    d25 = d26;
                    dir25 = dir26;
                }
                if (d25 > d34) {
                    d25 = d34;
                    dir25 = dir34;
                }
                if (d25 > d35) {
                    d25 = d35;
                    dir25 = dir35;
                }
                d25 += 12;
            }
            else if (rc.senseMapInfo(ml25).isPassable()) {
                if (d25 > d26) {
                    d25 = d26;
                    dir25 = dir26;
                }
                if (d25 > d34) {
                    d25 = d34;
                    dir25 = dir34;
                }
                if (d25 > d35) {
                    d25 = d35;
                    dir25 = dir35;
                }
                d25 += 8;
            }
        }
        if (rc.onTheMap(ml27)) {
            if (rc.senseMapInfo(ml27).isWater()) {
                if (d27 > d26) {
                    d27 = d26;
                    dir27 = dir26;
                }
                if (d27 > d35) {
                    d27 = d35;
                    dir27 = dir35;
                }
                if (d27 > d36) {
                    d27 = d36;
                    dir27 = dir36;
                }
                d27 += 12;
            }
            else if (rc.senseMapInfo(ml27).isPassable()) {
                if (d27 > d26) {
                    d27 = d26;
                    dir27 = dir26;
                }
                if (d27 > d35) {
                    d27 = d35;
                    dir27 = dir35;
                }
                if (d27 > d36) {
                    d27 = d36;
                    dir27 = dir36;
                }
                d27 += 8;
            }
        }
        if (rc.onTheMap(ml43)) {
            if (rc.senseMapInfo(ml43).isWater()) {
                if (d43 > d34) {
                    d43 = d34;
                    dir43 = dir34;
                }
                if (d43 > d35) {
                    d43 = d35;
                    dir43 = dir35;
                }
                if (d43 > d44) {
                    d43 = d44;
                    dir43 = dir44;
                }
                d43 += 12;
            }
            else if (rc.senseMapInfo(ml43).isPassable()) {
                if (d43 > d34) {
                    d43 = d34;
                    dir43 = dir34;
                }
                if (d43 > d35) {
                    d43 = d35;
                    dir43 = dir35;
                }
                if (d43 > d44) {
                    d43 = d44;
                    dir43 = dir44;
                }
                d43 += 8;
            }
        }
        if (rc.onTheMap(ml45)) {
            if (rc.senseMapInfo(ml45).isWater()) {
                if (d45 > d35) {
                    d45 = d35;
                    dir45 = dir35;
                }
                if (d45 > d36) {
                    d45 = d36;
                    dir45 = dir36;
                }
                if (d45 > d44) {
                    d45 = d44;
                    dir45 = dir44;
                }
                d45 += 12;
            }
            else if (rc.senseMapInfo(ml45).isPassable()) {
                if (d45 > d35) {
                    d45 = d35;
                    dir45 = dir35;
                }
                if (d45 > d36) {
                    d45 = d36;
                    dir45 = dir36;
                }
                if (d45 > d44) {
                    d45 = d44;
                    dir45 = dir44;
                }
                d45 += 8;
            }
        }
        if (rc.onTheMap(ml17)) {
            if (rc.senseMapInfo(ml17).isWater()) {
                if (d17 > d25) {
                    d17 = d25;
                    dir17 = dir25;
                }
                if (d17 > d26) {
                    d17 = d26;
                    dir17 = dir26;
                }
                if (d17 > d27) {
                    d17 = d27;
                    dir17 = dir27;
                }
                d17 += 12;
            }
            else if (rc.senseMapInfo(ml17).isPassable()) {
                if (d17 > d25) {
                    d17 = d25;
                    dir17 = dir25;
                }
                if (d17 > d26) {
                    d17 = d26;
                    dir17 = dir26;
                }
                if (d17 > d27) {
                    d17 = d27;
                    dir17 = dir27;
                }
                d17 += 8;
            }
        }
        if (rc.onTheMap(ml33)) {
            if (rc.senseMapInfo(ml33).isWater()) {
                if (d33 > d25) {
                    d33 = d25;
                    dir33 = dir25;
                }
                if (d33 > d34) {
                    d33 = d34;
                    dir33 = dir34;
                }
                if (d33 > d43) {
                    d33 = d43;
                    dir33 = dir43;
                }
                d33 += 12;
            }
            else if (rc.senseMapInfo(ml33).isPassable()) {
                if (d33 > d25) {
                    d33 = d25;
                    dir33 = dir25;
                }
                if (d33 > d34) {
                    d33 = d34;
                    dir33 = dir34;
                }
                if (d33 > d43) {
                    d33 = d43;
                    dir33 = dir43;
                }
                d33 += 8;
            }
        }
        if (rc.onTheMap(ml37)) {
            if (rc.senseMapInfo(ml37).isWater()) {
                if (d37 > d27) {
                    d37 = d27;
                    dir37 = dir27;
                }
                if (d37 > d36) {
                    d37 = d36;
                    dir37 = dir36;
                }
                if (d37 > d45) {
                    d37 = d45;
                    dir37 = dir45;
                }
                d37 += 12;
            }
            else if (rc.senseMapInfo(ml37).isPassable()) {
                if (d37 > d27) {
                    d37 = d27;
                    dir37 = dir27;
                }
                if (d37 > d36) {
                    d37 = d36;
                    dir37 = dir36;
                }
                if (d37 > d45) {
                    d37 = d45;
                    dir37 = dir45;
                }
                d37 += 8;
            }
        }
        if (rc.onTheMap(ml53)) {
            if (rc.senseMapInfo(ml53).isWater()) {
                if (d53 > d43) {
                    d53 = d43;
                    dir53 = dir43;
                }
                if (d53 > d44) {
                    d53 = d44;
                    dir53 = dir44;
                }
                if (d53 > d45) {
                    d53 = d45;
                    dir53 = dir45;
                }
                d53 += 12;
            }
            else if (rc.senseMapInfo(ml53).isPassable()) {
                if (d53 > d43) {
                    d53 = d43;
                    dir53 = dir43;
                }
                if (d53 > d44) {
                    d53 = d44;
                    dir53 = dir44;
                }
                if (d53 > d45) {
                    d53 = d45;
                    dir53 = dir45;
                }
                d53 += 8;
            }
        }
        if (rc.onTheMap(ml16)) {
            if (rc.senseMapInfo(ml16).isWater()) {
                if (d16 > d17) {
                    d16 = d17;
                    dir16 = dir17;
                }
                if (d16 > d25) {
                    d16 = d25;
                    dir16 = dir25;
                }
                if (d16 > d26) {
                    d16 = d26;
                    dir16 = dir26;
                }
                d16 += 12;
            }
            else if (rc.senseMapInfo(ml16).isPassable()) {
                if (d16 > d17) {
                    d16 = d17;
                    dir16 = dir17;
                }
                if (d16 > d25) {
                    d16 = d25;
                    dir16 = dir25;
                }
                if (d16 > d26) {
                    d16 = d26;
                    dir16 = dir26;
                }
                d16 += 8;
            }
        }
        if (rc.onTheMap(ml18)) {
            if (rc.senseMapInfo(ml18).isWater()) {
                if (d18 > d17) {
                    d18 = d17;
                    dir18 = dir17;
                }
                if (d18 > d26) {
                    d18 = d26;
                    dir18 = dir26;
                }
                if (d18 > d27) {
                    d18 = d27;
                    dir18 = dir27;
                }
                d18 += 12;
            }
            else if (rc.senseMapInfo(ml18).isPassable()) {
                if (d18 > d17) {
                    d18 = d17;
                    dir18 = dir17;
                }
                if (d18 > d26) {
                    d18 = d26;
                    dir18 = dir26;
                }
                if (d18 > d27) {
                    d18 = d27;
                    dir18 = dir27;
                }
                d18 += 8;
            }
        }
        if (rc.onTheMap(ml24)) {
            if (rc.senseMapInfo(ml24).isWater()) {
                if (d24 > d16) {
                    d24 = d16;
                    dir24 = dir16;
                }
                if (d24 > d25) {
                    d24 = d25;
                    dir24 = dir25;
                }
                if (d24 > d33) {
                    d24 = d33;
                    dir24 = dir33;
                }
                if (d24 > d34) {
                    d24 = d34;
                    dir24 = dir34;
                }
                d24 += 12;
            }
            else if (rc.senseMapInfo(ml24).isPassable()) {
                if (d24 > d16) {
                    d24 = d16;
                    dir24 = dir16;
                }
                if (d24 > d25) {
                    d24 = d25;
                    dir24 = dir25;
                }
                if (d24 > d33) {
                    d24 = d33;
                    dir24 = dir33;
                }
                if (d24 > d34) {
                    d24 = d34;
                    dir24 = dir34;
                }
                d24 += 8;
            }
        }
        if (rc.onTheMap(ml28)) {
            if (rc.senseMapInfo(ml28).isWater()) {
                if (d28 > d18) {
                    d28 = d18;
                    dir28 = dir18;
                }
                if (d28 > d27) {
                    d28 = d27;
                    dir28 = dir27;
                }
                if (d28 > d36) {
                    d28 = d36;
                    dir28 = dir36;
                }
                if (d28 > d37) {
                    d28 = d37;
                    dir28 = dir37;
                }
                d28 += 12;
            }
            else if (rc.senseMapInfo(ml28).isPassable()) {
                if (d28 > d18) {
                    d28 = d18;
                    dir28 = dir18;
                }
                if (d28 > d27) {
                    d28 = d27;
                    dir28 = dir27;
                }
                if (d28 > d36) {
                    d28 = d36;
                    dir28 = dir36;
                }
                if (d28 > d37) {
                    d28 = d37;
                    dir28 = dir37;
                }
                d28 += 8;
            }
        }
        if (rc.onTheMap(ml42)) {
            if (rc.senseMapInfo(ml42).isWater()) {
                if (d42 > d33) {
                    d42 = d33;
                    dir42 = dir33;
                }
                if (d42 > d34) {
                    d42 = d34;
                    dir42 = dir34;
                }
                if (d42 > d43) {
                    d42 = d43;
                    dir42 = dir43;
                }
                d42 += 12;
            }
            else if (rc.senseMapInfo(ml42).isPassable()) {
                if (d42 > d33) {
                    d42 = d33;
                    dir42 = dir33;
                }
                if (d42 > d34) {
                    d42 = d34;
                    dir42 = dir34;
                }
                if (d42 > d43) {
                    d42 = d43;
                    dir42 = dir43;
                }
                d42 += 8;
            }
        }
        if (rc.onTheMap(ml46)) {
            if (rc.senseMapInfo(ml46).isWater()) {
                if (d46 > d36) {
                    d46 = d36;
                    dir46 = dir36;
                }
                if (d46 > d37) {
                    d46 = d37;
                    dir46 = dir37;
                }
                if (d46 > d45) {
                    d46 = d45;
                    dir46 = dir45;
                }
                d46 += 12;
            }
            else if (rc.senseMapInfo(ml46).isPassable()) {
                if (d46 > d36) {
                    d46 = d36;
                    dir46 = dir36;
                }
                if (d46 > d37) {
                    d46 = d37;
                    dir46 = dir37;
                }
                if (d46 > d45) {
                    d46 = d45;
                    dir46 = dir45;
                }
                d46 += 8;
            }
        }
        if (rc.onTheMap(ml52)) {
            if (rc.senseMapInfo(ml52).isWater()) {
                if (d52 > d42) {
                    d52 = d42;
                    dir52 = dir42;
                }
                if (d52 > d43) {
                    d52 = d43;
                    dir52 = dir43;
                }
                if (d52 > d44) {
                    d52 = d44;
                    dir52 = dir44;
                }
                if (d52 > d53) {
                    d52 = d53;
                    dir52 = dir53;
                }
                d52 += 12;
            }
            else if (rc.senseMapInfo(ml52).isPassable()) {
                if (d52 > d42) {
                    d52 = d42;
                    dir52 = dir42;
                }
                if (d52 > d43) {
                    d52 = d43;
                    dir52 = dir43;
                }
                if (d52 > d44) {
                    d52 = d44;
                    dir52 = dir44;
                }
                if (d52 > d53) {
                    d52 = d53;
                    dir52 = dir53;
                }
                d52 += 8;
            }
        }
        if (rc.onTheMap(ml54)) {
            if (rc.senseMapInfo(ml54).isWater()) {
                if (d54 > d44) {
                    d54 = d44;
                    dir54 = dir44;
                }
                if (d54 > d45) {
                    d54 = d45;
                    dir54 = dir45;
                }
                if (d54 > d46) {
                    d54 = d46;
                    dir54 = dir46;
                }
                if (d54 > d53) {
                    d54 = d53;
                    dir54 = dir53;
                }
                d54 += 12;
            }
            else if (rc.senseMapInfo(ml54).isPassable()) {
                if (d54 > d44) {
                    d54 = d44;
                    dir54 = dir44;
                }
                if (d54 > d45) {
                    d54 = d45;
                    dir54 = dir45;
                }
                if (d54 > d46) {
                    d54 = d46;
                    dir54 = dir46;
                }
                if (d54 > d53) {
                    d54 = d53;
                    dir54 = dir53;
                }
                d54 += 8;
            }
        }
        if (rc.onTheMap(ml15)) {
            if (rc.senseMapInfo(ml15).isWater()) {
                if (d15 > d16) {
                    d15 = d16;
                    dir15 = dir16;
                }
                if (d15 > d24) {
                    d15 = d24;
                    dir15 = dir24;
                }
                if (d15 > d25) {
                    d15 = d25;
                    dir15 = dir25;
                }
                d15 += 12;
            }
            else if (rc.senseMapInfo(ml15).isPassable()) {
                if (d15 > d16) {
                    d15 = d16;
                    dir15 = dir16;
                }
                if (d15 > d24) {
                    d15 = d24;
                    dir15 = dir24;
                }
                if (d15 > d25) {
                    d15 = d25;
                    dir15 = dir25;
                }
                d15 += 8;
            }
        }
        if (rc.onTheMap(ml19)) {
            if (rc.senseMapInfo(ml19).isWater()) {
                if (d19 > d18) {
                    d19 = d18;
                    dir19 = dir18;
                }
                if (d19 > d27) {
                    d19 = d27;
                    dir19 = dir27;
                }
                if (d19 > d28) {
                    d19 = d28;
                    dir19 = dir28;
                }
                d19 += 12;
            }
            else if (rc.senseMapInfo(ml19).isPassable()) {
                if (d19 > d18) {
                    d19 = d18;
                    dir19 = dir18;
                }
                if (d19 > d27) {
                    d19 = d27;
                    dir19 = dir27;
                }
                if (d19 > d28) {
                    d19 = d28;
                    dir19 = dir28;
                }
                d19 += 8;
            }
        }
        if (rc.onTheMap(ml51)) {
            if (rc.senseMapInfo(ml51).isWater()) {
                if (d51 > d42) {
                    d51 = d42;
                    dir51 = dir42;
                }
                if (d51 > d43) {
                    d51 = d43;
                    dir51 = dir43;
                }
                if (d51 > d52) {
                    d51 = d52;
                    dir51 = dir52;
                }
                d51 += 12;
            }
            else if (rc.senseMapInfo(ml51).isPassable()) {
                if (d51 > d42) {
                    d51 = d42;
                    dir51 = dir42;
                }
                if (d51 > d43) {
                    d51 = d43;
                    dir51 = dir43;
                }
                if (d51 > d52) {
                    d51 = d52;
                    dir51 = dir52;
                }
                d51 += 8;
            }
        }
        if (rc.onTheMap(ml55)) {
            if (rc.senseMapInfo(ml55).isWater()) {
                if (d55 > d45) {
                    d55 = d45;
                    dir55 = dir45;
                }
                if (d55 > d46) {
                    d55 = d46;
                    dir55 = dir46;
                }
                if (d55 > d54) {
                    d55 = d54;
                    dir55 = dir54;
                }
                d55 += 12;
            }
            else if (rc.senseMapInfo(ml55).isPassable()) {
                if (d55 > d45) {
                    d55 = d45;
                    dir55 = dir45;
                }
                if (d55 > d46) {
                    d55 = d46;
                    dir55 = dir46;
                }
                if (d55 > d54) {
                    d55 = d54;
                    dir55 = dir54;
                }
                d55 += 8;
            }
        }
        if (rc.onTheMap(ml9)) {
            if (rc.senseMapInfo(ml9).isWater()) {
                if (d9 > d16) {
                    d9 = d16;
                    dir9 = dir16;
                }
                if (d9 > d17) {
                    d9 = d17;
                    dir9 = dir17;
                }
                if (d9 > d18) {
                    d9 = d18;
                    dir9 = dir18;
                }
                d9 += 12;
            }
            else if (rc.senseMapInfo(ml9).isPassable()) {
                if (d9 > d16) {
                    d9 = d16;
                    dir9 = dir16;
                }
                if (d9 > d17) {
                    d9 = d17;
                    dir9 = dir17;
                }
                if (d9 > d18) {
                    d9 = d18;
                    dir9 = dir18;
                }
                d9 += 8;
            }
        }
        if (rc.onTheMap(ml32)) {
            if (rc.senseMapInfo(ml32).isWater()) {
                if (d32 > d24) {
                    d32 = d24;
                    dir32 = dir24;
                }
                if (d32 > d33) {
                    d32 = d33;
                    dir32 = dir33;
                }
                if (d32 > d42) {
                    d32 = d42;
                    dir32 = dir42;
                }
                d32 += 12;
            }
            else if (rc.senseMapInfo(ml32).isPassable()) {
                if (d32 > d24) {
                    d32 = d24;
                    dir32 = dir24;
                }
                if (d32 > d33) {
                    d32 = d33;
                    dir32 = dir33;
                }
                if (d32 > d42) {
                    d32 = d42;
                    dir32 = dir42;
                }
                d32 += 8;
            }
        }
        if (rc.onTheMap(ml38)) {
            if (rc.senseMapInfo(ml38).isWater()) {
                if (d38 > d28) {
                    d38 = d28;
                    dir38 = dir28;
                }
                if (d38 > d37) {
                    d38 = d37;
                    dir38 = dir37;
                }
                if (d38 > d46) {
                    d38 = d46;
                    dir38 = dir46;
                }
                d38 += 12;
            }
            else if (rc.senseMapInfo(ml38).isPassable()) {
                if (d38 > d28) {
                    d38 = d28;
                    dir38 = dir28;
                }
                if (d38 > d37) {
                    d38 = d37;
                    dir38 = dir37;
                }
                if (d38 > d46) {
                    d38 = d46;
                    dir38 = dir46;
                }
                d38 += 8;
            }
        }
        if (rc.onTheMap(ml61)) {
            if (rc.senseMapInfo(ml61).isWater()) {
                if (d61 > d52) {
                    d61 = d52;
                    dir61 = dir52;
                }
                if (d61 > d53) {
                    d61 = d53;
                    dir61 = dir53;
                }
                if (d61 > d54) {
                    d61 = d54;
                    dir61 = dir54;
                }
                d61 += 12;
            }
            else if (rc.senseMapInfo(ml61).isPassable()) {
                if (d61 > d52) {
                    d61 = d52;
                    dir61 = dir52;
                }
                if (d61 > d53) {
                    d61 = d53;
                    dir61 = dir53;
                }
                if (d61 > d54) {
                    d61 = d54;
                    dir61 = dir54;
                }
                d61 += 8;
            }
        }
        if (rc.onTheMap(ml8)) {
            if (rc.senseMapInfo(ml8).isWater()) {
                if (d8 > d9) {
                    d8 = d9;
                    dir8 = dir9;
                }
                if (d8 > d15) {
                    d8 = d15;
                    dir8 = dir15;
                }
                if (d8 > d16) {
                    d8 = d16;
                    dir8 = dir16;
                }
                if (d8 > d17) {
                    d8 = d17;
                    dir8 = dir17;
                }
                d8 += 12;
            }
            else if (rc.senseMapInfo(ml8).isPassable()) {
                if (d8 > d9) {
                    d8 = d9;
                    dir8 = dir9;
                }
                if (d8 > d15) {
                    d8 = d15;
                    dir8 = dir15;
                }
                if (d8 > d16) {
                    d8 = d16;
                    dir8 = dir16;
                }
                if (d8 > d17) {
                    d8 = d17;
                    dir8 = dir17;
                }
                d8 += 8;
            }
        }
        if (rc.onTheMap(ml10)) {
            if (rc.senseMapInfo(ml10).isWater()) {
                if (d10 > d9) {
                    d10 = d9;
                    dir10 = dir9;
                }
                if (d10 > d17) {
                    d10 = d17;
                    dir10 = dir17;
                }
                if (d10 > d18) {
                    d10 = d18;
                    dir10 = dir18;
                }
                if (d10 > d19) {
                    d10 = d19;
                    dir10 = dir19;
                }
                d10 += 12;
            }
            else if (rc.senseMapInfo(ml10).isPassable()) {
                if (d10 > d9) {
                    d10 = d9;
                    dir10 = dir9;
                }
                if (d10 > d17) {
                    d10 = d17;
                    dir10 = dir17;
                }
                if (d10 > d18) {
                    d10 = d18;
                    dir10 = dir18;
                }
                if (d10 > d19) {
                    d10 = d19;
                    dir10 = dir19;
                }
                d10 += 8;
            }
        }
        if (rc.onTheMap(ml23)) {
            if (rc.senseMapInfo(ml23).isWater()) {
                if (d23 > d15) {
                    d23 = d15;
                    dir23 = dir15;
                }
                if (d23 > d24) {
                    d23 = d24;
                    dir23 = dir24;
                }
                if (d23 > d32) {
                    d23 = d32;
                    dir23 = dir32;
                }
                if (d23 > d33) {
                    d23 = d33;
                    dir23 = dir33;
                }
                d23 += 12;
            }
            else if (rc.senseMapInfo(ml23).isPassable()) {
                if (d23 > d15) {
                    d23 = d15;
                    dir23 = dir15;
                }
                if (d23 > d24) {
                    d23 = d24;
                    dir23 = dir24;
                }
                if (d23 > d32) {
                    d23 = d32;
                    dir23 = dir32;
                }
                if (d23 > d33) {
                    d23 = d33;
                    dir23 = dir33;
                }
                d23 += 8;
            }
        }
        if (rc.onTheMap(ml29)) {
            if (rc.senseMapInfo(ml29).isWater()) {
                if (d29 > d19) {
                    d29 = d19;
                    dir29 = dir19;
                }
                if (d29 > d28) {
                    d29 = d28;
                    dir29 = dir28;
                }
                if (d29 > d37) {
                    d29 = d37;
                    dir29 = dir37;
                }
                if (d29 > d38) {
                    d29 = d38;
                    dir29 = dir38;
                }
                d29 += 12;
            }
            else if (rc.senseMapInfo(ml29).isPassable()) {
                if (d29 > d19) {
                    d29 = d19;
                    dir29 = dir19;
                }
                if (d29 > d28) {
                    d29 = d28;
                    dir29 = dir28;
                }
                if (d29 > d37) {
                    d29 = d37;
                    dir29 = dir37;
                }
                if (d29 > d38) {
                    d29 = d38;
                    dir29 = dir38;
                }
                d29 += 8;
            }
        }
        if (rc.onTheMap(ml41)) {
            if (rc.senseMapInfo(ml41).isWater()) {
                if (d41 > d32) {
                    d41 = d32;
                    dir41 = dir32;
                }
                if (d41 > d33) {
                    d41 = d33;
                    dir41 = dir33;
                }
                if (d41 > d42) {
                    d41 = d42;
                    dir41 = dir42;
                }
                if (d41 > d51) {
                    d41 = d51;
                    dir41 = dir51;
                }
                d41 += 12;
            }
            else if (rc.senseMapInfo(ml41).isPassable()) {
                if (d41 > d32) {
                    d41 = d32;
                    dir41 = dir32;
                }
                if (d41 > d33) {
                    d41 = d33;
                    dir41 = dir33;
                }
                if (d41 > d42) {
                    d41 = d42;
                    dir41 = dir42;
                }
                if (d41 > d51) {
                    d41 = d51;
                    dir41 = dir51;
                }
                d41 += 8;
            }
        }
        if (rc.onTheMap(ml47)) {
            if (rc.senseMapInfo(ml47).isWater()) {
                if (d47 > d37) {
                    d47 = d37;
                    dir47 = dir37;
                }
                if (d47 > d38) {
                    d47 = d38;
                    dir47 = dir38;
                }
                if (d47 > d46) {
                    d47 = d46;
                    dir47 = dir46;
                }
                if (d47 > d55) {
                    d47 = d55;
                    dir47 = dir55;
                }
                d47 += 12;
            }
            else if (rc.senseMapInfo(ml47).isPassable()) {
                if (d47 > d37) {
                    d47 = d37;
                    dir47 = dir37;
                }
                if (d47 > d38) {
                    d47 = d38;
                    dir47 = dir38;
                }
                if (d47 > d46) {
                    d47 = d46;
                    dir47 = dir46;
                }
                if (d47 > d55) {
                    d47 = d55;
                    dir47 = dir55;
                }
                d47 += 8;
            }
        }
        if (rc.onTheMap(ml60)) {
            if (rc.senseMapInfo(ml60).isWater()) {
                if (d60 > d51) {
                    d60 = d51;
                    dir60 = dir51;
                }
                if (d60 > d52) {
                    d60 = d52;
                    dir60 = dir52;
                }
                if (d60 > d53) {
                    d60 = d53;
                    dir60 = dir53;
                }
                if (d60 > d61) {
                    d60 = d61;
                    dir60 = dir61;
                }
                d60 += 12;
            }
            else if (rc.senseMapInfo(ml60).isPassable()) {
                if (d60 > d51) {
                    d60 = d51;
                    dir60 = dir51;
                }
                if (d60 > d52) {
                    d60 = d52;
                    dir60 = dir52;
                }
                if (d60 > d53) {
                    d60 = d53;
                    dir60 = dir53;
                }
                if (d60 > d61) {
                    d60 = d61;
                    dir60 = dir61;
                }
                d60 += 8;
            }
        }
        if (rc.onTheMap(ml62)) {
            if (rc.senseMapInfo(ml62).isWater()) {
                if (d62 > d53) {
                    d62 = d53;
                    dir62 = dir53;
                }
                if (d62 > d54) {
                    d62 = d54;
                    dir62 = dir54;
                }
                if (d62 > d55) {
                    d62 = d55;
                    dir62 = dir55;
                }
                if (d62 > d61) {
                    d62 = d61;
                    dir62 = dir61;
                }
                d62 += 12;
            }
            else if (rc.senseMapInfo(ml62).isPassable()) {
                if (d62 > d53) {
                    d62 = d53;
                    dir62 = dir53;
                }
                if (d62 > d54) {
                    d62 = d54;
                    dir62 = dir54;
                }
                if (d62 > d55) {
                    d62 = d55;
                    dir62 = dir55;
                }
                if (d62 > d61) {
                    d62 = d61;
                    dir62 = dir61;
                }
                d62 += 8;
            }
        }
        if (rc.onTheMap(ml7)) {
            if (rc.senseMapInfo(ml7).isWater()) {
                if (d7 > d8) {
                    d7 = d8;
                    dir7 = dir8;
                }
                if (d7 > d15) {
                    d7 = d15;
                    dir7 = dir15;
                }
                if (d7 > d16) {
                    d7 = d16;
                    dir7 = dir16;
                }
                d7 += 12;
            }
            else if (rc.senseMapInfo(ml7).isPassable()) {
                if (d7 > d8) {
                    d7 = d8;
                    dir7 = dir8;
                }
                if (d7 > d15) {
                    d7 = d15;
                    dir7 = dir15;
                }
                if (d7 > d16) {
                    d7 = d16;
                    dir7 = dir16;
                }
                d7 += 8;
            }
        }
        if (rc.onTheMap(ml11)) {
            if (rc.senseMapInfo(ml11).isWater()) {
                if (d11 > d10) {
                    d11 = d10;
                    dir11 = dir10;
                }
                if (d11 > d18) {
                    d11 = d18;
                    dir11 = dir18;
                }
                if (d11 > d19) {
                    d11 = d19;
                    dir11 = dir19;
                }
                d11 += 12;
            }
            else if (rc.senseMapInfo(ml11).isPassable()) {
                if (d11 > d10) {
                    d11 = d10;
                    dir11 = dir10;
                }
                if (d11 > d18) {
                    d11 = d18;
                    dir11 = dir18;
                }
                if (d11 > d19) {
                    d11 = d19;
                    dir11 = dir19;
                }
                d11 += 8;
            }
        }
        if (rc.onTheMap(ml14)) {
            if (rc.senseMapInfo(ml14).isWater()) {
                if (d14 > d7) {
                    d14 = d7;
                    dir14 = dir7;
                }
                if (d14 > d15) {
                    d14 = d15;
                    dir14 = dir15;
                }
                if (d14 > d23) {
                    d14 = d23;
                    dir14 = dir23;
                }
                if (d14 > d24) {
                    d14 = d24;
                    dir14 = dir24;
                }
                d14 += 12;
            }
            else if (rc.senseMapInfo(ml14).isPassable()) {
                if (d14 > d7) {
                    d14 = d7;
                    dir14 = dir7;
                }
                if (d14 > d15) {
                    d14 = d15;
                    dir14 = dir15;
                }
                if (d14 > d23) {
                    d14 = d23;
                    dir14 = dir23;
                }
                if (d14 > d24) {
                    d14 = d24;
                    dir14 = dir24;
                }
                d14 += 8;
            }
        }
        if (rc.onTheMap(ml20)) {
            if (rc.senseMapInfo(ml20).isWater()) {
                if (d20 > d11) {
                    d20 = d11;
                    dir20 = dir11;
                }
                if (d20 > d19) {
                    d20 = d19;
                    dir20 = dir19;
                }
                if (d20 > d28) {
                    d20 = d28;
                    dir20 = dir28;
                }
                if (d20 > d29) {
                    d20 = d29;
                    dir20 = dir29;
                }
                d20 += 12;
            }
            else if (rc.senseMapInfo(ml20).isPassable()) {
                if (d20 > d11) {
                    d20 = d11;
                    dir20 = dir11;
                }
                if (d20 > d19) {
                    d20 = d19;
                    dir20 = dir19;
                }
                if (d20 > d28) {
                    d20 = d28;
                    dir20 = dir28;
                }
                if (d20 > d29) {
                    d20 = d29;
                    dir20 = dir29;
                }
                d20 += 8;
            }
        }
        if (rc.onTheMap(ml50)) {
            if (rc.senseMapInfo(ml50).isWater()) {
                if (d50 > d41) {
                    d50 = d41;
                    dir50 = dir41;
                }
                if (d50 > d42) {
                    d50 = d42;
                    dir50 = dir42;
                }
                if (d50 > d51) {
                    d50 = d51;
                    dir50 = dir51;
                }
                d50 += 12;
            }
            else if (rc.senseMapInfo(ml50).isPassable()) {
                if (d50 > d41) {
                    d50 = d41;
                    dir50 = dir41;
                }
                if (d50 > d42) {
                    d50 = d42;
                    dir50 = dir42;
                }
                if (d50 > d51) {
                    d50 = d51;
                    dir50 = dir51;
                }
                d50 += 8;
            }
        }
        if (rc.onTheMap(ml56)) {
            if (rc.senseMapInfo(ml56).isWater()) {
                if (d56 > d46) {
                    d56 = d46;
                    dir56 = dir46;
                }
                if (d56 > d47) {
                    d56 = d47;
                    dir56 = dir47;
                }
                if (d56 > d55) {
                    d56 = d55;
                    dir56 = dir55;
                }
                d56 += 12;
            }
            else if (rc.senseMapInfo(ml56).isPassable()) {
                if (d56 > d46) {
                    d56 = d46;
                    dir56 = dir46;
                }
                if (d56 > d47) {
                    d56 = d47;
                    dir56 = dir47;
                }
                if (d56 > d55) {
                    d56 = d55;
                    dir56 = dir55;
                }
                d56 += 8;
            }
        }
        if (rc.onTheMap(ml59)) {
            if (rc.senseMapInfo(ml59).isWater()) {
                if (d59 > d50) {
                    d59 = d50;
                    dir59 = dir50;
                }
                if (d59 > d51) {
                    d59 = d51;
                    dir59 = dir51;
                }
                if (d59 > d52) {
                    d59 = d52;
                    dir59 = dir52;
                }
                if (d59 > d60) {
                    d59 = d60;
                    dir59 = dir60;
                }
                d59 += 12;
            }
            else if (rc.senseMapInfo(ml59).isPassable()) {
                if (d59 > d50) {
                    d59 = d50;
                    dir59 = dir50;
                }
                if (d59 > d51) {
                    d59 = d51;
                    dir59 = dir51;
                }
                if (d59 > d52) {
                    d59 = d52;
                    dir59 = dir52;
                }
                if (d59 > d60) {
                    d59 = d60;
                    dir59 = dir60;
                }
                d59 += 8;
            }
        }
        if (rc.onTheMap(ml63)) {
            if (rc.senseMapInfo(ml63).isWater()) {
                if (d63 > d54) {
                    d63 = d54;
                    dir63 = dir54;
                }
                if (d63 > d55) {
                    d63 = d55;
                    dir63 = dir55;
                }
                if (d63 > d56) {
                    d63 = d56;
                    dir63 = dir56;
                }
                if (d63 > d62) {
                    d63 = d62;
                    dir63 = dir62;
                }
                d63 += 12;
            }
            else if (rc.senseMapInfo(ml63).isPassable()) {
                if (d63 > d54) {
                    d63 = d54;
                    dir63 = dir54;
                }
                if (d63 > d55) {
                    d63 = d55;
                    dir63 = dir55;
                }
                if (d63 > d56) {
                    d63 = d56;
                    dir63 = dir56;
                }
                if (d63 > d62) {
                    d63 = d62;
                    dir63 = dir62;
                }
                d63 += 8;
            }
        }
        if (rc.onTheMap(ml3)) {
            if (rc.senseMapInfo(ml3).isWater()) {
                if (d3 > d8) {
                    d3 = d8;
                    dir3 = dir8;
                }
                if (d3 > d9) {
                    d3 = d9;
                    dir3 = dir9;
                }
                if (d3 > d10) {
                    d3 = d10;
                    dir3 = dir10;
                }
                d3 += 12;
            }
            else if (rc.senseMapInfo(ml3).isPassable()) {
                if (d3 > d8) {
                    d3 = d8;
                    dir3 = dir8;
                }
                if (d3 > d9) {
                    d3 = d9;
                    dir3 = dir9;
                }
                if (d3 > d10) {
                    d3 = d10;
                    dir3 = dir10;
                }
                d3 += 8;
            }
        }
        if (rc.onTheMap(ml31)) {
            if (rc.senseMapInfo(ml31).isWater()) {
                if (d31 > d23) {
                    d31 = d23;
                    dir31 = dir23;
                }
                if (d31 > d32) {
                    d31 = d32;
                    dir31 = dir32;
                }
                if (d31 > d41) {
                    d31 = d41;
                    dir31 = dir41;
                }
                d31 += 12;
            }
            else if (rc.senseMapInfo(ml31).isPassable()) {
                if (d31 > d23) {
                    d31 = d23;
                    dir31 = dir23;
                }
                if (d31 > d32) {
                    d31 = d32;
                    dir31 = dir32;
                }
                if (d31 > d41) {
                    d31 = d41;
                    dir31 = dir41;
                }
                d31 += 8;
            }
        }
        if (rc.onTheMap(ml39)) {
            if (rc.senseMapInfo(ml39).isWater()) {
                if (d39 > d29) {
                    d39 = d29;
                    dir39 = dir29;
                }
                if (d39 > d38) {
                    d39 = d38;
                    dir39 = dir38;
                }
                if (d39 > d47) {
                    d39 = d47;
                    dir39 = dir47;
                }
                d39 += 12;
            }
            else if (rc.senseMapInfo(ml39).isPassable()) {
                if (d39 > d29) {
                    d39 = d29;
                    dir39 = dir29;
                }
                if (d39 > d38) {
                    d39 = d38;
                    dir39 = dir38;
                }
                if (d39 > d47) {
                    d39 = d47;
                    dir39 = dir47;
                }
                d39 += 8;
            }
        }
        if (rc.onTheMap(ml67)) {
            if (rc.senseMapInfo(ml67).isWater()) {
                if (d67 > d60) {
                    d67 = d60;
                    dir67 = dir60;
                }
                if (d67 > d61) {
                    d67 = d61;
                    dir67 = dir61;
                }
                if (d67 > d62) {
                    d67 = d62;
                    dir67 = dir62;
                }
                d67 += 12;
            }
            else if (rc.senseMapInfo(ml67).isPassable()) {
                if (d67 > d60) {
                    d67 = d60;
                    dir67 = dir60;
                }
                if (d67 > d61) {
                    d67 = d61;
                    dir67 = dir61;
                }
                if (d67 > d62) {
                    d67 = d62;
                    dir67 = dir62;
                }
                d67 += 8;
            }
        }
        if (rc.onTheMap(ml2)) {
            if (rc.senseMapInfo(ml2).isWater()) {
                if (d2 > d3) {
                    d2 = d3;
                    dir2 = dir3;
                }
                if (d2 > d7) {
                    d2 = d7;
                    dir2 = dir7;
                }
                if (d2 > d8) {
                    d2 = d8;
                    dir2 = dir8;
                }
                if (d2 > d9) {
                    d2 = d9;
                    dir2 = dir9;
                }
                d2 += 12;
            }
            else if (rc.senseMapInfo(ml2).isPassable()) {
                if (d2 > d3) {
                    d2 = d3;
                    dir2 = dir3;
                }
                if (d2 > d7) {
                    d2 = d7;
                    dir2 = dir7;
                }
                if (d2 > d8) {
                    d2 = d8;
                    dir2 = dir8;
                }
                if (d2 > d9) {
                    d2 = d9;
                    dir2 = dir9;
                }
                d2 += 8;
            }
        }
        if (rc.onTheMap(ml4)) {
            if (rc.senseMapInfo(ml4).isWater()) {
                if (d4 > d3) {
                    d4 = d3;
                    dir4 = dir3;
                }
                if (d4 > d9) {
                    d4 = d9;
                    dir4 = dir9;
                }
                if (d4 > d10) {
                    d4 = d10;
                    dir4 = dir10;
                }
                if (d4 > d11) {
                    d4 = d11;
                    dir4 = dir11;
                }
                d4 += 12;
            }
            else if (rc.senseMapInfo(ml4).isPassable()) {
                if (d4 > d3) {
                    d4 = d3;
                    dir4 = dir3;
                }
                if (d4 > d9) {
                    d4 = d9;
                    dir4 = dir9;
                }
                if (d4 > d10) {
                    d4 = d10;
                    dir4 = dir10;
                }
                if (d4 > d11) {
                    d4 = d11;
                    dir4 = dir11;
                }
                d4 += 8;
            }
        }
        if (rc.onTheMap(ml22)) {
            if (rc.senseMapInfo(ml22).isWater()) {
                if (d22 > d14) {
                    d22 = d14;
                    dir22 = dir14;
                }
                if (d22 > d23) {
                    d22 = d23;
                    dir22 = dir23;
                }
                if (d22 > d31) {
                    d22 = d31;
                    dir22 = dir31;
                }
                if (d22 > d32) {
                    d22 = d32;
                    dir22 = dir32;
                }
                d22 += 12;
            }
            else if (rc.senseMapInfo(ml22).isPassable()) {
                if (d22 > d14) {
                    d22 = d14;
                    dir22 = dir14;
                }
                if (d22 > d23) {
                    d22 = d23;
                    dir22 = dir23;
                }
                if (d22 > d31) {
                    d22 = d31;
                    dir22 = dir31;
                }
                if (d22 > d32) {
                    d22 = d32;
                    dir22 = dir32;
                }
                d22 += 8;
            }
        }
        if (rc.onTheMap(ml30)) {
            if (rc.senseMapInfo(ml30).isWater()) {
                if (d30 > d20) {
                    d30 = d20;
                    dir30 = dir20;
                }
                if (d30 > d29) {
                    d30 = d29;
                    dir30 = dir29;
                }
                if (d30 > d38) {
                    d30 = d38;
                    dir30 = dir38;
                }
                if (d30 > d39) {
                    d30 = d39;
                    dir30 = dir39;
                }
                d30 += 12;
            }
            else if (rc.senseMapInfo(ml30).isPassable()) {
                if (d30 > d20) {
                    d30 = d20;
                    dir30 = dir20;
                }
                if (d30 > d29) {
                    d30 = d29;
                    dir30 = dir29;
                }
                if (d30 > d38) {
                    d30 = d38;
                    dir30 = dir38;
                }
                if (d30 > d39) {
                    d30 = d39;
                    dir30 = dir39;
                }
                d30 += 8;
            }
        }
        if (rc.onTheMap(ml40)) {
            if (rc.senseMapInfo(ml40).isWater()) {
                if (d40 > d31) {
                    d40 = d31;
                    dir40 = dir31;
                }
                if (d40 > d32) {
                    d40 = d32;
                    dir40 = dir32;
                }
                if (d40 > d41) {
                    d40 = d41;
                    dir40 = dir41;
                }
                if (d40 > d50) {
                    d40 = d50;
                    dir40 = dir50;
                }
                d40 += 12;
            }
            else if (rc.senseMapInfo(ml40).isPassable()) {
                if (d40 > d31) {
                    d40 = d31;
                    dir40 = dir31;
                }
                if (d40 > d32) {
                    d40 = d32;
                    dir40 = dir32;
                }
                if (d40 > d41) {
                    d40 = d41;
                    dir40 = dir41;
                }
                if (d40 > d50) {
                    d40 = d50;
                    dir40 = dir50;
                }
                d40 += 8;
            }
        }
        if (rc.onTheMap(ml48)) {
            if (rc.senseMapInfo(ml48).isWater()) {
                if (d48 > d38) {
                    d48 = d38;
                    dir48 = dir38;
                }
                if (d48 > d39) {
                    d48 = d39;
                    dir48 = dir39;
                }
                if (d48 > d47) {
                    d48 = d47;
                    dir48 = dir47;
                }
                if (d48 > d56) {
                    d48 = d56;
                    dir48 = dir56;
                }
                d48 += 12;
            }
            else if (rc.senseMapInfo(ml48).isPassable()) {
                if (d48 > d38) {
                    d48 = d38;
                    dir48 = dir38;
                }
                if (d48 > d39) {
                    d48 = d39;
                    dir48 = dir39;
                }
                if (d48 > d47) {
                    d48 = d47;
                    dir48 = dir47;
                }
                if (d48 > d56) {
                    d48 = d56;
                    dir48 = dir56;
                }
                d48 += 8;
            }
        }
        if (rc.onTheMap(ml66)) {
            if (rc.senseMapInfo(ml66).isWater()) {
                if (d66 > d59) {
                    d66 = d59;
                    dir66 = dir59;
                }
                if (d66 > d60) {
                    d66 = d60;
                    dir66 = dir60;
                }
                if (d66 > d61) {
                    d66 = d61;
                    dir66 = dir61;
                }
                if (d66 > d67) {
                    d66 = d67;
                    dir66 = dir67;
                }
                d66 += 12;
            }
            else if (rc.senseMapInfo(ml66).isPassable()) {
                if (d66 > d59) {
                    d66 = d59;
                    dir66 = dir59;
                }
                if (d66 > d60) {
                    d66 = d60;
                    dir66 = dir60;
                }
                if (d66 > d61) {
                    d66 = d61;
                    dir66 = dir61;
                }
                if (d66 > d67) {
                    d66 = d67;
                    dir66 = dir67;
                }
                d66 += 8;
            }
        }
        if (rc.onTheMap(ml68)) {
            if (rc.senseMapInfo(ml68).isWater()) {
                if (d68 > d61) {
                    d68 = d61;
                    dir68 = dir61;
                }
                if (d68 > d62) {
                    d68 = d62;
                    dir68 = dir62;
                }
                if (d68 > d63) {
                    d68 = d63;
                    dir68 = dir63;
                }
                if (d68 > d67) {
                    d68 = d67;
                    dir68 = dir67;
                }
                d68 += 12;
            }
            else if (rc.senseMapInfo(ml68).isPassable()) {
                if (d68 > d61) {
                    d68 = d61;
                    dir68 = dir61;
                }
                if (d68 > d62) {
                    d68 = d62;
                    dir68 = dir62;
                }
                if (d68 > d63) {
                    d68 = d63;
                    dir68 = dir63;
                }
                if (d68 > d67) {
                    d68 = d67;
                    dir68 = dir67;
                }
                d68 += 8;
            }
        }
        if (rc.onTheMap(ml6)) {
            if (rc.senseMapInfo(ml6).isWater()) {
                if (d6 > d7) {
                    d6 = d7;
                    dir6 = dir7;
                }
                if (d6 > d14) {
                    d6 = d14;
                    dir6 = dir14;
                }
                if (d6 > d15) {
                    d6 = d15;
                    dir6 = dir15;
                }
                d6 += 12;
            }
            else if (rc.senseMapInfo(ml6).isPassable()) {
                if (d6 > d7) {
                    d6 = d7;
                    dir6 = dir7;
                }
                if (d6 > d14) {
                    d6 = d14;
                    dir6 = dir14;
                }
                if (d6 > d15) {
                    d6 = d15;
                    dir6 = dir15;
                }
                d6 += 8;
            }
        }
        if (rc.onTheMap(ml12)) {
            if (rc.senseMapInfo(ml12).isWater()) {
                if (d12 > d11) {
                    d12 = d11;
                    dir12 = dir11;
                }
                if (d12 > d19) {
                    d12 = d19;
                    dir12 = dir19;
                }
                if (d12 > d20) {
                    d12 = d20;
                    dir12 = dir20;
                }
                d12 += 12;
            }
            else if (rc.senseMapInfo(ml12).isPassable()) {
                if (d12 > d11) {
                    d12 = d11;
                    dir12 = dir11;
                }
                if (d12 > d19) {
                    d12 = d19;
                    dir12 = dir19;
                }
                if (d12 > d20) {
                    d12 = d20;
                    dir12 = dir20;
                }
                d12 += 8;
            }
        }
        if (rc.onTheMap(ml58)) {
            if (rc.senseMapInfo(ml58).isWater()) {
                if (d58 > d50) {
                    d58 = d50;
                    dir58 = dir50;
                }
                if (d58 > d51) {
                    d58 = d51;
                    dir58 = dir51;
                }
                if (d58 > d59) {
                    d58 = d59;
                    dir58 = dir59;
                }
                d58 += 12;
            }
            else if (rc.senseMapInfo(ml58).isPassable()) {
                if (d58 > d50) {
                    d58 = d50;
                    dir58 = dir50;
                }
                if (d58 > d51) {
                    d58 = d51;
                    dir58 = dir51;
                }
                if (d58 > d59) {
                    d58 = d59;
                    dir58 = dir59;
                }
                d58 += 8;
            }
        }
        if (rc.onTheMap(ml64)) {
            if (rc.senseMapInfo(ml64).isWater()) {
                if (d64 > d55) {
                    d64 = d55;
                    dir64 = dir55;
                }
                if (d64 > d56) {
                    d64 = d56;
                    dir64 = dir56;
                }
                if (d64 > d63) {
                    d64 = d63;
                    dir64 = dir63;
                }
                d64 += 12;
            }
            else if (rc.senseMapInfo(ml64).isPassable()) {
                if (d64 > d55) {
                    d64 = d55;
                    dir64 = dir55;
                }
                if (d64 > d56) {
                    d64 = d56;
                    dir64 = dir56;
                }
                if (d64 > d63) {
                    d64 = d63;
                    dir64 = dir63;
                }
                d64 += 8;
            }
        }
        if (rc.onTheMap(ml1)) {
            if (rc.senseMapInfo(ml1).isWater()) {
                if (d1 > d2) {
                    d1 = d2;
                    dir1 = dir2;
                }
                if (d1 > d6) {
                    d1 = d6;
                    dir1 = dir6;
                }
                if (d1 > d7) {
                    d1 = d7;
                    dir1 = dir7;
                }
                if (d1 > d8) {
                    d1 = d8;
                    dir1 = dir8;
                }
                d1 += 12;
            }
            else if (rc.senseMapInfo(ml1).isPassable()) {
                if (d1 > d2) {
                    d1 = d2;
                    dir1 = dir2;
                }
                if (d1 > d6) {
                    d1 = d6;
                    dir1 = dir6;
                }
                if (d1 > d7) {
                    d1 = d7;
                    dir1 = dir7;
                }
                if (d1 > d8) {
                    d1 = d8;
                    dir1 = dir8;
                }
                d1 += 8;
            }
        }
        if (rc.onTheMap(ml5)) {
            if (rc.senseMapInfo(ml5).isWater()) {
                if (d5 > d4) {
                    d5 = d4;
                    dir5 = dir4;
                }
                if (d5 > d10) {
                    d5 = d10;
                    dir5 = dir10;
                }
                if (d5 > d11) {
                    d5 = d11;
                    dir5 = dir11;
                }
                if (d5 > d12) {
                    d5 = d12;
                    dir5 = dir12;
                }
                d5 += 12;
            }
            else if (rc.senseMapInfo(ml5).isPassable()) {
                if (d5 > d4) {
                    d5 = d4;
                    dir5 = dir4;
                }
                if (d5 > d10) {
                    d5 = d10;
                    dir5 = dir10;
                }
                if (d5 > d11) {
                    d5 = d11;
                    dir5 = dir11;
                }
                if (d5 > d12) {
                    d5 = d12;
                    dir5 = dir12;
                }
                d5 += 8;
            }
        }
        if (rc.onTheMap(ml13)) {
            if (rc.senseMapInfo(ml13).isWater()) {
                if (d13 > d6) {
                    d13 = d6;
                    dir13 = dir6;
                }
                if (d13 > d14) {
                    d13 = d14;
                    dir13 = dir14;
                }
                if (d13 > d22) {
                    d13 = d22;
                    dir13 = dir22;
                }
                if (d13 > d23) {
                    d13 = d23;
                    dir13 = dir23;
                }
                d13 += 12;
            }
            else if (rc.senseMapInfo(ml13).isPassable()) {
                if (d13 > d6) {
                    d13 = d6;
                    dir13 = dir6;
                }
                if (d13 > d14) {
                    d13 = d14;
                    dir13 = dir14;
                }
                if (d13 > d22) {
                    d13 = d22;
                    dir13 = dir22;
                }
                if (d13 > d23) {
                    d13 = d23;
                    dir13 = dir23;
                }
                d13 += 8;
            }
        }
        if (rc.onTheMap(ml21)) {
            if (rc.senseMapInfo(ml21).isWater()) {
                if (d21 > d12) {
                    d21 = d12;
                    dir21 = dir12;
                }
                if (d21 > d20) {
                    d21 = d20;
                    dir21 = dir20;
                }
                if (d21 > d29) {
                    d21 = d29;
                    dir21 = dir29;
                }
                if (d21 > d30) {
                    d21 = d30;
                    dir21 = dir30;
                }
                d21 += 12;
            }
            else if (rc.senseMapInfo(ml21).isPassable()) {
                if (d21 > d12) {
                    d21 = d12;
                    dir21 = dir12;
                }
                if (d21 > d20) {
                    d21 = d20;
                    dir21 = dir20;
                }
                if (d21 > d29) {
                    d21 = d29;
                    dir21 = dir29;
                }
                if (d21 > d30) {
                    d21 = d30;
                    dir21 = dir30;
                }
                d21 += 8;
            }
        }
        if (rc.onTheMap(ml49)) {
            if (rc.senseMapInfo(ml49).isWater()) {
                if (d49 > d40) {
                    d49 = d40;
                    dir49 = dir40;
                }
                if (d49 > d41) {
                    d49 = d41;
                    dir49 = dir41;
                }
                if (d49 > d50) {
                    d49 = d50;
                    dir49 = dir50;
                }
                if (d49 > d58) {
                    d49 = d58;
                    dir49 = dir58;
                }
                d49 += 12;
            }
            else if (rc.senseMapInfo(ml49).isPassable()) {
                if (d49 > d40) {
                    d49 = d40;
                    dir49 = dir40;
                }
                if (d49 > d41) {
                    d49 = d41;
                    dir49 = dir41;
                }
                if (d49 > d50) {
                    d49 = d50;
                    dir49 = dir50;
                }
                if (d49 > d58) {
                    d49 = d58;
                    dir49 = dir58;
                }
                d49 += 8;
            }
        }
        if (rc.onTheMap(ml57)) {
            if (rc.senseMapInfo(ml57).isWater()) {
                if (d57 > d47) {
                    d57 = d47;
                    dir57 = dir47;
                }
                if (d57 > d48) {
                    d57 = d48;
                    dir57 = dir48;
                }
                if (d57 > d56) {
                    d57 = d56;
                    dir57 = dir56;
                }
                if (d57 > d64) {
                    d57 = d64;
                    dir57 = dir64;
                }
                d57 += 12;
            }
            else if (rc.senseMapInfo(ml57).isPassable()) {
                if (d57 > d47) {
                    d57 = d47;
                    dir57 = dir47;
                }
                if (d57 > d48) {
                    d57 = d48;
                    dir57 = dir48;
                }
                if (d57 > d56) {
                    d57 = d56;
                    dir57 = dir56;
                }
                if (d57 > d64) {
                    d57 = d64;
                    dir57 = dir64;
                }
                d57 += 8;
            }
        }
        if (rc.onTheMap(ml65)) {
            if (rc.senseMapInfo(ml65).isWater()) {
                if (d65 > d58) {
                    d65 = d58;
                    dir65 = dir58;
                }
                if (d65 > d59) {
                    d65 = d59;
                    dir65 = dir59;
                }
                if (d65 > d60) {
                    d65 = d60;
                    dir65 = dir60;
                }
                if (d65 > d66) {
                    d65 = d66;
                    dir65 = dir66;
                }
                d65 += 12;
            }
            else if (rc.senseMapInfo(ml65).isPassable()) {
                if (d65 > d58) {
                    d65 = d58;
                    dir65 = dir58;
                }
                if (d65 > d59) {
                    d65 = d59;
                    dir65 = dir59;
                }
                if (d65 > d60) {
                    d65 = d60;
                    dir65 = dir60;
                }
                if (d65 > d66) {
                    d65 = d66;
                    dir65 = dir66;
                }
                d65 += 8;
            }
        }
        if (rc.onTheMap(ml69)) {
            if (rc.senseMapInfo(ml69).isWater()) {
                if (d69 > d62) {
                    d69 = d62;
                    dir69 = dir62;
                }
                if (d69 > d63) {
                    d69 = d63;
                    dir69 = dir63;
                }
                if (d69 > d64) {
                    d69 = d64;
                    dir69 = dir64;
                }
                if (d69 > d68) {
                    d69 = d68;
                    dir69 = dir68;
                }
                d69 += 12;
            }
            else if (rc.senseMapInfo(ml69).isPassable()) {
                if (d69 > d62) {
                    d69 = d62;
                    dir69 = dir62;
                }
                if (d69 > d63) {
                    d69 = d63;
                    dir69 = dir63;
                }
                if (d69 > d64) {
                    d69 = d64;
                    dir69 = dir64;
                }
                if (d69 > d68) {
                    d69 = d68;
                    dir69 = dir68;
                }
                d69 += 8;
            }
        }

        int target_dx = target.x - ml35.x;
        int target_dy = target.y - ml35.y;
        switch (target_dx) {
            case -4:
                switch (target_dy) {
                    case -2:
                        return dir1;
                    case -1:
                        return dir2;
                    case 0:
                        return dir3;
                    case 1:
                        return dir4;
                    case 2:
                        return dir5;
                }
                break;
            case -3:
                switch (target_dy) {
                    case -3:
                        return dir6;
                    case -2:
                        return dir7;
                    case -1:
                        return dir8;
                    case 0:
                        return dir9;
                    case 1:
                        return dir10;
                    case 2:
                        return dir11;
                    case 3:
                        return dir12;
                }
                break;
            case -2:
                switch (target_dy) {
                    case -4:
                        return dir13;
                    case -3:
                        return dir14;
                    case -2:
                        return dir15;
                    case -1:
                        return dir16;
                    case 0:
                        return dir17;
                    case 1:
                        return dir18;
                    case 2:
                        return dir19;
                    case 3:
                        return dir20;
                    case 4:
                        return dir21;
                }
                break;
            case -1:
                switch (target_dy) {
                    case -4:
                        return dir22;
                    case -3:
                        return dir23;
                    case -2:
                        return dir24;
                    case -1:
                        return dir25;
                    case 0:
                        return dir26;
                    case 1:
                        return dir27;
                    case 2:
                        return dir28;
                    case 3:
                        return dir29;
                    case 4:
                        return dir30;
                }
                break;
            case 0:
                switch (target_dy) {
                    case -4:
                        return dir31;
                    case -3:
                        return dir32;
                    case -2:
                        return dir33;
                    case -1:
                        return dir34;
                    case 0:
                        return dir35;
                    case 1:
                        return dir36;
                    case 2:
                        return dir37;
                    case 3:
                        return dir38;
                    case 4:
                        return dir39;
                }
                break;
            case 1:
                switch (target_dy) {
                    case -4:
                        return dir40;
                    case -3:
                        return dir41;
                    case -2:
                        return dir42;
                    case -1:
                        return dir43;
                    case 0:
                        return dir44;
                    case 1:
                        return dir45;
                    case 2:
                        return dir46;
                    case 3:
                        return dir47;
                    case 4:
                        return dir48;
                }
                break;
            case 2:
                switch (target_dy) {
                    case -4:
                        return dir49;
                    case -3:
                        return dir50;
                    case -2:
                        return dir51;
                    case -1:
                        return dir52;
                    case 0:
                        return dir53;
                    case 1:
                        return dir54;
                    case 2:
                        return dir55;
                    case 3:
                        return dir56;
                    case 4:
                        return dir57;
                }
                break;
            case 3:
                switch (target_dy) {
                    case -3:
                        return dir58;
                    case -2:
                        return dir59;
                    case -1:
                        return dir60;
                    case 0:
                        return dir61;
                    case 1:
                        return dir62;
                    case 2:
                        return dir63;
                    case 3:
                        return dir64;
                }
                break;
            case 4:
                switch (target_dy) {
                    case -2:
                        return dir65;
                    case -1:
                        return dir66;
                    case 0:
                        return dir67;
                    case 1:
                        return dir68;
                    case 2:
                        return dir69;
                }
                break;
        }

        Direction ans = null;
        double bestScore = 0;
        double currDist = Math.sqrt(ml35.distanceSquaredTo(target));
        double cost = Double.MAX_VALUE;double score1 = (currDist - Math.sqrt(ml1.distanceSquaredTo(target))) / d1;
        if (score1 > bestScore) {
            bestScore = score1;
            ans = dir1;
            cost = d1;
        }
        double score2 = (currDist - Math.sqrt(ml2.distanceSquaredTo(target))) / d2;
        if (score2 > bestScore) {
            bestScore = score2;
            ans = dir2;
            cost = d2;
        }
        double score3 = (currDist - Math.sqrt(ml3.distanceSquaredTo(target))) / d3;
        if (score3 > bestScore) {
            bestScore = score3;
            ans = dir3;
            cost = d3;
        }
        double score4 = (currDist - Math.sqrt(ml4.distanceSquaredTo(target))) / d4;
        if (score4 > bestScore) {
            bestScore = score4;
            ans = dir4;
            cost = d4;
        }
        double score5 = (currDist - Math.sqrt(ml5.distanceSquaredTo(target))) / d5;
        if (score5 > bestScore) {
            bestScore = score5;
            ans = dir5;
            cost = d5;
        }
        double score6 = (currDist - Math.sqrt(ml6.distanceSquaredTo(target))) / d6;
        if (score6 > bestScore) {
            bestScore = score6;
            ans = dir6;
            cost = d6;
        }
        double score12 = (currDist - Math.sqrt(ml12.distanceSquaredTo(target))) / d12;
        if (score12 > bestScore) {
            bestScore = score12;
            ans = dir12;
            cost = d12;
        }
        double score13 = (currDist - Math.sqrt(ml13.distanceSquaredTo(target))) / d13;
        if (score13 > bestScore) {
            bestScore = score13;
            ans = dir13;
            cost = d13;
        }
        double score21 = (currDist - Math.sqrt(ml21.distanceSquaredTo(target))) / d21;
        if (score21 > bestScore) {
            bestScore = score21;
            ans = dir21;
            cost = d21;
        }
        double score22 = (currDist - Math.sqrt(ml22.distanceSquaredTo(target))) / d22;
        if (score22 > bestScore) {
            bestScore = score22;
            ans = dir22;
            cost = d22;
        }
        double score30 = (currDist - Math.sqrt(ml30.distanceSquaredTo(target))) / d30;
        if (score30 > bestScore) {
            bestScore = score30;
            ans = dir30;
            cost = d30;
        }
        double score31 = (currDist - Math.sqrt(ml31.distanceSquaredTo(target))) / d31;
        if (score31 > bestScore) {
            bestScore = score31;
            ans = dir31;
            cost = d31;
        }
        double score39 = (currDist - Math.sqrt(ml39.distanceSquaredTo(target))) / d39;
        if (score39 > bestScore) {
            bestScore = score39;
            ans = dir39;
            cost = d39;
        }
        double score40 = (currDist - Math.sqrt(ml40.distanceSquaredTo(target))) / d40;
        if (score40 > bestScore) {
            bestScore = score40;
            ans = dir40;
            cost = d40;
        }
        double score48 = (currDist - Math.sqrt(ml48.distanceSquaredTo(target))) / d48;
        if (score48 > bestScore) {
            bestScore = score48;
            ans = dir48;
            cost = d48;
        }
        double score49 = (currDist - Math.sqrt(ml49.distanceSquaredTo(target))) / d49;
        if (score49 > bestScore) {
            bestScore = score49;
            ans = dir49;
            cost = d49;
        }
        double score57 = (currDist - Math.sqrt(ml57.distanceSquaredTo(target))) / d57;
        if (score57 > bestScore) {
            bestScore = score57;
            ans = dir57;
            cost = d57;
        }
        double score58 = (currDist - Math.sqrt(ml58.distanceSquaredTo(target))) / d58;
        if (score58 > bestScore) {
            bestScore = score58;
            ans = dir58;
            cost = d58;
        }
        double score64 = (currDist - Math.sqrt(ml64.distanceSquaredTo(target))) / d64;
        if (score64 > bestScore) {
            bestScore = score64;
            ans = dir64;
            cost = d64;
        }
        double score65 = (currDist - Math.sqrt(ml65.distanceSquaredTo(target))) / d65;
        if (score65 > bestScore) {
            bestScore = score65;
            ans = dir65;
            cost = d65;
        }
        double score66 = (currDist - Math.sqrt(ml66.distanceSquaredTo(target))) / d66;
        if (score66 > bestScore) {
            bestScore = score66;
            ans = dir66;
            cost = d66;
        }
        double score67 = (currDist - Math.sqrt(ml67.distanceSquaredTo(target))) / d67;
        if (score67 > bestScore) {
            bestScore = score67;
            ans = dir67;
            cost = d67;
        }
        double score68 = (currDist - Math.sqrt(ml68.distanceSquaredTo(target))) / d68;
        if (score68 > bestScore) {
            bestScore = score68;
            ans = dir68;
            cost = d68;
        }
        double score69 = (currDist - Math.sqrt(ml69.distanceSquaredTo(target))) / d69;
        if (score69 > bestScore) {
            bestScore = score69;
            ans = dir69;
            cost = d69;
        }

        if (cost > 9999) {
            Bug.goTo(target, true);
            return null;
        }

        return ans;
    }

}
