package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class Reader {

    public static HashMap<String, Constraint> read(String filename) throws FileNotFoundException {
        HashMap<String, Constraint> constraints = new HashMap<>();

            File file = new File(Paths.get("src", "main", "data", filename).toString());
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
                        }else {
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


//                switch (cur.length) {
//                    case 2:
//                        if (!constraints.containsKey(cur[0])) {
//                            if (cur[0].equals("PBC")) {
//                                Environment.setConverter(cur[1]);
//                            } else {
//                                Constraint constraint = constraintMaker(cur[0], cur[1]);
//                                constraints.put(cur[0], constraint);
//                            }
//                        } else {
//                            System.out.println("Reader Error: double constraint!");
//                        }
//                        break;
//                    case 1:
//                        if (!constraints.containsKey(cur[0])) {
//                            Constraint constraint = constraintMaker(cur[0], "");
//                            constraints.put(cur[0], constraint);
//                        } else {
//                            System.out.println("Reader Error: double constraint!");
//                        }
//                        break;
//                    default:
//                        System.out.println("Reader Error at token: " + token);
//                }
            }


        return constraints;
    }

    private static Constraint constraintMaker(String type, String data) {
        switch (type) {
            case "Sudoku":
                return new Sudoku(data);
            case "Killer":
                return new Killer(data);
            case "AntiKnight":
                return new AntiKnight();
            case "Grid":
                return new Grid(data);
            case "SandwichSum":
                return new SandwichSum(data);
            default:
                throw new IllegalArgumentException();
        }
    }
}
