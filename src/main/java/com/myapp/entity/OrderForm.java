package com.myapp.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderForm {

    private Long productId;  // 주문할 상품 번호
    private int count;       // 주문 수량
}