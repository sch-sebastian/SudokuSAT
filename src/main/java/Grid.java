package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Grid extends Constraint {
    public Grid(String data) {
        super(data);
    }

    @Override
    public ClauseSet createClauses() {
        ClauseSet clauses = new ClauseSet();
        Scanner scanner = new Scanner(data);

        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                int cur = scanner.nextInt();
                if (0 < cur && cur < 10) {
                    clauses.add(new Clause(100 * x + 10 * y + cur));
                    //res.add("" + x + y + cur + " 0" + br);
                }
            }
        }

        return clauses;
    }
}
