package main.java;

import java.util.HashSet;
import java.util.Scanner;


public class Sudoku extends Constraint {


    public Sudoku(String data) {
        super(data);
    }

    public ClauseSet createClauses() {
        ClauseSet clauses = new ClauseSet();
        if (data.length() == 0) {
            clauses.addAll(oncePerCol());
            clauses.addAll(oncePerRow());
            clauses.addAll(oncePerBox());
        } else {
            Scanner scanner = new Scanner(data);
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case "row":
                        clauses.addAll(oncePerRow());
                        break;
                    case "column":
                        clauses.addAll(oncePerCol());
                        break;
                    case "box":
                        clauses.addAll(oncePerBox());
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }

        return clauses;
    }

    public static ClauseSet oneNumberPerEntry() {
        HashSet<Clause> res = new HashSet<>();
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                String line = "";
                res.add(new Clause(100 * x + 10 * y + 1, 100 * x + 10 * y + 2, 100 * x + 10 * y + 3, 100 * x + 10 * y + 4, 100 * x + 10 * y + 5, 100 * x + 10 * y + 6, 100 * x + 10 * y + 7, 100 * x + 10 * y + 8, 100 * x + 10 * y + 9));
            }
        }
        ClauseSet cs = new ClauseSet();
        cs.addAll(res);
        return cs;
    }

    public static ClauseSet oncePerCol() {
        HashSet<Clause> res = new HashSet<>();
        for (int y = 1; y <= 9; y++) {
            for (int z = 1; z <= 9; z++) {
                for (int x = 1; x <= 8; x++) {
                    for (int i = x + 1; i <= 9; i++) {
                        res.add(new Clause(-(100 * x + 10 * y + z), -(100 * i + 10 * y + z)));
                        //res.add("-" + x + y + z + " -" + i + y + z + " 0" + br);
                    }
                }
            }
        }
        ClauseSet cs = new ClauseSet();
        cs.addAll(res);
        return cs;
    }

    public static ClauseSet oncePerRow() {
        HashSet<Clause> res = new HashSet<>();
        for (int x = 1; x <= 9; x++) {
            for (int z = 1; z <= 9; z++) {
                for (int y = 1; y <= 8; y++) {
                    for (int i = y + 1; i <= 9; i++) {
                        res.add(new Clause(-(100 * x + 10 * y + z), -(100 * x + 10 * i + z)));
                        //res.add("-" + x + y + z + " -" + x + i + z + " 0" + br);
                    }
                }
            }
        }
        ClauseSet cs = new ClauseSet();
        cs.addAll(res);
        return cs;
    }

    public static ClauseSet oncePerBox() {
        HashSet<Clause> res = new HashSet<>();
        for (int z = 1; z <= 9; z++) {
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    for (int x = 1; x <= 3; x++) {
                        for (int y = 1; y <= 3; y++) {
                            for (int k = y + 1; k <= 3; k++) {
                                res.add(new Clause(-(100 * (3 * i + x) + 10 * (3 * j + y) + z), -(100 * (3 * i + x) + 10 * (3 * j + k) + z)));
                                //res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + x) + (3 * j + k) + z + " 0" + br);
                            }
                            for (int k = x + 1; k <= 3; k++) {
                                for (int l = 1; l <= 3; l++) {
                                    res.add(new Clause(-(100 * (3 * i + x) + 10 * (3 * j + y) + z), -(100 * (3 * i + k) + 10 * (3 * j + l) + z)));
                                    //res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + k) + (3 * j + l) + z + " 0" + br);
                                }
                            }
                        }
                    }
                }
            }
        }
        ClauseSet cs = new ClauseSet();
        cs.addAll(res);
        return cs;
    }
}
