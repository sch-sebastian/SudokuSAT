package main.java;

import java.io.*;
import java.util.HashMap;

import static main.java.Environment.check;
import static main.java.Environment.modelToArray;

public class Main {


    public static void main(String[] args) {
        int exitCode = run(args);
        System.exit(exitCode);
    }

    public static int run(String[] args) {
        Environment.init();
        int exitCode = 0;
        if (args.length < 1) {
            System.out.println("Invalid parameter(s): please provide a filename!");
            System.exit(40);
        }
        String solName = "";
        if (args.length > 1) {
            solName = args[1];
        }
        System.out.println("Task: " + args[0]);
        System.out.println("------------------------------------------");
        System.out.println("Starting Encoding...");
        long encodingStart = System.currentTimeMillis();
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(Sudoku.oneNumberPerEntry());


        try {
            HashMap<String, Constraint> constraints = Reader.read(args[0]);
            if (constraints.containsKey("Difficulty")) {
                return runDifficulties(constraints, solName);
            }
            for (String name : constraints.keySet()) {
                ClauseSet curClauses = constraints.get(name).createClauses();
                System.out.println(name + ": " + curClauses.size() + " clauses and " + curClauses.vars.size() + " variables.");
                clauses.addAll(curClauses);
            }
            System.out.println("Total: " + clauses.size() + " clauses and " + clauses.vars.size() + " variables.");

            Environment.writeDIMACS(clauses);
            System.out.println("Duration: " + (System.currentTimeMillis() - encodingStart) + "ms");
            System.out.println("------------------------------------------");
            System.out.println("Starting Solver " + solName + "...");
            long start = System.currentTimeMillis();
            boolean satisfiable = Solver.run(solName);
            System.out.println("Duration: " + (System.currentTimeMillis() - start) + "ms");
            if (satisfiable) {
                int[][] model = modelToArray();
                System.out.println("------------------------------------------");
                System.out.println("Starting Checks...");
                if (check(model, constraints)) {
                    System.out.println("------------------------------------------");
                    System.out.println("Correct Model:");
                    Environment.printSolution(model);
                } else {
                    System.out.println("------------------------------------------");
                    System.out.println("Faulty Model:");
                    Environment.printSolution(model);
                    exitCode = 20;
                }
            } else {
                exitCode = 10;
            }
            System.out.println("------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
            exitCode = 30;
        }
        return exitCode;
        /**
         * Exit codes:
         * 0:   success
         * 10:  unsatisfiable
         * 20:  test failed
         * 30:  IOException
         * 40:  missing parameter
         * */
    }

    public static int runDifficulties(HashMap<String, Constraint> constraints, String solName) {
        int exitCode = 0;
        System.out.println("Difficulty Constraint discovered!");
        System.out.println("Starting multiple runs...!");
        Difficulty diff = (Difficulty) constraints.get("Difficulty");
        constraints.remove("Difficulty");
        System.out.println("------------------------------------------");
        ClauseSet baseClauses = new ClauseSet();
        baseClauses.addAll(Sudoku.oneNumberPerEntry());
        for (String name : constraints.keySet()) {
            ClauseSet curClauses = constraints.get(name).createClauses();
            System.out.println(name + ": " + curClauses.size() + " clauses and " + curClauses.vars.size() + " variables.");
            baseClauses.addAll(curClauses);
        }
        System.out.println("Difficulty: " + 1 + " clause and " + 1 + " variable.");
        try {
            for (int i = diff.easy; i <= diff.hard; i++) {
                ClauseSet clauses = new ClauseSet();
                clauses.addAll(baseClauses);
                clauses.add(new Clause(100 * diff.x + 10 * diff.y + i));
                System.out.println("Total: " + clauses.size() + " clauses and " + clauses.vars.size() + " variables.");
                Environment.writeDIMACS(clauses);
                System.out.println("------------------------------------------");
                System.out.println("Starting Solver " + solName + "...");
                long start = System.currentTimeMillis();
                boolean satisfiable = Solver.run(solName);
                System.out.println("Duration: " + (System.currentTimeMillis() - start) + "ms");
                if (satisfiable) {
                    int[][] model = modelToArray();
                    System.out.println("------------------------------------------");
                    System.out.println("Starting Checks...");
                    if (check(model, constraints)) {
                        System.out.println("------------------------------------------");
                        System.out.println("Correct Model:");
                        Environment.printSolution(model);
                    } else {
                        System.out.println("------------------------------------------");
                        System.out.println("Faulty Model:");
                        Environment.printSolution(model);
                        exitCode = 20;
                    }
                } else {
                    exitCode = 10;
                }
                System.out.println("------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
            exitCode = 30;
        }
        return exitCode;
    }
}
