package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AdderNetwork extends PBCConverter {


    public ClauseSet createClauses(PBC pbc, int pbcVar) {
        ClauseSet clauses = new ClauseSet();

        //Trivial case if 0 variables
        if(pbc.vars.length == 0){
            if(pbc.rhs!=0){
                clauses.add(new Clause());
            }
            if (pbcVar != 0) {
                clauses.addVarToAll(-pbcVar);
            }
            return clauses;
        }



        //Initialize Buckets--------------------------------------------------------------------------------------------
        ArrayList<Integer>[] binNum = new ArrayList[pbc.vars.length];
        HashMap<Integer, LinkedList<Integer>> buckets = new HashMap<>();
        for (int i = 0; i < pbc.weights.length; i++) {
            binNum[i] = toBinary(pbc.weights[i]);
            for (int digit = 0; digit < binNum[i].size(); digit++) {
                if (binNum[i].get(digit) == 1) {
                    if (!buckets.containsKey(digit)) {
                        buckets.put(digit, new LinkedList<>());
                    }
                    buckets.get(digit).add(pbc.vars[i]);
                }
            }
        }

        //Clauses for LHS-----------------------------------------------------------------------------------------------
        PriorityQueue<Integer> sortedK = new PriorityQueue<>(buckets.keySet());
        ArrayList<Integer> output = new ArrayList<>();
        int donePos = -1;
        while (!sortedK.isEmpty()) {
            int i = sortedK.poll();

            //add False to output for empty buckets
            while (donePos < i - 1) {
                output.add(1000);
                donePos++;
            }

            while (buckets.get(i).size() >= 3) {
                int x = buckets.get(i).poll();
                int y = buckets.get(i).poll();
                int z = buckets.get(i).poll();
                AddResult curRes = fullAdd(x, y, z);
                clauses.addAll(curRes.clauses);
                buckets.get(i).add(curRes.sumVar);
                if (!buckets.containsKey(i + 1)) {
                    buckets.put(i + 1, new LinkedList<>());
                    sortedK.add(i + 1);
                }
                buckets.get(i + 1).add(curRes.carryVar);
            }

            if (buckets.get(i).size() == 2) {
                int x = buckets.get(i).poll();
                int y = buckets.get(i).poll();
                AddResult curRes = halfAdd(x, y);
                clauses.addAll(curRes.clauses);
                buckets.get(i).add(curRes.sumVar);
                if (!buckets.containsKey(i + 1)) {
                    buckets.put(i + 1, new LinkedList<>());
                    sortedK.add(i + 1);
                }
                buckets.get(i + 1).add(curRes.carryVar);
            }

            output.add(buckets.get(i).poll());
            donePos = i;
        }

        //Clauses to compare LHS to RHS---------------------------------------------------------------------------------
        //Note: this is not as stated in (Een 2006)
        ArrayList<Integer> binRHS = toBinary(pbc.rhs);
        while (output.size() < binRHS.size()) {
            output.add(1000);
        }
        while (binRHS.size() < output.size()) {
            binRHS.add(0);
        }
        for (int i = 0; i < binRHS.size(); i++) {
            if (binRHS.get(i) == 1) {
                clauses.add(new Clause(output.get(i)));
            } else {
                clauses.add(new Clause(-output.get(i)));
            }
        }

        if (pbcVar != 0) {
            clauses.addVarToAll(-pbcVar);
        }

        clauses.add(new Clause(-Environment.FALSE));
        clauses.add(new Clause(Environment.TRUE));

        return clauses;
    }

    /**
     * Returns the clauses needed to describe a full adder working on the 3 given variables.
     */
    private static AddResult fullAdd(int x, int y, int z) {
        ClauseSet clauses = new ClauseSet();
        int sumVar = Environment.getVC();
        Environment.incVC();
        int carryVar = Environment.getVC();
        Environment.incVC();

        //Sum Clauses
        clauses.add(new Clause(x, y, z, -sumVar));
        clauses.add(new Clause(x, -y, -z, -sumVar));
        clauses.add(new Clause(-x, y, -z, -sumVar));
        clauses.add(new Clause(-x, -y, z, -sumVar));
        clauses.add(new Clause(-x, -y, -z, sumVar));
        clauses.add(new Clause(-x, y, z, sumVar));
        clauses.add(new Clause(x, -y, z, sumVar));
        clauses.add(new Clause(x, y, -z, sumVar));

        //Carry Clauses
        clauses.add(new Clause(-x, -y, carryVar));
        clauses.add(new Clause(-x, -z, carryVar));
        clauses.add(new Clause(-y, -z, carryVar));
        clauses.add(new Clause(x, y, -carryVar));
        clauses.add(new Clause(x, z, -carryVar));
        clauses.add(new Clause(y, z, -carryVar));

        return new AddResult(sumVar, carryVar, clauses);

    }

    /**
     * Returns the clauses needed to describe a half adder working on the 2 given variables.
     */
    public static AddResult halfAdd(int x, int y) {
        ClauseSet clauses = new ClauseSet();
        int sumVar = Environment.getVC();
        Environment.incVC();
        int carryVar = Environment.getVC();
        Environment.incVC();

        //Sum Clauses
        clauses.add(new Clause(x, y, -sumVar));
        clauses.add(new Clause(x, -y, sumVar));
        clauses.add(new Clause(-x, y, sumVar));
        clauses.add(new Clause(-x, -y, -sumVar));

        //Carry Clauses
        clauses.add(new Clause(-x, -y, carryVar));
        clauses.add(new Clause(x, y, -carryVar));
        clauses.add(new Clause(-x, y, -carryVar));
        clauses.add(new Clause(x, -y, -carryVar));

        return new AddResult(sumVar, carryVar, clauses);

    }

    private static class AddResult {
        int sumVar;
        int carryVar;
        ClauseSet clauses;

        AddResult(int sumVar, int carryVar, ClauseSet clauses) {
            this.sumVar = sumVar;
            this.carryVar = carryVar;
            this.clauses = clauses;
        }
    }

    /**
     * Translates the given number to Binary and returns it as an ArrrayList,
     * with the least significant bit at the lowest index.
     */
    public static ArrayList<Integer> toBinary(int n) {
        ArrayList<Integer> binNum = new ArrayList<>();
        if (n == 0) {
            return binNum;
        }

        int digits = 0;
        while (Math.pow(2, digits) <= n) {
            digits++;
        }
        while (digits >= 1) {
            int divisor = (int) Math.pow(2, digits - 1);
            int quotient = n / divisor;
            binNum.add(0, quotient);
            n = n % divisor;
            digits--;
        }
        return binNum;
    }
}
