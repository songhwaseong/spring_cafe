package com.coffee.service;

import com.coffee.entity.CartProduct;
import com.coffee.entity.Order;
import com.coffee.entity.Product;
import com.coffee.repository.CartProductRepository;
import com.coffee.repository.OrderRepository;
import com.coffee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository ;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    /*상품 목록 가져 오기*/
    public List<Product> getProductList() {
        return this.productRepository.findProductByOrderByIdDesc();
    }

    /*상품 삭제 기능*/
    public boolean deleteProduct(Long id) throws DataIntegrityViolationException {

        List<CartProduct> cpList =  cartProductRepository.findAll();
        if(cpList.stream().filter(p-> p.getProduct() != null).anyMatch(p-> p.getProduct().getId().equals(id))){
            throw new DataIntegrityViolationException("카트에 해당상품 담겨있음");
        }

        List<Order> orderList = orderRepository.findAll();
        if(orderList.stream().map(Order::getOrderProducts).anyMatch(p-> p.stream().anyMatch(q -> q.getProduct().getId().equals(id)))){
            throw new DataIntegrityViolationException("주문내역에 해당 상품이 있어서 삭제 불능");
        }

        // 1. 상품 조회 (한 번만 조회)
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return false;
        }
        // 2. 이미지 삭제 (있으면)
        String fileName = product.getImage();

        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(productImageLocation + fileName);

            System.out.println("삭제될 이미지 이름: " + file.getName());

            if (file.exists()) {
                boolean deleted = file.delete();

                if (!deleted) {
                    System.out.println("이미지 삭제 실패: " + fileName);
                }
            }
        }

        // 3. DB 삭제 (항상 실행)
        productRepository.deleteById(id);

        return true;
    }

    /* 상품 등록 기능 */
    // import org.springframework.beans.factory.annotation.Value;
    // 상품 등록하기
    @Value("${productImageLocation}")
    private String productImageLocation; // 기본 값 : null

    // Base64 인코딩 문자열을 변환하여 이미지로 만들고, 저장해주는 메소드입니다.
    private String saveProductImage(String base64Image) {
        // 데이터 베이스와 이미지 경로에 저장될 이미지의 이름
        // 현재 시각을 '년월일시분' 포맷으로 변환 (예: 202510171430)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = LocalDateTime.now().format(formatter);

        // 데이터 베이스와 이미지 경로에 저장될 이미지의 이름
        String imageFileName = "product_" + formattedNow + ".jpg";

        // String 클래스 공부 : endsWith(), split() 메소드

        File imageFile = new File(productImageLocation  + imageFileName);
        System.out.println("이미지 이름1 : "+imageFileName);
        System.out.println("이미지 이름2 : "+imageFile.getName());

        // base64Image : JavaScript FileReader API에 만들어진 이미지입니다.
        // 메소드 체이닝 : 점을 연속적으로 찍어서 메소드를 계속 호출하는 것
        byte[] decodedImage = null;

        if(base64Image != null && base64Image.split(",").length > 1) {
            decodedImage = Base64.getDecoder().decode(base64Image.split(",")[1]);
        }


        // FileOutputStream는 바이트 파일을 처리해주는 자바의 Stream 클래스
        // 파일 정보를 byte 단위로 변환하여 이미지를 복사합니다.
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(decodedImage);
            return imageFileName;
        } catch (Exception e) {
            throw new IllegalStateException("이미지 파일 저장 중 오류가 발생했습니다.");
        }
    }

    public Product insertProduct(Product product) {

        if (product == null ||  product.getImage() == null || !product.getImage().startsWith("data:image")) return null;

        product.setImage(saveProductImage(product.getImage()));

        //product.setInputdate(LocalDate.now());
        System.out.println("서비스)상품 등록 정보");
        System.out.println(product);

        // save() 메소드는 CrudRepository에 포함되어 있습니다.
        return productRepository.save(product);
    }

    /* 상품 수정 기능 */
    // 상품 수정하기 get 방식 시작
    public Product getProductById(Long id) {
        // findById() 메소드는 CrudRepository에 포함되어 있습니다.
        // 그리고, Optional<>을 반환합니다.
        // Optional : 해당 상품이 있을 수도 있지만, 경우에 따라서 없을 수도 있습니다.
        Optional<Product> product = this.productRepository.findById(id);

        // 의미 있는 데이터이면 그냥 넘기고, 그렇지 않으면 null을 반환해 줍니다.
        return product.orElse(null);
    }

    // 상품 수정하기 put 방식 시작
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // 이전 이미지 파일을 삭제하는 메소드
    private void deleteOldImage(String oldImageFileName) {
        if (oldImageFileName == null || oldImageFileName.isBlank()) return;

        File oldImageFile = new File(productImageLocation + oldImageFileName);

        if(!(oldImageFile.exists() && oldImageFile.delete())) System.err.println("기존 이미지 삭제 실패 : " + oldImageFileName);
    }

    // Product 수정
    public void updateProduct(Product savedProduct, Product updatedProduct) {

        String imageFileName = savedProduct.getImage();
        if (updatedProduct.getImage() != null && updatedProduct.getImage().startsWith("data:image")){
            deleteOldImage(savedProduct.getImage());
            imageFileName = saveProductImage(updatedProduct.getImage());
        }

        savedProduct = Product.builder()
                        .id(updatedProduct.getId())
                        .name(updatedProduct.getName())
                        .price(updatedProduct.getPrice())
                        .category(updatedProduct.getCategory())
                        .stock(updatedProduct.getStock())
                        .description(updatedProduct.getDescription())
                        .image(imageFileName)
                        .build();
        productRepository.save(savedProduct);
    }

    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }
}
