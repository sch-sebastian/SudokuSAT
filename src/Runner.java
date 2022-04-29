import java.io.*;

public class Runner {

    static String br = "\n";

    public static void main(String[] args) {
        System.out.println("Starting Encoding...");
        long encodingStart = System.currentTimeMillis();

        String probNum = "03";
        ClauseSet clauses = new ClauseSet();

        clauses.addAll(Sudoku.createClauses("sudoku" + probNum));
        clauses.addAll(Killer.createClauses("group" + "03"));

        try {
            Environment.writeCNF(clauses);
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
