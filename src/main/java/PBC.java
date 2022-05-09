package main.java;

import java.util.ArrayList;

public class PBC {
    int[] vars;
    int[] weights;
    int rhs;

    public PBC(int[] vars, int[] weights, int rhs) {
        this.vars = vars;
        this.weights = weights;
        this.rhs = rhs;
    }

    /**
     * @param group The first element is the value that the cells should sum up to, then the groups cell
     *              coordinates follow (alternating x and y).
     */
    public PBC(ArrayList<Integer> group) {
        this.rhs = group.get(0);
        int n = ((group.size() - 1) / 2);
        this.vars = new int[n * 9];
        this.weights = new int[vars.length];
        for (int v = 0, i = 1; v < vars.length && i < group.size() - 1; v++, i = i + 2) {
            for (int z = 1; z <= 9; z++) {
                vars[n * (z - 1) + v] = 100 * group.get(i) + 10 * group.get(i + 1) + z;
                weights[n * (z - 1) + v] = z;
            }
        }
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
}
