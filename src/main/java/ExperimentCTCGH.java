package main.java;


public class ExperimentCTCGH {

    public static void main(String[] args) {
        int roundsConf = 60;
        int[] killerOptimize = {0};
        String[] solvers = {"Sat4j", "MiniSat"};
        String[] pbcEncoder = {"AdderNetwork"};

        String[] puzzles;
        if (args.length == 0) {
            puzzles = new String[]{"9MarksTheSpot.sdq", "chessSudoku.sdq", "FawltyTowers.sdq", "frozenPicnic.sdq",
                    "mark1.sdq", /*"nurikabeSudoku.sdq", */ "sudokuManOfMystery.sdq", "theMiracleThermo.sdq", "theOriginalSandwich.sdq",
                    "thePyramid.sdq", "thermo2020.sdq", "thermoCouples.sdq", "thermoSquares.sdq", "theRoadToGenius.sdq"};
        } else {
            puzzles = args;
        }

        boolean notFound = false;
        for (String puzzle : puzzles) {
            if (!ExperimentUtil.exists(puzzle)) {
                System.out.println(puzzle + " not found!");
                notFound = true;
            }
        }
        if (notFound) {
            return;
        }

        for (String sN : solvers) {
            for (String enc : pbcEncoder) {
                for (int opt : killerOptimize) {
                    for (String puzzle : puzzles) {
                        ExperimentUtil.delete(puzzle, sN, enc, opt);
                    }
                }
            }
        }

        int totalRounds = solvers.length * pbcEncoder.length * killerOptimize.length * roundsConf * puzzles.length;
        int j = 0;

        for (int opt : killerOptimize) {
            for (String puzzle : puzzles) {
                for (int i = 1; i <= roundsConf; i++) {
                    for (String enc : pbcEncoder) {
                        long[] encDat = ExperimentUtil.encode(puzzle, enc, opt);
                        for (String sN : solvers) {
                            j++;
                            System.out.println(j + "/" + totalRounds + " " + i + " " + sN + " " + enc + " " + opt + "   " + puzzle);
                            long solT = ExperimentUtil.solve(sN, enc);
                            ExperimentUtil.log(puzzle, encDat[0], solT, encDat[1], sN, enc, opt);
                        }
                    }
                }
            }
        }
    }


}
