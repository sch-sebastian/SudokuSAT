package exp.sebastian;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static String br = "\n";

    public static void main(String[] args) {
        ArrayList<String> clauses = new ArrayList<>();
        clauses.addAll(oneNumberPerEntry());
        clauses.addAll(oncePerCol());
        clauses.addAll(oncePerRow());
        clauses.addAll(oncePerBox());
        clauses.addAll(parse("sudoku01"));


        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter("inputTMP.txt"));
            myWriter.write("p cnf 999 " + clauses.size() + br);
            for(String clause : clauses){
                myWriter.write(clause);
            }
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Solver.run();

        ArrayList<Integer> sol = new ArrayList<>();
        int[][] grid = new int[9][9];
        try {
            Scanner scanner = new Scanner(new File("modelTMP.txt"));
            while (scanner.hasNextInt()) {
                int cur = scanner.nextInt();
                if (cur > 0) {
                    String str = Integer.toString(cur);
                    int x = Integer.parseInt(str.substring(0, 1));
                    int y = Integer.parseInt(str.substring(1, 2));
                    int z = Integer.parseInt(str.substring(2));
                    grid[x - 1][y - 1] = z;
                }
            }

            for (int x = 0; x < grid.length; x++) {
                String line = "";
                for (int y = 0; y < grid[0].length; y++) {
                    line = line + grid[x][y] + " ";
                }
                System.out.println(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


    public static ArrayList<String> oneNumberPerEntry() {
        ArrayList<String> res = new ArrayList<>();
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                String line = "";
                for (int z = 1; z <= 9; z++) {
                    line = line + x + y + z + " ";
                }
                res.add(line + "0" + br);
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerCol() {
        ArrayList<String> res = new ArrayList<>();
        for (int y = 1; y <= 9; y++) {
            for (int z = 1; z <= 9; z++) {
                for (int x = 1; x <= 8; x++) {
                    for (int i = x + 1; i <= 9; i++) {
                        res.add("-" + x + y + z + " -" + i + y + z + " 0" + br);
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerRow() {
        ArrayList<String> res = new ArrayList<>();
        for (int x = 1; x <= 9; x++) {
            for (int z = 1; z <= 9; z++) {
                for (int y = 1; y <= 8; y++) {
                    for (int i = y + 1; i <= 9; i++) {
                        res.add("-" + x + y + z + " -" + x + i + z + " 0" + br);
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> oncePerBox() {
        ArrayList<String> res = new ArrayList<>();
        for (int z = 1; z <= 9; z++) {
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    for (int x = 1; x <= 3; x++) {
                        for (int y = 1; y <= 3; y++) {
                            for (int k = y + 1; k <= 3; k++) {
                                res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + x) + (3 * j + k) + z + " 0" + br);
                            }
                            for (int k = x + 1; k <= 3; k++) {
                                for (int l = 1; l <= 3; l++) {
                                    res.add("-" + (3 * i + x) + (3 * j + y) + z + " -" + (3 * i + k) + (3 * j + l) + z + " 0" + br);
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public static ArrayList<String> parse(String filename) {
        ArrayList<String> res = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));

            for (int y = 1; y <= 9; y++) {
                for (int x = 1; x <= 9; x++) {
                    int cur = scanner.nextInt();
                    if (0 < cur && cur < 10) {
                        res.add("" + y + x + cur + " 0" + br);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

}
