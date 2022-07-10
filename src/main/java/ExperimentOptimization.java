package main.java;


public class ExperimentOptimization {

    public static void main(String[] args) {
        int roundsConf = 60;
        int[] killerOptimize = {0,1,2};
        String[] solvers = {"Sat4j"};
        String[] pbcEncoder = {"AdderNetwork"};

        String[] puzzles = new String[]{"TheTimesUK198.sdq", "TheTimesUK199.sdq", "TheTimesUK200.sdq"};


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
