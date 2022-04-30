package main.java;

public class UniqueLinkedList<T> {
    private UniqueLikedListNode<T> head;
    private UniqueLikedListNode<T> tail;
    public int size;

    public UniqueLinkedList() {
        size = 0;
    }

    private class UniqueLikedListNode<T>{
        T value;
        UniqueLikedListNode<T> child;

        public UniqueLikedListNode(T value) {
            this.value = value;
        }
    }

    public boolean add(T value){
        if(contains(value)){
            return false;
        }
        UniqueLikedListNode<T> fresh = new UniqueLikedListNode<>(value);

        if(head == null){
            head = fresh;
            tail =  fresh;
        }else{
            tail.child = fresh;
            tail =  tail.child;
        }
        size++;
        return true;
    }

    public T poll(){
        if(head == null){
            return null;
        }else{
            T res = head.value;
            if(head == tail){
                head = null;
                tail = null;
            }else{
                head = head.child;
            }
            size--;
            return res;
        }
    }

    public boolean contains(T value){
        if(head == null){
            return false;
        }
        UniqueLikedListNode<T> cur = head;
        while (cur != null){
            if(cur.value.equals(value)){
                return true;
            }
            cur = cur.child;
        }
        return false;
    }

    public T get(T value){
        UniqueLikedListNode<T> cur = head;
        while (cur != null){
            if(cur.value.equals(value)){
                return cur.value;
            }
            cur = cur.child;
        }
        return null;
    }




}
