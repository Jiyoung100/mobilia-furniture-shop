package com.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 로그인 화면 이동
    @GetMapping("/login")
    public String login() {
        return "customer/login";
    }

    // 로그인 실패
    @GetMapping("/loginFailed")
    public String loginFailed(Model model) {

        model.addAttribute("error", true);

        return "customer/login";
    }
}