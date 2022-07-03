package test.java;

import main.java.Main;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class SudokuKillerExperimentTest {

    @Test
    void Examples() {
        HashSet<String> ignore = new HashSet<>();
        File dir = new File(Paths.get("src", "main", "data").toString());
        File[] files = dir.listFiles();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String name = file.getName();
            if (!name.endsWith(".sdq") || ignore.contains(name)) {
                continue;
            }
            if (!name.contains("TheTimes") || ignore.contains(name)) {
                continue;
            }
            String[] args = {name, "MiniSat"};
            Assert.assertEquals(0, Main.run(args));
        }
    }
}
