package com.myapp.controller;

import com.myapp.entity.Cart;
import com.myapp.entity.CartItem;
import com.myapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    // 장바구니 추가
    @PostMapping("/add")
    @ResponseBody
    public long addCart(
            @RequestParam("customerId") String customerId,
            @RequestParam("productId") String productId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity) {

        cartService.addCart(customerId, productId, quantity);

        return cartService.getCartProductTypeCount(customerId);
    }

    // 장바구니 조회
    @GetMapping("/read/{customerId}")
    public String readCart(
            @PathVariable("customerId") String customerId,
            Model model) {

        Cart cart = cartService.getCartByCustomerId(customerId);
        List<CartItem> sortedItems = cartService.getCartItems(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("sortedItems", sortedItems);

        return "cart/cart";
    }

    // 장바구니 수량 변경
    @PutMapping("/update/{quantity}/{customerId}/{cartItemId}")
    public String updateCartQuantity(
            @PathVariable("quantity") int quantity,
            @PathVariable("customerId") String customerId,
            @PathVariable("cartItemId") Long cartItemId) {

        cartService.updateCartQuantityByCustomerId(
                quantity, customerId, cartItemId
        );

        return "redirect:/cart/read/" + customerId;
    }

    // 장바구니 개별 삭제
    @DeleteMapping("/removeone/{customerId}/{cartItemId}")
    public String deleteCartItem(
            @PathVariable("customerId") String customerId,
            @PathVariable("cartItemId") Long cartItemId) {

        cartService.deleteCartItem(customerId, cartItemId);

        return "redirect:/cart/read/" + customerId;
    }


    // 장바구니 전체 삭제
    @DeleteMapping("/removeall/{customerId}")
    public String deleteCart(@PathVariable("customerId") String customerId) {

        cartService.deleteCart(customerId);

        return "redirect:/cart/read/" + customerId;
    }


    // 장바구니 선택 삭제
    @DeleteMapping("/removefew/{customerId}/{ids}")
    public String deleteCartItemSelect(
            @PathVariable("customerId") String customerId,
            @PathVariable("ids") String ids) {

        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();

        cartService.deleteCartItemSelect(customerId, idList);

        return "redirect:/cart/read/" + customerId;
    }
}
