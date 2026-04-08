package com.coffee.constant;

import lombok.Getter;

@Getter
public enum Category {
    ALL("전체"), BREAD("빵"), BEVERAGE("음료수"), CAKE("케이크"), MACARON("마카롱"), CAT("고양이") ;

    private final String description ;

    Category(String description) {
        this.description = description ;
    }

}
