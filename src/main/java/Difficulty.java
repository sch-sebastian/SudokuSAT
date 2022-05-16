package main.java;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Difficulty extends Constraint {

    int easy;
    int hard;
    int x;
    int y;

    public Difficulty(String data) {
        super(data);
        Scanner scanner = new Scanner(this.data);
        try {
            int cord = scanner.nextInt();
            x = cord / 10;
            y = cord % 10;
            if (x > 9 || x < 1 || y > 9 || y < 1) {
                throw new IllegalArgumentException();
            }
            easy = scanner.nextInt();
            hard = scanner.nextInt();
        } catch (IllegalArgumentException e) {
            System.out.println("Difficulty.parseData Error: Illegal cell coordinates.");
        } catch (InputMismatchException e) {
            System.out.println("Difficulty.parseData Error: Next element not an Integer");
        } catch (NoSuchElementException e) {
            System.out.println("Difficulty.parseData Error: End of data to early.");
        } catch (IllegalStateException e) {
            System.out.println("Difficulty.parseData Error: Scanner closed to early.");
        }
    }


    @Override
    ClauseSet createClauses() {
        return null;
    }

    @Override
    int check(int[][] model) {
        return 0;
    }
}
