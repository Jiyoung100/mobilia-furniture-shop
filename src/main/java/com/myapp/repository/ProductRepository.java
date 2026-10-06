package com.myapp.repository;

import com.myapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 메인 페이지 - 신제품 슬라이드
    List<Product> findTop10ByOrderByRegDateDesc();

    // name 또는 category1 또는 category2에 키워드가 포함된 상품 목록 조회
    Page<Product> findByNameContainingOrCategory2Containing(String name, String category1, String category2, Pageable pageable);

    // 카테고리별 상품 조회
    Page<Product> findByCategory1(String category1, Pageable pageable);
    Page<Product> findByCategory1AndCategory2(String category1, String category2, Pageable pageable);
    // 카테고리2 배열 (중복 제거)
    @Query("select distinct p.category2 from Product p where p.category1 = :category1")
    List<String> findCategory2ByCategory1(String category1);

    // 브랜드로 상품 조회
    Page<Product> findByBrand(String brand, Pageable pageable);

    // 신상품 조회
    List<Product> findTop16ByOrderByRegDateDesc();

    // productId로 상품 조회
    Optional<Product> findByProductId(String productId);

    // 관련 상품 조회 (category2 기준)
    List<Product> findByCategory2AndIdNotOrderByIdDesc(String category2, Long id);

    // 어드민 영역 - 전체 상품 조회 페이징 + 검색 쿼리문
    // 다수의 Repository를 만들 필요 없이 쿼리문으로 한 번에 작성
    @Query("""
            select p from Product p
            where (:startDate is null or p.regDate >= :startDate)
              and (:endDate is null or p.regDate < :endDate)
              and (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
              and (:productId is null or lower(p.productId) like lower(concat('%', :productId, '%')))
              and (:country is null or lower(p.country) like lower(concat('%', :country, '%')))
            """)
    Page<Product> searchAdminProducts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("name") String name,
            @Param("productId") String productId,
            @Param("country") String country,
            Pageable pageable);
}
