package main.LinkedList;

public class LinkedListMiddleCopy {
    private Node head;
    private Node tail;
    private String data;
    public LinkedListMiddleCopy(){
        this.head=new Node(data);
        this.tail=head;
    }
    public void add(Node node){
      tail.next=node;
        tail=node;
    }
    public Node getHead() {
        return head;
    }

    private static class Node{
        private Node next;
        private String data;

        public Node(String data){
            this.data=data;
        }

        public Node getNext() {
            return next;
        }
        public String toString(){
            return this.data;
        }
    }

    public static void main(String[] args) {
        LinkedListMiddleCopy linkedList=new LinkedListMiddleCopy();
        LinkedListMiddleCopy.Node head=linkedList.head;
        linkedList.add(new LinkedListMiddleCopy.Node("1"));
        linkedList.add(new LinkedListMiddleCopy.Node("2"));
        linkedList.add(new LinkedListMiddleCopy.Node("3"));
        linkedList.add(new LinkedListMiddleCopy.Node("4"));
        linkedList.add(new LinkedListMiddleCopy.Node("5"));
        LinkedListMiddleCopy.Node current=head;
        LinkedListMiddleCopy.Node middle=head;
        int length=0;
        while (current.getNext()!=null) {
            length++;
            if (length % 2 == 0) {
                middle = middle.getNext();
            }
            current = current.getNext();
        }
            if(length%2==1){
                middle=middle.getNext();
            }

        System.out.println("lenght "+length);
        System.out.println("middle "+middle);
    }
}
