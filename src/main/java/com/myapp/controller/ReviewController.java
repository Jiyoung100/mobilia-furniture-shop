package com.myapp.controller;

import com.myapp.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public String createReview(@RequestParam("orderItemId") Long orderItemId,
                               @RequestParam("rating") int rating,
                               @RequestParam("content") String content,
                               Principal principal,
                               RedirectAttributes rttr) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // principal.getName() 문자열 전달
            reviewService.saveReview(orderItemId, principal.getName(), rating, content);
            rttr.addFlashAttribute("message", "후기가 성공적으로 등록되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("errorMessage", "후기 등록 중 오류가 발생했습니다.");
        }

        return "redirect:/orders";
    }
}