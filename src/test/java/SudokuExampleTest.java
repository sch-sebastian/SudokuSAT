package test.java;

import main.java.Main;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

public class SudokuExampleTest {

    @Test
    void Examples(){
        File dir = new File(Paths.get("src", "main", "data").toString());
        File[] files = dir.listFiles();
        for(File file: files){
            if(!file.isFile()){
                continue;
            }
            String name = file.getName();
            if(!name.endsWith(".sdq")){
                continue;
            }
            String[] args = {name, "Sat4j"};
            Assert.assertEquals(0, Main.run(args));
        }
    }
}
