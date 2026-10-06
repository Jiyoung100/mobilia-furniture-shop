package com.myapp.service;

import com.myapp.entity.OrderForm;
import com.myapp.entity.*;
import com.myapp.repository.CartItemRepository;
import com.myapp.repository.CustomerRepository;
import com.myapp.repository.OrderRepository;
import com.myapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    // 1. 상품 바로 주문하기 (수정된 배송지 반영)
    public Long order(OrderForm orderForm, String customerId,
                      String receiverName, String receiverPhone,
                      String zipcode, String basicAddress, String detailAddress) {

        // 상품 및 회원 조회
        Product product = productRepository.findById(orderForm.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 재고 검증
        if (product.getStock() < orderForm.getCount()) {
            throw new IllegalStateException("상품의 재고가 부족합니다. (현재 재고: " + product.getStock() + ")");
        }

        // 1) 주문자 스냅샷 생성
        OrderCustomer orderCustomer = new OrderCustomer();
        orderCustomer.setCustomerId(customer.getCustomerId());
        orderCustomer.setName(customer.getName());
        orderCustomer.setPhone(customer.getPhone());
        if (customer.getAddress() != null) {
            Address customerAddressSnapshot = new Address();
            customerAddressSnapshot.setCountry(customer.getAddress().getCountry());
            customerAddressSnapshot.setZipcode(customer.getAddress().getZipcode());
            customerAddressSnapshot.setBasicAddress(customer.getAddress().getBasicAddress());
            customerAddressSnapshot.setDetailAddress(customer.getAddress().getDetailAddress());
            orderCustomer.setAddress(customerAddressSnapshot);
        }

        // 2) 수정된 주소 정보로 배송지 엔티티 생성
        Address deliveryAddress = new Address();
        deliveryAddress.setCountry("대한민국");
        deliveryAddress.setZipcode(zipcode);
        deliveryAddress.setBasicAddress(basicAddress);
        deliveryAddress.setDetailAddress(detailAddress);

        // 3) 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setReceiverName(receiverName);
        delivery.setReceiverPhone(receiverPhone);
        delivery.setAddress(deliveryAddress);
        delivery.setStatus("상품준비중");

        // 4) 재고 차감
        product.setStock(product.getStock() - orderForm.getCount());

        // 5) 주문 항목 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setCount(orderForm.getCount());
        orderItem.setOrderPrice(product.getPrice());
        orderItem.setTotalPrice(product.getPrice() * orderForm.getCount());

        // 6) 주문 엔티티 생성 및 연결
        Order order = new Order();
        order.setOrderCustomer(orderCustomer);
        order.setDelivery(delivery);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("주문완료");

        order.getOrderItems().add(orderItem);
        orderItem.setOrder(order);

        orderRepository.save(order);
        return order.getId();
    }

    // 2. 회원 주문 내역 목록 조회
    @Transactional(readOnly = true)
    public Page<Order> getOrderList(String customerId, int page, String keyword,
                                    LocalDate startDate, LocalDate endDate, String sortType) {
        String searchKeyword = keyword == null ? "" : keyword.trim();

        // 시작일은 해당 날짜의 00:00부터 검색
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();

        // 종료일은 다음날 00:00 미만으로 검색
        LocalDateTime end = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        // 주문일 정렬
        Sort sort;
        if ("old".equals(sortType)) {
            sort = Sort.by("orderDate").ascending();
        } else {
            sort = Sort.by("orderDate").descending();
        }

        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<Order> orders = orderRepository.searchOrders(
                customerId, searchKeyword, start, end, pageable
        );

        // 화면에서 주문 상품을 사용할 수 있도록 조회
        for (Order order : orders.getContent()) {
            order.getOrderItems().size();
        }

        return orders;
    }

    // 3. 주문 취소
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 존재하지 않습니다."));

        // 이미 취소된 주문
        if ("주문취소".equals(order.getStatus())) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        // 상품준비중일 때만 주문 취소 가능
        if (!"상품준비중".equals(order.getDelivery().getStatus())) {
            throw new IllegalStateException("배송중이거나 배송완료된 주문은 취소할 수 없습니다.");
        }

        order.setStatus("주문취소");

        // 재고 복구
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getCount());
        }
    }

    // 4. 장바구니 선택/전체 상품 주문 (수정된 배송지 반영)
    public Long orderFromCart(List<Long> cartItemIds,
                              String customerId,
                              String receiverName,
                              String receiverPhone,
                              String country,
                              String zipcode,
                              String basicAddress,
                              String detailAddress) {

        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1) 주문자 스냅샷 생성
        OrderCustomer orderCustomer = new OrderCustomer();
        orderCustomer.setCustomerId(customer.getCustomerId());
        orderCustomer.setName(customer.getName());
        orderCustomer.setPhone(customer.getPhone());
        if (customer.getAddress() != null) {
            Address customerAddressSnapshot = new Address();
            customerAddressSnapshot.setCountry(customer.getAddress().getCountry());
            customerAddressSnapshot.setZipcode(customer.getAddress().getZipcode());
            customerAddressSnapshot.setBasicAddress(customer.getAddress().getBasicAddress());
            customerAddressSnapshot.setDetailAddress(customer.getAddress().getDetailAddress());
            orderCustomer.setAddress(customerAddressSnapshot);
        }

        // 2) 수정된 주소 정보를 바탕으로 배송지(Address) 엔티티 새로 생성
        Address deliveryAddress = new Address();
        deliveryAddress.setCountry(country != null && !country.isBlank() ? country : "대한민국");
        deliveryAddress.setZipcode(zipcode);
        deliveryAddress.setBasicAddress(basicAddress);
        deliveryAddress.setDetailAddress(detailAddress);

        // 3) 배송 정보 생성 (수정된 수령인/전화번호 세팅)
        Delivery delivery = new Delivery();
        delivery.setReceiverName(receiverName);
        delivery.setReceiverPhone(receiverPhone);
        delivery.setAddress(deliveryAddress);
        delivery.setStatus("상품준비중");

        // 4) 장바구니 상품들로 OrderItem 리스트 생성 및 재고 차감
        Order order = new Order();
        order.setOrderCustomer(orderCustomer);
        order.setDelivery(delivery);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("주문완료");

        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int count = cartItem.getQuantity();

            if (product.getStock() < count) {
                throw new IllegalStateException("[" + product.getName() + "] 상품의 재고가 부족합니다. (현재 재고: " + product.getStock() + ")");
            }

            // 재고 차감
            product.setStock(product.getStock() - count);

            // OrderItem 조립
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setCount(count);
            orderItem.setOrderPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice() * count);

            order.getOrderItems().add(orderItem);
        }

        // 5) 주문 저장 (Cascade로 Delivery, Address, OrderItem 함께 저장)
        orderRepository.save(order);

        // 6) 주문 완료된 장바구니 품목 일괄 삭제
        cartItemRepository.deleteAllById(cartItemIds);

        return order.getId();
    }

    // 5. 주문정보 조회 에러검증로직
    @Transactional(readOnly = true)
    public Order getOrderDetail(Long orderId, String customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 본인 주문인지 검증 (관리자 권한 예외 처리)
        Customer customer = customerRepository.findByCustomerId(customerId).orElse(null);
        boolean isAdmin = customer != null && (customer.getRole() == Role.ADMIN || customer.getRole() == Role.MANAGER);
        if (!isAdmin && !order.getOrderCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalStateException("해당 주문을 조회할 권한이 없습니다.");
        }

        // 1. 주문 항목 및 상품 초기화
        order.getOrderItems().forEach(item -> {
            if (item.getProduct() != null) {
                item.getProduct().getName();
                item.getProduct().getFileName1();
            }
        });

        // 2. 배송 정보 및 주소 초기화
        if (order.getDelivery() != null && order.getDelivery().getAddress() != null) {
            order.getDelivery().getAddress().getZipcode(); // 프록시 강제 로딩
        }

        return order;
    }

    // ##########################################################################
    // 어드민 영역

    // 관리자 주문 다중 조건 검색 + 페이징 (10개 단위)
    @Transactional(readOnly = true)
    public Page<Order> selectAdminOrderList(int page, Long orderId, String receiverName,
                                            String receiverPhone, String deliveryStatus, String orderStatus) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "orderDate"));

        Page<Order> orders = orderRepository.searchAdminOrders(
                orderId,
                cleanSearchValue(receiverName),
                cleanSearchValue(receiverPhone),
                cleanSearchValue(deliveryStatus),
                cleanSearchValue(orderStatus),
                pageable
        );

        // 뷰 렌더링에 필요한 연관 엔티티 로딩
        for (Order order : orders.getContent()) {
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(item -> {
                    if (item.getProduct() != null) {
                        item.getProduct().getName();
                        item.getProduct().getFileName1();
                    }
                });
            }
            if (order.getDelivery() != null) {
                order.getDelivery().getStatus();
            }
        }
        return orders;
    }

    // 검색어 공백 정리 메소드
    private String cleanSearchValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    // 관리자 배송 상태 변경
    public void updateDeliveryStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getDelivery() != null) {
            order.getDelivery().setStatus(status);
            if ("배송완료".equals(status)) {
                order.getDelivery().setDeliveryDate(LocalDateTime.now());
            }
        }
    }

    // 관리자 주문 상태 변경
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if(order.getStatus().equals(status)) {
            return;
        }

        // 취소 후 다시 주문 상태 변경 불가
        if("주문취소".equals(order.getStatus())) {
            throw new IllegalArgumentException("이미 주문이 취소된 상태입니다.");
        }

        // 주문 취소 시 재고 복구
        if("주문취소".equals(status)) {
            for(OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if(product != null) {
                    product.setStock(product.getStock() + item.getCount());
                }
            }
        }
        order.setStatus(status);
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public Order selectOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getOrderCustomer() != null && order.getOrderCustomer().getAddress() != null) {
            order.getOrderCustomer().getAddress().getZipcode();
        }
        if (order.getDelivery() != null && order.getDelivery().getAddress() != null) {
            order.getDelivery().getAddress().getZipcode();
        }
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> {
                if (item.getProduct() != null) {
                    item.getProduct().getName();
                    item.getProduct().getFileName1();
                }
            });
        }

        return order;
    }

    // 관리자 상세페이지 수정
    @Transactional
    public void updateCustomerDetail(Order order) {
        if(order == null || order.getId() == null) {
            throw new IllegalArgumentException("유효하지 않은 주문 정보입니다.");
        }

        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 배송 및 수령인 정보 수정 메소드 위임
        if(order.getDelivery() != null) {
            updateDeliveryDetails(existingOrder, order.getDelivery());
        }

        // 주문상태 변경 메소드 위임
        if(order.getStatus() != null) {
            updateOrderStatus(existingOrder.getId(), order.getStatus());
        }
    }

    // 배송 및 수령인 정보 갱신
    private void updateDeliveryDetails(Order existingOrder, Delivery newDelivery) {
        if(existingOrder.getDelivery() == null) {
            existingOrder.setDelivery(new Delivery());
        }
        Delivery delivery = existingOrder.getDelivery();

        if(newDelivery.getReceiverName() != null) {
            delivery.setReceiverName(newDelivery.getReceiverName().trim());
        }
        if(newDelivery.getReceiverPhone() != null) {
            delivery.setReceiverPhone(newDelivery.getReceiverPhone().trim());
        }
        if(newDelivery.getAddress() != null) {
            updateAddressDetails(delivery, newDelivery.getAddress());
        }
        if(newDelivery.getStatus() != null) {
            updateDeliveryStatus(existingOrder.getId(), newDelivery.getStatus());
        }
    }

    // 주소 정보 갱신
    private void updateAddressDetails(Delivery delivery, Address newAddress) {
        if(delivery.getAddress() == null) {
            delivery.setAddress(new Address());
        }
        Address address = delivery.getAddress();
        String country = newAddress.getCountry();

        address.setCountry(country != null && !country.isBlank() ? country.trim() : "대한민국");
        address.setZipcode(newAddress.getZipcode());
        address.setBasicAddress(newAddress.getBasicAddress());
        address.setDetailAddress(newAddress.getDetailAddress());
    }

}