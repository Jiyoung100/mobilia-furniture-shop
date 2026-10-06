package com.myapp.service;

import com.myapp.entity.Customer;
import com.myapp.entity.Role;
import com.myapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 ID 중복 확인
    public boolean isDuplicateCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId).isPresent();
    }

    // 회원 등록
    public void addCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    // 회원 이름 검색어를 적용하고 한 페이지에 10명씩 회원 목록 조회
    public Page<Customer> getCustomerList(String name, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        if (name == null || name.isBlank()) {
            return customerRepository.findAll(pageRequest);
        }
        return customerRepository.findByNameContaining(name.trim(), pageRequest);
    }

    // 단일 회원 조회 (아이디 기반)
    public Customer selectCustomer(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // id기반 단일 회원 조회
    public Customer selectCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 회원 정보 수정
    @Transactional
    public void updateCustomer(Customer customer) {
        Customer c = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 새 비밀번호가 입력된 경우 암호화 적용
        if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
            c.setPassword(passwordEncoder.encode(customer.getPassword()));
        }

        // 기본 개인정보 필드 변경
        c.setName(customer.getName());
        c.setNickname(customer.getNickname());
        c.setBirthday(customer.getBirthday());
        c.setGender(customer.getGender());
        c.setPhone(customer.getPhone());
        c.setEmail(customer.getEmail());

        // 관리자 수정 시 권한(Role) 변경 반영
        if (customer.getRole() != null) {
            c.setRole(customer.getRole());
        }

        // 주소 정보 업데이트 (기존 영속 객체의 값만 변경하여 dirty checking)
        if (customer.getAddress() != null && c.getAddress() != null) {
            c.getAddress().setZipcode(customer.getAddress().getZipcode());
            c.getAddress().setBasicAddress(customer.getAddress().getBasicAddress());
            c.getAddress().setDetailAddress(customer.getAddress().getDetailAddress());
            // 국적 정보가 폼에서 넘어왔다면 반영
            if (customer.getAddress().getCountry() != null) {
                c.getAddress().setCountry(customer.getAddress().getCountry());
            }
        }
    }

    // 회원 탈퇴 (물리 삭제)
    @Transactional
    public void deleteCustomer(String customerId) {
        Customer c = selectCustomer(customerId);
        customerRepository.delete(c);
    }

    // ##########################################################################
    // 2. 어드민 영역

    // 관리자 회원 다중 조건 검색 + 페이징
    public Page<Customer> selectCustomerList(
            int page,
            LocalDate startDate,
            LocalDate endDate,
            String customerId,
            String name,
            String phone,
            String gender,
            Role role,
            String sort) {

        Sort.Direction direction = "oldest".equals(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, 10, Sort.by(direction, "regDate"));

        return customerRepository.searchAdminCustomers(
                startDate == null ? null : startDate.atStartOfDay(),
                endDate == null ? null : endDate.plusDays(1).atStartOfDay(),
                cleanSearchValue(customerId),
                cleanSearchValue(name),
                cleanSearchValue(phone),
                cleanSearchValue(gender),
                role,
                pageable);
    }

    // 검색어에 공백이 있는 경우엔 잘라서 처리해주는 메소드
    private String cleanSearchValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    // 선택 회원 일괄 삭제
    public void deleteCustomers(List<Long> customerIds) {
        customerRepository.deleteAllById(customerIds);
    }

    // 전체 회원 삭제
    public void deleteAllCustomers() {
        customerRepository.deleteAllInBatch();
    }

    // 회원 번호로 회원 삭제
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
