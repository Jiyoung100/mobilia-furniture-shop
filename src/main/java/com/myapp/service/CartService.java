package com.myapp.service;

import com.myapp.entity.Cart;
import com.myapp.entity.CartItem;
import com.myapp.entity.Product;
import com.myapp.repository.CartItemRepository;
import com.myapp.repository.CartRepository;
import com.myapp.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // 장바구니 상품 추가
    public void addCart(String customerId, String productId, int quantity) {

        // 1. 회원 장바구니 조회
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElse(null);

        // 장바구니가 없으면 생성
        if (cart == null) {

            cart = new Cart();
            cart.setCustomerId(customerId);
            cart.setRegCartDate(LocalDateTime.now());

            cart = cartRepository.save(cart);
        }

        // 2. 상품 조회
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("상품을 찾을 수 없습니다.")
                );

        // 장바구니 추가 수량 검사
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException(
                    "현재 구매 가능한 최대 수량은 " + product.getStock() + "개입니다."
            );
        }

        // 3. 기존 장바구니에서 동일한 상품 조회
        List<CartItem> sameItems = cartItemRepository.findByCart_CartIdAndProduct_ProductIdOrderByIdAsc(
                cart.getCartId(),productId
        );

        if (sameItems.isEmpty()) {
            // 동일 상품이 없다면 새 항목 생성
            CartItem cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(product.getPrice() * quantity);
            cartItem.setRegCartItemDate(LocalDateTime.now());

            cartItemRepository.save(cartItem);
        } else {

            // 동일 상품이 있다면 기존 수량 합산
            CartItem cartItem = sameItems.get(0);

            int totalQuantity  = quantity;

            for (CartItem item : sameItems) {
                totalQuantity += item.getQuantity();
            }

            // 기존 수량 + 추가 수량이 재고를 넘는지 검사
            if (totalQuantity > product.getStock()) {
                throw new IllegalArgumentException(
                        "현재 구매 가능한 최대 수량은 " + product.getStock() + "개입니다."
                );
            }

            cartItem.setQuantity(totalQuantity);
            cartItem.setTotalPrice(product.getPrice() * totalQuantity);

            cartItemRepository.save(cartItem);

            // 기존에 중복 생성된 나머지 행 제거
            for (int i = 1; i < sameItems.size(); i++) {
                cartItemRepository.delete(sameItems.get(i));
            }
        }

        cartItemRepository.flush();

        // 4. 장바구니 전체 수량 및 금액 갱신
        updateCartTotal(cart);
    }

    // 화면에 출력할 장바구니 상품 조회
    public List<CartItem> getCartItems(Cart cart) {

        if (cart == null) {
            return List.of();
        }

        return cartItemRepository.findByCart_CartIdOrderByRegCartItemDateDesc(
                cart.getCartId()
        );
    }

    // 장바구니 조회
    public Cart getCartByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId).orElse(null);
    }

    // 장바구니 상품 수량 변경
    public void updateCartQuantityByCustomerId(
            int quantity, String customerId, Long cartItemId) {

        // 1. 수량 검사
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }

        // 2. 회원 장바구니 조회
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니가 없습니다.")
                );

        // 3. 수량을 변경할 CartItem 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니 상품이 없습니다.")
                );

        // 4. 해당 회원의 장바구니 상품인지 확인
        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("장바구니 상품이 일치하지 않습니다.");
        }

        // 상품 재고 확인
        if (quantity > cartItem.getProduct().getStock()) {
            throw new IllegalArgumentException(
                    "현재 구매 가능한 최대 수량은 " + cartItem.getProduct().getStock() + "개입니다."
            );
        }

        // 5. 수량 변경
        cartItem.setQuantity(quantity);

        // 6. 상품별 합계 금액 변경
        cartItem.setTotalPrice(cartItem.getProduct().getPrice() * quantity);

        // 7. 변경된 CartItem 저장
        cartItemRepository.saveAndFlush(cartItem);

        // 8. 장바구니 전체 수량 및 금액 갱신
        updateCartTotal(cart);
    }

    // 장바구니 전체 수량 및 금액 갱신
    private void updateCartTotal(Cart cart) {
        List<CartItem> items =
                cartItemRepository.findByCart_CartIdOrderByRegCartItemDateDesc(
                        cart.getCartId()
                );

        int grandTotalCount = 0;
        int grandTotalPrice = 0;

        for (CartItem item : items) {
            grandTotalCount += item.getQuantity();
            grandTotalPrice += item.getTotalPrice();
        }

        cart.setGrandTotalCount(grandTotalCount);
        cart.setGrandTotalPrice(grandTotalPrice);

        cartRepository.save(cart);
    }

    // 장바구니 개별 삭제
    public void deleteCartItem(String customerId, Long cartItemId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니가 없습니다.")
                );

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니 상품이 없습니다.")
                );

        // 해당 고객의 장바구니 상품인지 확인
        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("장바구니 상품이 일치하지 않습니다.");
        }

        // 상품 삭제
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();

        // 장바구니가 비었으면 Cart도 삭제
        if (getCartItems(cart).isEmpty()) {
            cartRepository.delete(cart);
        } else {
            updateCartTotal(cart);
        }
    }

    // 장바구니 전체 삭제
    public void deleteCart(String customerId) {
        // 1. 회원 장바구니 조회
        Cart cart = cartRepository.findByCustomerId(customerId).orElse(null);

        if (cart == null) {
            return;
        }

        // 2. 장바구니에 등록된 CartItem 조회
        List<CartItem> items = getCartItems(cart);

        // 3. CartItem 먼저 삭제
        cartItemRepository.deleteAll(items);
        cartItemRepository.flush();

        // 4. Cart 삭제
        cartRepository.delete(cart);
    }


    // 장바구니 선택 삭제
    public void deleteCartItemSelect(String customerId, List<Long> idList) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("장바구니가 없습니다.")
                );

        for (Long id : idList) {
            CartItem cartItem = cartItemRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("장바구니 상품이 없습니다.")
                    );

            // 다른 고객의 상품이 섞여 있는지 검사
            if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
                throw new IllegalArgumentException("장바구니 상품이 일치하지 않습니다.");
            }

            cartItemRepository.delete(cartItem);
        }

        cartItemRepository.flush();

        if (getCartItems(cart).isEmpty()) {
            cartRepository.delete(cart);
        } else {
            updateCartTotal(cart);
        }
    }

    // 장바구니 상품 종류 개수 조회
    public long getCartProductTypeCount(String customerId) {
        return cartItemRepository.countCartProductType(customerId);
    }
}