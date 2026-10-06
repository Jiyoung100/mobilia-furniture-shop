package com.myapp.controller;

import com.myapp.service.ReviewCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    // 댓글 등록
    @PostMapping("/reviews/{reviewId}/comments")
    public String saveComment(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam("content") String content,
            Principal principal,
            RedirectAttributes rttr) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Long productId = reviewCommentService.saveComment(
                    reviewId,
                    principal.getName(),
                    content
            );

            return "redirect:/products/detail/" + productId + "#product-review";

        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }

    // 댓글 수정
    @PostMapping("/review-comments/{commentId}/update")
    public String updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestParam("content") String content,
            Principal principal,
            RedirectAttributes rttr) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Long productId = reviewCommentService.updateComment(
                    commentId,
                    principal.getName(),
                    content
            );

            return "redirect:/products/detail/" + productId + "#product-review";

        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }

    // 댓글 삭제
    @PostMapping("/review-comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable("commentId") Long commentId,
            Principal principal,
            RedirectAttributes rttr) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            Long productId = reviewCommentService.deleteComment(
                    commentId,
                    principal.getName()
            );

            return "redirect:/products/detail/" + productId + "#product-review";

        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }
}