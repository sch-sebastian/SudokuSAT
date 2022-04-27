import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class Clause {
    HashSet<Integer> literals;

    public Clause() {
        this.literals  = new HashSet<>();
    }

    public Clause(int... literals) {
        this.literals  = new HashSet<>();
        for(int i: literals){
            this.literals.add(i);
        }
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
        String res = "";
        for(Integer i:literals){
            res = res + i + " ";
        }
        if(res.length()>0){
            res = res.substring(0,res.length()-1);
        }
        return res;
    }
}
