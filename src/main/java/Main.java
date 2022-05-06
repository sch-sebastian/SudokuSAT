package main.java;

import java.io.*;
import java.util.HashMap;

import static main.java.Environment.check;
import static main.java.Environment.modelToArray;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid parameter(s): please provide a filename!");
            System.exit(42);
        }
        System.out.println("Task: " + args[0]);
        System.out.println("------------------------------------------");
        System.out.println("Starting Encoding...");
        long encodingStart = System.currentTimeMillis();
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(Sudoku.oneNumberPerEntry());


        try {
            HashMap<String, Constraint> constraints = Reader.read(args[0]);
            for(String name : constraints.keySet()){
                ClauseSet curClauses = constraints.get(name).createClauses();
                System.out.println(name + ": " + curClauses.size() + " clauses and " + curClauses.vars.size() + " variables.");
                clauses.addAll(curClauses);
            }
            System.out.println("Total: " + clauses.size() + " clauses and " + clauses.vars.size() + " variables.");

            Environment.writeDIMACS(clauses);
            System.out.println("Duration: " + (System.currentTimeMillis() - encodingStart) + "ms");
            System.out.println("------------------------------------------");
            System.out.println("Starting Solver...");
            long start = System.currentTimeMillis();
            boolean satisfiable = Solver.run();
            System.out.println("Duration: " + (System.currentTimeMillis() - start) + "ms");
            if (satisfiable) {
                int[][] model = modelToArray();
                System.out.println("------------------------------------------");
                System.out.println("Starting Tests...");
                if (check(model, constraints)) {
                    System.out.println("------------------------------------------");
                    System.out.println("Correct Model:");
                    Environment.printSolution(model);
                } else {
                    System.out.println("------------------------------------------");
                    System.out.println("Faulty Model:");
                    Environment.printSolution(model);
                }
                System.out.println("------------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
