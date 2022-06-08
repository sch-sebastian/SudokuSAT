package test.java;

import main.java.Clause;
import main.java.ClauseSet;
import main.java.Main;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NurikabeTest {

    private final int twoNeighbours = 1000000;
    private final int island = 1400000;
    private final int ocean = 1500000;
    private final int flood = 2000000;
    private final int walk = 10000000;

    @Test
    void notIslandImpliesNotWalked() {
        ClauseSet clauses = new ClauseSet();
        clauses.add(new Clause(-(island + 100 * 1 + 10 * 5 + 5)));
        clauses.add(new Clause(walk + 1000000 * 1 + 10000 * 1 + 1000 * 5 + 100 * 5 + 10 * 5 + 5));

        String[] args = {"nurikabeEmpty.sdq", "MiniSat"};
        Main.run(args, clauses);

        /*Result without "not IslandNN -> not walkedDDNNxSySXY":
            Task: nurikabeEmpty.sdq
            ------------------------------------------
            Starting Encoding...
            Nurikabe: 12732430 clauses and 1166387 variables.
            Sudoku: 7290 clauses and 729 variables.
            Total: 12739803 clauses and 1166387 variables.
            Duration: 71446ms
            ------------------------------------------
            Starting Solver MiniSat... 2022-06-02 09:14:20.955
            Satisfiable !
            Duration: 96397ms
            Satisfiable !
            ------------------------------------------
            Starting Checks...
            Nurikabe: check not implemented
            Sudoku: successful
            ------------------------------------------
            Correct Model:
            1 8 3 5 4 2 7 9 6
            2 7 6 9 3 8 4 5 1
            9 4 5 6 7 1 2 3 8
            6 9 4 2 5 3 1 8 7
            5 2 1 7 8 6 9 4 3
            8 3 7 1 9 4 6 2 5
            4 6 8 3 1 9 5 7 2
            7 1 9 8 2 5 3 6 4
            3 5 2 4 6 7 8 1 9
            ------------------------------------------
            Islands:
            14 14 14 14 00 01 01 01 01
            14 14 14 14 00 00 00 01 01
            00 00 00 00 00 06 00 00 00
            00 09 00 06 06 06 00 05 05
            09 09 09 00 06 06 00 05 05
            09 09 09 00 06 06 00 00 05
            09 09 00 00 00 00 00 12 00
            00 00 00 04 00 12 12 12 00
            04 04 04 04 00 00 00 00 00
            ------------------------------------------

            After adding the clauses for the implication the Problem becomes unsatisfiable, which is what we want.
            */
    }

}