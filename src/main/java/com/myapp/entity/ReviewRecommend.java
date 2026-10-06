package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(
        name = "review_recommend",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"review_id", "customer_id"}
        )
)
public class ReviewRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 추천 대상 후기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 추천한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 추천 일시
    @Column(nullable = false)
    private LocalDateTime regDate;

    public ReviewRecommend() {
        this.regDate = LocalDateTime.now();
    }
}