package test.java;

import main.java.AdderNetwork;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

class AdderNetworkTest {

    @Test
    void toBinary() {
        ArrayList<Integer>[] binNum = new ArrayList[17];

        ArrayList<Integer> zero = new ArrayList<>();
        binNum[0] = zero;

        ArrayList<Integer> one = new ArrayList<>();
        one.add(1);
        binNum[1] = one;

        ArrayList<Integer> two = new ArrayList<>();
        two.add(0);
        two.add(1);
        binNum[2] = two;

        ArrayList<Integer> three = new ArrayList<>();
        three.add(1);
        three.add(1);
        binNum[3] = three;

        ArrayList<Integer> four = new ArrayList<>();
        four.add(0);
        four.add(0);
        four.add(1);
        binNum[4] = four;

        ArrayList<Integer> five = new ArrayList<>();
        five.add(1);
        five.add(0);
        five.add(1);
        binNum[5] = five;

        ArrayList<Integer> six = new ArrayList<>();
        six.add(0);
        six.add(1);
        six.add(1);
        binNum[6] = six;

        ArrayList<Integer> seven = new ArrayList<>();
        seven.add(1);
        seven.add(1);
        seven.add(1);
        binNum[7] = seven;

        ArrayList<Integer> eight = new ArrayList<>();
        eight.add(0);
        eight.add(0);
        eight.add(0);
        eight.add(1);
        binNum[8] = eight;

        ArrayList<Integer> nine = new ArrayList<>();
        nine.add(1);
        nine.add(0);
        nine.add(0);
        nine.add(1);
        binNum[9] = nine;

        ArrayList<Integer> ten = new ArrayList<>();
        ten.add(0);
        ten.add(1);
        ten.add(0);
        ten.add(1);
        binNum[10] = ten;

        ArrayList<Integer> eleven = new ArrayList<>();
        eleven.add(1);
        eleven.add(1);
        eleven.add(0);
        eleven.add(1);
        binNum[11] = eleven;

        ArrayList<Integer> twelve = new ArrayList<>();
        twelve.add(0);
        twelve.add(0);
        twelve.add(1);
        twelve.add(1);
        binNum[12] = twelve;

        ArrayList<Integer> thirdteen = new ArrayList<>();
        thirdteen.add(1);
        thirdteen.add(0);
        thirdteen.add(1);
        thirdteen.add(1);
        binNum[13] = thirdteen;

        ArrayList<Integer> fourteen = new ArrayList<>();
        fourteen.add(0);
        fourteen.add(1);
        fourteen.add(1);
        fourteen.add(1);

        binNum[14] = fourteen;
        ArrayList<Integer> fifteen = new ArrayList<>();
        fifteen.add(1);
        fifteen.add(1);
        fifteen.add(1);
        fifteen.add(1);
        binNum[15] = fifteen;

        ArrayList<Integer> sixteen = new ArrayList<>();
        sixteen.add(0);
        sixteen.add(0);
        sixteen.add(0);
        sixteen.add(0);
        sixteen.add(1);
        binNum[16] = sixteen;


        for (int n = 0; n < binNum.length; n++) {
            ArrayList<Integer> res = AdderNetwork.toBinary(n);
            Assert.assertEquals(binNum[n].size(), res.size());
            for (int i = 0; i < res.size(); i++) {
                Assert.assertEquals(binNum[n].get(i), res.get(i));
            }
        }
    }
}