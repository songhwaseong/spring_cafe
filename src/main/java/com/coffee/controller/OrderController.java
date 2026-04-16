package com.coffee.controller;

import com.coffee.constant.OrderStatus;
import com.coffee.constant.Role;
import com.coffee.dto.OrderDetailDto;
import com.coffee.dto.OrderDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /* 리액트에서 '주문하기' 버튼 클릭 시 호출되는 엔드포인트 */
    @PostMapping("")
    public ResponseEntity<?> order(@Valid @RequestBody OrderDto dto, BindingResult bindingResult) {
        // 1) 유효성 검사 결과 확인
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            // 400 Bad Request + 에러 메시지
            return new ResponseEntity<>(
                    Map.of(
                            "message", "주문에 문제가 있습니다.",
                            "errors", errors
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
        System.out.println("주문 요청 DTO: " + dto);

        // 핵심 로직은 서비스로 위임
        try {
            Order savedOrder = orderService.createOrder(dto);
            return ResponseEntity.ok("주문이 완료되었습니다. 주문 번호 : " + savedOrder.getId());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    // 특정한 회원의 주문 정보를 최신 날짜 순으로 조회합니다.
    // http://localhost:9000/order/list?memberId=회원아이디&role=USER
    @GetMapping("/list") // 리액트의 OrderList.js 파일 내의 useEffect 참조
    public ResponseEntity<List<OrderDetailDto>> getOrderList(@RequestParam Long memberId, @RequestParam Role role) {
        System.out.println("로그인 한 사람의 id : " + memberId);
        System.out.println("로그인 한 사람 역할 : " + role);


        return ResponseEntity.ok(orderService.getOrderListByRole(memberId, role));
    }

    // 관리자가 수행하는 주문된 상품에 대한 `완료` 처리 기능
    @PutMapping("/update/status/{orderId}")
    public ResponseEntity<String> statusChange(@PathVariable Long orderId, @RequestParam OrderStatus status){
        System.out.println("수정할 항목의 아이디 : " + orderId);
        System.out.println("변경하고자 하는 주문 상태 : " + status);

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    // `관리자` 또는 `당사자`가 주문에 대한 삭제 요청을 하였습니다.
    // 주문된 상품에 대한 `취소` 기능
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {

        try {
            // 서비스에 모든 비즈니스 로직을 위임
            String message = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
