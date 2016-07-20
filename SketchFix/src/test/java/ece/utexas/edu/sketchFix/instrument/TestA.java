package ece.utexas.edu.sketchFix.instrument;

public class TestA
{
    public static void main(String[] args) {
        printOne();
        printOne();
        printTwo();
    }
    
    public static void printOne() {
        System.out.println("Hello World");
    }
    
    public static void printTwo() {
        printOne();
        printOne();
    }
}
