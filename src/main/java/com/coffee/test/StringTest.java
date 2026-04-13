package com.coffee.test;

import com.coffee.entity.Product;

import java.util.Arrays;

public class StringTest {
    public static void main(String[] args) {
        String str = "Beare.hellow.World";

        System.out.println(str.substring("Beare ".length()));
        System.out.println(str.substring("Beare ".length(),"Beare ".length()+"hellow".length()));

        System.out.println(str +" toLowerCase " + str.toLowerCase());
        System.out.println(str +" toUpperCase " + str.toUpperCase());
        System.out.println(str +" replaceAll " + str.replaceAll("\\.", ""));  //.을 바꿀때 \\. 사용!!!
        System.out.println(str +" substring(0,3) " + str.substring(0,3));
        System.out.println(str +" substring(3) " + str.substring(3));
        System.out.println(str +" trim " + str.trim());
        System.out.println(str +" getBytes " + Arrays.toString(str.getBytes()));
        System.out.println(str +" charAt " + str.charAt(1));
        System.out.println(str +" length " + str.length());
        System.out.println(str +" startsWith " + str.startsWith("a"));
        System.out.println(str +" endsWith " + str.endsWith("e"));
        System.out.println(str +" indexOf " + str.indexOf("E"));
        System.out.println(str +" contains " + str.contains("d"));
        System.out.println(str +" repeat " + str.repeat(3));
        System.out.println(str +" join " + String.join(",", "a","b","c","d"));
        System.out.println(str.split("\\.")[0]);
        if(findProduct() && findProduct2()){
            System.out.println("확인 확인");
        }
    }

    public static boolean findProduct() {
        System.out.println("findProduct 실행");
        return false;
    }
    public static boolean findProduct2() {
        System.out.println("findProduct2 실행");
        return true;
    }
}
