package application;

import java.util.EmptyStackException;

public class MyStack {
	private int top;
	private int stackSize;
	private int front;
    private String[] array;
 
    public MyStack(int stackSize) {
        top = -1;
        front = -1;
        this.stackSize = stackSize;
        array = new String[this.stackSize];
    }
    
    public boolean isEmpty() {
    	if(front == top) {
    		front = -1;
    		top = -1;
    	}
        return (front == top);
    }
    
    
    public boolean isFull() {
        return (top == this.stackSize-1);
    }
    
    public void push(String item) {
        if(isFull()) {
            System.out.println("Stack is full!");
        } else {             
        	array[++top] = item;
            System.out.println("Inserted Item : " + item);
        }
    }
    
    public String pop() {
        if(isEmpty()) {
            System.out.println("Deleting fail! Stack is empty!");
            //throw new EmptyStackException();
            return null;
        } else { 
            System.out.println("Deleted Item : " + array[front+1]);
            //int num = top;
            //top--;
            front = (front + 1)%stackSize;
            return array[front];
        }
    }
    
    public String peek() {
        if(isEmpty()) {
            System.out.println("Peeking fail! Stack is empty!");
            //throw new EmptyStackException();
            return null;
        } else { 
            System.out.println("Peeked Item : " + array[top]);
            return array[top];
        }
    }
    
    public void clear() {
        if(isEmpty()) {
            System.out.println("Stack is already empty!");
        } else {
            top = -1;
            array = new String[this.stackSize];
            System.out.println("Stack is clear!");
        }
    }
    
    public void printStack() {
        if(isEmpty()) {
            System.out.println("Stack is empty!");
        } else {
            System.out.print("Stack elements : ");
            for(int i=0; i<=top; i++) {
                System.out.print(array[i] + " ");
            }
            System.out.println();
        }
    }
}
