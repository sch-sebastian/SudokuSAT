package main.java;

import java.util.ArrayList;

public class ClauseUtil {
    /**
     * Returns a ClauseSet, off all clauses needed to specify that two cells must differ from each other.
     *
     * @param cell1 coordinates of first cell
     * @param cell2 coordinates of second cell
     * @return the set of clauses
     */
    public static ClauseSet createUnequalClause(int[] cell1, int[] cell2) {
        ClauseSet clauses = new ClauseSet();
        for (int i = 1; i <= 9; i++) {
            clauses.add(new Clause(-(100 * cell1[0] + 10 * cell1[1] + i), -(100 * cell2[0] + 10 * cell2[1] + i)));
        }
        return clauses;
    }

    /**
     * Returns a ClauseSet, off all clauses needed to specify that it holds for the entire field that certain relative
     * cell positions must differ from each other.
     *
     * @param offsets each tuple specifies the relative cell position from which the relative center (0, 0) should
     *                deffer
     * @return the set of clauses
     */
    public static ClauseSet notEqual(ArrayList<Tuple<Integer>> offsets) {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                int[] center = {x, y};
                for (Tuple<Integer> os : offsets) {
                    if (x + os.get(0) <= 9 && x + os.get(0) >= 1 && y + os.get(1) <= 9 && y + os.get(1) >= 1) {
                        int[] neighbour = {x + os.get(0), y + os.get(1)};
                        clauses.addAll(createUnequalClause(center, neighbour));
                    }
                }
            }
        }
        return clauses;
    }
}
