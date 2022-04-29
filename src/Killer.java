import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    //------------------------------------------------------------------------------

    public static ClauseSet createClauses(String groupFile) {
        return createClauses(parseGroups(groupFile));
    }


    private static ClauseSet createClauses(ArrayList<Integer>[] groups) {
        ClauseSet res = new ClauseSet();
        for (ArrayList<Integer> group : groups) {
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
            BDD bdd = new BDD(vars, weights, rhs);
            //res.addAll(bdd.getClauses());
            res.addAll(bdd.getLessClauses());
        }
        return res;
    }


    private static ArrayList<Integer>[] parseGroups(String groupFile) {
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

    //for testing
    public static void main(String[] args) {
        Killer k = new Killer("group03");
        System.out.println("done");
    }


}
