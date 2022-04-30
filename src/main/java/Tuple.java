package main.java;

import java.lang.reflect.Array;

public class Tuple<T> {
    private T[] data;

    public Tuple(T head, T... tail){
        data =  (T[]) Array.newInstance(head.getClass() ,1+tail.length);
        data[0] = head;
        for(int i = 1; i<data.length; i++){
            data[i] = tail[i-1];
        }
    }

    public T get(int i){
        return data[i];
    }
}
