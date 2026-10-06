package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @Setter @ToString @NoArgsConstructor
public class Cart {

    // 장바구니 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    // 회원 ID
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private String customerId;

    // 장바구니 총 금액
    private int grandTotalPrice;

    // 장바구니 상품 총 수량
    private int grandTotalCount;

    // 장바구니 생성일
    private LocalDateTime regCartDate;

}
