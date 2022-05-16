package main.java;

import java.io.*;
import java.util.*;

import static main.java.PowerSet.getSumCombi;

public class Environment {

    public static PBCConverter converter = new AdderNetwork();

    public static int[][] solution;

    private static int varCounter = 1002;
    public static int FALSE = 1000;
    public static int TRUE = 1001;
    public static Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static HashMap<Integer, ArrayList<ArrayList<Integer>>>[] sumCombinations = getSumCombi(numbers);

    public static void init() {
        converter = new AdderNetwork();
        solution = null;
        varCounter = 1002;
    }

    public static void incVC() {
        varCounter++;
    }

    public static int getVC() {
        return varCounter;
    }

    public static void writeDIMACS(ClauseSet clauseSet, boolean... sort) throws IOException {
        File file = new File("input.tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("p cnf " + clauseSet.getMax() + " " + clauseSet.size() + "\n");
        if (sort.length > 0 && !sort[0]) {
            for (Clause cl : clauseSet.clauses) {
                bw.write(cl.toString() + " 0\n");
            }
        } else {
            for (Clause cl : clauseSet.getSortedClauses()) {
                bw.write(cl.toString() + " 0\n");
            }
        }

        bw.flush();
        bw.close();
    }

    public static void printSolution(int[][] grid) throws FileNotFoundException {
        for (int y = 0; y < grid.length; y++) {
            String line = "";
            for (int x = 0; x < grid[0].length; x++) {
                line = line + grid[x][y] + " ";
            }
            System.out.println(line);
        }
    }


    public static ClauseSet toClauses(PBC pbc, int pbcVar) {
        return converter.createClauses(pbc, pbcVar);
    }

    public static void setConverter(String type) {
        type = type.trim();
        switch (type) {
            case "BDD":
                converter = new BDD();
                break;
            case "AdderNetwork":
                converter = new AdderNetwork();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void setSolution(String solStr) {
        solution = new int[9][9];
        Scanner scanner = new Scanner(solStr);
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                int cur = scanner.nextInt();
                if (0 < cur && cur < 10) {
                    solution[x - 1][y - 1] = cur;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    public static int[][] modelToArray() throws FileNotFoundException {
        int[][] grid = new int[9][9];

        Scanner scanner = new Scanner(new File("model.tmp"));
        while (scanner.hasNext() && !scanner.hasNextInt()){
            scanner.next();
        }
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
        return grid;
    }

    public static boolean check(int[][] model, HashMap<String, Constraint> constraints) {
        boolean isCorrect = true;
        for (String constraintName : constraints.keySet()) {
            Constraint curCons = constraints.get(constraintName);
            int checkResult = curCons.check(model);
            if (checkResult == -1) {
                System.out.println(constraintName + ": failed");
                isCorrect = false;
            } else if (checkResult == 0) {
                System.out.println(constraintName + ": check not implemented");
            } else {
                System.out.println(constraintName + ": successful");
            }
        }
        if (solution != null) {
            boolean equal = true;
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    if (model[x][y] != solution[x][y]) {
                        equal = false;
                    }
                }
            }
            if (!equal) {
                System.out.println("Solution: failed");
                isCorrect = false;
            } else {
                System.out.println("Solution: successful");
            }

        }
        return isCorrect;
    }


    /**
     * @param groups The first element of each group is the value that the cells should sum up to, then the groups cell
     *               coordinates follow (alternating x and y).
     * @return An array of PBCs created from the given groups.
     */
    public static PBC[] toPBCArray(ArrayList<Integer>[] groups) {
        PBC[] pbcs = new PBC[groups.length];
        for (int i = 0; i < groups.length; i++) {
            pbcs[i] = toPBC(groups[i]);
        }
        return pbcs;
    }

    /**
     * @param group The first element of group is the value that the cells should sum up to, then the groups cell
     *              coordinates follow (alternating x and y).
     * @return An array of PBCs created from the given groups.
     */
    public static PBC toPBC(ArrayList<Integer> group) {
        int rhs = group.get(0);
        int n = ((group.size() - 1) / 2);
        int[] vars = new int[n * 9];
        int[] weights = new int[vars.length];
        for (int v = 0, i = 1; v < vars.length && i < group.size() - 1; v++, i = i + 2) {
            for (int z = 1; z <= 9; z++) {
                vars[n * (z - 1) + v] = 100 * group.get(i) + 10 * group.get(i + 1) + z;
                weights[n * (z - 1) + v] = z;
            }
        }
        return new PBC(vars, weights, rhs);
    }

    public static ArrayList<Integer> getAllowedValues(int sum, int l){
        ArrayList<ArrayList<Integer>> combiList = sumCombinations[l].get(sum);
        HashSet<Integer> allowedValues = new HashSet<>();

        for (ArrayList<Integer> combi : combiList) {
            for (Integer i : combi) {
                allowedValues.add(i);
            }
        }
        return new ArrayList<>(allowedValues);
    }
}
