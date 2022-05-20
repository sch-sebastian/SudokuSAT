package main.java;

import javax.swing.*;
import java.util.*;

public class Tower extends Constraint {
    private final int optimized;

    public Tower(int optimized, String data) {
        super(data);
        this.optimized = optimized;
    }

    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    private int[][] parseData() {
        int[][] towers = new int[10][2];
        Scanner scanner = new Scanner(data);
        try {
            for (int x = 1; x <= 9; x++) {
                towers[x][0] = scanner.nextInt();
            }
            for (int x = 1; x <= 9; x++) {
                towers[x][1] = scanner.nextInt();
            }
            towers[0][0] = scanner.nextInt();
            towers[0][1] = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Tower.parseData Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("Tower.parseData Error: End of data to early.");
        } catch (IllegalStateException e) {
            System.out.println("Tower.parseData Error: Scanner closed to early.");
        } finally {
            scanner.close();
        }
        return towers;
    }

    ClauseSet createClauses(int[][] towerData) {
        ClauseSet clauses = new ClauseSet();
        int[] faulty = new int[9];
        int[] increasing = new int[9];
        int[] summing = new int[9];
        int[] weights = new int[9];
        for (int i = 1; i <= faulty.length; i++) {
            faulty[i-1] = 1100 + 10 * i + 0;
            increasing[i-1] = 1100 + 10 * i + 1;
            summing[i-1] = 1100 + 10 * i + 2;
            weights[i-1] = 1;
        }
        //number of faulty towers, towers that increase and towers that have a certain sum must be correct.
        PBC faultyPbc = new PBC(faulty, weights, towerData[0][0] + towerData[0][1]);
        PBC increasingPbc = new PBC(increasing, weights, 9 - towerData[0][0]);
        PBC summingPbc = new PBC(summing, weights, 9 - towerData[0][1]);
        clauses.addAll(Environment.toClauses(faultyPbc, 0));
        clauses.addAll(Environment.toClauses(increasingPbc, 0));
        clauses.addAll(Environment.toClauses(summingPbc, 0));

        for (int x = 0; x < faulty.length; x++) {
            //tower not faulty or (summing xor increasing)
            clauses.add(new Clause(-faulty[x], summing[x], increasing[x]));
            clauses.add(new Clause(-faulty[x], -summing[x], -increasing[x]));
            //not (summing xor increasing) or faulty
            clauses.add(new Clause(faulty[x], summing[x], -increasing[x]));
            clauses.add(new Clause(faulty[x], -summing[x], increasing[x]));
        }

        //not increasing or arrowheads in tower
        for (int x = 1; x <= 9; x++) {
            ClauseSet cur = new ClauseSet();
            for (int y = 9; y > (9 - towerData[x][1] + 1); y--) {
                ArrowHead ah = new ArrowHead("" + (10 * x + y) + " " + (10 * x + y-1));
                cur.addAll(ah.createClauses());
            }
            cur.addVarToAll(-increasing[x-1]);
            clauses.addAll(cur);
        }

        //not increasing or tower is killer cage
        for (int x = 1; x <= 9; x++) {
            ClauseSet cur = new ClauseSet();
            ArrayList<Integer>[] groups = new ArrayList[1];
            groups[0] = new ArrayList<>();
            groups[0].add(towerData[x][0]);
            for (int y = 9; y > (9 - towerData[x][1]); y--) {
                groups[0].add(x);
                groups[0].add(y);
            }
            Killer cage = new Killer("");
            cage.optimized = optimized;
            cur.addAll(cage.createClauses(groups));
            cur.addVarToAll(-summing[x-1]);
            clauses.addAll(cur);
        }
        return clauses;
    }


    @Override
    int check(int[][] model) {
        return 0;
    }
}
