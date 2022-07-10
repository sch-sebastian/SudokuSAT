package main.java;

import java.util.*;

import static main.java.Environment.sumCombinations;

public class SandwichSum extends Constraint {

    int optimized;
    /* 0 = Naive
     * 1 = Check if sum achievable with number of cells.
     * 2 = Prohibit cell values that can not be used for a sum.
     * */

    public SandwichSum(String... data) {
        super(data);
        this.optimized = 0;
    }

    public SandwichSum(int optimized, String... data) {
        super(data);
        this.optimized = optimized;
    }


    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    ClauseSet createClauses(HashMap<Integer, Integer>[] sums) {
        ClauseSet clauses = new ClauseSet();

        //Row constraints-----------------------------------------------------------------------------------------------
        for (int y = 1; y <= 9; y++) {
            if (!sums[1].containsKey(y)) {
                continue;
            }
            int sum = sums[1].get(y);
            ArrayList<Integer> sumVariables = new ArrayList<>();
            for (int l = 0; l <= 7; l++) {
                if (optimized > 0 && !sumCombinations[l].containsKey(sum)) {
                    //Sum impossible with cur length l
                    for (int x = 1; x <= 9 - l - 1; x++) {
                        //No 9-1 Positions with current length
                        clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * (x + l + 1) + 10 * y + 9)));
                        clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * (x + l + 1) + 10 * y + 1)));
                    }
                } else {
                    ArrayList<Integer> allowedValues = null;

                    if (optimized > 1) {
                        //Create list of allowed values;
                        ArrayList<ArrayList<Integer>> combiList = sumCombinations[l].get(sum);
                        allowedValues = new ArrayList<>();

                        for (ArrayList<Integer> combi : combiList) {
                            for (Integer i : combi) {
                                if (!allowedValues.contains(i)) {
                                    allowedValues.add(i);
                                }
                            }
                        }
                    }

                    for (int x = 1; x <= 9 - l - 1; x++) {
                        int sumVar = Environment.getVC();
                        Environment.incVC();
                        sumVariables.add(sumVar);
                        //Not this position or not this sum.
                        clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * (x + l + 1) + 10 * y + 9), sumVar));
                        clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * (x + l + 1) + 10 * y + 1), sumVar));
                        PBC pbc;
                        if (allowedValues != null) {
                            //optimized > 1
                            pbc = toRowPBC(x, x + l + 1, y, sum, allowedValues);
                            for (int z = 1; z <= 9; z++) {
                                if (!allowedValues.contains(z)) {
                                    //Not forbidden values or not this sum
                                    for (int xS = x + 1; xS <= x + l; xS++) {
                                        clauses.add(new Clause(-(100 * xS + 10 * y + z), -sumVar));
                                    }
                                }
                            }
                        } else {
                            //optimized = 0 or 1
                            pbc = toRowPBC(x, x + l + 1, y, sum);
                        }
                        clauses.addAll(Environment.toClauses(pbc, sumVar));
                    }
                }
            }
            //At least one sum is true
            clauses.add(new Clause(sumVariables));
            //At most one sum is true
            for (int i = 0; i < sumVariables.size() - 1; i++) {
                for (int j = i + 1; j < sumVariables.size(); j++) {
                    clauses.add(new Clause(-sumVariables.get(i), -sumVariables.get(j)));
                }
            }
        }

        //Column constraints--------------------------------------------------------------------------------------------
        for (int x = 1; x <= 9; x++) {
            if (!sums[0].containsKey(x)) {
                continue;
            }
            int sum = sums[0].get(x);
            ArrayList<Integer> sumVariables = new ArrayList<>();
            for (int l = 0; l <= 7; l++) {
                if (optimized > 0 && !sumCombinations[l].containsKey(sum)) {
                    //Sum impossible with cur length l
                    for (int y = 1; y <= 9 - l - 1; y++) {
                        //No 9-1 Positions with current length
                        clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * x + 10 * (y + l + 1) + 9)));
                        clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * x + 10 * (y + l + 1) + 1)));
                    }
                } else {
                    ArrayList<Integer> allowedValues = null;

                    if (optimized > 1) {
                        ArrayList<ArrayList<Integer>> combiList = sumCombinations[l].get(sum);
                        allowedValues = new ArrayList<>();

                        for (ArrayList<Integer> combi : combiList) {
                            for (Integer i : combi) {
                                if (!allowedValues.contains(i)) {
                                    allowedValues.add(i);
                                }
                            }
                        }
                    }
                    for (int y = 1; y <= 9 - l - 1; y++) {
                        int sumVar = Environment.getVC();
                        Environment.incVC();
                        sumVariables.add(sumVar);
                        //Not this position or not this sum.
                        clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * x + 10 * (y + l + 1) + 9), sumVar));
                        clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * x + 10 * (y + l + 1) + 1), sumVar));
                        PBC pbc;
                        if (allowedValues != null) {
                            //optimized > 1
                            pbc = toColumnPBC(y, y + l + 1, x, sum, allowedValues);
                            for (int z = 1; z <= 9; z++) {
                                if (!allowedValues.contains(z)) {
                                    //Not forbidden values or not this sum
                                    for (int yS = y + 1; yS <= y + l; yS++) {
                                        clauses.add(new Clause(-(100 * x + 10 * yS + z), -sumVar));
                                    }
                                }
                            }
                        } else {
                            //optimized = 1
                            pbc = toColumnPBC(y, y + l + 1, x, sum);
                        }
                        clauses.addAll(Environment.toClauses(pbc, sumVar));
                    }
                }
            }
            //At least one sum is true
            clauses.add(new Clause(sumVariables));
            //At most one sum is true
            for (int i = 0; i < sumVariables.size() - 1; i++) {
                for (int j = i + 1; j < sumVariables.size(); j++) {
                    clauses.add(new Clause(-sumVariables.get(i), -sumVariables.get(j)));
                }
            }
        }
        return clauses;
    }

    private HashMap<Integer, Integer>[] parseData() {
        HashMap<Integer, Integer>[] sums = new HashMap[2];
        try {
            Scanner scanner = new Scanner(data);

            int totalCol = scanner.nextInt();
            sums[0] = new HashMap<>();
            for (int i = 0; i < totalCol; i++) {
                int col = scanner.nextInt();
                int sum = scanner.nextInt();
                sums[0].put(col, sum);
            }

            sums[1] = new HashMap<>();
            if (!scanner.hasNextInt()) {
                return sums;
            }
            int totalRow = scanner.nextInt();
            for (int i = 0; i < totalRow; i++) {
                int row = scanner.nextInt();
                int sum = scanner.nextInt();
                sums[1].put(row, sum);
            }

        } catch (InputMismatchException e) {
            System.out.println("SandwichSum.parseData Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("SandwichSum.parseData Error: End of data to early.");
        } catch (IllegalStateException e) {
            System.out.println("SandwichSum.parseData Error: Scanner closed to early.");
        }
        return sums;
    }


    private PBC toRowPBC(int x1, int x2, int y, int sum, ArrayList<Integer>... allowedValuesArray) {
        ArrayList<Integer> allowedValues;
        if (allowedValuesArray.length < 1) {
            allowedValues = new ArrayList<>();
            allowedValues.addAll(List.of(Environment.numbers));
        } else {
            allowedValues = allowedValuesArray[0];
        }
        Collections.sort(allowedValues);
        int nCells = (x2 - x1 - 1);
        int[] vars = new int[nCells * allowedValues.size()];
        int[] weights = new int[vars.length];
        for (int i = 0; i < allowedValues.size(); i++) {
            for (int j = 0; j < nCells; j++) {
                weights[i * nCells + j] = allowedValues.get(i);
                vars[i * nCells + j] = 100 * (x1 + j + 1) + 10 * y + allowedValues.get(i);
            }
        }

        return new PBC(vars, weights, sum);
    }


    private PBC toColumnPBC(int y1, int y2, int x, int sum, ArrayList<Integer>... allowedValuesArray) {
        ArrayList<Integer> allowedValues;
        if (allowedValuesArray.length < 1) {
            allowedValues = new ArrayList<>();
            allowedValues.addAll(List.of(Environment.numbers));
        } else {
            allowedValues = allowedValuesArray[0];
        }
        Collections.sort(allowedValues);
        int nCells = (y2 - y1 - 1);
        int[] vars = new int[nCells * allowedValues.size()];
        int[] weights = new int[vars.length];
        for (int i = 0; i < allowedValues.size(); i++) {
            for (int j = 0; j < nCells; j++) {
                weights[i * nCells + j] = allowedValues.get(i);
                vars[i * nCells + j] = 100 * x + 10 * (y1 + 1 + j) + allowedValues.get(i);
            }
        }

        return new PBC(vars, weights, sum);
    }


    @Override
    int check(int[][] model) {
        return 0;
    }
}
