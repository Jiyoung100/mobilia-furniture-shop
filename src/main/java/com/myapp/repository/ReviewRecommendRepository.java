package com.myapp.repository;

import com.myapp.entity.ReviewRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRecommendRepository
        extends JpaRepository<ReviewRecommend, Long> {

    // 특정 회원이 해당 후기를 추천했는지 조회
    Optional<ReviewRecommend>
    findByReview_IdAndCustomer_CustomerId(
            Long reviewId,
            String customerId
    );

    // 후기 추천 수
    long countByReview_Id(Long reviewId);
}