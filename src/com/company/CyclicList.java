package com.company;

import java.util.Iterator;

public class CyclicList<T> implements Iterable<T> {

    class ListNode {
        private T value;
        private ListNode next;

        ListNode(T value, ListNode next) {
            this.value = value;
            this.next = next;
        }

        ListNode() {
            this(null, null);
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public ListNode getNext() {
            return next;
        }

        public void setNext(ListNode next) {
            this.next = next;
        }

    }

    private ListNode head = null;
    private ListNode tail = null;
    private int count = 0;

    public void add(T value) {
        if (head == null) {
            head = tail = new ListNode(value, null);
        } else {
            tail.next = new ListNode(value, null);
            tail = tail.next;
            tail.next = head;
        }
        count++;
    }

    public void remove(T value){

    }

    public T get() throws Exception {
        if (head == null) {
            throw new Exception("Queue is empty!");
        }
        T result = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        count--;
        return result;
    }

    public int getCount() {
        return count;
    }

    public ListNode getHead() {
        return head;
    }

    public void setHead(ListNode head) {
        this.head = head;
    }

    public ListNode getTail() {
        return tail;
    }

    public void setTail(ListNode tail) {
        this.tail = tail;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private ListNode current = head;
            // 2 вариант: private int currentCount = count;

            @Override
            public boolean hasNext() {
                return current.getNext() != null;
                // 2 вариант: currentCount > 0
            }

            @Override
            public T next() {
                T result = current.getValue();
                current = current.next;
                // 2 вариант: currentCount--;
                return result;
            }
        };
    }

}
