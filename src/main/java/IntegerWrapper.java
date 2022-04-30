package main.java;

public class IntegerWrapper implements Comparable<Integer> {
    public Integer val;
    public IntegerWrapper(Integer val) {
        this.val = val;
    }

    public void inc(){
        val++;
    }

    @Override
    public int compareTo(Integer o) {
        return val.compareTo(o);
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
