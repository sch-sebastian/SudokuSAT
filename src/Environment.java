import java.io.*;
import java.util.Scanner;

public class Environment {
    static int varCounter = 1000;

    public static void writeCNF(ClauseSet clauseSet) throws IOException {
        File file = new File("input.tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("p cnf " + clauseSet.getMax() + " " + clauseSet.size() + "\n"); //TODO: Implement highest var-name correct
        for (Clause cl : clauseSet.clauses) {
            bw.write(cl.toString() + " 0\n");
        }
        bw.flush();
        bw.close();
    }

    public static void printSolution() throws FileNotFoundException {
        int[][] grid = new int[9][9];

        Scanner scanner = new Scanner(new File("model.tmp"));
        while (scanner.hasNextInt()) {
            int cur = scanner.nextInt();
            if (cur > 0 && cur < 1000) {
                String str = Integer.toString(cur);
                int x = Integer.parseInt(str.substring(0, 1));
                int y = Integer.parseInt(str.substring(1, 2));
                int z = Integer.parseInt(str.substring(2));
                grid[x - 1][y - 1] = z;
            }
        }

        for (int y = 0; y < grid.length; y++) {
            String line = "";
            for (int x = 0; x < grid[0].length; x++) {
                line = line + grid[x][y] + " ";
            }
            System.out.println(line);
        }
    }


}
