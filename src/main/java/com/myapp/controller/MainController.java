package com.myapp.controller;

import com.myapp.entity.Product;
import com.myapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ProductService productService;

    // 제품 전체 조회
    @GetMapping
    public String getProductAll(Model model){
        List<Product> newProductList = productService.getNewProductList();

        model.addAttribute("newProductList", newProductList);
        return "/product/products";
    }
}
