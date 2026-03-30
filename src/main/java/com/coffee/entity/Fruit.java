package com.coffee.entity;


import lombok.*;

@Data
@Builder
public class Fruit {
    private String id;
    private String name;
    private int price;

    public Fruit(){}

    public Fruit(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
