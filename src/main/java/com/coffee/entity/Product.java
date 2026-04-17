package com.coffee.entity;

import com.coffee.constant.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.* ;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 상품 1개에 대한 정보를 저장하고 있는 자바 클래스
@Data
@Builder
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    // 엔터티 코딩 작성시 database의 제약 조건도 같이 고려해야 합니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id ;

    @Column(nullable = false) // 값 입력 getImage
    @NotBlank(message = "상품 이름은 필수 입력 사항입니다.")
    private String name ;

    @Column(nullable = false)
    @Min(value = 100, message = "가격은 100원이상이어야 합니다.") // cf) @Max
    @Max(value = 100000, message = "가격은 100000원이하이어야 합니다.")
    private int price ;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "카테고리는 반드시 선택해야 합니다.")
    private Category category;

    @Column(nullable = false)
    //@Min(value = 10, message = "재고 수량은 10개 이상이어야 합니다.")
    //@Max(value = 1000, message = "재고 수량은 1000개 이하이어야 합니다.")
    private int stock ;

    @Column(nullable = false)
    @NotBlank(message = "이미지는 필수 입력 사항입니다.")
    private String image ;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "상품 설명은 필수 입력 사항입니다.")
    @Size(max = 1000, message = "상품에 대한 설명은 최대 1,000 자리 이하로만 입력해 주세요.")
    private String description ;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime inputdate ; // 등록 일자
}
