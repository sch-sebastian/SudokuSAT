package main.java;

import java.util.*;

public class Clause implements Comparable<Clause> {
    HashSet<Integer> literals;

    public Clause() {
        this.literals = new HashSet<>();
    }

    public Clause(int... literals) {
        this.literals = new HashSet<>();
        for (int i : literals) {
            this.literals.add(i);
        }
    }

    public int size() {
        return literals.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return Objects.equals(literals, clause.literals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals);
    }

    @Override
    public String toString() {
        ArrayList<Integer> sortedLiterals = new ArrayList<>(literals);
        Collections.sort(sortedLiterals);
        String res = "";
        for (Integer i : sortedLiterals) {
            res = res + i + " ";
        }
        if (res.length() > 0) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }


    @Override
    public int compareTo(Clause o) {
        if (literals.size() < o.literals.size()) {
            return -1;
        } else if (literals.size() > o.literals.size()) {
            return 1;
        } else {
            ArrayList<Integer> sortedLiterals = new ArrayList<>(literals);
            Collections.sort(sortedLiterals);
            ArrayList<Integer> otherSortedLiterals = new ArrayList<>(o.literals);
            Collections.sort(otherSortedLiterals);
            for (int i = 0; i < sortedLiterals.size(); i++) {
                if (sortedLiterals.get(i) < otherSortedLiterals.get(i)) {
                    return -1;
                } else if (sortedLiterals.get(i) > otherSortedLiterals.get(i)) {
                    return 1;
                }
            }
            return 0;
        }
    }
}
