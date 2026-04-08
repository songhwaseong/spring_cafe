package com.coffee.repository;

import com.coffee.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByOrderByIdDesc(); // 상품 목록을 아이디 역순으로 조회하기
}
