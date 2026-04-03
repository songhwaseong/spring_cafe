package com.coffee.controller;

import com.coffee.entity.Coffee;
import com.coffee.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController // 컨트롤러는 특정 요청에 대한 처리를 수행해 줍니다.
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    @GetMapping("/api/coffee")
    public Coffee test(Integer id){

        //builder 사용

//        return Fruit
//                .builder()
//                .id("banana")
//                .name("바나나")
//                .price(1000)
//                .build() ;
        log.info("=======================> fruit");
        log.info("===> {}", coffeeService.getCoffee(id));
        return coffeeService.getCoffee(id);
    }

    @GetMapping("/api/coffee/list")
    public List<Coffee> test02(){
        log.info("=======================> list");
        return coffeeService.getCoffeeList() ;
    }
}
