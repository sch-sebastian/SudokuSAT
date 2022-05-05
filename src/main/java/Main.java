package main.java;

import java.io.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        if(args.length!=1){
            System.out.println("Invalid parameter(s): please provide a filename!");
            System.exit(42);
        }
        System.out.println("Task: " + args[0]);
        System.out.println("Starting Encoding...");
        long encodingStart = System.currentTimeMillis();
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(Sudoku.oneNumberPerEntry());

        HashMap<String, Constraint> constraints = Reader.read(args[0]);
        for(Constraint constraint : constraints.values()){
            clauses.addAll(constraint.createClauses());
        }

        try {
            Environment.writeDIMACS(clauses);
            System.out.println("Duration: " + (System.currentTimeMillis() - encodingStart) + "ms");
            System.out.println("Starting Solver...");
            long start = System.currentTimeMillis();
            Solver.run();
            System.out.println("Duration: " + (System.currentTimeMillis() - start) + "ms");
            Environment.printSolution();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
