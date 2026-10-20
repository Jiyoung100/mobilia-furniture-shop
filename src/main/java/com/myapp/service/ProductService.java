package com.myapp.service;

import com.myapp.entity.Product;
import com.myapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    // [ 상품 전체 조회 ]
    public List<Product> getProductList(){
        return productRepository.findAll();
    }

    // [ 메인 페이지 ] - 신제품 슬라이드
    public List<Product> getNewProductList(){ return productRepository.findTop10ByOrderByRegDateDesc(); }

    // [ 메인 페이지 ] - 브랜드별 슬라이드
    public List<Product> getBrandProductList(String brand) { return productRepository.findTop8ByBrandOrderByRegDateDesc(brand); }

    // [ 신상품 페이지 ]
    public List<Product> getNewProduct(){ return productRepository.findTop16ByOrderByRegDateDesc(); }

    // [ 상품 상세 조회 ]
    public Product getProductById(Long id){
        Optional<Product> product = productRepository.findById(id);
        return product.get();
    }

    // [ 관련 상품 조회 ]
    public List<Product> getProductListBySlider(String category2, Long id) {

        List<Product> productList = productRepository.findByCategory2AndIdNotOrderByIdDesc(category2, id);
        Collections.shuffle(productList);

        if(productList.size() > 10) {
            return productList.subList(0, 10);
        }

        return productList;
    }

    // [ 정렬 메서드 ] - 신제품, 낮은가격순, 높은가격순
    private Pageable getPageable(int page, String sort){
        Sort sortOption;

        if ("lowPrice".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.ASC, "price");
        } else if ("highPrice".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.DESC, "price");
        } else {
            sortOption = Sort.by(Sort.Direction.DESC, "regDate"); // 기본값 = 신제품순
        }
        return PageRequest.of(page, 12, sortOption);
    }

    // [ 상품 검색 페이지 ] - 페이징
    // 1. 공통 전체 페이징 (한 페이지당 12개, 상품 등록일 기준 내림차순)
    public Page<Product> getProductList(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "regDate"));
        return productRepository.findAll(pageable);
    }

    // 2. 검색 + 페이징 (검색어 없으면 전체상품표시)
    public Page<Product> searchProducts(String keyword, int page) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductList(page);
        }

        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "regDate"));
        String trimmed = keyword.trim();
        return productRepository.findByNameContainingOrCategory2Containing(trimmed, trimmed, trimmed, pageable);
    }

    // [ 모든 제품 페이지 ] - 정렬, 페이징
    public Page<Product> getAllProducts(int page, String sort) {
        Pageable pageable = getPageable(page, sort);
        return productRepository.findAll(pageable);
    }

    // [ 공간별 가구 페이지 ] - 카테고리별 정렬, 페이징
    // category1 전체 조회
    public Page<Product> getProductsByCategory1(int page, String sort, String category1) {
        Pageable pageable = getPageable(page, sort);
        return productRepository.findByCategory1(category1, pageable);
    }
    // categor1 > category2 조회
    public Page<Product> getProductsByCategory1AndCategory2(int page, String sort, String category1, String category2) {
        Pageable pageable = getPageable(page, sort);
        return productRepository.findByCategory1AndCategory2(category1, category2, pageable);
    }
    // category1에 들어갈 category2 list -> 페이지내 버튼 출력용
    public List<String> getCategory2List(String category1){
        return productRepository.findCategory2ByCategory1(category1);
    }

    // [ 브랜드 페이지 ] - 브랜드별 정렬, 페이징
    public Page<Product> getProductsByBrand(int page, String sort, String brand){
        Pageable pageable = getPageable(page, sort);
        return productRepository.findByBrand(brand, pageable);
    }

