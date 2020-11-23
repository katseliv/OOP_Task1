package game.Objects;

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
    private int size = 0;

    public void add(T value) {
        if (head == null) {
            head = tail = new ListNode(value, null);
        } else {
            tail.next = new ListNode(value, null);
            tail = tail.next;
            tail.next = head;
        }
        size++;
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
        size--;
        return result;
    }

    public void remove(T value) {
        if (head == null)       //если список пуст -
            return;             //ничего не делаем

        if (head == tail) {     //если список состоит из одного элемента
            head = null;        //очищаем указатели начала и конца
            tail = null;
            size--;
            return;             //и выходим
        }

        if (head.value == value) {  //если первый элемент - тот, что нам нужен
            head = head.next;       //переключаем указатель начала на второй элемент
            size--;
            return;                 //и выходим
        }

        ListNode element = head;                //иначе начинаем искать
        while (element.next != null) {          //пока следующий элемент существует
            if (element.next.value == value) {  //проверяем следующий элемент
                if (tail == element.next) {     //если он последний
                    tail = element;             //то переключаем указатель на последний элемент на текущий
                }
                element.next = element.next.next; //найденный элемент выкидываем
                size--;
                return;                           //и выходим
            }
            element = element.next;               //иначе ищем дальше
        }

    }

    public int getSize() {
        return size;
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

            @Override
            public void remove() {

            }
        };
    }

}
