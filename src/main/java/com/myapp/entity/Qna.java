package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성 회원
    @ManyToOne
    private Customer customer;

    // 문의분류
    private String category;

    // 상품코드
    private String productId;

    // 제목
    private String title;

    // 문의내용
    @Column(columnDefinition = "text")
    private String content;

    // 관리자 답변
    @Column(columnDefinition = "text")
    private String answer;

    // 문의 작성일
    private LocalDateTime createDate;

    // 답변 작성일
    private LocalDateTime answerDate;

}
