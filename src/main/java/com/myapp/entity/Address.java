package com.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @ToString
@Builder @NoArgsConstructor @AllArgsConstructor
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 국적
    @Column(nullable = false, columnDefinition = "varchar(30)")
    @NotBlank(message = "국가명은 필수 입력항목입니다.")
    private String country;

    // 우편번호
    @Column(nullable = false, columnDefinition = "varchar(10)")
    @NotBlank(message = "우편번호는 필수 입력항목입니다.")
    private String zipcode;

    // 기본주소
    @Column(nullable = false, columnDefinition = "varchar(100)")
    @NotBlank(message = "기본주소는 필수 입력항목입니다.")
    private String basicAddress;

    // 상세주소
    @Column(nullable = false, columnDefinition = "varchar(100)")
    @NotBlank(message = "상세주소는 필수 입력항목입니다.")
    private String detailAddress;

}
