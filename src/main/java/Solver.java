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

    public static boolean run(String... args) {
        String solName = "";
        if (args.length > 0 && args[0] != null) {
            solName = args[0];
        }
        switch (solName) {
            case "Sat4j":
                return runSat4j();
            case "MiniSat":
                return runMiniSat();
            case "":
                System.out.println("Solver Warning: running default solver (Sat4j)!");
                return runSat4j();
            default:
                System.out.println("Solver Warning: unknown solver " + solName + ", running default (Sat4j)!");
                return runSat4j();
        }
    }

    public static boolean runSat4j() {
        boolean satisfiable = false;
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        PrintWriter out = null;
        try {
            out = new PrintWriter("model.tmp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // CNF filename is given on the command line
        try {
            IProblem problem = reader.parseInstance("input.tmp");
            if (problem.isSatisfiable()) {
                satisfiable = true;
                System.out.println("Satisfiable !");
                reader.decode(problem.model(), out);
                out.flush();
                out.close();
            } else {
                System.out.println("Unsatisfiable !");
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

    public static boolean runMiniSat() {
        String command = "";
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            if(!checkWSL()){
                System.out.println("Solver Error: running MiniSat on Windows requires WSL!");
                throw new RuntimeException();
            }
            String workPath = System.getProperty("user.dir").replace("\\", "/").replace(":", "");
            command = "wsl cd /mnt/" + workPath + "; ls; ./MiniSat_v1.14_linux input.tmp model.tmp";
        } else {
            String workPath = System.getProperty("user.dir");
            command =  workPath + "/MiniSat_v1.14_linux input.tmp model.tmp";
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
            if (output.get(output.size() - 1).contains("SATISFIABLE")) {
                System.out.println("Satisfiable !");
                return true;
            } else {
                System.out.println("Unsatisfiable !");
                return false;
            }//TODO: Add output for trivial or timeout!

        } catch (IOException e) {
            if(e.getMessage().toLowerCase().contains("permission denied"))
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

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

    public static void allow(String workPath){
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