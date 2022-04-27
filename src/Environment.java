import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class Environment {
    static int varCounter = 0;

    public static void writeCNF(CNF cnf) throws IOException {
        File file = new File("inputTMP.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("p cnf " + 1200 + " " + cnf.clauses.size() + "\n"); //TODO: Implement highest var-name correct
        for (Clause cl : cnf.clauses) {
            bw.write(cl.toString() + " 0\n");
        }
        bw.flush();
        bw.close();
    }


}
