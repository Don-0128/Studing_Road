package com_itheima03;

public class Box {
    private int milk;
    private boolean isEmpty = false;

    public synchronized void putMilk(int milk) {
        if(isEmpty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.milk = milk;
        System.out.println("送奶工将第" + this.milk + "瓶奶放入了奶箱");
        isEmpty = true;

        //唤醒其他线程
        notifyAll();
    }

    public synchronized void getMilk() {
        if (!isEmpty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("用户拿到了第" + this.milk + "瓶奶");
        isEmpty = false;

        //唤醒其他线程
        notifyAll();
    }
}
