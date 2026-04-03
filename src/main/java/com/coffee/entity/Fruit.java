package com.coffee.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@Entity
public class Fruit {
    @Id
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
