package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 대상 후기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 댓글 내용
    @Column(columnDefinition = "text", nullable = false)
    private String content;

    // 작성 일시
    @Column(nullable = false)
    private LocalDateTime regDate;

    // 수정 일시
    private LocalDateTime updateDate;

    public ReviewComment() {
        this.regDate = LocalDateTime.now();
    }
}