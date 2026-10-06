package com.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@Builder @NoArgsConstructor @AllArgsConstructor
public class Customer {

    // 회원번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 ID
    @Column(unique = true, columnDefinition = "varchar(30)")
    // @Pattern(regexp = "^[^\\s]{8,16}$",
            // message = "공백 없이 8~16글자 사이로 입력하세요.")
    @NotBlank(message = "회원ID는 필수 입력 항목입니다.")
    private String customerId;

    // 비밀번호
    @Column(nullable = false)
    // @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            // message = "영문, 숫자, 특수문자를 포함하여 공백 없이 8~16글자로 입력하세요.")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    // 비밀번호 확인
    @Transient
    //@NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String password1;

    // 이름
    @Column(nullable = false, columnDefinition = "varchar(30)")
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    // 닉네임
    @Column(nullable = false, columnDefinition = "varchar(30)")
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    private String nickname;

    // 이메일
    @Column(nullable = false, columnDefinition = "varchar(50)")
    // @Pattern(regexp = "^[a-zA-Z0-9._%+-]{2,}+@[a-zA-Z0-9.-]{2,}\\.[a-zA-Z]{2,6}$",
            // message = "올바른 이메일 형식으로 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    // 성별
    @Column(nullable = false, columnDefinition = "char(2)")
    @NotBlank(message = "성별은 필수 선택 항목입니다.")
    private String gender;

    // 생년월일
    @Column(nullable = false)
    @NotNull(message = "생일은 필수 선택 항목입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    // 휴대폰 번호
    @Column(nullable = false, columnDefinition = "char(13)")
    // @Pattern(regexp = "010-(\\d{4})-(\\d{4})$",
            // message = "010-xxxx-xxxx 형식으로 입력하세요.")
    @NotBlank(message = "휴대폰 번호는 필수 입력 항목입니다.")
    private String phone;

    // 가입일
    @Column(columnDefinition = "datetime default now()")
    private LocalDateTime regDate;

    // 주소
    @OneToOne(cascade = CascadeType.ALL)
    @Valid
    private Address address;

    // 사용자 역할
    @Enumerated(EnumType.STRING)
    private Role role;

    // 회원 생성 메소드
    public static Customer createCustomer(Customer customer, PasswordEncoder passwordEncoder) {
        customer.setPassword( passwordEncoder.encode(customer.getPassword()));
        customer.setRole(Role.USER);
        customer.setRegDate(LocalDateTime.now());
        return customer;
    }
}
