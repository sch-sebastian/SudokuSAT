import java.util.Collections;
import java.util.HashSet;

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
}
