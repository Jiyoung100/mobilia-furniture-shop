package com.myapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    private String receiverName;        // 수령고객이름
    private String receiverPhone;       // 수령고객 전화번호

    private String status;      // 배송상태 (상품준비중, 배송중, 배송완료)

    private LocalDateTime deliveryDate; // 배송일 (배송완료 누른 시점)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;        // 배송주소
}