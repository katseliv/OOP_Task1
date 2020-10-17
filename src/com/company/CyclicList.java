package com.company;

import java.util.Iterator;

public class CyclicList<T> implements Iterable<T> {

    class ListNode {
        T value;
        ListNode next;

        ListNode(T value, ListNode next) {
            this.value = value;
            this.next = next;
        }

        ListNode() {
            this(null, null);
        }

        public ListNode getNext() {
            return next;
        }

        public T getValue() {
            return value;
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
        }
        count++;
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

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private ListNode current = head;
            private int currentCount = count;

            @Override
            public boolean hasNext() {
                return currentCount > 0;
            }

            @Override
            public T next() {
                T result = current.getValue();
                current = current.next;
                currentCount--;
                return result;
            }
        };
    }

}
