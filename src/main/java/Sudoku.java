package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Sudoku {

    //---Non-static-part-in-case-we-need-instances-later----------------------------
    private ClauseSet clauses;

    public Sudoku() {
        clauses = new ClauseSet();
        clauses.addAll(oneNumberPerEntry());
        clauses.addAll(oncePerCol());
        clauses.addAll(oncePerRow());
        clauses.addAll(oncePerBox());
    }

    public Sudoku(String fileName) {
        clauses = new ClauseSet();
        clauses.addAll(oneNumberPerEntry());
        clauses.addAll(oncePerCol());
        clauses.addAll(oncePerRow());
        clauses.addAll(oncePerBox());
        clauses.addAll(parseField(fileName));
    }

    public ClauseSet getClauses() {
        return clauses;
    }
    //------------------------------------------------------------------------------

    public static ClauseSet createClauses(String... fileName) {
        ClauseSet res = new ClauseSet();
        res.addAll(oneNumberPerEntry());
        res.addAll(oncePerCol());
        res.addAll(oncePerRow());
        res.addAll(oncePerBox());
        if (fileName.length != 0) {
            res.addAll(parseField(fileName[0]));
        }
        return res;
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

    public static ClauseSet parseField(String filename) {
        HashSet<Clause> res = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(filename));

            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    int cur = scanner.nextInt();
                    if (0 < cur && cur < 10) {
                        res.add(new Clause(100 * x + 10 * y + cur));
                        //res.add("" + x + y + cur + " 0" + br);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ClauseSet cs = new ClauseSet();
        cs.addAll(res);
        return cs;
    }
}
