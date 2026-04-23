package com.coffee.controller;

import com.coffee.constant.Category;
import com.coffee.dto.SearchDto;
import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService ;

//    @GetMapping("/list") // 상품 목록을 List 컬렉션으로 반환해 줍니다.
//    public List<Product> list(){
//        return this.productService.getProductList() ;
//    }

    // 클라이언트가 특정 상품 id에 대하여 "삭제" 요청을 하였습니다.
    // @PathVariable는 URL의 경로 변수를 메소드의 매개 변수로 값을 전달해 줍니다.
    @DeleteMapping("/delete/{id}") // {id}는 경로 변수라고 하며, 가변 매개 변수로 이해하면 됩니다.
    public ResponseEntity<String> delete(@PathVariable Long id){ // {id}으로 넘겨온 상품의 아이디가, 변수 id에 할당됩니다.
        try{
            boolean isDeleted = this.productService.deleteProduct(id);

            if(isDeleted){
                return ResponseEntity.ok(id + "번 상품이 삭제 되었습니다.");
            }else{
                return ResponseEntity.badRequest().body(id + "번 상품이 존재하지 않습니다.");
            }
        }catch (DataIntegrityViolationException err){       //DB 무결성오류시 (foreignKey 같이 제약조건 걸린 데이터 삭제시 exp 발생)
            //String message = "DB 데이터 무결성 오류 \n 관리자 문의바람.";
            return ResponseEntity.badRequest().body(err.getMessage());

        }catch (Exception err){
            return ResponseEntity.internalServerError().body("오류 발생 : " + err.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody Product product, BindingResult bindingResult) {
        // ✅ 1. 유효성 검사 실패 시
        if (bindingResult.hasErrors() || !product.getImage().startsWith("data:image") || product.getStock() < 10  || product.getStock() > 1000) {
            System.out.println(bindingResult);
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            if(!product.getImage().startsWith("data:image")){
                errors.put("image", "이미지 파일을 업로드 부탁드립니다. .jpg, .jpeg .png 등");
            }

            if(product.getStock() < 10 || product.getStock() > 1000){
                errors.put("stock", "재고 수량은 10개 이상 1000개 이하 이어야 합니다.");
            }

            // 400 Bad Request + 에러 메시지
            return new ResponseEntity<>(
                    Map.of(
                            "message", "상품 등록 유효성 검사에 문제가 있습니다.",
                            "errors", errors
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // ✅ 2. 상품 등록 시도
        String message = "";
        String error = "";
        try {
            Product savedProduct = productService.insertProduct(product);
            return ResponseEntity.ok(Map.of(
                    "message", "Product insert successfully",
                    "image", savedProduct.getImage()
            ));
        } catch (IllegalStateException ex) { // 경로 또는 이미지 저장 문제
            message =  ex.getMessage();
            error =  "File saving error";
        } catch (NullPointerException ex) { // 경로 또는 이미지 저장 문제
            message =  ex.getMessage();
            error =  "bad image file Format";
        }  catch (Exception err) { // DB 오류 등
            message =  err.getMessage();
            error =  "Internal Server Error";
        }
        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "message", message,
                        "error", error
                ));
    }

    // 상품 수정 페이지 get 방식
    // 프론트 앤드의 상품 수정 페이지에서 요청이 들어 왔습니다.
    @GetMapping("/update/{id}") // 상품의 id 정보를 이용하여 해당 상품 Bean 객체를 반환해 줍니다.
    public ResponseEntity<?> getUpdate(@PathVariable Long id){
        System.out.println("수정할 상품 번호 : " + id);

        Optional<Product> product = this.productService.findById(id) ;

        return product.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("item","no"))
                : ResponseEntity.ok(product.get());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable Long id,
                                       @Valid @RequestBody Product updatedProduct,
                                       BindingResult bindingResult) {
        // 1. 유효성 검사
        if (bindingResult.hasErrors() || (updatedProduct.getImage().startsWith("data") && !updatedProduct.getImage().startsWith("data:image")) || updatedProduct.getStock() < 10  || updatedProduct.getStock() > 1000) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
            }
            if(updatedProduct.getImage().startsWith("data") && !updatedProduct.getImage().startsWith("data:image")){
                errors.put("image", "이미지 파일을 업로드 부탁드립니다. .jpg, .jpeg .png 등");
            }
            if(updatedProduct.getStock() < 10 || updatedProduct.getStock() > 1000){
                errors.put("stock", "재고 수량은 10개 이상 1000개 이하 이어야 합니다.");
            }

            // 400 Bad Request + 에러 메시지
            return new ResponseEntity<>(
                    Map.of(
                            "message", "상품 등록 유효성 검사에 문제가 있습니다.",
                            "errors", errors
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        // 2. 상품 조회
        Optional<Product> findProduct = productService.findById(id);

        if (findProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Product savedProduct = findProduct.get();
            productService.updateProduct(savedProduct, updatedProduct);

            return ResponseEntity.ok(Map.of("message", "상품 수정 성공"));

        } catch (Exception err) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", err.getMessage(),
                            "error", "상품 수정 실패"
                    ));
        }
    }

    @GetMapping("/detail/{id}") // 프론트 엔드가 상품에 대한 상세 정보를 요청하였습니다.
    public ResponseEntity<?> detail(@PathVariable Long id){
        Optional<Product> product = this.productService.findById(id) ;

        return product.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("item","no"))
                : ResponseEntity.ok(product.get());
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Product>> listProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "all") String searchDateType,
            @RequestParam(defaultValue = "ALL") Category category,
            @RequestParam(defaultValue = "") String searchMode ,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "Id") String orderBy
    ){
        SearchDto searchDto = SearchDto.builder()
                                        .searchMode(searchMode)
                                        .searchDateType(searchDateType)
                                        .category(category)
                                        .searchKeyword(searchKeyword)
                                        .orderBy(orderBy)
                                        .build();

        Page<Product> products = productService.listProducts(searchDto, pageNumber, pageSize) ;

        System.out.println("검색 조건 : " + searchDto);
        System.out.println("총 상품 개수 : " + products.getTotalElements());
        System.out.println("총 페이지 번호 : " + products.getTotalPages());
        System.out.println("현재 페이지 번호 : " + products.getNumber());

        // Http 응답 코드 200과 함께 상품 정보를 json 형태로 반환해 줍니다.
        return ResponseEntity.ok(products) ;
    }

    @GetMapping("") // 홈 페이지에 보여줄 큰 이미지들에 대한 정보를 읽어 옵니다.
    public List<Product> getBigsizeProducts(@RequestParam(required = false) String filter){
        return productService.getProductsByFilter(filter) ;
    }
}

