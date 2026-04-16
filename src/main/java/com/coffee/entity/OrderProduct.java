package com.coffee.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "order_products")
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct { // 한개의 `주문 상품`을 의미하는 자바 엔터티 클래스
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_product_id")
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order ; // 주문과 다대일 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product ; // 상품과 다대일 관계

    @Column(nullable = false)
    private int quantity ; // 주문 수량

    @Column(nullable = false)
    private int price ;
}
