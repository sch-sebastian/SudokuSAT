package main.java;

import java.util.*;

import static main.java.PBC.toPBC;

public class SandwichSum extends Constraint {

    public SandwichSum(String... data) {
        super(data);
    }


    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    ClauseSet createClauses(HashMap<Integer, Integer>[] sums) {
        ClauseSet clauses = new ClauseSet();
        //Create clauses for rows
        for (int y = 1; y <= 9; y++) {
            if(!sums[1].containsKey(y)){
                continue;
            }
            ArrayList<Integer> sumVariables = new ArrayList<>();
            for (int l = 0; l <= 7; l++) {
                for (int x = 1; x <= 9 - l - 1; x++) {
                    int sumVar = Environment.getVC();
                    Environment.incVC();
                    sumVariables.add(sumVar);
                    clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * (x + l + 1) + 10 * y + 9), sumVar));
                    clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * (x + l + 1) + 10 * y + 1), sumVar));
                    PBC pbc = toRowPBC(x,x + l + 1,y,sums[1].get(y));
                    clauses.addAll(Environment.toClauses(pbc, sumVar));
                }
            }
            //At least one sum is true
            clauses.add(new Clause(sumVariables));
            //At most one sum is true
            for(int i = 0; i < sumVariables.size()-1; i++){
                for(int j = i+1; j < sumVariables.size(); j++){
                    clauses.add(new Clause(-sumVariables.get(i), -sumVariables.get(j)));
                }
            }
        }

        //Create clauses for columns
        for (int x = 1; x <= 9; x++) {
            if(!sums[0].containsKey(x)){
                continue;
            }
            ArrayList<Integer> sumVariables = new ArrayList<>();
            for (int l = 0; l <= 7; l++) {
                for (int y = 1; y <= 9 - l - 1; y++) {
                    int sumVar = Environment.getVC();
                    Environment.incVC();
                    sumVariables.add(sumVar);
                    clauses.add(new Clause(-(100 * x + 10 * y + 1), -(100 * x + 10 * (y + l + 1) + 9), sumVar));
                    clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * x + 10 * (y + l + 1) + 1), sumVar));
                    PBC pbc = toColumnPBC(y,y + l + 1,x,sums[0].get(x));
                    clauses.addAll(Environment.toClauses(pbc, sumVar));
                }
            }
            //At least one sum is true
            clauses.add(new Clause(sumVariables));
            //At most one sum is true
            for(int i = 0; i < sumVariables.size()-1; i++){
                for(int j = i+1; j < sumVariables.size(); j++){
                    clauses.add(new Clause(-sumVariables.get(i), -sumVariables.get(j)));
                }
            }
        }

        return clauses;
    }

    private HashMap<Integer, Integer>[] parseData() {
        HashMap<Integer, Integer>[] sums = new HashMap[2];
        try {
            Scanner scanner = new Scanner(data);

            int totalCol = scanner.nextInt();
            sums[0] = new HashMap<>();
            for (int i = 0; i < totalCol; i++) {
                int col = scanner.nextInt();
                int sum = scanner.nextInt();
                sums[0].put(col,sum);
            }

            sums[1] = new HashMap<>();
            if(!scanner.hasNextInt()){
                return sums;
            }
            int totalRow = scanner.nextInt();
            for (int i = 0; i < totalRow; i++) {
                int row = scanner.nextInt();
                int sum = scanner.nextInt();
                sums[1].put(row,sum);
            }

        } catch (InputMismatchException e) {
            System.out.println("SandwichSum.parseData Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("SandwichSum.parseData Error: End of data to early.");
        } catch (IllegalStateException e) {
            System.out.println("SandwichSum.parseData Error: Scanner closed to early.");
        }
        return sums;
    }

    private PBC toRowPBC(int x1, int x2, int y, int sum){
        ArrayList<Integer> group = new ArrayList<>();
        group.add(sum);
        for(int x = x1+1; x<=x2-1; x++){
            group.add(x);
            group.add(y);
        }
        return toPBC(group);
    }

    private PBC toColumnPBC(int y1, int y2, int x, int sum){
        ArrayList<Integer> group = new ArrayList<>();
        group.add(sum);
        for(int y = y1+1; y<=y2-1; y++){
            group.add(x);
            group.add(y);
        }
        return toPBC(group);
    }

    @Override
    int check(int[][] model) {
        return 0;
    }
}
