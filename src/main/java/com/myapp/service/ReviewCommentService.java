package com.myapp.service;

import com.myapp.entity.Customer;
import com.myapp.entity.Review;
import com.myapp.entity.ReviewComment;
import com.myapp.repository.CustomerRepository;
import com.myapp.repository.ReviewCommentRepository;
import com.myapp.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCommentService {

    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    // 댓글 조회
    public List<ReviewComment> getComments(Long reviewId) {
        return reviewCommentRepository.findByReviewIdOrderByRegDateAsc(reviewId);
    }

    // 댓글 등록
    @Transactional
    public Long saveComment(Long reviewId, String customerId, String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new IllegalArgumentException("후기가 존재하지 않습니다.")
                );

        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원이 존재하지 않습니다.")
                );

        ReviewComment comment = new ReviewComment();
        comment.setReview(review);
        comment.setCustomer(customer);
        comment.setContent(content.trim());

        reviewCommentRepository.save(comment);

        return review.getProduct().getId();
    }

    // 댓글 수정
    @Transactional
    public Long updateComment(Long commentId, String customerId, String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }

        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("댓글이 존재하지 않습니다.")
                );

        // 본인 댓글인지 확인
        if (!comment.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(content.trim());
        comment.setUpdateDate(LocalDateTime.now());

        return comment.getReview().getProduct().getId();
    }

    // 댓글 삭제
    @Transactional
    public Long deleteComment(Long commentId, String customerId) {

        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("댓글이 존재하지 않습니다.")
                );

        // 본인 댓글인지 확인
        if (!comment.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        Long productId = comment.getReview().getProduct().getId();

        reviewCommentRepository.delete(comment);

        return productId;
    }
}