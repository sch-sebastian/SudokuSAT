package main.java;

import java.util.ArrayList;

import static main.java.ClauseUtil.notEqual;

public class AntiKnight extends Constraint {


    public AntiKnight(){
        super();
    }

    /**
     * Returns a ClauseSet, off all clauses needed to specify the Anti-Knight-Move rule.
     * "No digit can be a knight’s move away from an identical digit" CTCGH: xiii
     *
     * @param cell1 coordinates of first cell
     * @param cell2 coordinates of second cell
     * @return the set of clauses
     */
    public ClauseSet createClauses() {
        ArrayList<Tuple<Integer>> offsets = new ArrayList<>();
        offsets.add(new Tuple<>(-1, -2));
        offsets.add(new Tuple<>(1, -2));
        offsets.add(new Tuple<>(-2, -1));
        offsets.add(new Tuple<>(2, -1));
        offsets.add(new Tuple<>(-2, 1));
        offsets.add(new Tuple<>(2, 1));
        offsets.add(new Tuple<>(-1, 2));
        offsets.add(new Tuple<>(1, 2));
        return notEqual(offsets);
    }

    @Override
    int check(int[][] model) {
        return 0;
    }
}
