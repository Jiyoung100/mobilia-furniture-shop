package com.myapp.repository;

import com.myapp.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCommentRepository
        extends JpaRepository<ReviewComment, Long> {

    // 특정 후기의 댓글 작성순 조회
    @Query("""
            select rc
            from ReviewComment rc
            join fetch rc.customer
            where rc.review.id = :reviewId
            order by rc.regDate asc
            """)
    List<ReviewComment> findByReviewIdOrderByRegDateAsc(
            @Param("reviewId") Long reviewId
    );
}