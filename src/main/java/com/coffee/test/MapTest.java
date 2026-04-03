package com.coffee.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapTest {
    public static void main(String[] args) {
        Map<String , String> errors = new HashMap<>();
        errors.put("name", "John");
        errors.put("age", "23");

        errors.forEach((key, value) -> System.out.println(key + ": " + value));

        Map<String, String> tt = Map.of("name", "John", "age", "23");
    }
}
