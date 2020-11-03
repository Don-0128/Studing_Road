package CalculatorDemo;

public class Operator {
    public void start(double a, char x, double b) {
        switch (x) {
            case '+':
                addition add = new addition();
                System.out.println("运算结果为：" + add.calculate(a, b));
                break;
            case '-':
                subtraction sub = new subtraction();
                System.out.println("运算结果为：" + sub.calculate(a, b));
                break;
            case '*':
                multiplication mul = new multiplication();
                System.out.println("运算结果为：" + mul.calculate(a, b));
                break;
            case '/':
                division div = new division();
                System.out.println("运算结果为：" + div.calculate(a, b));
                break;
            default:
                System.out.println("你输入的运算符号有误");
                break;
        }
    }
}
