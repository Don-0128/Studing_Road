package com_itheima03;

public class BoxDemo {
    public static void main(String[] args) {
        //创建奶箱对象，这是共享数据区域
        Box b = new Box();

        //创建生产者对象，把奶箱对象作为构造方法参数传递，因为在这个类中要调用存储牛奶的操作
        Producer p = new Producer(b);

        //创建消费者对象，把奶箱对象作为构造方法参数传递，因为在这个类中要调用获取牛奶的操作
        Customer c = new Customer(b);

        //创建两个线程，分别把消费者和生产者对象作为构造方法参数传递
        Thread t1 = new Thread(p);
        Thread t2 = new Thread(c);

        //启动线程
        t1.start();
        t2.start();

        //IllegalMonitorStateException：抛出以表示线程已尝试在对象的监视器上等待或通知其他线程等待对象的监视器，而不用有指定的监视器
        //即未同步

        //添加同步后未唤醒，需要唤醒其他线程
    }
}
