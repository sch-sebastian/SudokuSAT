import java.util.Collections;
import java.util.HashSet;

public class CNF {
    HashSet<Clause> clauses;
    HashSet<Integer> vars;

    public CNF() {
        clauses = new HashSet<>();
        vars = new HashSet<>();
    }

    public void add(Clause cl){
        clauses.add(cl);
        vars.addAll(cl.literals);
    }

    public int getMax(){
        return Collections.max(vars);
    }
}
