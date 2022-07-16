package main.java;


public class ExperimentKiller {

    public static void main(String[] args) {
        /* Define experiment configuration...
            -Solver
            -Killer Optimization
            -PBC encoding Method
            -Puzzle instances
        */
        int roundsConf = 60;
        int[] killerOptimize = {0};
        String[] solvers = {"Sat4j"};
        String[] pbcEncoder = {/*"BDD",*/ "AdderNetwork"};

        String[] puzzles = new String[]{"TheTimesK016.sdq", "TheTimesK017.sdq", "TheTimesK018.sdq", "TheTimesK019.sdq",
                "TheTimesK020.sdq", "TheTimesK021.sdq", "TheTimesK022.sdq", "TheTimesK023.sdq", "TheTimesK024.sdq",
                "TheTimesK025.sdq","TheTimesUK191.sdq", "TheTimesUK192.sdq", "TheTimesUK193.sdq" ,"TheTimesUK194.sdq",
                "TheTimesUK195.sdq", "TheTimesUK196.sdq", "TheTimesUK197.sdq", "TheTimesUK198.sdq", "TheTimesUK199.sdq", "TheTimesUK200.sdq"};


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
                            //System.out.println(j + "/" + totalRounds + " " + i + " " + sN + " " + enc + " " + opt + "   " + puzzle);
                            long solT = ExperimentUtil.solve(sN, enc);
                            ExperimentUtil.log(puzzle, encDat[0], solT, encDat[1], sN, enc, opt);
                        }
                    }
                }
                System.out.println(j + "/" + totalRounds);
            }
        }
    }
}
