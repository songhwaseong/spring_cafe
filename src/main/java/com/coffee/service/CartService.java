package com.coffee.service;

import com.coffee.dto.CartItemDto;
import com.coffee.dto.CartProductDto;
import com.coffee.entity.*;
import com.coffee.repository.CartRepository;
import com.coffee.repository.MemberRepository;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    // 카트에 상품 담기 로직
    private final CartRepository cartRepository ;
    private final MemberService memberService ;
    private final ProductService productService ;
    private final CartProductService cartProductService ;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    private CartProduct findExistingProduct(Cart cart, Product product) {

        return cart.getCartProducts() != null
                ? cart.getCartProducts()
                    .stream()
                    .filter(p-> p.getProduct() != null)
                    .filter(p-> Objects.equals(p.getProduct().getId(), product.getId()))
                    .findFirst()
                    .orElse(null)
                : null;
    }
    // import org.springframework.transaction.annotation.Transactional;
    @Transactional
    public String addProductToCart(CartProductDto dto, String email) throws Exception {

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new RuntimeException("회원 없음");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("상품 없음"));

//        // 1. 회원 및 상품 검증
//        Optional<Member> memberOptional = memberService.findMemberById(dto.getMemberId());
//        Optional<Product> productOptional = productService.findProductById(dto.getProductId());
//
//        if (memberOptional.isEmpty() || productOptional.isEmpty()) {
//            throw new IllegalArgumentException("회원 또는 상품 정보가 올바르지 않습니다.");
//        }
//
//        Member member = memberOptional.get();
//        Product product = productOptional.get();

        // 2. 재고 확인
//        if (product.getStock() < dto.getQuantity()) {
//            throw new IllegalArgumentException("재고 수량이 부족합니다.");
//        }

        // 3. Cart 조회 또는 생성
        Cart cart = cartRepository.findByMember(member).orElse(null);
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setMember(member);
            cart = saveCart(newCart);
        }

        // 4. 기존 상품 있는지 확인 후 수량 처리
        CartProduct existingCartProduct = findExistingProduct(cart, product);
        if (existingCartProduct != null) {
            // 2. 재고 확인
            if (product.getStock() < existingCartProduct.getQuantity() + dto.getQuantity()) {
                throw new IllegalArgumentException("재고 수량이 부족합니다.");
            }

            existingCartProduct.setQuantity(existingCartProduct.getQuantity() + dto.getQuantity());
            cartProductService.saveCartProduct(existingCartProduct);
        } else {
            cartProductService.saveCartProduct(CartProduct
                    .builder()
                    .cart(cart)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build());
        }

        return "요청하신 상품이 장바구니에 추가되었습니다.";
    }

    /* 특정 회원이 가지고 있는 카트 상품 목록을 조회해주는 메소드입니다. */
    public List<CartItemDto> getCartItemsByMemberId(Long memberId) throws  Exception {
        // 1. 회원 조회
        Member member = memberService.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 회원입니다."));

        // 2. 회원의 카트 조회
        Cart cart = cartRepository.findByMember(member)
                .orElseGet(Cart::new); // 없으면 빈 카트 생성

        // 3. CartProduct → CartItemDto 변환
        return cart.getCartProducts() != null
                ? cart.getCartProducts().stream()
                       .map(CartItemDto::new)  //생성자를 통해서 값을넘김
                       .toList()
                : null
                ;
    }
}
