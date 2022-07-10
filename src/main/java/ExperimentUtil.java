package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

public class ExperimentUtil {
    public static boolean exists(String puzzle) {
        String basePath = System.getProperty("user.dir");
        if (!ExperimentKiller.class.getResource("ExperimentKiller.class").toString().toLowerCase().contains(".jar")) {
            basePath = Paths.get(basePath, "src", "main", "data").toString();
        }
        File file = new File(Paths.get(basePath, puzzle).toString());
        return file.exists();
    }

    public static void log(String puzzle, long encT, long solT, long clauses, String solName, String encoding, int optimized) {
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
            String line = "" + encT + ";"+ solT + ";" + clauses;
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


    public static long solve(String solName, String encoding) {

        long solverStart = 0;
        long solverTime = 0;

        Environment.init();
        Environment.setConverter(encoding);

        solverStart = currentTimeMillis();
        boolean solvable = Solver.run(solName);
        solverTime = currentTimeMillis() - solverStart;
        if (!solvable) {
            System.out.println("Unsolvable puzzle encountered!");
            System.exit(-1);
        }
        return solverTime;
    }

    public static long[] encode(String filename, String encoding, int killerOptimize) {
        long encodingStart = 0;
        long encodingTime = 0;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        long[] res = {encodingTime, clauses.size()};
        return res;
    }





}
