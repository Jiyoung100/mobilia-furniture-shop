package com.myapp.service;

import com.myapp.entity.Customer;
import com.myapp.repository.CustomerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 스프링 시큐리티에서 로그인 인증시에 사용하는 서비스
@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    // 로그인 인증시에 사용하는 메소드
    @Override
    public UserDetails loadUserByUsername(@NonNull String customerId)
            throws UsernameNotFoundException {

        Optional<Customer> optionalCustomer =
                customerRepository.findByCustomerId(customerId);

        if (optionalCustomer.isEmpty()) {
            throw new UsernameNotFoundException(customerId);
        }

        Customer customer = optionalCustomer.get();

        return User.builder()
                .username(customer.getCustomerId())
                .password(customer.getPassword())
                .roles(customer.getRole().name())
                .build();
    }
}