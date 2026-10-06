package com.myapp.repository;

import com.myapp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 해당 장바구니의 모든 상품 조회 (최신순)
    List<CartItem> findByCart_CartIdOrderByRegCartItemDateDesc(Long cartId);

    // 해당 장바구니에 동일한 상품이 있는지 조회
    List<CartItem> findByCart_CartIdAndProduct_ProductIdOrderByIdAsc(Long cartId, String productId);

    // 회원 장바구니의 상품 종류 개수 조회
    @Query("select count(distinct ci.product.name) from CartItem ci where ci.cart.customerId = :customerId")
    long countCartProductType(@Param("customerId") String customerId);
}
