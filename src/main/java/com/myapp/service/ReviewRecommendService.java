package com.myapp.service;

import com.myapp.entity.Customer;
import com.myapp.entity.Review;
import com.myapp.entity.ReviewRecommend;
import com.myapp.repository.CustomerRepository;
import com.myapp.repository.ReviewRecommendRepository;
import com.myapp.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewRecommendService {

    private final ReviewRecommendRepository reviewRecommendRepository;
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public Long toggleRecommend(Long reviewId, String customerId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("후기가 존재하지 않습니다.")
                );

        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원이 존재하지 않습니다.")
                );

        // 본인이 작성한 후기는 추천 불가
        if (review.getCustomer().getCustomerId().equals(customerId)) {
            return review.getProduct().getId();
        }

        reviewRecommendRepository
                .findByReview_IdAndCustomer_CustomerId(reviewId, customerId)
                .ifPresentOrElse(
                        // 이미 추천했다면 추천 취소
                        reviewRecommendRepository::delete,

                        // 추천하지 않았다면 추천 등록
                        () -> {
                            ReviewRecommend recommend = new ReviewRecommend();
                            recommend.setReview(review);
                            recommend.setCustomer(customer);
                            reviewRecommendRepository.save(recommend);
                        }
                );
        return review.getProduct().getId();
    }

    // 후기 추천 개수
    public long getRecommendCount(Long reviewId) {
        return reviewRecommendRepository.countByReview_Id(reviewId);
    }

    // 로그인 회원이 해당 후기를 추천했는지 확인
    public boolean isRecommended(Long reviewId, String customerId) {

        if (customerId == null) {
            return false;
        }

        return reviewRecommendRepository
                .findByReview_IdAndCustomer_CustomerId(reviewId, customerId)
                .isPresent();
    }
}