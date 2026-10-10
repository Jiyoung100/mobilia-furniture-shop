package com.myapp.controller;

import com.myapp.entity.Review;
import com.myapp.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public String createReview(@RequestParam("orderItemId") Long orderItemId,
                               @RequestParam("rating") int rating,
                               @RequestParam("content") String content,
                               @RequestParam(value = "reviewImage", required = false) MultipartFile reviewImage,
                               Principal principal,
                               RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // reviewImage 파일 인자 추가 전달
            reviewService.saveReview(orderItemId, principal.getName(), rating, content, reviewImage);
            rttr.addFlashAttribute("message", "후기가 성공적으로 등록되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("errorMessage", "후기 등록 중 오류가 발생했습니다.");
        }

        return "redirect:/orders";
    }

    // 1. 특정 orderItemId에 작성된 리뷰 정보 조회 (비동기 Fetch용)
    @GetMapping("/reviews/by-order-item/{orderItemId}")
    @ResponseBody
    public ResponseEntity<?> getReviewByOrderItem(@PathVariable("orderItemId") Long orderItemId) {
        Review review = reviewService.getReviewByOrderItem(orderItemId);
        if (review == null) {
            return ResponseEntity.ok(Map.of("exists", false));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("exists", true);
        data.put("reviewId", review.getId());
        data.put("rating", review.getRating());
        data.put("content", review.getContent());
        data.put("reviewImage", review.getReviewImage() != null ? review.getReviewImage() : "");
        return ResponseEntity.ok(data);
    }

    // 2. 후기 수정 처리
    @PostMapping("/reviews/update")
    public String updateReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("rating") int rating,
                               @RequestParam("content") String content,
                               @RequestParam(value = "reviewImage", required = false) MultipartFile reviewImage,
                               Principal principal,
                               RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            reviewService.updateReview(reviewId, principal.getName(), rating, content, reviewImage);
            rttr.addFlashAttribute("message", "후기가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/orders";
    }
}