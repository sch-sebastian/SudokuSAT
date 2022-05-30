package main.java;

import java.util.*;

import static main.java.Environment.*;


public class Killer extends Constraint {

    int optimized;
    /* 0 = Naive
     * 2 = Prohibit cell values that can not be used for a sum.
     * 3 = Do not use PBC, but Support clauses with knowledge from the Powerset.
     * */

    public Killer(String data) {
        super(data);
        this.optimized = 0;
    }

    public Killer(int optimized, String data) {
        super(data);
        this.optimized = optimized;
    }

    @Override
    public ClauseSet createClauses() {
        return createClauses(parseGroups());
    }

    @Override
    int check(int[][] model) {
        return 0;
    }


    public ClauseSet createClauses(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        if (optimized == 3) {
            clauses.addAll(createLessClauses(groups));
        } else if (optimized == 2) {
            for (ArrayList<Integer> group : groups) {
                ArrayList<Integer> allowedVAlues = getAllowedValues(group.get(0), (group.size() - 1) / 2);
                PBC pbc = toPBC(group, allowedVAlues);
                clauses.addAll(Environment.toClauses(pbc, 0));
            }
        } else {
            PBC[] pbcs = toPBCArray(groups);
            for (PBC pbc : pbcs) {
                clauses.addAll(Environment.toClauses(pbc, 0));
            }
        }
        return clauses;
    }

    /**
     * Reads and parses the given group data and returns the groups as an Array of Integer ArrayLists. The first element
     * of each ArrayList is the value that the cells of the corresponding group should sum up to, then the group's cell
     * coordinates follow (alternating x and y).
     */
    public ArrayList<Integer>[] parseGroups() {
        try {
            Scanner scanner = new Scanner(data);
            int numGroups = scanner.nextInt();
            ArrayList<Integer>[] groups = new ArrayList[numGroups];
            for (int i = 0; i < numGroups; i++) {
                groups[i] = new ArrayList<>();
                groups[i].add(scanner.nextInt());
            }
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    int curGroup = scanner.nextInt();
                    if (curGroup != 0) {
                        groups[curGroup - 1].add(x);
                        groups[curGroup - 1].add(y);
                    }
                }
            }
            scanner.close();
            return groups;
        } catch (InputMismatchException e) {
            System.out.println("parseGroups Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("parseGroups Error: End of File to early.");
        } catch (IllegalStateException e) {
            System.out.println("parseGroups Error: Scanner closed to early.");
        }
        return null;
    }

    /**
     * Creates clauses for Killer Sudoku without the use of PBC.
     */
    public static ClauseSet createLessClauses(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(oncePerGroupClauses(groups));
        clauses.addAll(atLeastOneFittingNumberPerGroupEntry(groups));
        return clauses;
    }

    /**
     * Creates clauses to ensure each value can only be present at most once per group.
     *
     * @param groups The first element of each group is the value that the cells should sum up to, then the group's cell
     *               coordinates follow (alternating x and y).
     */
    private static ClauseSet oncePerGroupClauses(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        for (ArrayList<Integer> group : groups) {
            for (int i = 1; i < group.size() - 3; i = i + 2) {
                int xi = group.get(i);
                int yi = group.get(i + 1);
                for (int j = i + 2; j < group.size() - 1; j = j + 2) {
                    int xj = group.get(j);
                    int yj = group.get(j + 1);
                    for (int z = 1; z <= 9; z++) {
                        clauses.add(new Clause(-(100 * xi + 10 * yi + z), -(100 * xj + 10 * yj + z)));
                        //clauses.add("-" + xi + yi + z + " -" + xj + yj + z + " 0" + br);
                    }
                }

            }
        }
        return clauses;
    }

    /**
     * Creates clauses to ensure that for all groups in each group cell there is a fitting number.
     * This is done using knowledge from the powerset of {1,..,9} to check which values are even possible within a group
     * with given size and sum and in what combinations they are allowed.
     * <p>
     * Support clauses are created with the idea that either a certain number combination (that has the required sum) is
     * not present or each group cell must have value from it.
     * <p>
     * At-most clauses are created, because only one number combination can be present per group.
     * <p>
     * One At-least clauses is created, because one number combination per group must be present.
     *
     * @param groups The first element of each group is the value that the cells should sum up to, then the group's cell
     *               coordinates follow (alternating x and y).
     */
    private static ClauseSet atLeastOneFittingNumberPerGroupEntry(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        for (ArrayList<Integer> group : groups) {
            int numCells = (group.size() - 1) / 2;
            int sum = group.get(0);
            ArrayList<ArrayList<Integer>> combis = sumCombinations[numCells].get(sum);
            ArrayList<Integer> comLiterals = new ArrayList<>();
            for (ArrayList<Integer> combi : combis) {
                int comNum = Environment.getVC();
                comLiterals.add(comNum);
                Environment.incVC();
                for (int g = 1; g < group.size() - 1; g = g + 2) {
                    int x = group.get(g);
                    int y = group.get(g + 1);
                    ArrayList<Integer> curLiterals = new ArrayList<>();
                    curLiterals.add(-comNum);
                    for (Integer z : combi) {
                        curLiterals.add(100 * x + 10 * y + z);
                    }
                    //Clause:   either it is not the current sum-combination or the
                    //          cell has a number of the sum-combination
                    clauses.add(new Clause(curLiterals));
                }
            }
            //Clauses: at most one combination is true
            for (int a = 0; a < comLiterals.size() - 1; a++) {
                for (int b = a + 1; b < comLiterals.size(); b++) {
                    clauses.add(new Clause(-comLiterals.get(a), -comLiterals.get(b)));
                }
            }
            //Clause: at least one combination is true
            clauses.add(new Clause(comLiterals));
        }
        return clauses;
    }


    /**
     * @param group            The first element of group is the value that the cells should sum up to, then the group's
     *                         cell coordinates follow (alternating x and y).
     * @param allowedValuesSet set of values that are allowed in the group cells.
     * @return a PBC created from the given group only containing the allowed values.
     */
    private static PBC toPBC(ArrayList<Integer> group, ArrayList<Integer>... allowedValuesSet) {
        ArrayList<Integer> allowedValues;
        if (allowedValuesSet.length < 1) {
            allowedValues = new ArrayList<>();
            allowedValues.addAll(List.of(Environment.numbers));
        } else {
            allowedValues = allowedValuesSet[0];
        }
        Collections.sort(allowedValues);
        int nCells = (group.size() - 1) / 2;
        int[] vars = new int[nCells * allowedValues.size()];
        int[] weights = new int[vars.length];
        for (int i = 0; i < allowedValues.size(); i++) {
            for (int j = 0; j < nCells; j++) {
                weights[i * nCells + j] = allowedValues.get(i);
                vars[i * nCells + j] = 100 * group.get(j * 2 + 1) + 10 * group.get(j * 2 + 2) + allowedValues.get(i);
            }
        }
        return new PBC(vars, weights, group.get(0));
    }
}
