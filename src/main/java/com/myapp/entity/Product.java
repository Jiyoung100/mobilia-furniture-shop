package com.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
public class Product {

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품코드
    @NotBlank(message = "상품코드를 입력해주세요.")
    @Pattern(
            regexp = "^(lr|dn|br|bt|kd|ho|en|bc)-(001|002|003)-\\d{3}$",
            message = "상품코드는 lr-001-001 형식으로 입력해주세요."
    )
    @Column(unique = true, columnDefinition = "varchar(30)")
    private String productId;

    // 제품명
    @NotBlank(message = "상품명을 입력해주세요.")
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private String name;

    // 가격
    @NotNull(message = "가격을 입력해주세요.")
    @Column(nullable = false)
    private Integer price;

    // 브랜드
    @NotBlank(message = "브랜드를 입력해주세요.")
    @Column(columnDefinition = "varchar(50)")
    private String brand;

    // 카테고리1
    @NotBlank(message = "공간 분류를 선택해주세요.")
    @Column(columnDefinition = "varchar(20)")
    private String category1;

    // 카테고리2
    @NotBlank(message = "품목 분류를 선택해주세요.")
    @Column(columnDefinition = "varchar(20)")
    private String category2;

    // 상품설명
    @NotBlank(message = "상품 설명을 입력해주세요.")
    @Column(columnDefinition = "text")
    private String description;

    // 제조국
    @NotBlank(message = "제조국을 입력해주세요.")
    @Column(columnDefinition = "varchar(30)")
    private String country;

    // 재고
    @NotNull(message = "재고를 입력해주세요.")
    private Integer stock;

    // 사이즈
    @NotBlank(message = "상품 사이즈를 입력해주세요.")
    @Column(columnDefinition = "varchar(100)")
    private String size;

    // 소재
    @NotBlank(message = "소재를 입력해주세요.")
    @Column(columnDefinition = "varchar(100)")
    private String material;

    // 등록일
    @Column(columnDefinition = "datetime default now()")
    private LocalDateTime regDate;

    // 출시일
    @NotNull(message = "출시일을 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;

    // 상품 이미지 (썸네일용)
    @Column(columnDefinition = "varchar(100) default 'default.jpg'")
    private String fileName1;

    // 상품 상세 이미지 1
    @Column(columnDefinition = "varchar(100) default 'default.jpg'")
    private String fileName2;

    // 상품 상세 이미지 2
    @Column(columnDefinition = "varchar(100) default 'default.jpg'")
    private String fileName3;

    // 상품 상세 이미지 3
    @Column(columnDefinition = "varchar(100) default 'default.jpg'")
    private String fileName4;

    @Transient
    private MultipartFile productImage1;

    @Transient
    private MultipartFile productImage2;

    @Transient
    private MultipartFile productImage3;

    @Transient
    private MultipartFile productImage4;

}
