package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CartItem {

    // 장바구니 항목 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 장바구니
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 상품
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 상품 수량
    private int quantity = 1;

    // 해당 상품의 총 금액
    private int totalPrice;

    // 장바구니 등록일
    private LocalDateTime regCartItemDate;
}
