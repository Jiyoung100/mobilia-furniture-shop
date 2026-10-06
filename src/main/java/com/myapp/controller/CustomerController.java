package com.myapp.controller;

import com.myapp.entity.Address;
import com.myapp.entity.Customer;
import com.myapp.entity.Role;
import com.myapp.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/customers")
@Controller
@Log
public class CustomerController {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    // ################################################################################################################
    // [[ 1. 사용자 영역 ]]

    // [ 회원 가입 ] : 페이지 이동
    @GetMapping("/signup")
    public String signup(Model model) {
        Customer customer = new Customer();
        customer.setAddress(new Address());

        model.addAttribute("customer", customer);

        return "customer/signup";
    }

    // [ 회원 가입 ] : 처리
    @PostMapping("/signup")
    public String addCustomer(@Valid @ModelAttribute("customer") Customer customer,
                              BindingResult bindingResult) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            log.info("회원 가입에 실패하였습니다.");
            return "customer/signup";
        }
        if (customer.getPassword() == null || customer.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "password_error", "비밀번호는 필수 입력 항목입니다.");
            return "customer/signup";
        }
        if (customer.getPassword1() == null || customer.getPassword1().isBlank()) {
            bindingResult.rejectValue("password1", "password1_error", "비밀번호 확인은 필수 입력 항목입니다.");
            return "customer/signup";
        }
        // 회원 아이디 중복 검사
        if (customerService.isDuplicateCustomerId(customer.getCustomerId())) {
            bindingResult.rejectValue("customerId", "customerId_error","회원ID가 이미 존재합니다.");
            return "customer/signup";
        }
        // 비밀번호 확인
        if (!customer.getPassword().equals(customer.getPassword1())) {
            bindingResult.rejectValue("password1","password_error","비밀번호가 일치하지 않습니다.");
            return "customer/signup";
        }

        // 회원 생성
        Customer c = Customer.createCustomer(customer, passwordEncoder);
        // 회원 저장
        customerService.addCustomer(c);

        return "redirect:/";
    }

    // [ 회원 정보 수정 ] : 폼 이동
    @GetMapping("/profile")
    public String updateCustomerForm(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        // 로그인된 계정 ID로 정보 조회
        Customer customer = customerService.selectCustomer(principal.getName());
        customer.setPassword(""); // 화면 노출 방지용 빈값

        model.addAttribute("customer", customer);
        return "customer/updateCustomer";
    }

    // [ 회원 정보 수정 ] : 처리
    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute("customer") Customer customer,
                                 BindingResult bindingResult,
                                 Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        // 새 비밀번호를 입력했을 때만 확인란과 일치 여부 검증
        if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
            if (!customer.getPassword().equals(customer.getPassword1())) {
                bindingResult.rejectValue("password1", "password_error", "비밀번호가 일치하지 않습니다.");
                return "customer/updateCustomer";
            }
        }

        customerService.updateCustomer(customer);
        return "redirect:/";
    }

    // [ 회원 탈퇴 ] : 처리 (물리 삭제 후 시큐리티 로그아웃)
    @GetMapping("/delete")
    public String deleteCustomer(Principal principal, HttpServletRequest request, HttpServletResponse response) {
        if (principal != null) {
            // DB 물리 삭제
            customerService.deleteCustomer(principal.getName());

            // 시큐리티 세션 및 인증 정보 무효화
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
        }
        return "redirect:/";
    }

    // ##########################################################################
    // [[ 2. 어드민 영역 ]]

    // [ 회원 전체 조회 및 다중 조건 검색 ]
    @GetMapping("/admin/list")
    public String getCustomerList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            Model model) {

        Page<Customer> paging = customerService.selectCustomerList(
                page, startDate, endDate, customerId, name, phone, gender, role, sort);

        model.addAttribute("paging", paging);
        model.addAttribute("startDate", startDate == null ? "" : startDate);
        model.addAttribute("endDate", endDate == null ? "" : endDate);
        model.addAttribute("customerId", customerId == null ? "" : customerId);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("phone", phone == null ? "" : phone);
        model.addAttribute("gender", gender == null ? "" : gender);
        model.addAttribute("role", role == null ? "" : role.name());
        model.addAttribute("sort", sort);

        return "customer/selectCustomerList";
    }

    // 관리자 회원 정보 수정 페이지 이동
    @GetMapping("/admin/update/{id}")
    public String updateCustomer(@PathVariable("id") Long id, Model model) {
        Customer customer = customerService.selectCustomerById(id);
        // 화면에 패스워드 노출 방지
        customer.setPassword("");

        model.addAttribute("customer", customer);
        return "customer/adminUpdateCustomer";
    }

    // 관리자 회원 정보 수정 처리
    @PostMapping("/admin/update/{id}")
    public String updateCustomer(@Valid @ModelAttribute("customer") Customer customer,
                                 BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "customer/adminUpdateCustomer";
        }

        if(customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
            if(!customer.getPassword().equals(customer.getPassword1())) {
                bindingResult.rejectValue("password1", "password_error","비밀번호가 일치하지 않습니다.");
                return "customer/adminUpdateCustomer";
            }
        }
        customerService.updateCustomer(customer);

        return "redirect:/customers/admin/list";
    }

    // 관리자 단일 회원 삭제
    @PostMapping("/admin/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers/admin/list";
    }

    // 선택 회원 일괄 삭제 처리
    @PostMapping("/admin/delete")
    public String deleteCustomers(@RequestParam("customerIds") List<Long> customerIds) {
        customerService.deleteCustomers(customerIds);
        return "redirect:/customers/admin/list";
    }

    // 전체 회원 삭제 처리
    @PostMapping("/admin/deleteAll")
    public String deleteAllCustomers() {
        customerService.deleteAllCustomers();
        return "redirect:/customers/admin/list";
    }

}