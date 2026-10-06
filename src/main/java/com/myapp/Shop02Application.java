package com.myapp;

import com.myapp.entity.Address;
import com.myapp.entity.Customer;
import com.myapp.entity.Role;
import com.myapp.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class Shop02Application {

    public static void main(String[] args) {
        SpringApplication.run(Shop02Application.class, args);
    }

    // 테스트용 : USER(5명), MANAGER(1명), ADMIN(1명)
    //@Bean
    public CommandLineRunner run(CustomerService customerService){
        return (String[] args) -> {
            // 주소 7개
            Address address1 = Address.builder().country("대한민국").zipcode("06128")
                    .basicAddress("서울 강남구 강남대로 480").detailAddress("강남빌딩 303호").build();
            Address address2 = Address.builder().country("대한민국").zipcode("08226")
                    .basicAddress("서울 구로구 경서로 12").detailAddress("대상빌딩 505호").build();
            Address address3 = Address.builder().country("대한민국").zipcode("02230")
                    .basicAddress("서울 중랑구 경재로 72").detailAddress("우리아파트 1205호").build();
            Address address4 = Address.builder().country("대한민국").zipcode("22243")
                    .basicAddress("인천 미추홀구 경원대로 648").detailAddress("경원빌딩 404호").build();
            Address address5 = Address.builder().country("대한민국").zipcode("10273")
                    .basicAddress("경기 고양시 덕양구 대양로 259-35").detailAddress("대양빌딩 606호").build();
            Address address6 = Address.builder().country("대한민국").zipcode("12244")
                    .basicAddress("경기 남양주시 경강로 1-28").detailAddress("경강빌딩 303호").build();
            Address address7 = Address.builder().country("대한민국").zipcode("14110")
                    .basicAddress("경기 안양시 동안구 갈산로 6").detailAddress("갈산빌딩 301호").build();
            // 사용자 7명
            // USER
            Customer customer1 = Customer.builder().customerId("aaaa").name("황동만").nickname("슈퍼맨").gender("남")
                    .birthday(LocalDate.of(1985,3,13)).phone("010-1111-1111").email("aaaa@naver.com").regDate(LocalDateTime.now())
                    .address(address1).role(Role.USER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            Customer customer2 = Customer.builder().customerId("bbbb").name("변은아").nickname("원더우먼").gender("여")
                    .birthday(LocalDate.of(1995,6,18)).phone("010-2222-2222").email("bbbb@naver.com").regDate(LocalDateTime.now())
                    .address(address2).role(Role.USER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            Customer customer3 = Customer.builder().customerId("cccc").name("박경새").nickname("베트맨").gender("남")
                    .birthday(LocalDate.of(1983,10,20)).phone("010-3333-3333").email("cccc@naver.com").regDate(LocalDateTime.now())
                    .address(address3).role(Role.USER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            Customer customer4 = Customer.builder().customerId("dddd").name("고혜진").nickname("나타샤").gender("여")
                    .birthday(LocalDate.of(1985,12,25)).phone("010-4444-4444").email("dddd@naver.com").regDate(LocalDateTime.now())
                    .address(address4).role(Role.USER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            Customer customer5 = Customer.builder().customerId("eeee").name("황진만").nickname("엔트맨").gender("남")
                    .birthday(LocalDate.of(1982,8,18)).phone("010-5555-5555").email("eeee@naver.com").regDate(LocalDateTime.now())
                    .address(address5).role(Role.USER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            // MANAGER
            Customer customer6 = Customer.builder().customerId("manager").name("오정희").nickname("진그레이").gender("여")
                    .birthday(LocalDate.of(1966,11,11)).phone("010-6666-6666").email("manager@kakao.com").regDate(LocalDateTime.now())
                    .address(address6).role(Role.MANAGER).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            // ADMIN
            Customer customer7 = Customer.builder().customerId("admin").name("장미란").nickname("미스틱").gender("여")
                    .birthday(LocalDate.of(1992,4,21)).phone("010-7777-7777").email("admin@kakao.com").regDate(LocalDateTime.now())
                    .address(address7).role(Role.ADMIN).password(new BCryptPasswordEncoder().encode("1234")).password1("1234").build();
            // 회원 등록
            customerService.addCustomer(customer1);
            customerService.addCustomer(customer2);
            customerService.addCustomer(customer3);
            customerService.addCustomer(customer4);
            customerService.addCustomer(customer5);
            customerService.addCustomer(customer6);
            customerService.addCustomer(customer7);

        };
    }
}
