package com.myapp.service;

import com.myapp.entity.Customer;
import com.myapp.entity.OrderCustomer;
import com.myapp.entity.OrderItem;
import com.myapp.entity.Review;
import com.myapp.repository.CustomerRepository;
import com.myapp.repository.OrderItemRepository;
import com.myapp.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    // ProductService와 동일한 업로드 경로 설정 사용
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long saveReview(Long orderItemId, String customerLoginId, int rating, String content, MultipartFile reviewImageFile) throws IOException {
        // 1. 주문 상품 확인
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문 상품입니다."));

        // 2. 주문자 정보 확인 (OrderCustomer)
        OrderCustomer orderCustomer = orderItem.getOrder().getOrderCustomer();
        if (orderCustomer == null) {
            throw new IllegalStateException("주문자 정보가 존재하지 않습니다.");
        }

        // 3. 본인 검증: OrderCustomer의 customerId(회원 아이디)와 로그인 아이디 비교
        if (!orderCustomer.getCustomerId().equals(customerLoginId)) {
            throw new IllegalStateException("본인이 주문한 상품에 대해서만 후기를 작성할 수 있습니다.");
        }

        // 4. 배송완료 상태인지 확인
        if (orderItem.getOrder().getDelivery() == null ||
                !"배송완료".equals(orderItem.getOrder().getDelivery().getStatus())) {
            throw new IllegalStateException("배송이 완료된 상품만 후기를 작성할 수 있습니다.");
        }

        // 5. 이미 작성된 후기가 있는지 중복 확인
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            throw new IllegalStateException("이미 후기를 작성한 상품입니다.");
        }

        // 6. 작성자 Customer 엔티티 조회
        Customer customer = customerRepository.findByCustomerId(customerLoginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 7. 후기 객체 생성 및 기본 정보 세팅
        Review review = new Review();
        review.setCustomer(customer);
        review.setProduct(orderItem.getProduct());
        review.setOrderItem(orderItem);
        review.setRating(rating);
        review.setContent(content);

        // 8. 이미지 첨부 파일이 있는 경우 저장 처리
        if (reviewImageFile != null && !reviewImageFile.isEmpty()) {
            String savedFileName = saveFile(reviewImageFile, uploadDir);
            review.setReviewImage(savedFileName);
        }

        return reviewRepository.save(review).getId();
    }

    // 파일 저장 로직 (ProductService 방식과 통일)
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        File dest = new File(uploadDir + savedFileName);
        file.transferTo(dest);
        return savedFileName;
    }

    // 2. 특정 상품의 후기 목록 최신순 조회
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByRegDateDesc(productId);
    }

    // 3. 상품별 평균 별점 조회 (소수점 첫째 자리까지 반올림, 없으면 0.0)
    public double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // 4. 상품별 총 후기 수 조회
    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    // 5. 주문 상품 건에 후기가 이미 작성되었는지 여부 확인
    public boolean hasReview(Long orderItemId) {
        return reviewRepository.existsByOrderItemId(orderItemId);
    }


    // 특정 주문 상품 건의 리뷰 상세 조회
    public Review getReviewByOrderItem(Long orderItemId) {
        return reviewRepository.findByOrderItemId(orderItemId)
                .orElse(null);
    }

    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, String customerLoginId, int rating, String content, MultipartFile reviewImageFile) throws IOException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));

        // 본인 작성 후기인지 검증
        if (!review.getCustomer().getCustomerId().equals(customerLoginId)) {
            throw new IllegalStateException("본인이 작성한 후기만 수정할 수 있습니다.");
        }

        // 별점 및 내용 갱신 (Dirty Checking)
        review.setRating(rating);
        review.setContent(content);

        // 새 이미지가 업로드된 경우 교체
        if (reviewImageFile != null && !reviewImageFile.isEmpty()) {
            String savedFileName = saveFile(reviewImageFile, uploadDir);
            review.setReviewImage(savedFileName);
        }
    }
}