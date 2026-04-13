package com.coffee.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Category {
    ALL("전체"), BREAD("빵"), BEVERAGE("음료수"), CAKE("케이크"), MACARON("마카롱"), CAT("고양이") ;

    private final String description ;

    Category(String description) {
        this.description = description.isEmpty() ? "전체" : description ;
    }


    // Controller에서 RequestBody로 값을 받는데 Enum에 없는 값을 RequestBody에 입력했을 때 json parsing error 로 아래와 같이
    // 해당하는 값이 없을경우 null 값으로 유효성 검사 처리
    @JsonCreator
    public static Category getEnumFromValue(String value) {
        try {
            return Category.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

}
