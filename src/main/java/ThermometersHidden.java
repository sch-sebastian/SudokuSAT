package main.java;

import java.util.*;

public class ThermometersHidden extends Constraint {

    public ThermometersHidden(String... data) {
        super(data);
    }

    @Override
    ClauseSet createClauses() {
        return createClauses(parseData());
    }

    ClauseSet createClauses(ArrayList<ArrayList<Tuple<Integer>>> thermometers) {
        ClauseSet clauses = new ClauseSet();
        clauses.addAll(atLeastOneGridPositionPerThermoField(thermometers));
        clauses.addAll(atMostOneThermoFieldInEachGridCell(thermometers));
        clauses.addAll(fitThermos(thermometers));
        return clauses;
    }

    private ArrayList<ArrayList<Tuple<Integer>>> parseData() {
        ArrayList<ArrayList<Tuple<Integer>>> thermometers = new ArrayList<>();
        Scanner scanner = new Scanner(data);
        while (scanner.hasNextInt()) {
            int a = scanner.nextInt();
            if (a == 0) {
                thermometers.add(new ArrayList<>());
                continue;
            }
            Tuple<Integer> t;

            if (a == 1) {
                t = new Tuple<>(0, -1);
            } else if (a == 2) {
                t = new Tuple<>(1, 0);
            } else if (a == 3) {
                t = new Tuple<>(0, 1);
            } else if (a == 4) {
                t = new Tuple<>(-1, 0);
            } else {
                throw new IllegalArgumentException();
            }
            thermometers.get(thermometers.size() - 1).add(t);
        }


        return thermometers;
    }

    private static ClauseSet atLeastOneGridPositionPerThermoField(ArrayList<ArrayList<Tuple<Integer>>> thermometers) {
        ClauseSet clauses = new ClauseSet();
        for (int t = 1; t <= thermometers.size(); t++) {
            for (int d = 1; d <= thermometers.get(t - 1).size() + 1; d++) {
                ArrayList<Integer> literals = new ArrayList<>();
                for (int y = 1; y <= 9; y++) {
                    for (int x = 1; x <= 9; x++) {
                        literals.add(1000 * (t + 10) + 100 * d + 10 * x + y);
                    }
                }
                clauses.add(new Clause(literals));
            }
        }
        return clauses;
    }

    private static ClauseSet atMostOneThermoFieldInEachGridCell(ArrayList<ArrayList<Tuple<Integer>>> thermometers) {
        ClauseSet clauses = new ClauseSet();
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                for (int t = 1; t <= thermometers.size(); t++) {
                    for (int d = 1; d <= thermometers.get(t - 1).size() + 1; d++) {
                        for (int k = d + 1; k <= thermometers.get(t - 1).size() + 1; k++) {
                            clauses.add(new Clause(-(1000 * (t + 10) + 100 * d + 10 * x + y), -(1000 * (t + 10) + 100 * k + 10 * x + y)));
                        }
                        for (int k = t + 1; k <= thermometers.size(); k++) {
                            for (int l = 1; l <= thermometers.get(t - 1).size() + 1; l++) {
                                clauses.add(new Clause(-(1000 * (t + 10) + 100 * d + 10 * x + y), -(1000 * (k + 10) + 100 * l + 10 * x + y)));
                            }
                        }
                    }
                }
            }
        }
        return clauses;
    }

    private static ClauseSet fitThermos(ArrayList<ArrayList<Tuple<Integer>>> thermometers) {
        ClauseSet clauses = new ClauseSet();
        for (int t = 1; t <= thermometers.size(); t++) {
            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    ArrayList<Tuple<Integer>> thermometer = thermometers.get(t - 1);
                    if (isInside(thermometer, x, y)) {
                        int xT = x;
                        int yT = y;
                        for (int d = 1; d <= thermometer.size(); d++) {
                            int xTnext = xT + thermometer.get(d - 1).get(0);
                            int yTnext = yT + thermometer.get(d - 1).get(1);
                            //This grid cell is a certain thermofield iff certain other cell is a certain thermofield.
                            clauses.add(new Clause(-(1000 * (t + 10) + 100 * d + 10 * xT + yT), (1000 * (t + 10) + 100 * (d + 1) + 10 * xTnext + yTnext)));
                            clauses.add(new Clause((1000 * (t + 10) + 100 * d + 10 * xT + yT), -(1000 * (t + 10) + 100 * (d + 1) + 10 * xTnext + yTnext)));
                            //This grid cell must be smaller than certain other grid cell, or this grid cell is not certain thermofield.
                            clauses.addAll(ArrowHead.createAsmallerB(xT, yT, xTnext, yTnext, (1000 * (t + 10) + 100 * d + 10 * xT + yT)));
                            xT = xTnext;
                            yT = yTnext;
                        }
                    } else {
                        //Thermometer is not at this position
                        int xT = x;
                        int yT = y;
                        clauses.add(new Clause(-(1000 * (t + 10) + 100 * 1 + 10 * xT + yT)));
                        for (int d = 2; d <= thermometer.size() + 1; d++) {
                            xT = xT + thermometer.get(d - 2).get(0);
                            yT = yT + thermometer.get(d - 2).get(1);
                            if (!(1 > xT || xT > 9 || 1 > yT || yT > 9)) {
                                clauses.add(new Clause(-(1000 * (t + 10) + 100 * d + 10 * xT + yT)));
                            }
                        }
                    }
                }
            }
        }
        return clauses;
    }

    private static boolean isInside(ArrayList<Tuple<Integer>> thermometer, int x, int y) {
        for (Tuple<Integer> t : thermometer) {
            x = x + t.get(0);
            y = y + t.get(1);
            if (1 > x || x > 9 || 1 > y || y > 9) {
                return false;
            }
        }
        return !(1 > x || x > 9 || 1 > y || y > 9);
    }

    @Override
    int check(int[][] model) {
        return 0;
    }
}
