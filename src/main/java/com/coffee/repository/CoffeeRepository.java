package com.coffee.repository;

import com.coffee.entity.Coffee;
import com.coffee.entity.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeRepository extends JpaRepository<Coffee, Integer> {
}
