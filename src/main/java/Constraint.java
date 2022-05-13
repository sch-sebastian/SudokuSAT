package main.java;

public abstract class Constraint {
    protected String data;

    public Constraint(String... data) {
        if (data.length > 0) {
            this.data = data[0].trim();
        }

    }

    abstract ClauseSet createClauses();

    abstract int check(int[][] model);


}