//######################################################################################################################
    // [ 어드민 영역 ]

    // 이미지 저장 경로 - application.yaml에 설정한 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    // [ 상품 등록 ]
    public void addProduct(Product product) throws IOException {
        // 이미지 저장 경로
        File dir = new File(uploadDir);

        // 업로드 폴더가 없으면
        if(!dir.exists()){ dir.mkdirs(); }

        // 이미지 저장 후 DB에 저장할 파일명
        product.setFileName1(saveFile(product.getProductImage1(), uploadDir));
        product.setFileName2(saveFile(product.getProductImage2(), uploadDir));
        product.setFileName3(saveFile(product.getProductImage3(), uploadDir));
        product.setFileName4(saveFile(product.getProductImage4(), uploadDir));
        // 상품 등록일 설정
        product.setRegDate(LocalDateTime.now());
        // DB에 저장
        productRepository.save(product);
    }

    // [ 상품 수정 ]
    public void updateProduct(Product product) throws IOException {
        // id조회, 없을 시 예외처리를 할 수 있는 orElseThrow함수 채택
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 존재하지 않습니다. id=" + product.getId()));

        // 수정 내용
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setCategory1(product.getCategory1());
        existingProduct.setCategory2(product.getCategory2());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCountry(product.getCountry());
        existingProduct.setStock(product.getStock());
        existingProduct.setSize(product.getSize());
        existingProduct.setMaterial(product.getMaterial());
        existingProduct.setCreateDate(product.getCreateDate());

        // 경로 설정
        File dir= new File(uploadDir);

        // 업로드 폴더가 없으면 폴더 생성
        if(!dir.exists()){ dir.mkdirs(); }

        // 수정 페이지에서 딱히 수정 할 사진이 없을 경우에는 기존에 있던 사진 그대로 유지하는 조건문
        if(product.getProductImage1() != null && !product.getProductImage1().isEmpty()) {
            existingProduct.setFileName1(saveFile(product.getProductImage1(), uploadDir));
        }
        if(product.getProductImage2() != null && !product.getProductImage2().isEmpty()) {
            existingProduct.setFileName2(saveFile(product.getProductImage2(), uploadDir));
        }
        if(product.getProductImage3() != null && !product.getProductImage3().isEmpty()) {
            existingProduct.setFileName3(saveFile(product.getProductImage3(), uploadDir));
        }
        if(product.getProductImage4() != null && !product.getProductImage4().isEmpty()) {
            existingProduct.setFileName4(saveFile(product.getProductImage4(), uploadDir));
        }

        productRepository.save(existingProduct);
    }

    // 이미지 저장
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        // 사용자가 업로드 한 원본 파일(사진)명을 저장
        String originalFileName = file.getOriginalFilename();

        // 파일 중복을 방지하기 위해 UUID.randomUUID() 메소드 채택하여 고유식별자와 원본 파일명을 붙임
        // -ex) cf1df3a3-bdb4-4dda-9ceb-7b9ea5ac63bb_사진.png
        // 만약 사용자끼리 같은 파일명의 사진을 올릴 경우에 발생하는 충돌을 줄일 수 있음
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 파일이 저장될 전체 경로(폴더 경로 + 저장할 파일명)를 다루는 dest객체 생성
        // -ex) "C:/dw0428/upload/cf1df3a3-bdb4-4dda-9ceb-7b9ea5ac63bb_사진.png
        File dest = new File(uploadDir + savedFileName);

        // 지정한 경로에 사진을 저장해주는 역할
        file.transferTo(dest);

        // 서버에 최종 저장 된 파일 리턴
        return savedFileName;
    }

    // 상품 삭제
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 선택 상품 일괄 삭제(List형식으로 받아서 처리)
    public void deleteProducts(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
    }

    // 전체 상품 삭제
    // deleteAll << 상품을 하나 씩 조회 한 뒤 엔티티 단위로 삭제 하기 때문에 대용량으로 삭제 하게 되는 경우 속도저하 발생
    // deleteAllInBatch << 조회 과정 없이 바로 DB에서 삭제하기 때문에 해당 함수 채택(속도측면에서 유리함)
    public void deleteAllProducts() {
        productRepository.deleteAllInBatch();
    }

    // 관리자 상품 검색 + 페이징
    public Page<Product> selectProductList(int page, LocalDate startDate, LocalDate endDate,
                                           String name, String productId, String country, String sort) {

        String sortBy;
        Sort.Direction direction;

        if("oldest".equals(sort)) {
            sortBy = "regDate";
            direction = Sort.Direction.ASC;
        } else if("lowPrice".equals(sort)) {
            sortBy = "price";
            direction = Sort.Direction.ASC;
        } else if("highPrice".equals(sort)) {
            sortBy = "price";
            direction = Sort.Direction.DESC;
        } else {
            sortBy = "regDate";
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(direction, sortBy));


        return productRepository.searchAdminProducts(
                // 시작 날짜와 끝 날짜를 지정 할 수 있는 공식
                startDate == null ? null : startDate.atStartOfDay(),
                endDate == null ? null : endDate.plusDays(1).atStartOfDay(),

                // 아래에 있는 cleanSearchValue 메소드를 이용하여 불필요한 공백을 처리해줌
                cleanSearchValue(name),
                cleanSearchValue(productId),
                cleanSearchValue(country),
                pageable);
    }

    // 검색어에 공백이 있는 경우엔 잘라서 처리해주는 메소드
    private String cleanSearchValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
