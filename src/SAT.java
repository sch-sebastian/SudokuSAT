import java.io.File;
import java.io.FileNotFoundException;
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
        for (Clause curCL : this.clauses) {
            for (Literal curLi : curCL) {
                if (curLi.sign) {
                    if (pos.containsKey(curLi)) {
                        pos.put(curLi, pos.get(curLi) + 1);
                    } else {
                        pos.put(curLi, 1);
                    }
                } else {
                    if (neg.containsKey(curLi)) {
                        neg.put(curLi, neg.get(curLi) + 1);
                    } else {
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
        while (clauses.size() != 0 && clauses.peek().size() == 1) {
            unitPropagation();
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

        ArrayList<Literal> cand = new ArrayList<>();
        Iterator<Literal> iterLi = allLiterals.iterator();
        cand.add(iterLi.next());
        Literal l2 = iterLi.next();
        while (iterLi.hasNext() && l2.name.equals(cand.get(0).name)) {
            l2 = iterLi.next();
        }
        if (!l2.name.equals(cand.get(0).name)) {
            cand.add(l2);
        }
        if (cand.size() == 1) {
            PriorityQueue<Clause> c1 = new PriorityQueue<>(clauses);
            c1.add(new Clause(cand.get(0)));
            SAT sat1 = new SAT(c1);
            SolveSolution s1 = sat1.solve();
            if (s1.succ) {
                s1.assignment.addAll(solution);
                return s1;
            }
            PriorityQueue<Clause> c2 = new PriorityQueue<>(clauses);
            c2.add(new Clause(new Literal(cand.get(0).name, !cand.get(0).sign)));
            SAT sat2 = new SAT(c2);
            SolveSolution s2 = sat2.solve();
            if (s2.succ) {
                s2.assignment.addAll(solution);
                return s2;
            }
            return new SolveSolution(false);
        } else {
            PriorityQueue<Clause> c1 = new PriorityQueue<>(clauses);
            c1.add(new Clause(cand.get(0), cand.get(1)));
            SAT sat1 = new SAT(c1);
            SolveSolution s1 = sat1.solve();
            if (s1.succ) {
                s1.assignment.addAll(solution);
                return s1;
            }
            PriorityQueue<Clause> c2 = new PriorityQueue<>(clauses);
            c2.add(new Clause(new Literal(cand.get(0).name, !cand.get(0).sign), new Literal(cand.get(1).name, !cand.get(1).sign)));
            SAT sat2 = new SAT(c2);
            SolveSolution s2 = sat2.solve();
            if (s2.succ) {
                s2.assignment.addAll(solution);
                return s2;
            }
            return new SolveSolution(false);
        }
    }

    public void setPures() {
        HashSet<Literal> purePos = new HashSet<>(pos.keySet());
        HashSet<Literal> pureNeg = new HashSet<>(neg.keySet());


        for (Literal negLi : neg.keySet()) {
            purePos.remove(new Literal(negLi.name, true));
        }

        for (Literal posLi : pos.keySet()) {
            pureNeg.remove(new Literal(posLi.name, false));
        }
        HashSet<Clause> toDelete = new HashSet<>();
        for (Literal literal : purePos) {
            solution.add(literal);
            for (Clause clause : clauses) {
                if (clause.contains(literal)) {
                    toDelete.add(clause);
                }
            }
        }
        for (Literal literal : pureNeg) {
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
        if (clauses.peek().size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void unitPropagation() {
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
        if (li.sign) {
            if (pos.get(li) - 1 > 0) {
                pos.put(li, pos.get(li) - 1);
            } else {
                pos.remove(li);
                Literal invLi = new Literal(li.name, !li.sign);
                if (!neg.containsKey(invLi) && !solution.contains(li) && !solution.contains(invLi)) {
                    solution.add(li);
                }
            }
        } else {
            if (neg.get(li) - 1 > 0) {
                neg.put(li, neg.get(li) - 1);
            } else {
                neg.remove(li);
                Literal invLi = new Literal(li.name, !li.sign);
                if (!pos.containsKey(invLi) && !solution.contains(li) && !solution.contains(new Literal(li.name, !li.sign))) {
                    solution.add(li);
                }
            }
        }
    }


    public static PriorityQueue<Clause> parse(String filename) throws FileNotFoundException {
        PriorityQueue<Clause> clauses = new PriorityQueue<>();
        Scanner scanner = new Scanner(new File(filename));
        HashSet<Literal> curLiterals = new HashSet<>();
        while (scanner.hasNext()) {
            if (!scanner.hasNextInt()) {
                String curStr = scanner.next();
                if (curStr.equals("c") || curStr.equals("p")) {
                    scanner.nextLine();
                }
            } else {
                int num = scanner.nextInt();
                if (num == 0) {
                    clauses.add(new Clause((HashSet<Literal>) curLiterals.clone()));
                    curLiterals.clear();
                } else {
                    curLiterals.add(new Literal(String.valueOf(Math.abs(num)), num > 0));
                }
            }
        }
        return clauses;
    }


    public static void main(String[] args) {

        try {
            PriorityQueue<Clause> clauses = parse("testInput");
            SAT sat1 = new SAT(clauses);
            SolveSolution solution = sat1.solve();
            System.out.println(solution.succ);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
