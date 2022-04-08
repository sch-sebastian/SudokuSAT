import java.util.*;

public class SAT {

    PriorityQueue<Clause> clauses;
    HashMap<Literal, Integer> pos;
    HashMap<Literal, Integer> neg;
    HashSet<Literal> solution;

    static class SolveSolution {
        boolean succ;
        HashSet<Literal> assignment;

        SolveSolution(boolean succ, HashSet<Literal> assignment) {
            this.succ = succ;
            this.assignment = assignment;
        }

        SolveSolution(boolean succ) {
            this.succ = succ;
        }
    }

    public SAT(PriorityQueue<Clause> clauses) {
        this.clauses = clauses;
        this.pos = new HashMap<>();
        this.neg = new HashMap<>();
        this.solution = new HashSet<>();
        Iterator<Clause> iterCl = this.clauses.iterator();
        while (iterCl.hasNext()) {
            Clause curCL = iterCl.next();
            Iterator<Literal> iterLi = curCL.iterator();
            while (iterLi.hasNext()) {
                Literal curLi = iterLi.next();
                if (curLi.sign) {
                    if(pos.containsKey(curLi)){
                        pos.put(curLi, pos.get(curLi) + 1);
                    }else{
                        pos.put(curLi, 1);
                    }
                } else {
                    if(neg.containsKey(curLi)){
                        neg.put(curLi, neg.get(curLi) + 1);
                    }else{
                        neg.put(curLi, 1);
                    }
                }
            }
        }
    }

    public SAT(PriorityQueue<Clause> clauses, HashMap<Literal, Integer> pos,
               HashMap<Literal, Integer> neg,
               HashSet<Literal> solution) {
        this.clauses = clauses;
        this.pos = pos;
        this.neg = neg;
        this.solution = solution;
    }

    public SolveSolution solve() {
        while ( clauses.size() != 0 && clauses.peek().size() == 1) {
            unitPropagation();
            if (abortCheck()) {
                return new SolveSolution(false);
            }
        }
        int oldN = 0;
        while (oldN != clauses.size()) {
            oldN = clauses.size();
            setPures();
        }
        if (clauses.isEmpty()) {
            return new SolveSolution(true, solution);
        }
        if (clauses.peek().size() == 0) {
            return new SolveSolution(false);
        }
        HashSet<Literal> allLiterals = new HashSet<>(pos.keySet());
        allLiterals.addAll(neg.keySet());
        Iterator<Literal> iterLi = allLiterals.iterator();
        Literal literal = iterLi.next();
        PriorityQueue<Clause> c1 = new PriorityQueue<>(clauses);
        c1.add(new Clause(literal));
        SAT sat1 = new SAT(c1, new HashMap<>(pos), new HashMap<>(neg), new HashSet<>(solution));
        SolveSolution s1 = sat1.solve();
        if (s1.succ) {
            return s1;
        }
        PriorityQueue<Clause> c2 = new PriorityQueue<>(clauses);
        c2.add(new Clause(new Literal(literal.name, !literal.sign)));
        SAT sat2 = new SAT(c2, new HashMap<>(pos), new HashMap<>(neg), new HashSet<>(solution));
        return sat2.solve();
    }

    public void setPures() {
        HashMap<Literal, Integer> posCopy = (HashMap<Literal, Integer>) pos.clone();
        HashMap<Literal, Integer> negCopy = (HashMap<Literal, Integer>) neg.clone();

        HashMap<Literal, Integer> purePos = new HashMap<>(posCopy);
        purePos.keySet().removeAll(negCopy.keySet());

        HashMap<Literal, Integer> pureNeg = new HashMap<>(negCopy);
        pureNeg.keySet().removeAll(posCopy.keySet());

        HashSet<Clause> toDelete = new HashSet<>();
        for (Literal literal : purePos.keySet()) {
            solution.add(literal);
            for (Clause clause : clauses) {
                if (clause.contains(literal)) {
                    toDelete.add(clause);
                }
            }
        }
        for (Literal literal : pureNeg.keySet()) {
            solution.add(literal);
            for (Clause clause : clauses) {
                if (clause.contains(literal)) {
                    toDelete.add(clause);
                }
            }
        }
        removeClauses(toDelete);
    }

    public boolean abortCheck() {
        if (clauses.poll().size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public  void unitPropagation() {
        Iterator<Literal> iterLi = clauses.poll().iterator();
        Literal curLi = iterLi.next();
        subtractLiteral(curLi);
        solution.add(curLi);
        HashSet<Clause> toDelete = new HashSet<>();
        for (Clause clause : clauses) {
            if (clause.contains(curLi)) {
                toDelete.add(clause);
            } else if (clause.contains(new Literal(curLi.name, !curLi.sign))) {
                Literal negCurLi = new Literal(curLi.name, !curLi.sign);
                clause.remove(negCurLi);
                subtractLiteral(negCurLi);
            }
        }
        removeClauses(toDelete);
    }

    public void removeClauses(HashSet<Clause> toDelete) {
        for (Clause clause : toDelete) {
            for (Literal literal : clause) {
                subtractLiteral(literal);
            }
            clauses.remove(clause);
        }
    }

    public void subtractLiteral(Literal li) {
        if (pos.containsKey(li)) {
            if (pos.get(li) - 1 > 0) {
                pos.put(li, pos.get(li) - 1);
            } else {
                pos.remove(li);
            }
        } else {
            if (neg.get(li) - 1 > 0) {
                neg.put(li, neg.get(li) - 1);
            } else {
                neg.remove(li);
            }
        }
    }


    public static void main(String[] args) {
        HashSet<Literal> h1 = new HashSet<>();
        h1.add(new Literal("x", true));
        Clause c1 = new Clause(h1);

        HashSet<Literal> h2 = new HashSet<>();
        h2.add(new Literal("x", false));
        h2.add(new Literal("y", true));
        h2.add(new Literal("z", true));
        Clause c2 = new Clause(h2);

        PriorityQueue<Clause> pq1 = new PriorityQueue<>();
        pq1.add(c1);
        pq1.add(c2);

        SAT sat1 = new SAT(pq1);
        SolveSolution solution  = sat1.solve();
        System.out.println(solution.succ);
    }
}
