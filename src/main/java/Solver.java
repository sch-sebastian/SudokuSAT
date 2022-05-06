package main.java;

import org.sat4j.maxsat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Solver {

        public static boolean run() {
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
                                reader.decode(problem.model(),out);
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
}