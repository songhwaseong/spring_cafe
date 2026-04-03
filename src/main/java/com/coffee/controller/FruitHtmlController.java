package com.coffee.controller;
import com.coffee.constant.Role;
import com.coffee.entity.Fruit;
import com.coffee.entity.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller // 컨트롤러는 특정 요청에 대한 처리를 수행해 줍니다.
public class FruitHtmlController {

    @GetMapping("/fruit")
    public String test(Model model) {
        model.addAttribute("fruit", Fruit.builder().id("banana").name("바나나").price(1000).build());
        return "fruit" ;
    }

    @GetMapping("/fruitList")
    public String testList(Model model) {
        List<Fruit> fruitList = new ArrayList<>();
        for(int i=0;i< 100;i++) {
            fruitList.add(new Fruit("apple" + i, "사과" + i, 1000 + i));
        }
        model.addAttribute("fruitList", fruitList);
        return "fruitList" ;
    }
}
