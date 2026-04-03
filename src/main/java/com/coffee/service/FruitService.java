package com.coffee.service;

import com.coffee.entity.Fruit;
import com.coffee.repository.FruitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FruitService {
    @Autowired
    FruitRepository fruitRepository;

    public List<Fruit> getFruitList (){
        return fruitRepository.findAll();
    }
}
