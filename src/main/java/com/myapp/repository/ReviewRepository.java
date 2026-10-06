package com.myapp.repository;

import com.myapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. 특정 상품의 모든 리뷰 최신순 조회
    @Query("select r from Review r join fetch r.customer where r.product.id = :productId order by r.regDate desc")
    List<Review> findByProductIdOrderByRegDateDesc(@Param("productId") Long productId);

    // 2. 특정 주문 상품(orderItem) 건으로 작성된 리뷰가 있는지 확인 (중복 작성 방지용)
    boolean existsByOrderItemId(Long orderItemId);

    // 3. 특정 주문 상품(orderItem)의 리뷰 단건 조회
    Optional<Review> findByOrderItemId(Long orderItemId);

    // 4. 상품별 평균 별점 계산 (리뷰가 없으면 null 반환)
    @Query("select avg(r.rating) from Review r where r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    // 5. 상품별 총 리뷰 개수
    long countByProductId(Long productId);
}