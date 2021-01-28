package stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class PolishNotation {

    public static void main(String[] args) {
        //完成中缀转后缀的功能
        String expression = "1+((2+3)*4)-(5*1)";
        List<String> infixExpressionList = toInfixExpressionList(expression);
        List<String> parseSuffixExpressionList = parseSuffixExpression(infixExpressionList);
        System.out.println(parseSuffixExpressionList);
        System.out.println(calculate(parseSuffixExpressionList));

        //1.直接对string进行操作有诸多不便，因此先将string转成中缀表达式对应的的list
        //2.list转后缀表达式
        //List<String> list = toInfixExpressionList(expression);
        //System.out.println(list);

        //String suffixExpression = "4 5 * 8 - 60 + 8 2 / +";
        //1.先将"3 4 + 5 * 6 -" => 放入一个ArrayList中
        //2.将ArrayList传入一个方法，配合栈完成计算
        //3.Reverse Polish Notation
        //List<String> rpnList = getListString(suffixExpression);
        //System.out.println("rpnList= " + rpnList);
        //int res = calculate(rpnList);
        //System.out.println("The result is " + res);
    }

    //将逆波兰表达式的信息放入一个ArrayList中
    public static List<String> getListString(String suffixExpression) {
        //将suffixExpression分割
        String[] split = suffixExpression.split(" ");
        List<String> list = new ArrayList<String>();
        //使用了allAll方法代替了增强for
        Collections.addAll(list, split);
        return list;
    }

    //完成对逆波兰表达式的运算
    public static int calculate(List<String> ls) {
        //创建一个栈
        Stack<String> stack = new Stack<>();
        //遍历ls
        for (String item : ls) {
            //stack.push(item);
            //使用正则表达式取出数
            if (item.matches("\\d+")) {//匹配的是多位数
                stack.push(item);
            } else {
                //pop出两个数并运算，再入栈
                int num2 = Integer.parseInt(stack.pop());
                int num1 = Integer.parseInt(stack.pop());
                int res = 0;
                if (item.equals("+")) {
                    res = num1 + num2;
                } else if (item.equals("-")) {
                    res = num1 - num2;
                } else if (item.equals("*")) {
                    res = num1 * num2;
                } else if (item.equals("/")) {
                    res = num1 / num2;
                } else {
                    throw new RuntimeException("运算符有误！");
                }
                stack.push("" + res);
            }
        }
        //最后留在stack中的就是计算结果
        return Integer.parseInt(stack.pop());
    }

    //完成中缀转List
    public static List<String> toInfixExpressionList(String s) {
        //定义一个list存放中缀表达式对应的内容
        List<String> ls = new ArrayList<>();
        int i = 0;// 这是一个指针用于遍历中缀表达式字符串
        String str;// 用于对多位数的拼接操作
        char c;// 每遍历一个字符就放到c
        do {
            //非数字则直接加入ls
            if ((c = s.charAt(i)) < 48 || (c = s.charAt(i)) > 57) {
                ls.add("" + c);
                i++;
            } else {// 数字分为个位数和多位数
                str = "";
                while (i < s.length() && (c = s.charAt(i)) >= 48 && (c = s.charAt(i)) <= 57) {
                    str += c;
                    i++;
                }
                ls.add(str);
            }
        } while (i < s.length());
        return ls;
    }

    //完成list转后缀
    public static List<String> parseSuffixExpression(List<String> ls) {
        //初始化栈
        Stack<String> s1 = new Stack<>();// 符号栈
        List<String> s2 = new ArrayList<>();// 用AL代替只进不出的中间结果栈

        for (String item : ls) {
            if (item.matches("\\d+")) {
                s2.add(item);
            } else if (item.equals("(")) {
                s1.push(item);
            } else if (item.equals(")")) {
                //如果是右括号，则依次弹出s1的数据直到遇到左括号，此时将括号丢弃
                while (!s1.peek().equals("(")) {
                    s2.add(s1.pop());
                }
                s1.pop();// 将左括号弹出栈，消除括号
            } else {
                //当item的优先级小于等于栈顶运算符，将s1栈顶的运算符弹出加入s2中，
                while (s1.size() != 0 && Operation.getValue(s1.peek()) >= Operation.getValue(item)) {
                    s2.add(s1.pop());
                }
                s1.push(item);
            }
        }
        while (s1.size() != 0) {
            s2.add(s1.pop());
        }
        return s2;
    }
}

//编写一个类，可以返回运算符的优先级
class Operation {
    private static int ADD = 1;
    private static int SUB = 1;
    private static int MUL = 2;
    private static int DIV = 2;

    //写一个方法返回优先级对应的数字
    public static int getValue(String operation) {
        int result = 0;
        switch (operation) {
            case "+":
                result = ADD;
                break;
            case "-":
                result = SUB;
                break;
            case "*":
                result = MUL;
                break;
            case "/":
                result = DIV;
                break;
            default:
                System.out.println("不存在该运算符");
                break;
        }
        return result;
    }
}
