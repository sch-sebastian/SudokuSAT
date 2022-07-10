package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

public class ExperimentKiller {

    public static void main(String[] args) {
        int roundsConf = 100;
        int[] killerOptimize = {0, 1, 2};
        String[] solvers = {"Sat4j", "MiniSat"};
        String[] pbcEncoder = {"BDD", "AdderNetwork"};

        String[] puzzles = new String[]{/*"TheTimesUK191.sdq", "TheTimesUK192.sdq", */"TheTimesUK193.sdq" /*,"TheTimesUK194.sdq",
                    "TheTimesUK195.sdq", "TheTimesUK196.sdq", "TheTimesUK197.sdq", "TheTimesUK198.sdq", "TheTimesUK199.sdq", "TheTimesUK200.sdq"*/};


        boolean notFound = false;
        for (String puzzle : puzzles) {
            if (!exists(puzzle)) {
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
                        delete(puzzle, sN, enc, opt);
                    }
                }
            }
        }

        int totalRounds = solvers.length * pbcEncoder.length * killerOptimize.length * roundsConf;
        int j = 0;
        for (int i = 0; i < roundsConf; i++) {
            for (String sN : solvers) {
                for (String enc : pbcEncoder) {
                    for (int opt : killerOptimize) {
                        System.out.print((j + 1) + "/" + totalRounds + " " + (i+1) + " " + sN + " "+ enc + " " + opt + "   ");
                        j++;
                        for (String puzzle : puzzles) {
                            log(puzzle, run(puzzle, sN, enc, opt), sN, enc, opt);
                            System.out.print(puzzle + " ");
                        }
                        System.out.println();
                    }
                }
            }
        }
    }

    public static boolean exists(String puzzle) {
        String basePath = System.getProperty("user.dir");
        if (!ExperimentKiller.class.getResource("ExperimentKiller.class").toString().toLowerCase().contains(".jar")) {
            basePath = Paths.get(basePath, "src", "main", "data").toString();
        }
        File file = new File(Paths.get(basePath, puzzle).toString());
        return file.exists();
    }

    public static void log(String puzzle, long[] data, String solName, String encoding, int optimized) {
        FileWriter fw = null;
        try {
            File folder = new File("log");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(Paths.get("log", solName + "_" + encoding + "_" + optimized + "_" + puzzle.substring(0, puzzle.length() - 4) + ".csv").toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, true);

            BufferedWriter bw = new BufferedWriter(fw);
            String line = "";
            for (int i = 0; i < data.length; i++) {
                line = line + data[i] + ";";
            }
            if (line.length() > 0) {
                line = line.substring(0, line.length() - 1);
            }
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String puzzle, String solName, String encoding, int optimized) {
        File folder = new File("log");
        if (!folder.exists()) {
            return;
        }
        File file = new File(Paths.get("log", solName + "_" + encoding + "_" + optimized + "_" + puzzle.substring(0, puzzle.length() - 4) + ".csv").toString());
        if (file.exists()) {
            file.delete();
        }
    }

    public static long[] run(String filename, String solName, String encoding, int killerOptimize) {
        long encodingStart = 0;
        long encodingTime = 0;
        long solverStart = 0;
        long solverTime = 0;

        Environment.init();
        Environment.setConverter(encoding);

        encodingStart = currentTimeMillis();
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(Sudoku.oneNumberPerEntry());
        try {
            HashMap<String, Constraint> constraints = Reader.read(filename);
            for (String name : constraints.keySet()) {
                if (name.equals("Killer")) {
                    ((Killer) constraints.get(name)).optimized = killerOptimize;
                }
                ClauseSet curClauses = constraints.get(name).createClauses();
                clauses.addAll(curClauses);
            }
            Environment.writeDIMACS(clauses, "input.tmp");
            encodingTime = currentTimeMillis() - encodingStart;
            solverStart = currentTimeMillis();
            boolean solvable = Solver.run(solName);
            if (!solvable) {
                System.out.println("Unsolvable puzzle encountered!");
                System.exit(-1);
            }
            solverTime = currentTimeMillis() - solverStart;
        } catch (IOException e) {
            e.printStackTrace();
        }

        long[] res = {encodingTime, solverTime, clauses.size()};

        return res;
    }
}
