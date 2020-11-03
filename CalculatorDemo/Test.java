package CalculatorDemo;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        double a, b;
        char x;
        System.out.println("请输入算式。且运算符和数字之间以空格隔开：");
        Scanner sc = new Scanner(System.in);
        a = sc.nextDouble();
        x = sc.next().charAt(0);
        b = sc.nextDouble();
        Operator o = new Operator();
        o.start(a, x, b);
    }
}
