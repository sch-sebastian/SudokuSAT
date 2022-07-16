package main.java;

import org.sat4j.maxsat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.ArrayList;

public class Solver {

    /**
     * Runs the specified SAT-Solver or the default one.
     *
     * @param args name of the SAT-Solver to use.
     */
    public static boolean run(String... args) {
        String solName = "";
        if (args.length > 0 && args[0] != null) {
            solName = args[0];
        }
        switch (solName) {
            case "Sat4j":
                return runSat4j("input.tmp", "model.tmp");
            case "MiniSat":
                return runMiniSat("input.tmp", "model.tmp");
            case "":
                System.out.println("Solver Warning: running default solver (Sat4j)!");
                return runSat4j("input.tmp", "model.tmp");
            default:
                System.out.println("Solver Warning: unknown solver " + solName + ", running default (Sat4j)!");
                return runSat4j("input.tmp", "model.tmp");
        }
    }

    /**
     * Runs the Sat4j-Default-Solver.
     * Beforehand the problem must have been written into a DIMACS file callend "input.tmp".
     * Afterwards a model for the problem (if one exists) can be found in "model.tmp".
     *
     * @return if the SAT-Problem was satisfiable.
     */
    public static boolean runSat4j(String inputFile, String outputFile) {
        boolean satisfiable = false;
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(7200); // 2 hour timeout
        Reader reader = new DimacsReader(solver);
        PrintWriter out = null;
        try {
            out = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // CNF filename is given on the command line
        try {
            IProblem problem = reader.parseInstance(inputFile);
            if (problem.isSatisfiable()) {
                satisfiable = true;
                reader.decode(problem.model(), out);
                out.flush();
                out.close();
            }
        } catch (ParseFormatException | IOException e) {
            // TODO Auto-generated catch block
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }
        return satisfiable;
    }


    /**
     * Runs the MiniSat-Solver from a binary file (Either using WSL or natively on Linux).
     * Beforehand the problem must have been written into a DIMACS file called "input.tmp".
     * Afterwards a model for the problem (if one exists) can be found in "model.tmp".
     *
     * @return if the SAT-Problem was satisfiable.
     */
    public static boolean runMiniSat(String inputFile, String outputFile) {
        String command = "";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            if (!checkWSL()) {
                System.out.println("Solver Error: running MiniSat on Windows requires WSL!");
                throw new RuntimeException();
            }
            String workPath = System.getProperty("user.dir").replace("\\", "/").replace(":", "");
            command = "wsl cd /mnt/" + workPath + "; ./MiniSat_v1.14_linux " + inputFile + " " + outputFile;
        } else {
            String workPath = System.getProperty("user.dir");
            command = workPath + "/MiniSat_v1.14_linux " + inputFile + " " + outputFile;
            allow(workPath);
        }
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ArrayList<String> output = new ArrayList<>();
            String outputLine = br.readLine();
            while (outputLine != null) {
                output.add(outputLine);
                outputLine = br.readLine();
            }
            if (output.get(output.size() - 1).contains("UNSATISFIABLE")) {
                return false;
            } else {
                return true;
            }//TODO: Option for timeout

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    /**
     * Checks if WSL is installed by running a simple WSL command.
     * (Should only be called on Windows-Systems)
     */
    public static boolean checkWSL() {
        String command = "wsl ls";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    /**
     * Sets the Permission for the MiniSat binary to be executable.
     * (Should only be called on Unix-Systems)
     */
    public static void allow(String workPath) {
        String command = "chmod +x " + workPath + "/MiniSat_v1.14_linux";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }
}