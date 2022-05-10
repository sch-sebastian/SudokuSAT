package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PowerSet<T extends Comparable> {

    ArrayList<ArrayList<ArrayList<T>>> lengthSets;
    ArrayList<ArrayList<T>> sets;

    public PowerSet(T[] elements) {
        lengthSets = generate(elements.length, elements);
        sets = new ArrayList<>();
        sets.add(new ArrayList<>());
        for (ArrayList<ArrayList<T>> set : lengthSets) {
            sets.addAll(set);
        }
    }


    private ArrayList<ArrayList<ArrayList<T>>> generate(int length, T[] elements) {
        if (length == 1) {
            ArrayList<ArrayList<ArrayList<T>>> pS = new ArrayList<>();
            pS.add(new ArrayList<>());
            ArrayList<ArrayList<T>> curLength = new ArrayList<>();
            for (T element : elements) {
                ArrayList<T> cur = new ArrayList<T>();
                cur.add(element);
                curLength.add(cur);
            }
            pS.add(curLength);
            return pS;
        } else {
            ArrayList<ArrayList<ArrayList<T>>> partial = generate(length - 1, elements);
            ArrayList<ArrayList<T>> curLength = new ArrayList<>();
            for (ArrayList<T> tuple : partial.get(partial.size() - 1)) {
                for (T element : elements) {
                    if (element.compareTo(tuple.get(tuple.size() - 1)) > 0) {
                        ArrayList<T> cur = (ArrayList<T>) tuple.clone();
                        cur.add(element);
                        curLength.add(cur);
                    }
                }
            }
            partial.add(curLength);
            return partial;
        }
    }

    public static HashMap<Integer, ArrayList<ArrayList<Integer>>>[] getSumCombi(Integer[] numbers) {
        PowerSet<Integer> pS = new PowerSet<>(numbers);

        HashMap<Integer, ArrayList<ArrayList<Integer>>>[] sumCombi = new HashMap[numbers.length + 1];
        for (int i = 0; i < sumCombi.length; i++) {
            sumCombi[i] = new HashMap<>();
        }
        for (ArrayList<Integer> set : pS.sets) {
            Integer sum = 0;
            for (Integer element : set) {
                sum = sum + element;
            }
            if (!sumCombi[set.size()].containsKey(sum)) {
                sumCombi[set.size()].put(sum, new ArrayList<>());
            }
            sumCombi[set.size()].get(sum).add(set);
        }
        return sumCombi;
    }

}
