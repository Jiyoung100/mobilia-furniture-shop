package com.myapp.controller;

import com.myapp.service.ReviewRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReviewRecommendController {

    private final ReviewRecommendService reviewRecommendService;

    // 후기 추천 / 추천 취소
    @PostMapping("/reviews/{reviewId}/recommend")
    @ResponseBody
    public Map<String, Object> toggleRecommend(
            @PathVariable("reviewId") Long reviewId,
            Principal principal) {

        reviewRecommendService.toggleRecommend(
                reviewId,
                principal.getName()
        );

        long recommendCount =
                reviewRecommendService.getRecommendCount(reviewId);

        boolean recommended =
                reviewRecommendService.isRecommended(
                        reviewId,
                        principal.getName()
                );

        return Map.of("recommendCount", recommendCount, "recommended", recommended);
    }
}