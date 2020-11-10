package Poker_02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

public class PokerDemo {
    public static void main(String[] args) {
        HashMap<Integer, String> pokers = new HashMap<>();
        ArrayList<Integer> array = new ArrayList<>();
        String[] colors = {"♦", "♣", "♥", "♠"};
        String[] numbers = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A","2"};
        int index = 0;
        for (String number : numbers) {
            for (String color : colors) {
                pokers.put(index, color + number);
                array.add(index);
                index++;
            }
        }
        pokers.put(index, "joker");
        array.add(index);
        index++;
        pokers.put(index, "JOKER");
        array.add(index);
        index++;
        Collections.shuffle(array);
        TreeSet<Integer> player1 = new TreeSet<>();
        TreeSet<Integer> player2 = new TreeSet<>();
        TreeSet<Integer> player3 = new TreeSet<>();
        TreeSet<Integer> Lord_poker = new TreeSet<>();
        for (int i = 0; i < array.size(); i++) {
            int poker = array.get(i);
            if (i >= array.size() - 3) {
                Lord_poker.add(poker);
            } else if (i % 3 == 0) {
                player1.add(poker);
            } else if (i % 3 == 1) {
                player2.add(poker);
            } else if (i % 3 == 2) {
                player3.add(poker);
            }
        }
        lookPoker("张三",player1,pokers);
        lookPoker("李四",player2,pokers);
        lookPoker("王五",player3,pokers);
        lookPoker("底牌",Lord_poker,pokers);
    }
    public static void lookPoker(String name, TreeSet<Integer> ts, HashMap<Integer,String> hm){
        System.out.print(name + "的牌是：");
        for (Integer key : ts){
            String poker = hm.get(key);
            System.out.print(poker + " ");
        }
        System.out.println();
    }

}
