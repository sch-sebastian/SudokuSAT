package main.java;

import java.util.ArrayList;
import java.util.Scanner;

public class Nurikabe extends Constraint {

    private final int optimized;
    private final int twoNeighbours = 1000000;
    private final int island = 1400000;
    private final int ocean = 1500000;
    private final int flood = 2000000;
    private final int walk = 10000000;
    private final int maxIslands = 14;

    public Nurikabe(int optimized, String data) {
        super(data);
        this.optimized = optimized;
    }

    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    ClauseSet createClauses(ArrayList<ArrayList<Tuple<Integer>>> arrows) {
        ClauseSet clauses = new ClauseSet();

        clauses.addAll(noRepetitionInIsland());
        clauses.addAll(onlyTouchDiagonally());
        clauses.addAll(islandImplications());
        clauses.addAll(oneIslandOrOceanPerCell());
        clauses.addAll(oceanCellInOneFloodLayerPerSource());
        clauses.addAll(floodImplications());
        clauses.addAll(islandContinuation());
        clauses.addAll(islandCellInOneWalkLayerPerSource());
        clauses.addAll(arrowImplications(arrows));
        clauses.addAll(noOceanSquare());

        return clauses;
    }

    ClauseSet noOceanSquare() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                clauses.add(new Clause(-(ocean + 10 * x + y), -(ocean + 10 * (x + 1) + y), -(ocean + 10 * x + (y + 1)), -(ocean + 10 * (x + 1) + (y + 1))));
            }
        }
        return clauses;
    }

    ClauseSet noRepetitionInIsland() {
        ClauseSet clauses = new ClauseSet();
        for (int n = 1; n <= maxIslands; n++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    for (int z = 1; z <= 9; z++) {
                        for (int k = y + 1; k <= 9; k++) {
                            clauses.add(new Clause(-(island + 100 * n + 10 * x + y), -(island + 100 * n + 10 * x + k), -(100 * x + 10 * y + z), -(100 * x + 10 * k + z)));
                            //This is not needed if normal sudoku rules apply
                        }
                        for (int k = x + 1; k <= 9; k++) {
                            for (int l = 1; l <= 9; l++) {
                                /*if (l == y) {
                                    continue;
                                    //Only allowed if normal sudoku rules apply
                                }*/
                                clauses.add(new Clause(-(island + 100 * n + 10 * x + y), -(island + 100 * n + 10 * k + l), -(100 * x + 10 * y + z), -(100 * k + 10 * l + z)));
                            }
                        }
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet onlyTouchDiagonally() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                for (int n = 1; n <= maxIslands; n++) {
                    for (int k = 1; k <= maxIslands; k++) {
                        if (k == n) {
                            continue;
                        }
                        if (x + 1 <= 9) {
                            clauses.add(new Clause(-(island + 100 * n + 10 * x + y), -(island + 100 * k + 10 * (x + 1) + y)));
                        }
                        if (y + 1 <= 9) {
                            clauses.add(new Clause(-(island + 100 * n + 10 * x + y), -(island + 100 * k + 10 * x + (y + 1))));
                        }
                    }
                }

            }
        }
        return clauses;
    }

    ClauseSet islandImplications() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                for (int n = 1; n <= maxIslands; n++) {
                    ArrayList<Integer> literals = new ArrayList<>();
                    literals.add(-(island + 100 * n + 10 * x + y));
                    for (int d = 1; d <= 18; d++) {
                        int twoNeighboursDDNNXY = twoNeighbours + 10000 * d + 100 * n + 10 * x + y;
                        switch (d) {
                            case 1:
                                if (y - 2 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 2))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 2:
                                if (y - 1 >= 1 && x + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + (y - 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 3:
                                if (y - 1 >= 1 && x + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + (y - 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 4:
                                if (x + 2 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 2) + y)));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 5:
                                if (x + 1 <= 9 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 6:
                                if (x + 1 <= 9 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 7:
                                if (y + 2 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 2))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 8:
                                if (x - 1 >= 1 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 9:
                                if (x - 1 >= 1 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 10:
                                if (x - 2 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 2) + y)));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 11:
                                if (x - 1 >= 1 && y - 1 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + (y - 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 12:
                                if (x - 1 >= 1 && y - 1 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + (y - 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 13:
                                if (x + 1 <= 9 && y - 1 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 14:
                                if (y - 1 >= 1 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 15:
                                if (y - 1 >= 1 && x - 1 >= 1) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y - 1))));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 16:
                                if (x + 1 <= 9 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 17:
                                if (x - 1 >= 1 && x + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x + 1) + y)));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                            case 18:
                                if (x - 1 >= 1 && y + 1 <= 9) {
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * (x - 1) + y)));
                                    clauses.add(new Clause(-twoNeighboursDDNNXY, (island + 100 * n + 10 * x + (y + 1))));
                                    literals.add(twoNeighboursDDNNXY);
                                }
                                break;
                        }
                    }
                    clauses.add(new Clause(literals));  //islandNNXY --> at least one twoNeighboursDDNNXY
                }
            }
        }
        return clauses;
    }

    ClauseSet oneIslandOrOceanPerCell() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                int oceanXY = ocean + 10 * x + y;
                ArrayList<Integer> literals = new ArrayList<>();
                literals.add(oceanXY);
                for (int n = 1; n <= maxIslands; n++) {
                    literals.add(island + 100 * n + 10 * x + y);
                    clauses.add(new Clause(-oceanXY, -(island + 100 * n + 10 * x + y))); //ocean --> not island
                    for (int k = n + 1; k <= maxIslands; k++) {
                        clauses.add(new Clause(-(island + 100 * n + 10 * x + y), -(island + 100 * k + 10 * x + y)));
                        //At most one island per cell
                    }
                }
                clauses.add(new Clause(literals));  //at least one island or ocean per cell
            }
        }
        return clauses;
    }

    ClauseSet oceanCellInOneFloodLayerPerSource() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                for (int yS = 1; yS <= 9; yS++) {
                    for (int xS = 1; xS <= 9; xS++) {
                        int oceanXsYs = ocean + 10 * xS + yS;
                        ArrayList<Integer> literals = new ArrayList<>();
                        literals.add(-oceanXsYs);
                        literals.add(-(ocean + 10 * x + y));
                        for (int d = 1; d <= 49; d++) {
                            literals.add((flood + 10000 * d + 1000 * xS + 100 * yS + 10 * x + y));
                            for (int k = d + 1; k <= 49; k++) {
                                clauses.add(new Clause(-(flood + 10000 * d + 1000 * xS + 100 * yS + 10 * x + y), -(flood + 10000 * k + 1000 * xS + 100 * yS + 10 * x + y)));
                                //At most one floodlayer per cell and source
                            }
                            clauses.add(new Clause((ocean + 10 * x + y), -(flood + 10000 * d + 1000 * xS + 100 * yS + 10 * x + y)));
                            //not ocean --> not flooded
                        }
                        clauses.add(new Clause(literals)); //source ocean --> cell is not ocean or at least one floodlayer
                    }
                }
            }
        }
        return clauses;
    }


    ClauseSet floodImplications() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                clauses.add(new Clause(-(ocean + 10 * x + y), (flood + 10000 * 1 + 1000 * x + 100 * y + 10 * x + y)));
                //floodlayer of source is 1
                for (int yS = 1; yS <= 9; yS++) {
                    for (int xS = 1; xS <= 9; xS++) {
                        if (x == xS && y == yS) {
                            continue;
                        }
                        clauses.add(new Clause(-(flood + 10000 * 1 + 1000 * xS + 100 * yS + 10 * x + y)));
                        //Not source --> not layer 1
                        for (int d = 2; d <= 49; d++) {
                            ArrayList<Integer> literals = new ArrayList<>();
                            literals.add(-(flood + 10000 * d + 1000 * xS + 100 * yS + 10 * x + y));
                            literals.add(-(ocean + 10 * xS + yS));
                            if (x + 1 <= 9) {
                                literals.add(flood + 10000 * (d - 1) + 1000 * xS + 100 * yS + 10 * (x + 1) + y);
                            }
                            if (x - 1 >= 1) {
                                literals.add(flood + 10000 * (d - 1) + 1000 * xS + 100 * yS + 10 * (x - 1) + y);
                            }
                            if (y + 1 <= 9) {
                                literals.add(flood + 10000 * (d - 1) + 1000 * xS + 100 * yS + 10 * x + (y + 1));
                            }
                            if (y - 1 >= 1) {
                                literals.add(flood + 10000 * (d - 1) + 1000 * xS + 100 * yS + 10 * x + (y - 1));
                            }
                            clauses.add(new Clause(literals)); // flooded ocean cell that is not source must have neighbour with lower layer
                        }
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet islandContinuation() {
        ClauseSet clauses = new ClauseSet();
        for (int n = 1; n <= maxIslands; n++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    clauses.add(new Clause(-(island + 100 * n + 10 * x + y), (walk + 1000000 * 1 + 10000 * n + 1000 * x + 100 * y + 10 * x + y)));
                    //walk layer of source is 1
                    for (int yS = 1; yS <= 9; yS++) {
                        for (int xS = 1; xS <= 9; xS++) {
                            if (x == xS && y == yS) {
                                continue;
                            }
                            clauses.add(new Clause(-(walk + 1000000 * 1 + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y)));
                            //Not source --> not layer 1 // this works
                            for (int d = 2; d <= 9; d++) {
                                ArrayList<Integer> literals = new ArrayList<>();
                                literals.add(-(walk + 1000000 * d + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y));
                                literals.add(-(island + 100 * n + 10 * xS + yS));
                                if (x + 1 <= 9) {
                                    literals.add(walk + 1000000 * (d - 1) + 10000 * n + 1000 * xS + 100 * yS + 10 * (x + 1) + y);
                                }
                                if (x - 1 >= 1) {
                                    literals.add(walk + 1000000 * (d - 1) + 10000 * n + 1000 * xS + 100 * yS + 10 * (x - 1) + y);
                                }
                                if (y + 1 <= 9) {
                                    literals.add(walk + 1000000 * (d - 1) + 10000 * n + 1000 * xS + 100 * yS + 10 * x + (y + 1));
                                }
                                if (y - 1 >= 1) {
                                    literals.add(walk + 1000000 * (d - 1) + 10000 * n + 1000 * xS + 100 * yS + 10 * x + (y - 1));
                                }
                                clauses.add(new Clause(literals)); // walked island cell that is not source must have neighbour with lower layer
                            }
                        }
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet islandCellInOneWalkLayerPerSource() {
        ClauseSet clauses = new ClauseSet();
        for (int n = 1; n <= maxIslands; n++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    for (int yS = 1; yS <= 9; yS++) {
                        for (int xS = 1; xS <= 9; xS++) {
                            int islandNXsYs = island + 100 * n + 10 * xS + yS;
                            ArrayList<Integer> literals = new ArrayList<>();
                            literals.add(-islandNXsYs);
                            literals.add(-(island + 100 * n + 10 * x + y));
                            for (int d = 1; d <= 9; d++) {
                                literals.add((walk + 1000000 * d + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y));
                                for (int k = d + 1; k <= 9; k++) {
                                    clauses.add(new Clause(-(walk + 1000000 * d + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y), -(walk + 1000000 * k + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y)));
                                    //At most one walklayer per cell and source and island
                                }
                                clauses.add(new Clause((island + 100 * n + 10 * x + y), -(walk + 1000000 * d + 10000 * n + 1000 * xS + 100 * yS + 10 * x + y)));
                                //not islandNNXY -> not walkedDDNNxSySXY
                                //this clauses increase solve time by more than 3x
                            }
                            clauses.add(new Clause(literals)); //source island --> cell is not island or at least one walklayer
                        }
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet arrowImplications(ArrayList<ArrayList<Tuple<Integer>>> arrows) {
        ClauseSet clauses = new ClauseSet();
        for (ArrayList<Tuple<Integer>> arrow : arrows) {
            int[] weights = new int[arrow.size() - 1];
            int[] oceanXY = new int[arrow.size() - 1];
            int[] islandXY = new int[arrow.size() - 1];
            for (int i = 1; i < arrow.size(); i++) {
                weights[i - 1] = 1;
                oceanXY[i - 1] = ocean + 10 * arrow.get(i).get(0) + arrow.get(i).get(1);
                islandXY[i - 1] = Environment.getVC();
                Environment.incVC();
            }
            if (oceanXY.length == 0) {
                clauses.add(new Clause());
                // An arrow that points directly outside the grid makes the puzzle unsat.
            }
            for (int z = 1; z <= 9; z++) {
                if (oceanXY.length < z) {
                    clauses.add(new Clause(-(100 * arrow.get(0).get(0) + 10 * arrow.get(0).get(1) + z)));
                    // An arrow cell value can not be larger than the number of cells in its direction.
                    // Adding this and leaving away the corresponding PBC does not decrease the number of clauses by
                    // much, but decreases the solving time by more than 6x.
                    continue;
                }
                //Ocean-------------------------------------------------------------------------------------------------
                PBC pbcOcean = new PBC(oceanXY, weights, z);
                int oceanVar = Environment.getVC();
                Environment.incVC();
                clauses.addAll(Environment.toClauses(pbcOcean, oceanVar));
                clauses.add(new Clause(-(ocean + 10 * arrow.get(0).get(0) + arrow.get(0).get(1)), -(100 * arrow.get(0).get(0) + 10 * arrow.get(0).get(1) + z), oceanVar));
                // not arrow is ocean or not arrow has sum or sum is correct

                //Island-------------------------------------------------------------------------------------------------
                PBC pbcIsland = new PBC(islandXY, weights, z);
                int islandVar = Environment.getVC();
                Environment.incVC();
                clauses.addAll(Environment.toClauses(pbcIsland, islandVar));
                clauses.add(new Clause((ocean + 10 * arrow.get(0).get(0) + arrow.get(0).get(1)), -(100 * arrow.get(0).get(0) + 10 * arrow.get(0).get(1) + z), islandVar));
                // not arrow is island or not arrow has sum or sum is correct
                for (int i = 0; i < islandXY.length; i++) {
                    clauses.add(new Clause(-islandXY[i], -(ocean + 10 * arrow.get(i + 1).get(0) + arrow.get(i + 1).get(1))));
                    clauses.add(new Clause(islandXY[i], (ocean + 10 * arrow.get(i + 1).get(0) + arrow.get(i + 1).get(1))));
                    //island     -> not ocean;
                    //not island -> ocean
                }
                //clauses.add(new Clause(-(100 * arrow.get(0).get(0) + 10 * arrow.get(0).get(1) + z), oceanVar, islandVar)); //arrow cell has value z --> at least one sum true; TODO: is at most also needed?

            }
        }
        return clauses;
    }

    private ArrayList<ArrayList<Tuple<Integer>>> parseData() {
        ArrayList<ArrayList<Tuple<Integer>>> arrows = new ArrayList<>();
        Scanner scanner = new Scanner(data);

        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                String cur = scanner.next().toLowerCase();
                if (cur.equals("u")) {
                    ArrayList<Tuple<Integer>> arrow = new ArrayList<>();
                    for (int curY = y; curY >= 1; curY--) {
                        arrow.add(new Tuple<>(x, curY));
                    }
                    arrows.add(arrow);
                } else if (cur.equals("r")) {
                    ArrayList<Tuple<Integer>> arrow = new ArrayList<>();
                    for (int curX = x; curX <= 9; curX++) {
                        arrow.add(new Tuple<>(curX, y));
                    }
                    arrows.add(arrow);
                } else if (cur.equals("d")) {
                    ArrayList<Tuple<Integer>> arrow = new ArrayList<>();
                    for (int curY = y; curY <= 9; curY++) {
                        arrow.add(new Tuple<>(x, curY));
                    }
                    arrows.add(arrow);
                } else if (cur.equals("l")) {
                    ArrayList<Tuple<Integer>> arrow = new ArrayList<>();
                    for (int curX = x; curX >= 1; curX--) {
                        arrow.add(new Tuple<>(curX, y));
                    }
                    arrows.add(arrow);
                }
            }
        }
        return arrows;
    }


    @Override
    int check(int[][] model) {
        return 0;
    }
}
