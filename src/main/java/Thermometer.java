package main.java;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Thermometer extends Constraint {


    boolean frozen = false;

    public Thermometer(String... data) {
        super(data);
        if (this.data.startsWith("frozen")) {
            frozen = true;
        }
    }

    @Override
    ClauseSet createClauses() {
        ClauseSet clauses = new ClauseSet();
        ArrayList<ArrayList<Integer>> thermometers = parseData();
        if (frozen) {
            for (ArrayList<Integer> thermo : thermometers) {
                for (int i = 0; i < thermo.size() - 1; i++) {
                    ArrowHead ah = new ArrowHead("EQ " + thermo.get(i) + " " + thermo.get(i + 1));
                    clauses.addAll(ah.createClauses());
                }
            }
        } else {
            for (ArrayList<Integer> thermo : thermometers) {
                for (int i = 0; i < thermo.size() - 1; i++) {
                    ArrowHead ah = new ArrowHead("" + thermo.get(i) + " " + thermo.get(i + 1));
                    clauses.addAll(ah.createClauses());
                }
            }
        }
        return clauses;
    }

    private ArrayList<ArrayList<Integer>> parseData() {
        ArrayList<ArrayList<Integer>> thermometers = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(data);
            if (scanner.hasNext() && !scanner.hasNextInt()) {
                //Skip frozen
                scanner.next();
            }
            while (scanner.hasNextInt()) {
                int a = scanner.nextInt();
                if (a == 0) {
                    thermometers.add(new ArrayList<>());
                    continue;
                }
                thermometers.get(thermometers.size() - 1).add(a);
            }
            scanner.close();

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Thermometer.parseData Error: No thermometer present.");
            throw e;
        } catch (IllegalArgumentException e) {
            System.out.println("Thermometer.parseData Error: Illegal cell index encountered.");
            throw e;
        } catch (InputMismatchException e) {
            System.out.println("Thermometer.parseData Error: Next element not an Integer");
            throw e;
        } catch (NoSuchElementException e) {
            System.out.println("Thermometer.parseData Error: End of data to early.");
            throw e;
        } catch (IllegalStateException e) {
            System.out.println("Thermometer.parseData Error: Scanner closed to early.");
            throw e;
        }
        return thermometers;
    }


    @Override
    int check(int[][] model) {
        return 0;
    }
}
