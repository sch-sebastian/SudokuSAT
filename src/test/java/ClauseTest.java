package test.java;

import main.java.Clause;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;


class ClauseTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }

    @Test
    void compareToTest() {
        ArrayList<Clause> sortedList = new ArrayList<>();
        sortedList.add(new Clause(12, 100, 200));
        sortedList.add(new Clause(-12, -5, 200));
        sortedList.add(new Clause(-12, 5, 200));
        sortedList.add(new Clause(14, 100, 200));
        Collections.sort(sortedList);
        Assert.assertEquals(sortedList.get(0), new Clause(-12, -5, 200));
        Assert.assertEquals(sortedList.get(1), new Clause(-12, 5, 200));
        Assert.assertEquals(sortedList.get(2), new Clause(12, 100, 200));
        Assert.assertEquals(sortedList.get(3), new Clause(14, 100, 200));
    }
}