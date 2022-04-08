import java.util.Objects;

public class Literal {
    String name;
    boolean sign;

    public Literal(String name, boolean positiv){
        this.name = name;
        this.sign = positiv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Literal literal = (Literal) o;
        return sign == literal.sign && Objects.equals(name, literal.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sign);
    }
}
