import java.io.*;

public class Runner {

    static String br = "\n";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String probNum = "03";
        ClauseSet clauses = new ClauseSet();

        clauses.addAll(Sudoku.createClauses("sudoku" + probNum));
        clauses.addAll(Killer.createClauses("group" + "02"));

        try {
            Environment.writeCNF(clauses);
            Solver.run();
            Environment.printSolution();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Duration: " + (System.currentTimeMillis() - start));
    }
}
