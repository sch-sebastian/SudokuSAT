package main.java;

import java.util.*;

public class ArrowHead extends Constraint {

    static boolean useSupport = true;

    boolean EQ = false;

    public ArrowHead(String... data) {
        super(data);
        if (this.data.startsWith("EQ")) {
            EQ = true;
        }
    }


    @Override
    ClauseSet createClauses() {
        ClauseSet clauses = new ClauseSet();
        ArrayList<Integer> indices = parseData();
        if (!EQ) {
            for (int i = 0; i <= indices.size() - 4; i = i + 4) {
                clauses.addAll(createAsmallerB(indices.get(i), indices.get(i + 1), indices.get(i + 2), indices.get(i + 3)));
            }
        } else {
            for (int i = 0; i <= indices.size() - 4; i = i + 4) {
                clauses.addAll(createAsmallerEqB(indices.get(i), indices.get(i + 1), indices.get(i + 2), indices.get(i + 3)));
            }
        }

        return clauses;
    }


    private ArrayList<Integer> parseData() {
        ArrayList<Integer> indices = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(data);
            if (scanner.hasNext() && !scanner.hasNextInt()) {
                //Skip EQ
                scanner.next();
            }
            while (scanner.hasNextInt()) {
                int a = scanner.nextInt();
                int b = scanner.nextInt();
                int aX = a / 10;
                int aY = a % 10;
                int bX = b / 10;
                int bY = b % 10;
                if (aX > 9 || aX < 1 || aY > 9 || aY < 1 || bX > 9 || bX < 1 || bY > 9 || bY < 1)
                    throw new IllegalArgumentException();
                indices.add(aX);
                indices.add(aY);
                indices.add(bX);
                indices.add(bY);
            }
            scanner.close();

        } catch (IllegalArgumentException e) {
            System.out.println("ArrowHead.parseData Error: Illegal cell index encountered.");
            throw e;
        } catch (InputMismatchException e) {
            System.out.println("ArrowHead.parseData Error: Next element not an Integer");
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("ArrowHead.parseData Error: End of data to early.");
            throw e;
        } catch (IllegalStateException e) {
            System.out.println("ArrowHead.parseData Error: Scanner closed to early.");
            throw e;
        }
        return indices;
    }

    public static ClauseSet createAsmallerB(int aX, int aY, int bX, int bY) {
        ClauseSet clauses = new ClauseSet();

        if (useSupport) {
            //Support Encoding
            for (int i = 1; i <= 9; i++) {
                ArrayList<Integer> literals = new ArrayList<>();
                literals.add(-(100 * aX + 10 * aY + i));
                for (int j = i + 1; j <= 9; j++) {
                    literals.add(100 * bX + 10 * bY + j);
                }
                clauses.add(new Clause(literals));
            }
        } else {
            //Conflict Encoding
            clauses.add(new Clause(-(100 * aX + 10 * aY + 9)));
            clauses.add(new Clause(-(100 * bX + 10 * bY + 1)));
            for (int i = 2; i <= 8; i++) {
                for (int j = 1; j <= i; j++) {
                    clauses.add(new Clause(-(100 * aX + 10 * aY + i), -(100 * bX + 10 * bY + j)));
                }
            }
        }
        return clauses;
    }

    /**
     * Creates a clause set that is only satisfied if cell a has a smaller or equal value than cell b.
     *
     * @param aX X-index of cell a.
     * @param aY Y-index of cell a.
     * @param bX X-index of cell b.
     * @param bY Y-index of cell b.
     */
    public static ClauseSet createAsmallerEqB(int aX, int aY, int bX, int bY) {
        ClauseSet clauses = new ClauseSet();
        if (useSupport) {
            //Support Encoding
            for (int i = 2; i <= 9; i++) {
                ArrayList<Integer> literals = new ArrayList<>();
                literals.add(-(100 * aX + 10 * aY + i));
                for (int j = i; j <= 9; j++) {
                    literals.add(100 * bX + 10 * bY + j);
                }
                clauses.add(new Clause(literals));
            }
        } else {
            //Conflict Encoding
            for (int i = 2; i <= 9; i++) {
                for (int j = 1; j <= i - 1; j++) {
                    clauses.add(new Clause(-(100 * aX + 10 * aY + i), -(100 * bX + 10 * bY + j)));
                }
            }
        }
        return clauses;
    }

    @Override
    int check(int[][] model) {
        return 0;
    }
}
