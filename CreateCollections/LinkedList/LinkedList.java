package main.LinkedList;

public class LinkedList {
    private Node head;
    private Node tail;

    public LinkedList(){
        this.head=new Node("Head");
        tail=head;
    }
    public Node head(){
        return head;
    }
    public void add(Node node){
        tail.next=node;
        tail=node;
    }


    public static class Node{
        private Node next;
        private String data;

        public Node(String data){
            this.data=data;
        }

        public Node getNext() {
            return next;
        }

        public String toString() {
            return this.data;
        }
    }

    public static void main(String[] args) {
        LinkedList linkedList=new LinkedList();
        LinkedList.Node head=linkedList.head();
        linkedList.add(new LinkedList.Node("1"));
        linkedList.add(new LinkedList.Node("2"));
        linkedList.add(new LinkedList.Node("3"));
        linkedList.add(new LinkedList.Node("4"));
        linkedList.add(new LinkedList.Node("5"));
        linkedList.add(new LinkedList.Node("6"));
        linkedList.add(new LinkedList.Node("7"));
        linkedList.add(new LinkedList.Node("8"));

        LinkedList.Node current = head;
        int lenght=0;
        LinkedList.Node middle=head;

        while (current.getNext()!=null){
            lenght++;
            if (lenght%2==0){
                middle=middle.getNext();
            }
            current=current.getNext();
        }
        if(lenght%2==1){
            middle=middle.getNext();
        }
        System.out.println("length is: "+lenght);
        System.out.println("middle is: "+middle);
    }
}
