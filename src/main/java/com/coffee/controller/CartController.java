package com.coffee.controller;

import com.coffee.dto.CartItemDto;
import com.coffee.dto.CartProductDto;
import com.coffee.entity.Member;
import com.coffee.service.CartProductService;
import com.coffee.service.CartService;
import com.coffee.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/insert")
    public ResponseEntity<String> addToCart(
            @RequestBody CartProductDto dto,
            Authentication authentication
    ) {
        try {
            return ResponseEntity.ok(cartService.addProductToCart(dto, authentication.getName()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    private final MemberService memberService ;

    @GetMapping("/list")
    public ResponseEntity<?> getCartProducts(Authentication authentication) {

        String email = authentication.getName();

        Member member = memberService.findByEmail(email);
        try{
            return ResponseEntity.ok(cartService.getCartItemsByMemberId(member.getId()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 내의 특정 상품 수량 변경
    private final CartProductService cartProductService;

    @PatchMapping("/edit/{cartProductId}")
    public ResponseEntity<String> editCartProductQuantity(
            @PathVariable Long cartProductId,
            @RequestParam(required = false) Integer quantity) {

        System.out.println("카트 상품 아이디 : " + cartProductId);
        System.out.println("변경할 갯수 : " + quantity);

        try{
            return ResponseEntity.ok(cartProductService.editCartProductQuantity(cartProductId, quantity));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{cartProductId}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable Long cartProductId){
        System.out.println("삭제할 카트 상품 아이디 : " + cartProductId);

        cartProductService.deleteCartProductById(cartProductId);

        return ResponseEntity.ok("카트 상품 " + cartProductId + "번이 장바구니 목록에서 삭제 되었습니다.") ;
    }

    @DeleteMapping("/deleteList")
    public ResponseEntity<String> deleteCartProducts(@RequestBody List<Long> cartProductId){

        log.info("====================> {}", cartProductId);
        log.info("====================> {}", cartProductId);
        log.info("====================> {}", cartProductId);

        cartProductId.forEach(cartProductService::deleteCartProductById);

        return ResponseEntity.ok("카트 상품 " + cartProductId + "번이 장바구니 목록에서 삭제 되었습니다.") ;
    }

}
