package main.java;

import java.util.ArrayList;

public class PBConstraint {
    int[] vars;
    int[] weights;
    int rhs;

    public PBConstraint(int[] vars, int[] weights, int rhs) {
        this.vars = vars;
        this.weights = weights;
        this.rhs = rhs;
    }

    /**
     * @param group The first element is the value that the cells should sum up to, then the groups cell
     *              coordinates follow (alternating x and y).
     */
    public PBConstraint(ArrayList<Integer> group) {
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
    public static PBConstraint[] toPBCArray(ArrayList<Integer>[] groups) {
        PBConstraint[] pbcs = new PBConstraint[groups.length];
        for (int i = 0; i < groups.length; i++) {
            pbcs[i] = new PBConstraint(groups[i]);
        }
        return pbcs;
    }
}
