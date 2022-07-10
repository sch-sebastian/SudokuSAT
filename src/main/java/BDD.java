package main.java;

import java.util.ArrayList;
import java.util.Collections;

public class BDD extends PBCConverter {

    private static class BDDNode {
        int depth;
        int sub_tree_num;
        int sign;
        int sum;
        BDDNode child_true;
        BDDNode child_false;

        public BDDNode(int sub_tree_num, int sum, int depth) {
            this.sub_tree_num = sub_tree_num;
            this.sum = sum;
            this.depth = depth;
            this.sign = 0;
        }

        public BDDNode(int sub_tree_num, int sum, int depth, int sign) {
            this.sub_tree_num = sub_tree_num;
            this.sum = sum;
            this.depth = depth;
            this.sign = sign;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BDDNode bddNode = (BDDNode) o;
            return depth == bddNode.depth && sum == bddNode.sum;
        }
    }

    private static class VariableWeightPair implements Comparable<VariableWeightPair>{
        int variable;
        int weight;


        public VariableWeightPair(int variable, int weight) {
            this.variable = variable;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VariableWeightPair vwp = (VariableWeightPair) o;
            return variable == vwp.variable && weight == vwp.weight;
        }

        @Override
        public int compareTo(VariableWeightPair o) {
           if(weight > o.weight){
               return 1;
           }else if(weight<o.weight){
               return -1;
           }else {
               return 0;
           }
        }
    }

    PBC sort(PBC pbc){
        ArrayList<VariableWeightPair> vwps = new ArrayList<>();
        for(int i = 0; i < pbc.vars.length;i++){
            vwps.add(new VariableWeightPair(pbc.vars[i], pbc.weights[i]));
        }
        Collections.sort(vwps);
        for(int i = 0; i < pbc.vars.length;i++){
            pbc.vars[i] = vwps.get(vwps.size()-1-i).variable;
            pbc.weights[i] = vwps.get(vwps.size()-1-i).weight;
        }
        return pbc;
    }


    public ClauseSet createClauses(PBC pbc, int pbcVar) {

        pbc = sort(pbc);
        ClauseSet clauses = new ClauseSet();

        //Trivial case if 0 variables
        if (pbc.vars.length == 0) {
            if (pbc.rhs != 0) {
                clauses.add(new Clause());
            }
            if (pbcVar != 0) {
                clauses.addVarToAll(-pbcVar);
            }
            return clauses;
        }


        int[] rest = new int[pbc.weights.length];
        //calculate rest
        int sum = 0;
        for (int i = 0; i < pbc.weights.length - 1; i++) {
            sum = sum + pbc.weights[i];
            rest[i + 1] = sum;
        }

        //Construct BDD
        UniqueLinkedList<BDDNode> queue = new UniqueLinkedList<>();
        BDDNode root = new BDDNode(Environment.getVC(), 0, 0);
        Environment.incVC();

        queue.add(root);
        while (queue.size != 0) {
            BDDNode cur = queue.poll();
            BDDNode cf;
            BDDNode ct;

            if (cur.depth == pbc.vars.length - 1) {
                //Last Layer
                if (cur.sum + pbc.weights[pbc.weights.length - cur.depth - 1] != pbc.rhs) {
                    ct = new BDDNode(Environment.getVC(), cur.sum + pbc.weights[pbc.weights.length - cur.depth - 1], cur.depth + 1, -1);
                    Environment.incVC();
                } else {
                    ct = new BDDNode(Environment.getVC(), cur.sum + pbc.weights[pbc.weights.length - cur.depth - 1], cur.depth + 1, 1);
                    Environment.incVC();
                }
                if (cur.sum != pbc.rhs) {
                    cf = new BDDNode(Environment.getVC(), cur.sum, cur.depth + 1, -1);
                    Environment.incVC();
                } else {
                    cf = new BDDNode(Environment.getVC(), cur.sum, cur.depth + 1, 1);
                    Environment.incVC();
                }
            } else {
                //Internal Layer
                if (cur.sum >= pbc.rhs) {
                    //if current var true too high
                    ct = new BDDNode(Environment.getVC(), cur.sum + pbc.weights[pbc.weights.length - cur.depth - 1], cur.depth + 1, -1);
                    Environment.incVC();
                } else {
                    ct = new BDDNode(Environment.getVC(), cur.sum + pbc.weights[pbc.weights.length - cur.depth - 1], cur.depth + 1);
                    if (!queue.contains(ct)) {
                        queue.add(ct);
                        Environment.incVC();
                    } else {
                        ct = queue.get(ct);
                    }
                }
                if (cur.sum + rest[rest.length - (cur.depth + 1)] < pbc.rhs) {
                    //if current var false too low
                    cf = new BDDNode(Environment.getVC(), cur.sum, cur.depth + 1, -1);
                    Environment.incVC();
                } else {
                    cf = new BDDNode(Environment.getVC(), cur.sum, cur.depth + 1);
                    if (!queue.contains(cf)) {
                        queue.add(cf);
                        Environment.incVC();
                    } else {
                        cf = queue.get(cf);
                    }
                }
            }
            cur.child_true = ct;
            cur.child_false = cf;
        }

        clauses.add(new Clause(root.sub_tree_num));
        queue = new UniqueLinkedList<>();
        queue.add(root);
        while (queue.size != 0) {
            BDDNode cur = queue.poll();
            //Positive occurrence clauses.
            clauses.add(new Clause(-cur.sub_tree_num, -pbc.vars[pbc.vars.length - cur.depth - 1], cur.child_true.sub_tree_num));
            clauses.add(new Clause(-cur.sub_tree_num, pbc.vars[pbc.vars.length - cur.depth - 1], cur.child_false.sub_tree_num));
            //Negative occurrence clauses (maybe not necessary but increase speed 10x)
            clauses.add(new Clause(cur.sub_tree_num, -pbc.vars[pbc.vars.length - cur.depth - 1], -cur.child_true.sub_tree_num));
            clauses.add(new Clause(cur.sub_tree_num, pbc.vars[pbc.vars.length - cur.depth - 1], -cur.child_false.sub_tree_num));
            //Red-clauses, are redundant but increase strength of unit propagation.
            clauses.add(new Clause(-cur.child_true.sub_tree_num, -cur.child_false.sub_tree_num, cur.sub_tree_num));
            clauses.add(new Clause(cur.child_true.sub_tree_num, cur.child_false.sub_tree_num, -cur.sub_tree_num));
            if (cur.child_true.sign == 0) {
                queue.add(cur.child_true);
            } else {
                clauses.add(new Clause(cur.child_true.sub_tree_num * cur.child_true.sign));
            }
            if (cur.child_false.sign == 0) {
                queue.add(cur.child_false);
            } else {
                clauses.add(new Clause(cur.child_false.sub_tree_num * cur.child_false.sign));
            }
        }
        if (pbcVar != 0) {
            clauses.addVarToAll(-pbcVar);
        }
        return clauses;
    }
}


