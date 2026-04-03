package com.coffee.service;

import com.coffee.entity.Coffee;
import com.coffee.entity.Fruit;
import com.coffee.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoffeeService {
    @Autowired
    CoffeeRepository coffeeRepository;

    public Coffee getCoffee (Integer id){
        return coffeeRepository.findById(id).orElse(new Coffee());
    }

    public List<Coffee> getCoffeeList (){
        return coffeeRepository.findAll();
    }
}
