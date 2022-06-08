package main.java;

import java.awt.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;
import static main.java.Environment.*;

public class NurikabeResearch {

    static int twoNeighbours = 1000000;
    static int island = 1400000;
    static int ocean = 1500000;
    static int flood = 2000000;
    static int walk = 10000000;

    public static void main(String[] args) {
        int islandNum = 14;

        for (int upper = 12; upper <= islandNum; upper++) {
            ClauseSet clauses = new ClauseSet();
            for (int n = 1; n <= upper; n++) {
                ArrayList<Integer> literals = new ArrayList<>();
                for (int y = 1; y <= 9; y++) {
                    for (int x = 1; x <= 9; x++) {
                        literals.add(island + 100 * n + 10 * x + y);
                    }
                }
                clauses.add(new Clause(literals));
            }
            System.out.println("------------------------------------------");
            System.out.println("[" + new Timestamp(currentTimeMillis()) + "] Encoding...");

            Nurikabe nurikabe = new Nurikabe(0, "");
            //clauses.addAll(nurikabe.noRepetitionInIsland());
            clauses.addAll(nurikabe.onlyTouchDiagonally());
            clauses.addAll(nurikabe.islandImplications());
            clauses.addAll(nurikabe.oneIslandOrOceanPerCell());
            clauses.addAll(nurikabe.oceanCellInOneFloodLayerPerSource());
            clauses.addAll(nurikabe.floodImplications());
            clauses.addAll(nurikabe.islandContinuation());
            clauses.addAll(nurikabe.islandCellInOneWalkLayerPerSource());
            //clauses.addAll(nurikabe.arrowImplications(arrows));
            clauses.addAll(nurikabe.noOceanSquare());

            try {
                String inFile = "input" + upper + ".tmp";
                String outFile = "model" + upper + ".tmp";
                System.out.println("[" + new Timestamp(currentTimeMillis()) + "] Total: " + clauses.size() + " clauses and " + clauses.vars.size() + " variables.");
                Environment.writeDIMACS(clauses, inFile);
                System.out.println("[" + new Timestamp(currentTimeMillis()) + "] Searching model with " + upper + " islands...");
                boolean satisfiable = Solver.runMiniSat(inFile, outFile);
                if (satisfiable) {
                    //System.setOut(out);
                    System.out.println("[" + new Timestamp(currentTimeMillis()) + "] Model found!");
                    printIsland(outFile);
                } else {
                    //System.setOut(out);
                    System.out.println("[" + new Timestamp(currentTimeMillis()) + "] No model with " + upper + " islands found.");
                    System.exit(0);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
