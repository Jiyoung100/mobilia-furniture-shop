package com.myapp.controller;

import com.myapp.entity.Product;
import com.myapp.entity.Review;
import com.myapp.entity.ReviewComment;
import com.myapp.service.ProductService;
import com.myapp.service.ReviewCommentService;
import com.myapp.service.ReviewRecommendService;
import com.myapp.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/products")
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;
    private final ReviewRecommendService reviewRecommendService;

    // ################################################################################################################
    // 1. 사용자 영역

    // [ 제품 상세 조회 ]
    @GetMapping("/detail/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Principal principal, Model model){
        Product product = productService.getProductById(id);

        List<Product> sliderProductList = productService.getProductListBySlider(
                product.getCategory2(), product.getId());

        model.addAttribute("product", product);
        model.addAttribute("sliderProductList", sliderProductList);

        // 후기 관련 데이터
        List<Review> reviewList = reviewService.getReviewsByProduct(id);
        double avgRating = reviewService.getAverageRating(id);
        long reviewCount = reviewService.getReviewCount(id);

        // 후기별 댓글 목록
        Map<Long, List<ReviewComment>> commentMap = new HashMap<>();

        for (Review review : reviewList) {
            commentMap.put(
                    review.getId(),
                    reviewCommentService.getComments(review.getId())
            );
        }

        // 후기별 추천 개수
        Map<Long, Long> recommendCountMap = new HashMap<>();
        // 로그인 회원의 후기별 추천 여부
        Map<Long, Boolean> recommendedMap = new HashMap<>();

        for (Review review : reviewList) {
            recommendCountMap.put(
                    review.getId(),
                    reviewRecommendService.getRecommendCount(review.getId())
            );
            recommendedMap.put(
                    review.getId(),
                    reviewRecommendService.isRecommended(
                            review.getId(),
                            principal != null ? principal.getName() : null
                    )
            );
        }

        model.addAttribute("reviewList", reviewList);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("commentMap", commentMap);
        model.addAttribute("recommendCountMap", recommendCountMap);
        model.addAttribute("recommendedMap", recommendedMap);

        return "product/product";
    }

    // [ 제품 검색, 페이징 ]
    @GetMapping("/search")
    public String searchProduct(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // 검색어 + 페이징 처리된 데이터 가져오기
        Page<Product> paging = productService.searchProducts(keyword, page);

        model.addAttribute("keyword", keyword);
        model.addAttribute("paging", paging);

        return "product/searchProduct";
    }

    // [ 모든 제품 페이지 ] - 페이징, 정렬
    @GetMapping("/all")
    public String allProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "new") String sort,
            Model model) {

        Page<Product> paging = productService.getAllProducts(page, sort);
        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);

        return "product/allProducts";
    }

    // [ 공간별 가구 페이지 ] - 페이징, 정렬
    @GetMapping("/category/{category1}")
    public String categoryProducts(@PathVariable("category1") String category1,
                                   @RequestParam(name = "category2", required = false) String category2,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "sort", defaultValue = "new") String sort,
                                   Model model){
        Page<Product> paging;

        if(category2 == null || category2.isEmpty()){
            paging = productService.getProductsByCategory1(page, sort, category1);
        } else {
            paging = productService.getProductsByCategory1AndCategory2(page, sort, category1, category2);
        }
        List<String> category2List = productService.getCategory2List(category1);

        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);
        model.addAttribute("category1", category1);
        model.addAttribute("category2", category2);
        model.addAttribute("category2List", category2List);

        return "product/categoryProducts";
    }

    // [ 브랜드 페이지 ] - 페이징, 정렬
    @GetMapping("/brand/{brand}")
    public String brandProducts(@PathVariable("brand") String brand,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "sort", defaultValue = "new") String sort,
                                   Model model){

        Page<Product> paging = productService.getProductsByBrand(page, sort, brand);

        model.addAttribute("paging", paging);
        model.addAttribute("sort", sort);
        model.addAttribute("brand", brand);

        return "product/brandProducts";
    }

    // [ 신상품 페이지 ] - 페이징
    @GetMapping("/new")
    public String newProducts(Model model){
        List<Product> newProductList = productService.getNewProduct();
        model.addAttribute("newProductList", newProductList);
        return "product/newProducts";
    }

    // ################################################################################################################
    // 2. 어드민 영역

    // [ 상품 등록 ] : 페이지 이동
    @GetMapping("/admin/add")
    public String addProduct(Model model){
        model.addAttribute("product", new Product());
        return "product/addProduct";
    }

    // [ 상품 등록 ] : 처리
    @PostMapping("/admin/add")
    public String addProduct(
            @Valid Product product,
            BindingResult bindingResult) throws IOException {

        MultipartFile[] images = {
                product.getProductImage1(), product.getProductImage2(), product.getProductImage3(), product.getProductImage4()
        };
        String[] fieldNames = {
                "productImage1", "productImage2", "productImage3", "productImage4"
        };
        String[] imageNames = {
                "썸네일 이미지", "상세 이미지1", "상세 이미지2", "상세 이미지3"
        };

        // 이미지 필수 입력 검사
        for(int i = 0; i < images.length; i++) {
            if(images[i] == null || images[i].isEmpty()) {
                bindingResult.rejectValue(fieldNames[i], "required",imageNames[i] + "를 선택해주세요.");
            }
        }
        if(bindingResult.hasErrors()){
            return "product/addProduct";
        }

        productService.addProduct(product);

        return "redirect:/";
    }

    // [ 상품 수정 ] : 페이지 이동
    @GetMapping("/admin/detail/{id}")
    public String updateProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/updateProduct";
    }

    // [ 상품 수정 ] : 처리
    @PostMapping("/admin/detail/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid Product product, BindingResult bindingResult) throws IOException {
        // 에러 발생 시 리턴 처리
        if(bindingResult.hasErrors()){
            return "product/updateProduct";
        }
        product.setId(id);
        productService.updateProduct(product);

        return "redirect:/products/admin/list";
    }

    // [ 상품 삭제 ] : 처리
    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/admin/list";
    }

    // [ 상품 전체 조회 ]
    @GetMapping("/admin/list")
    public String selectProductList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String country,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            Model model) {

        Page<Product> paging = productService.selectProductList(page, startDate, endDate, name, productId, country, sort);

        model.addAttribute("paging", paging);
        // 검색 옵션들이 null이라면 공백으로 처리하고 옵션이 있다면 그대로 값을 받아서 사용
        model.addAttribute("startDate", startDate == null ? "" : startDate);
        model.addAttribute("endDate", endDate == null ? "" : endDate);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("productId", productId == null ? "" : productId);
        model.addAttribute("country", country == null ? "" : country);
        model.addAttribute("sort", sort);

        return "product/selectProductList";
    }

    // 선택 상품 일괄 삭제 처리
    @PostMapping("/admin/delete")
    public String deleteProducts(@RequestParam("productIds") List<Long> productIds) {
        productService.deleteProducts(productIds);
        return "redirect:/products/admin/list";
    }

    // 전체 상품 삭제 처리
    @PostMapping("/admin/deleteAll")
    public String deleteAllProducts() {
        productService.deleteAllProducts();
        return "redirect:/products/admin/list";
    }

}
