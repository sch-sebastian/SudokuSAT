import java.io.*;
import java.util.*;

public class Main {

    static String br = "\n";

    public static void main(String[] args) {
        String probNum = "02";
        ArrayList<String> clauses = new ArrayList<>();

        clauses.addAll(oneNumberPerEntry());
        clauses.addAll(oncePerCol());
        clauses.addAll(oncePerRow());
        clauses.addAll(oncePerBox());
        clauses.addAll(parse("sudoku" + probNum));

        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        HashMap<Integer, ArrayList<ArrayList<Integer>>>[] combinations = getSumCombi(numbers);

        ArrayList<Integer>[] groups = getGroups("group" + probNum);
        IntegerWrapper varCount = new IntegerWrapper(999);
        clauses.addAll(oncePerGroup(groups));
        clauses.addAll(atLeastOneFittingNumberPerGroupEntry(groups, combinations, varCount));

        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter("inputTMP.txt"));
            myWriter.write("p cnf " + varCount + " " + clauses.size() + br);
            for (String clause : clauses) {
                myWriter.write(clause);
            }
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Solver.run();

        ArrayList<Integer> sol = new ArrayList<>();
        int[][] grid = new int[9][9];
        try {
            Scanner scanner = new Scanner(new File("modelTMP.txt"));
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static ArrayList<Integer>[] getGroups(String groupFile) {
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

    public static HashMap<Integer, ArrayList<ArrayList<Integer>>>[] getSumCombi(Integer[] numbers) {
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


    public static ArrayList<String> oneNumberPerEntry() {
        ArrayList<String> res = new ArrayList<>();
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                String line = "";
                for (int z = 1; z <= 9; z++) {
                    line = line + x + y + z + " ";
                }
                res.add(line + "0" + br);
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerGroup(ArrayList<Integer>[] groups) {
        ArrayList<String> res = new ArrayList<>();
        for (ArrayList<Integer> group : groups) {
            for (int i = 1; i < group.size() - 3; i = i + 2) {
                int xi = group.get(i);
                int yi = group.get(i + 1);
                for (int j = i + 2; j < group.size() - 1; j = j + 2) {
                    int xj = group.get(j);
                    int yj = group.get(j + 1);
                    for (int z = 1; z <= 9; z++) {
                        res.add("-" + xi + yi + z + " -" + xj + yj + z + " 0" + br);
                    }
                }

            }
        }
        return res;
    }

    public static ArrayList<String> atLeastOneFittingNumberPerGroupEntry(ArrayList<Integer>[] groups, HashMap<Integer, ArrayList<ArrayList<Integer>>>[] combinations, IntegerWrapper varCount) {
        ArrayList<String> res = new ArrayList<>();
        for (ArrayList<Integer> group : groups) {
            int numCells = (group.size() - 1) / 2;
            int sum = group.get(0);
            ArrayList<ArrayList<Integer>> combis = combinations[numCells].get(sum);
            int yStart = varCount.val + 1;
            for (ArrayList<Integer> combi : combis) {
                varCount.inc();
                for (int g = 1; g < group.size() - 1; g = g + 2) {
                    int x = group.get(g);
                    int y = group.get(g + 1);
                    String clause = "-" + varCount;
                    for (Integer z : combi) {
                        clause = clause + " " + x + y + z;
                    }
                    clause = clause + " 0" + br;
                    res.add(clause);
                }
            }
            String posClause = "";
            for (int a = yStart; a < varCount.val; a++) {
                posClause = posClause + " " + a;
                for (int b = a + 1; b <= varCount.val; b++) {
                    res.add("-" + a + " -" + b + " 0" + br);
                }
            }
            posClause = posClause + " " + varCount.val + " 0" + br;
            res.add(posClause);

        }
        return res;
    }

    public static ArrayList<String> oncePerCol() {
        ArrayList<String> res = new ArrayList<>();
        for (int y = 1; y <= 9; y++) {
            for (int z = 1; z <= 9; z++) {
                for (int x = 1; x <= 8; x++) {
                    for (int i = x + 1; i <= 9; i++) {
                        res.add("-" + x + y + z + " -" + i + y + z + " 0" + br);
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerRow() {
        ArrayList<String> res = new ArrayList<>();
        for (int x = 1; x <= 9; x++) {
            for (int z = 1; z <= 9; z++) {
                for (int y = 1; y <= 8; y++) {
                    for (int i = y + 1; i <= 9; i++) {
                        res.add("-" + x + y + z + " -" + x + i + z + " 0" + br);
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerBox() {
        ArrayList<String> res = new ArrayList<>();
        for (int z = 1; z <= 9; z++) {
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    for (int x = 1; x <= 3; x++) {
                        for (int y = 1; y <= 3; y++) {
                            for (int k = y + 1; k <= 3; k++) {
                                res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + x) + (3 * j + k) + z + " 0" + br);
                            }
                            for (int k = x + 1; k <= 3; k++) {
                                for (int l = 1; l <= 3; l++) {
                                    res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + k) + (3 * j + l) + z + " 0" + br);
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> parse(String filename) {
        ArrayList<String> res = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));

            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    int cur = scanner.nextInt();
                    if (0 < cur && cur < 10) {
                        res.add("" + x + y + cur + " 0" + br);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

}
