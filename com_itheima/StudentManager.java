package com_itheima;

import java.util.ArrayList;
import java.util.Scanner;

public class StudentManager {
    public static void main(String[] args) {
        //创建集合对象，用于存储学生数据
        ArrayList<Student> array = new ArrayList<Student>();
        while (true) {
            System.out.println("--------欢迎来到学生管理系统--------");
            System.out.println("1 添加学生");
            System.out.println("2 删除学生");
            System.out.println("3 修改学生");
            System.out.println("4 查看所有学生");
            System.out.println("5 退出系统");
            System.out.print("请输入你的选择：");
            Scanner sc = new Scanner(System.in);
            String line = sc.nextLine();
            switch (line) {
                case "1":
                    //System.out.println("添加学生");
                    addStudent(array);
                    break;
                case "2":
                    //System.out.println("删除学生");
                    deleteStudent(array);
                    break;
                case "3":
                    //System.out.println("修改学生");
                    upDateStudent(array);
                    break;
                case "4":
                    //System.out.println("查看所有学生");
                    findAllStudent(array);
                    break;
                case "5":
                    System.out.println("谢谢使用");
//                  break；
                    System.exit(0);//JVM退出
            }
        }
    }

    public static void addStudent(ArrayList<Student> array) {
        //键盘录入学生对象所需要的数据，显示提示信息，提示要输入何种信息
        Scanner sc = new Scanner(System.in);
        //为了能让sid在while循环外面被访问到，将其定义在循环外侧
        String sid;

        //为了能让程序回到这里，使用while循环实现
        while (true) {
            System.out.println("请输入学生学号：");
            sid = sc.nextLine();
            //调用判断是否重复的方法
            boolean flag = isUsed(array, sid);
            if (flag) {
                System.out.println("你输入的学号已经被使用，请重新输入");
            } else {
                break;
            }
        }
        System.out.println("请输入学生姓名：");
        String name = sc.nextLine();
        System.out.println("请输入学生年龄：");
        String age = sc.nextLine();
        System.out.println("请输入学生居住地：");
        String address = sc.nextLine();
        //创建学生对象，把键盘录入的数据赋值给学生对象的成员变量
        Student s = new Student();
        s.setSid(sid);
        s.setName(name);
        s.setAge(age);
        s.setAddress(address);
        //将学生对象添加到集合中
        array.add(s);
        //给出添加成功提示
        System.out.println("添加学生成功！");

    }

    public static boolean isUsed(ArrayList<Student> array, String sid) {
        //如果与集合中某一个学生学号相同，返回true；如果都不相同，则返回false；
        boolean flag = false;
        for (int i = 0; i < array.size(); i++) {
            Student s = array.get(i);
            if (s.getSid().equals(sid)) {
                flag = true;
            }
        }

        return flag;

    }

    public static void findAllStudent(ArrayList<Student> array) {
        //判断集合中是否有数据，没有给出提示
        if (array.size() == 0) {
            System.out.println("无信息，请先添加信息在查询");
            //为了让程序不再执行，给出return；
            return;
        }
        //显示表头信息
        // \t 用于缩进
        System.out.println("学号\t\t\t姓名\t\t年龄\t\t居住地");
        //将集合中的数据取出用于遍历，年龄补充显示“岁”
        for (int i = 0; i < array.size(); i++) {
            Student s = array.get(i);
            System.out.println(s.getSid() + "\t" + s.getName() + "\t" + s.getAge() + "岁\t\t" + s.getAddress());
        }
    }

    public static void deleteStudent(ArrayList<Student> array) {
        //键盘录入要删除学生的学号，显示提示信息
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要删除学生的学号:");
        String sid = sc.nextLine();
        //遍历集合将对应学生从集合中删除
        for (int i = 0; i < array.size(); i++) {
            Student s = array.get(i);
            if (s.getSid().equals(sid)) {
                array.remove(i);
                System.out.println("删除成功！");
                break;
            } /*else if (array.size() == 0) {
                System.out.println("无任何学生信息！");
            } else {
                System.out.println("请检查输入的学生信息是否准确无误");
            }
            */
        }
    }

    public static void upDateStudent(ArrayList<Student> array) {
        //键盘录入要修改学生的学号
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要修改学生的学号");
        String sid = sc.nextLine();
        //键盘录入要修改的学生信息
        System.out.println("请输入学生新姓名");
        String name = sc.nextLine();
        System.out.println("请输入学生新年龄");
        String age = sc.nextLine();
        System.out.println("请输入学生新居住地");
        String address = sc.nextLine();
        //创建学生对象
        Student s = new Student();
        s.setSid(sid);
        s.setAddress(address);
        s.setAge(age);
        s.setName(name);
        //遍历集合修改对应学生的信息、
        for (int i = 0; i < array.size(); i++) {
            Student student = array.get(i);
            if (student.getSid().equals(sid)) {
                array.set(i, s);
                break;
            }
        }
        System.out.println("修改学生信息成功");
    }
}
