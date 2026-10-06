package com.myapp.controller;

import com.myapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final CartService cartService;

    // 모든 화면에 장바구니 상품 종류 개수 전달
    @ModelAttribute("cartCount")
    public long cartCount(Principal principal) {
        if (principal == null) {
            return 0;
        }

        return cartService.getCartProductTypeCount(principal.getName());
    }
}
