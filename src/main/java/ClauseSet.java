package main.java;

import java.util.*;

public class ClauseSet {
    HashSet<Clause> clauses;
    HashSet<Integer> vars;

    public ClauseSet() {
        clauses = new HashSet<>();
        vars = new HashSet<>();
    }

    public void add(Clause cl) {
        clauses.add(cl);
        for (int i : cl.literals) {
            vars.add(Math.abs(i));
        }
    }

    public void addAll(HashSet<Clause> clauses) {
        for (Clause clause : clauses) {
            add(clause);
        }
    }

    public void addAll(ClauseSet clauseset) {
        for (Clause clause : clauseset.clauses) {
            add(clause);
        }
    }

    public int getMax() {
        return Collections.max(vars);
    }

    public int size() {
        return clauses.size();
    }

    public void addVarToAll(int var){
        vars.add(Math.abs(var));
        for(Clause clause : clauses){
            clause.literals.add(var);
        }
    }

    public ArrayList<Clause> getSortedClauses() {
        ArrayList<Clause> sortedClauses = new ArrayList<>();
        sortedClauses.addAll(clauses);
        Collections.sort(sortedClauses);
        Collections.reverse(sortedClauses);
        return sortedClauses;
    }
}
