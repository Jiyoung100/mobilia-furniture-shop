package com.myapp.repository;

import com.myapp.entity.Customer;
import com.myapp.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 회원 아이디로 회원 조회
    Optional<Customer> findByCustomerId(String customerId);

    // 회원 아이디 중복 확인
    boolean existsByCustomerId(String customerId);

    // 회원 이름에 검색어가 포함된 회원을 페이지 단위로 조회
    Page<Customer> findByNameContaining(String name, Pageable pageable);

    // 어드민 영역 - 전체 회원 조회 페이징 + 검색 쿼리문
    // 단일 쿼리로 조건(가입일, ID, 이름, 전화번호, 성별, 권한) 검색
    @Query("""
            select c from Customer c
            where (:startDate is null or c.regDate >= :startDate)
              and (:endDate is null or c.regDate < :endDate)
              and (:customerId is null or lower(c.customerId) like lower(concat('%', :customerId, '%')))
              and (:name is null or lower(c.name) like lower(concat('%', :name, '%')))
              and (:phone is null or c.phone like concat('%', :phone, '%'))
              and (:gender is null or c.gender = :gender)
              and (:role is null or c.role = :role)
            """)
    Page<Customer> searchAdminCustomers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("customerId") String customerId,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("gender") String gender,
            @Param("role") Role role,
            Pageable pageable);
}
