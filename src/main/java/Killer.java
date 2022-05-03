package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Killer {

    //---Non-static-part-in-case-we-need-instances-later----------------------------
    private ArrayList<Integer>[] groups;
    private ClauseSet clauses;

    public Killer(String groupFile) {
        clauses = new ClauseSet();
        groups = parseGroups(groupFile);
        clauses = createClauses(groups);
    }

    public ClauseSet getClauses() {
        return clauses;
    }

    // Implementation of PB-Encoding encoding---------------------------------------------------------------------------

    public static ClauseSet createClauses(String groupFile) {
        return createClauses(parseGroups(groupFile));
    }


    private static ClauseSet createClauses(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        PBConstraint[] pbcs = PBConstraint.toPBCArray(groups);
        for(PBConstraint pbc : pbcs){
            BDD bdd = new BDD(pbc);
            clauses.addAll(bdd.getClauses());
        }
        return clauses;
    }


    public static ArrayList<Integer>[] parseGroups(String groupFile) {
        try {
            Scanner scanner = new Scanner(new File(groupFile));
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InputMismatchException e) {
            System.out.println("Scanner Error in getGroups: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("Scanner Error in getGroups: End of File to early.");
        } catch (IllegalStateException e) {
            System.out.println("Scanner Error in getGroups: Scanner closed to early.");
        }
        return null;
    }

    // Implementation of Intuitive encoding-----------------------------------------------------------------------------
    private static HashMap<Integer, ArrayList<ArrayList<Integer>>>[] sumCombinations;

    private static void initSumCombinations() {
        if (sumCombinations == null) {
            Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
            sumCombinations = getSumCombi(numbers);
        }
    }

    public static ClauseSet createLessClauses(String groupFile) {
        return createLessClauses(parseGroups(groupFile));
    }

    public static ClauseSet createLessClauses(ArrayList<Integer>[] groups) {
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(oncePerGroupClauses(groups));
        clauses.addAll(atLeastOneFittingNumberPerGroupEntry(groups));
        return clauses;
    }


    private static HashMap<Integer, ArrayList<ArrayList<Integer>>>[] getSumCombi(Integer[] numbers) {
        PowerSet<Integer> pS = new PowerSet<>(numbers);

        HashMap<Integer, ArrayList<ArrayList<Integer>>>[] sumCombi = new HashMap[numbers.length + 1];
        for (int i = 0; i < sumCombi.length; i++) {
            sumCombi[i] = new HashMap<>();
        }
        for (ArrayList<Integer> set : pS.sets) {
            Integer sum = 0;
            for (Integer element : set) {
                sum = sum + element;
            }
            if (!sumCombi[set.size()].containsKey(sum)) {
                sumCombi[set.size()].put(sum, new ArrayList<>());
            }
            sumCombi[set.size()].get(sum).add(set);
        }
        return sumCombi;
    }

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

    private static ClauseSet atLeastOneFittingNumberPerGroupEntry(ArrayList<Integer>[] groups) {
        initSumCombinations();
        ClauseSet clauses = new ClauseSet();
        for (ArrayList<Integer> group : groups) {
            int numCells = (group.size() - 1) / 2;
            int sum = group.get(0);
            ArrayList<ArrayList<Integer>> combis = sumCombinations[numCells].get(sum);
            int yStart = Environment.getVC();
            for (ArrayList<Integer> combi : combis) {
                int comNum = Environment.getVC();
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
            ArrayList<Integer> comLiterals = new ArrayList<>();
            for (int a = yStart; a < Environment.getVC() - 1; a++) {
                comLiterals.add(a);
                for (int b = a + 1; b < Environment.getVC(); b++) {
                    clauses.add(new Clause(-a, -b));
                }
            }
            if (yStart < Environment.getVC()) {
                comLiterals.add(Environment.getVC() - 1);
            }
            //Clause: at least one combination is true
            clauses.add(new Clause(comLiterals));
        }
        return clauses;
    }

    //for testing
    public static void main(String[] args) {
        Killer k = new Killer("group03");
        System.out.println("done");
    }


}
