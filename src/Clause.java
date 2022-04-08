import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public class Clause implements Comparable, Iterable<Literal> {
    HashSet<Literal> literals;

    public Clause(HashSet<Literal> literals) {
        this.literals = literals;
    }

    public Clause(Literal l) {
        this.literals = new HashSet<>();
        this.literals.add(l);
    }

    public Clause() {
        this.literals = new HashSet<>();
    }

    public int size(){
        return literals.size();
    }

    public Iterator<Literal> iterator(){
        return literals.iterator();
    }

    public boolean contains(Literal li){
        return literals.contains(li);
    }

    public boolean remove(Object o){
        return literals.remove(o);
    }

    @Override
    public int compareTo(Object o) {
        Clause other = (Clause) o;
        int ts = literals.size();
        int os = other.literals.size();
        if (ts == os) {
            return 0;
        } else if (ts < os) {
            return -1;
        } else {
            return 1;
        }
    }
}
