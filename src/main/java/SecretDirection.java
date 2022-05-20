package main.java;

import java.util.*;

public class SecretDirection extends Constraint {

    public SecretDirection(String... data) {
        super(data);
    }

    private int[] parseData() {
        int[] data = new int[3];
        Scanner scanner = new Scanner(this.data);

        try {
            int pos = scanner.nextInt();
            data[2] = scanner.nextInt();
            data[0] = pos / 10;
            data[1] = pos % 10;
        } catch (InputMismatchException e) {
            System.out.println("SecretDirection.parseData Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("SecretDirection.parseData Error: End of data to early.");
        } catch (IllegalStateException e) {
            System.out.println("SecretDirection.parseData Error: Scanner closed to early.");
        } finally {
            scanner.close();
        }
        //TODO: Generally handle exceptions in a smart way...
        return data;
    }

    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    ClauseSet createClauses(int[] origin) {
        ClauseSet clauses = new ClauseSet();
        clauses.add(new Clause(100 * origin[0] + 10 * origin[1] + origin[2])); //Set origin value
        clauses.add(new Clause(1000 + 10 * origin[0] + origin[1])); //Set origin as path
        //TODO:check if one above is needed.
        clauses.addAll(atLeastOneCenter9());
        clauses.addAll(atMostOneCenter9());
        clauses.addAll(center9IsPath());
        clauses.addAll(atMostOnceInPath());
        clauses.addAll(atMostOneCellPerDepth());
        clauses.addAll(nineInPathIsCenter());
        clauses.addAll(pathImplications(origin[0], origin[1]));
        return clauses;
    }

    ClauseSet pathImplications(int xR, int yR) {
        ClauseSet clauses = new ClauseSet();
        //PathXY0 is true for root
        clauses.add(new Clause(10000 + 1000 * xR + 100 * yR + 0));
        //PathXY0 is false for all except the root
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                if (x == xR && y == yR) {
                    continue;
                }
                clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + 0)));
            }
        }
        for (int yBox = 0; yBox < 3; yBox++) {
            for (int xBox = 0; xBox < 3; xBox++) {
                for (int yIB = 1; yIB <= 3; yIB++) {
                    for (int xIB = 1; xIB <= 3; xIB++) {
                        for (int d = 0; d <= 63; d++) {
                            for (int yIBNine = 1; yIBNine <= 3; yIBNine++) {
                                for (int xIBNine = 1; xIBNine <= 3; xIBNine++) {
                                    if (xIBNine == 2 && yIBNine == 2) {
                                        continue;
                                    }
                                    for (int z = 1; z <= 8; z++) {
                                        ArrayList<Integer> literals = new ArrayList<>();
                                        int x = xBox * 3 + xIB;
                                        int y = yBox * 3 + yIB;
                                        int xNine = (xBox * 3 + xIBNine);
                                        int yNine = (yBox * 3 + yIBNine);
                                        literals.add(-(10000 + 1000 * x + 100 * y + d));
                                        literals.add(-(100 * xNine + 10 * yNine + 9));
                                        literals.add(-(100 * x + 10 * y + z));
                                        int xS = 0;
                                        int yS = 0;
                                        if (xIBNine == 1 && yIBNine == 1) {
                                            xS = x - z;
                                            yS = y - z;
                                        } else if (xIBNine == 2 && yIBNine == 1) {
                                            xS = x;
                                            yS = y - z;
                                        } else if (xIBNine == 3 && yIBNine == 1) {
                                            xS = x + z;
                                            yS = y - z;
                                        } else if (xIBNine == 1 && yIBNine == 2) {
                                            xS = x - z;
                                            yS = y;
                                        } else if (xIBNine == 3 && yIBNine == 2) {
                                            xS = x + z;
                                            yS = y;
                                        } else if (xIBNine == 1 && yIBNine == 3) {
                                            xS = x - z;
                                            yS = y + z;
                                        } else if (xIBNine == 2 && yIBNine == 3) {
                                            xS = x;
                                            yS = y + z;
                                        } else if (xIBNine == 3 && yIBNine == 3) {
                                            xS = x + z;
                                            yS = y + z;
                                        }
                                        if (xS <= 9 && xS >= 1 && yS <= 9 && yS >= 1) {
                                            // If successor inside grid: either xy is not path or xy has not value z or
                                            // the 9 is not in this position or xSyS is successor
                                            literals.add(10000 + 1000 * xS + 100 * yS + (d + 1));
                                        }
                                        // If successor outside grid: either xy is not path or xy has not value z or the
                                        // 9 is not in this position
                                        clauses.add(new Clause(literals));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet center9IsPath() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 2; y <= 8; y = y + 3) {
            for (int x = 2; x <= 8; x = x + 3) {
                ArrayList<Integer> literals = new ArrayList<>();
                //xy is not 9 or xy is Path (in some depth)
                literals.add(-(100 * x + 10 * y + 9));
                for (int d = 0; d <= 64; d++) {
                    literals.add(10000 + 1000 * x + 100 * y + d);
                }
                clauses.add(new Clause(literals));
            }
        }
        return clauses;
    }

    ClauseSet nineInPathIsCenter() {
        ClauseSet clauses = new ClauseSet();
        for (int d = 0; d <= 64; d++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    if ((x == 2 && y == 2) || (x == 5 && y == 2) || (x == 8 && y == 2) || (x == 2 && y == 5) || (x == 5 && y == 5) || (x == 8 && y == 5) || (x == 2 && y == 8) || (x == 5 && y == 8) || (x == 8 && y == 8)) {
                        continue;
                    }
                    //For non-box-center cells it holds that.
                    //Not in path or is not nine
                    clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + d), -(100 * x + 10 * y + 9)));
                }
            }
        }
        return clauses;
    }

    ClauseSet atMostOnceInPath() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 2; y <= 8; y = y + 3) {
            for (int x = 2; x <= 8; x = x + 3) {
                for (int d = 0; d <= 63; d++) {
                    for (int k = d + 1; k <= 64; k++) {
                        clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + d), -(10000 + 1000 * x + 100 * y + k)));
                    }
                }
            }
        }
        return clauses;
    }

    ClauseSet atMostOneCellPerDepth() {
        ClauseSet clauses = new ClauseSet();
        for (int d = 0; d <= 64; d++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    for (int k = y + 1; k <= 9; k++) {
                        clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + d), -(10000 + 1000 * x + 100 * k + d)));
                    }
                    for (int k = x + 1; k <= 9; k++) {
                        for (int l = 1; l <= 9; l++) {
                            clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + d), -(10000 + 1000 * k + 100 * l + d)));
                        }
                    }
                }
            }
        }
        return clauses;
    }


    ClauseSet atLeastOneCenter9() {
        ClauseSet clauses = new ClauseSet();
        ArrayList<Integer> literals = new ArrayList<>();
        for (int y = 2; y <= 8; y = y + 3) {
            for (int x = 2; x <= 8; x = x + 3) {
                literals.add(100 * x + 10 * y + 9);
            }
        }
        clauses.add(new Clause(literals));
        return clauses;
    }

    ClauseSet atMostOneCenter9() {
        ClauseSet clauses = new ClauseSet();
        for (int y = 2; y <= 8; y = y + 3) {
            for (int x = 2; x <= 8; x = x + 3) {
                for (int k = y + 3; k <= 8; k = k + 3) {
                    clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * x + 10 * k + 9)));
                }
                for (int k = x + 3; k <= 8; k = k + 3) {
                    for (int l = 2; l <= 8; l = l + 3) {
                        clauses.add(new Clause(-(100 * x + 10 * y + 9), -(100 * k + 10 * l + 9)));
                    }
                }
            }
        }
        return clauses;
    }

    @Override
    int check(int[][] model) {
        return 0;
    }

     /*ClauseSet pathImplications(int xR, int yR) {
        ClauseSet clauses = new ClauseSet();
        //PathXY0 is true for root
        clauses.add(new Clause(10000 + 1000 * xR + 100 * yR + 0));
        //PathXY0 is false for all except the root
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                if (x == xR && y == yR) {
                    continue;
                }
                clauses.add(new Clause(-(10000 + 1000 * x + 100 * y + 0)));
            }
        }
        HashMap<Integer, HashSet<Tuple<Integer>>> neighbourMap = new HashMap<>();
        for (int y = 1; y <= 9; y++) {
            for (int x = 1; x <= 9; x++) {
                neighbourMap.put(10 * x + y, getNeighbours(x, y));
            }
        }
        for (int d = 1; d <= 80; d++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    System.out.println(x + "  " + y + "  " +d);
                    HashSet<Tuple<Integer>> neighbours = neighbourMap.get(10 * x + y);
                    ArrayList<ArrayList<Integer>> partition = new ArrayList<>();
                    for (Tuple<Integer> neighbour : neighbours) {
                        ArrayList<Integer> cur = new ArrayList<>();
                        cur.add(10000 + 1000 * neighbour.get(0) + 100 * neighbour.get(1) + (d - 1));
                        cur.add(100 * neighbour.get(0) + 10 * neighbour.get(1) + getNeighbourDistance(x, y, neighbour.get(0), neighbour.get(1)));
                        int[] ninePos = getNinePosition(x, y, neighbour.get(0), neighbour.get(1));
                        cur.add(100 * ninePos[0] + 10 * ninePos[1] + 9);
                        partition.add(cur);
                    }
                    ArrayList<ArrayList<Integer>> subsetsOfPartition = getSubsetsOfPartition(partition);
                    ClauseSet curClauses = new ClauseSet();
                    for (ArrayList<Integer> subset : subsetsOfPartition) {
                        curClauses.add(new Clause(subset));
                    }
                    curClauses.addVarToAll(-(10000 + 1000 * x + 100 * y + d));
                    clauses.addAll(curClauses);
                }
            }
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    HashSet<Tuple<Integer>> neighbours = neighbourMap.get(10 * x + y);
                    ArrayList<ArrayList<Integer>> partition = new ArrayList<>();
                    for (Tuple<Integer> neighbour : neighbours) {
                        ArrayList<Integer> cur = new ArrayList<>();
                        cur.add(10000 + 1000 * neighbour.get(0) + 100 * neighbour.get(1) + (d));
                        cur.add(100 * x + 10 * y + getNeighbourDistance(x, y, neighbour.get(0), neighbour.get(1)));
                        int[] ninePos = getNinePosition(neighbour.get(0), neighbour.get(1), x, y);
                        cur.add(100 * ninePos[0] + 10 * ninePos[1] + 9);
                        partition.add(cur);
                    }
                    ArrayList<ArrayList<Integer>> subsetsOfPartition = getSubsetsOfPartition(partition);
                    ClauseSet curClauses = new ClauseSet();
                    for (ArrayList<Integer> subset : subsetsOfPartition) {
                        curClauses.add(new Clause(subset));
                    }
                    curClauses.addVarToAll(-(10000 + 1000 * x + 100 * y + (d - 1)));
                    curClauses.addVarToAll((100 * x + 10 * y + 9));
                    clauses.addAll(curClauses);
                }
            }
        }
        return clauses;
    }

    int getNeighbourDistance(int x1, int y1, int x2, int y2) {
        if (x1 != x2) {
            return Math.abs(x1 - x2);
        } else {
            return Math.abs(y1 - y2);
        }
    }

    int[] getNinePosition(int xS, int yS, int xP, int yP) {
        int[] res = new int[2];
        int xBox = xP / 3;
        int yBox = yP / 3;
        if (xP % 3 != 0) {
            xBox++;
        }
        if (yP % 3 != 0) {
            yBox++;
        }
        res[0] = (xBox - 1) * 3;
        res[1] = (yBox - 1) * 3;
        if (xS < xP) {
            res[0] = res[0] + 1;
        } else if (xS == xP) {
            res[0] = res[0] + 2;
        } else {
            res[0] = res[0] + 2;
        }
        if (yS < yP) {
            res[1] = res[1] + 1;
        } else if (yS == yP) {
            res[1] = res[1] + 2;
        } else {
            res[1] = res[1] + 2;
        }
        return res;
    }

    static HashSet<Tuple<Integer>> getNeighbours(int xCur, int yCur) {
        HashSet<Tuple<Integer>> neighbours = new HashSet<>();

        //row
        for (int x = 1; x <= 9; x++) {
            if (x == xCur) {
                continue;
            }
            neighbours.add(new Tuple<>(x, yCur));
        }
        //column
        for (int y = 1; y <= 9; y++) {
            if (y == yCur) {
                continue;
            }
            neighbours.add(new Tuple<>(xCur, y));
        }
        //diag
        for (int k = -8; k <= 8; k++) {
            if (k == 0) {
                continue;
            }
            int x1 = xCur + k;
            int y1 = yCur + k;
            int x2 = xCur + k;
            int y2 = yCur - k;
            if (x1 >= 1 && x1 <= 9 && y1 >= 1 && y1 <= 9) {
                neighbours.add(new Tuple<>(x1, y1));
            }
            if (x2 >= 1 && x2 <= 9 && y2 >= 1 && y2 <= 9) {
                neighbours.add(new Tuple<>(x2, y2));
            }
        }
        return neighbours;
    }





    static ArrayList<ArrayList<Integer>> getSubsetsOfPartition(ArrayList<ArrayList<Integer>> partitions) {
        return getSubsetsOfPartition(partitions, partitions.size(), 1);
    }

    static ArrayList<ArrayList<Integer>> getSubsetsOfPartition(ArrayList<ArrayList<Integer>> tuples, int nPart, int depth) {
        if (tuples.size() == 1) {
            ArrayList<Integer> lastTuple = tuples.get(0);
            ArrayList<ArrayList<Integer>> full = new ArrayList<>();
            for (int i = 0; i < (int) Math.pow(lastTuple.size(), nPart); i++) {
                System.out.println(i);
                full.add(new ArrayList<>());
                full.get(full.size() - 1).add(lastTuple.get((i % lastTuple.size())));
            }
            return full;
        } else {
            ArrayList<Integer> head = tuples.get(0);
            tuples.remove(0);
            ArrayList<ArrayList<Integer>> partial = getSubsetsOfPartition(tuples, nPart, depth + 1);
            for (int i = 0; i < partial.size(); i++) {
                partial.get(i).add(head.get((int) ((i / Math.pow(head.size(), nPart - depth)) % head.size())));
            }
            return partial;
        }
    }*/
}
