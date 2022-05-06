package main.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class Sudoku extends Constraint {

    //for testing
    public void setRulesToCheck(ArrayList<String> rulesToCheck) {
        this.rulesToCheck = rulesToCheck;
    }

    private ArrayList<String> rulesToCheck;

    public Sudoku(String data) {
        super(data);
        rulesToCheck = new ArrayList<>();
    }


    public ClauseSet createClauses() {
        ClauseSet clauses = new ClauseSet();
        if (data.length() == 0) {
            clauses.addAll(oncePerRow());
            rulesToCheck.add("column");
            clauses.addAll(oncePerCol());
            rulesToCheck.add("row");
            clauses.addAll(oncePerBox());
            rulesToCheck.add("box");
        } else {
            Scanner scanner = new Scanner(data);
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case "row":
                        clauses.addAll(oncePerCol());
                        rulesToCheck.add("row");
                        break;
                    case "column":
                        clauses.addAll(oncePerRow());
                        rulesToCheck.add("column");
                        break;
                    case "box":
                        clauses.addAll(oncePerBox());
                        rulesToCheck.add("box");
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }

        return clauses;
    }

    @Override
    public int check(int[][] model) {
        for (String rule : rulesToCheck) {
            switch (rule) {
                case "row":
                    for (int y = 0; y < model[0].length; y++) {
                        for (int x = 0; x < model.length - 1; x++) {
                            for (int i = x + 1; i < model.length; i++) {
                                if (model[x][y] == model[i][y]) {
                                    return -1;
                                }
                            }
                        }
                    }
                    break;
                case "column":
                    for (int x = 0; x < model.length; x++) {
                        for (int y = 0; y < model[0].length - 1; y++) {
                            for (int i = y + 1; i < model[0].length; i++) {
                                if (model[x][y] == model[x][i]) {
                                    return -1;
                                }
                            }
                        }
                    }
                    break;
                case "box":
                    for (int by = 0; by < 3; by++) {
                        for (int bx = 0; bx < 3; bx++) {
                            for (int y = 0; y < 3; y++) {
                                for (int x = 0; x < 3; x++) {
                                    for (int l = 0; l < 3; l++) {
                                        for (int k = 0; k < 3; k++) {
                                            if (x == k && y == l) {
                                                continue;
                                            }
                                            if (model[3 * bx + x][3 * by + y] == model[3 * bx + k][3 * by + l]) {
                                                return -1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return 1;
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

    public static ClauseSet oncePerRow() {
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

    public static ClauseSet oncePerCol() {
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

    public static ClauseSet oncePerBox() { //TODO: Check correctness
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
