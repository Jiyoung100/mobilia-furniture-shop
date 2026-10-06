package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter @ToString
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성 고객 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 리뷰 대상 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 해당주문 상품 건 (중복 작성 방지 및 구매 검증용 1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", unique = true, nullable = false)
    private OrderItem orderItem;

    // 별점 (1 ~ 5)
    @Column(nullable = false)
    private int rating;

    // 리뷰 내용
    @Column(columnDefinition = "text", nullable = false)
    private String content;

    // 이미지 첨부 기능 (추후 확장 대비 파일명 저장용 필드)
    private String reviewImage;

    // 작성 일시
    @Column(nullable = false)
    private LocalDate regDate; // 필드 타입 확인

    public Review() {
        this.regDate = LocalDate.now();
    }
}
