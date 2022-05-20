package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class Reader {

    public static HashMap<String, Constraint> read(String filename) throws FileNotFoundException {
        HashMap<String, Constraint> constraints = new HashMap<>();
        String basePath = System.getProperty("user.dir");
        if (!Reader.class.getResource("Reader.class").toString().toLowerCase().contains(".jar")) {
            basePath = Paths.get(basePath, "src", "main", "data").toString();
        }
        File file = new File(Paths.get(basePath, filename).toString());
        Scanner scanner = new Scanner(file).useDelimiter("\\s*\\}*\\s*\\{|\\}\\s*");
        while (scanner.hasNext()) {
            String token = scanner.next();
            String[] cur = token.split(":");
            if (cur.length > 0 && cur[0].equalsIgnoreCase("c")) {
                continue;
            }
            if (cur.length == 0 || cur.length > 2) {
                System.out.println("Reader Error at token: " + token);
                throw new IllegalArgumentException();
            }
            switch (cur[0]) {
                case "PBC":
                    if (cur.length > 1) {
                        Environment.setConverter(cur[1]);
                    }
                    break;
                case "Solution":
                    if (cur.length == 2) {
                        Environment.setSolution(cur[1]);
                    } else {
                        throw new IllegalArgumentException();
                    }
                    break;
                default:
                    if (!constraints.containsKey(cur[0])) {
                        if (cur.length == 1) {
                            Constraint constraint = constraintMaker(cur[0], "");
                            constraints.put(cur[0], constraint);
                        } else {
                            Constraint constraint = constraintMaker(cur[0], cur[1]);
                            constraints.put(cur[0], constraint);
                        }
                    } else {
                        System.out.println("Reader Error: double constraint!");
                        throw new IllegalArgumentException();
                    }
            }
        }
        return constraints;
    }

    private static Constraint constraintMaker(String type, String data) {
        int optimized = 0;
        if (data.contains("[")) {
            int start = data.indexOf("[");
            int end = data.indexOf("]");
            try {
                String num = data.substring(start + 1, end);
                optimized = Integer.parseInt(num);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Reader Error: " + type + " optimization specification faulty!");
                throw new IllegalArgumentException();
            } catch (NumberFormatException e) {
                System.out.println("Reader Error: " + type + " optimization specification faulty!");
                throw new IllegalArgumentException();
            }
        }
        switch (type) {
            case "Sudoku":
                return new Sudoku(data);
            case "Killer":
                return new Killer(optimized, data);
            case "AntiKnight":
                return new AntiKnight();
            case "Grid":
                return new Grid(data);
            case "SandwichSum":
                return new SandwichSum(optimized, data);
            case "ArrowHead":
                return new ArrowHead(data);
            case "Thermometer":
                return new Thermometer(data);
            case "Difficulty":
                return new Difficulty(data);
            case "ThermometersHidden":
                return new ThermometersHidden(data);
            case "SecretDirection":
                return new SecretDirection(data);
            case "Tower":
                return new Tower(optimized, data);
            default:
                System.out.println("Reader Error: unknown constraint \"" + type + "\".");
                throw new IllegalArgumentException();
        }
    }
}
