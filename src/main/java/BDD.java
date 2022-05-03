package main.java;

public class BDD {

    int[] rest;
    int[] vars;
    BDDNode root;

    private class BDDNode {
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

    public BDD(PBConstraint pbc) {
        this.vars = pbc.vars;
        rest = new int[pbc.weights.length];
        //calculate rest
        int sum = 0;
        for (int i = 0; i < pbc.weights.length - 1; i++) {
            sum = sum + pbc.weights[i];
            rest[i + 1] = sum;
        }

        //Construct BDD
        UniqueLinkedList<BDDNode> queue = new UniqueLinkedList<>();
        root = new BDDNode(Environment.getVC(), 0, 0);
        Environment.incVC();

        queue.add(root);
        while (queue.size != 0) {
            BDDNode cur = queue.poll();
            BDDNode cf;
            BDDNode ct;

            if (cur.depth == vars.length - 1) {
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
    }

    public ClauseSet getClauses() {
        ClauseSet res = new ClauseSet();
        res.add(new Clause(root.sub_tree_num));
        UniqueLinkedList<BDDNode> queue = new UniqueLinkedList<>();
        queue.add(root);
        while (queue.size != 0) {
            BDDNode cur = queue.poll();
            //Positive occurrence clauses.
            res.add(new Clause(-cur.sub_tree_num, -vars[vars.length - cur.depth - 1], cur.child_true.sub_tree_num));
            res.add(new Clause(-cur.sub_tree_num, vars[vars.length - cur.depth - 1], cur.child_false.sub_tree_num));
            //Negative occurrence clauses (maybe not necessary but increase speed 10x)
            res.add(new Clause(cur.sub_tree_num, -vars[vars.length - cur.depth - 1], -cur.child_true.sub_tree_num));
            res.add(new Clause(cur.sub_tree_num, vars[vars.length - cur.depth - 1], -cur.child_false.sub_tree_num));
            //Red-clauses, are redundant but increase strength of unit propagation.
            res.add(new Clause(-cur.child_true.sub_tree_num, -cur.child_false.sub_tree_num, cur.sub_tree_num));
            res.add(new Clause(cur.child_true.sub_tree_num, cur.child_false.sub_tree_num, -cur.sub_tree_num));
            if (cur.child_true.sign == 0) {
                queue.add(cur.child_true);
            } else {
                res.add(new Clause(cur.child_true.sub_tree_num * cur.child_true.sign));
            }
            if (cur.child_false.sign == 0) {
                queue.add(cur.child_false);
            } else {
                res.add(new Clause(cur.child_false.sub_tree_num * cur.child_false.sign));
            }
        }
        return res;
    }

    //Although it needs fewer clauses the resulting encoding of this version is less efficient.
    public ClauseSet getLessClauses() {
        ClauseSet res = new ClauseSet();
        res.add(new Clause(root.sub_tree_num));
        UniqueLinkedList<BDDNode> queue = new UniqueLinkedList<>();
        queue.add(root);
        while (queue.size != 0) {
            BDDNode cur = queue.poll();
            if (cur.child_true.sign == 0) {
                res.add(new Clause(-cur.sub_tree_num, -vars[vars.length - cur.depth - 1], cur.child_true.sub_tree_num));
                res.add(new Clause(cur.sub_tree_num, -vars[vars.length - cur.depth - 1], -cur.child_true.sub_tree_num));
                queue.add(cur.child_true);
            } else if(cur.child_true.sign == -1){
                res.add(new Clause(-cur.sub_tree_num, -vars[vars.length - cur.depth - 1]));
            }else{
                res.add(new Clause(cur.sub_tree_num, -vars[vars.length - cur.depth - 1]));
            }

            if (cur.child_false.sign == 0) {
                res.add(new Clause(-cur.sub_tree_num, vars[vars.length - cur.depth - 1], cur.child_false.sub_tree_num));
                res.add(new Clause(cur.sub_tree_num, vars[vars.length - cur.depth - 1], -cur.child_false.sub_tree_num));
                queue.add(cur.child_false);
            } else if(cur.child_false.sign == -1){
                res.add(new Clause(-cur.sub_tree_num, vars[vars.length - cur.depth - 1]));
            }else{
                res.add(new Clause(cur.sub_tree_num, vars[vars.length - cur.depth - 1]));
            }
        }
        return res;
    }

    //for testing
/*    public static void main(String[] args) {
        Environment.varCounter = 1001;
        int[] vars = {1, 2, 3};
        int[] weights = {2, 3, 5};
        int rhs = 5;
        BDD bdd = new BDD(vars, weights, rhs);
        ClauseSet bddClauses = bdd.getClauses();
        try {
            Environment.writeCNF(bddClauses);
            Solver.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}


