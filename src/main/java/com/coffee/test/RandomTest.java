package com.coffee.test;

import java.util.Random;

public class RandomTest {
    public static void main(String[] args) {
        Random rand = new Random();
        boolean bool = rand.nextBoolean();
        System.out.println(bool);

        int jusa = rand.nextInt(1,7);
        System.out.println(jusa);

        String[] menu = {"제육","돈가스","오무라이스","떡볶이"};

        String msg = "오늘 점심 메뉴 : " + menu[rand.nextInt(menu.length)];
        System.out.println(msg);

        int price = 1000 * rand.nextInt(3, 8);  //3000원이상 7000원이하
        System.out.println("가격 : "+ price);

    }
}
