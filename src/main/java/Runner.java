package main.java;

import java.io.*;
import java.nio.file.Paths;

public class Runner {

    static String br = "\n";

    public static void main(String[] args) {
        System.out.println("Starting Encoding...");
        long encodingStart = System.currentTimeMillis();

        String fieldNum = "03";
        String groupNum = "03";
        ClauseSet clauses = new ClauseSet();

        clauses.addAll(Sudoku.createClauses(Paths.get("src","main", "data", "sudoku" + fieldNum).toString()));
        clauses.addAll(Killer.createClauses(Paths.get("src","main", "data", "group" + groupNum).toString()));

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
