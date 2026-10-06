package com.myapp.controller;

import com.myapp.entity.*;
import com.myapp.repository.CartItemRepository;
import com.myapp.repository.ProductRepository;
import com.myapp.service.CustomerService;
import com.myapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final CustomerService customerService;
    private final CartItemRepository cartItemRepository;

    // 1. [바로구매] 주문서 작성 화면 (단건 -> 통합 템플릿 orderForm.html)
    @GetMapping("/order")
    public String orderFormPage(@RequestParam("productId") Long id,
                                @RequestParam(name = "quantity", defaultValue = "1") int count,
                                Principal principal,
                                Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        Customer customer = customerService.selectCustomer(principal.getName());

        // 단건 주문 상품도 템플릿의 반복문 구조(orderItems)에 맞추어 세팅
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setCount(count);
        item.setOrderPrice(product.getPrice());
        item.setTotalPrice(product.getPrice() * count);

        model.addAttribute("orderType", "DIRECT");
        model.addAttribute("productId", id);
        model.addAttribute("count", count);
        model.addAttribute("orderItems", List.of(item));
        model.addAttribute("totalPrice", product.getPrice() * count);
        model.addAttribute("customer", customer);

        return "order/orderForm";
    }

    // 2. [장바구니] 주문서 작성 화면 (cart.html -> 통합 템플릿 orderForm.html)
    @PostMapping("/order/sheet")
    public String orderSheetFromCart(@RequestParam("cartItemIds") List<Long> cartItemIds,
                                     Principal principal,
                                     Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);
        Customer customer = customerService.selectCustomer(principal.getName());

        List<OrderItem> orderItems = new ArrayList<>();
        int totalPrice = 0;

        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProduct(ci.getProduct());
            oi.setCount(ci.getQuantity());
            oi.setOrderPrice(ci.getProduct().getPrice());
            oi.setTotalPrice(ci.getProduct().getPrice() * ci.getQuantity());
            orderItems.add(oi);

            totalPrice += oi.getTotalPrice();
        }

        model.addAttribute("orderType", "CART");
        model.addAttribute("cartItemIds", cartItemIds);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("customer", customer);

        return "order/orderForm";
    }

    // 3. [통합 결제 처리] 바로구매와 장바구니 주문을 한 곳에서 분기 처리
    @PostMapping("/order/process")
    public String processOrder(@RequestParam("orderType") String orderType,
                               @RequestParam(value = "productId", required = false) Long productId,
                               @RequestParam(value = "count", required = false, defaultValue = "1") int count,
                               @RequestParam(value = "cartItemIds", required = false) List<Long> cartItemIds,
                               @RequestParam("receiverName") String receiverName,
                               @RequestParam("receiverPhone") String receiverPhone,
                               @RequestParam(value = "country", defaultValue = "대한민국") String country,
                               @RequestParam("zipcode") String zipcode,
                               @RequestParam("basicAddress") String basicAddress,
                               @RequestParam("detailAddress") String detailAddress,
                               Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            if ("DIRECT".equals(orderType)) {
                // 단건 바로구매 주문 처리
                OrderForm orderForm = new OrderForm();
                orderForm.setProductId(productId);
                orderForm.setCount(count);

                orderService.order(orderForm, principal.getName(),
                        receiverName, receiverPhone, zipcode, basicAddress, detailAddress);
            } else {
                // 장바구니 주문 처리 (장바구니 아이템 자동 삭제 포함)
                orderService.orderFromCart(cartItemIds, principal.getName(),
                        receiverName, receiverPhone, country, zipcode, basicAddress, detailAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if ("DIRECT".equals(orderType)) {
                return "redirect:/products/detail/" + productId;
            } else {
                return "redirect:/cart/read/" + principal.getName();
            }
        }

        return "redirect:/orders";
    }

    // 4. 내 주문 내역 목록
    @GetMapping("/orders")
    public String orderHistory(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                               @RequestParam(name = "startDate", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(name = "endDate", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               @RequestParam(name = "sortType", defaultValue = "recent") String sortType,
                               Principal principal,
                               Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Page<Order> orderPage;

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            model.addAttribute("dateError", "시작일은 종료일보다 늦을 수 없습니다.");
            orderPage = Page.empty();
        } else {
            orderPage = orderService.getOrderList(
                    principal.getName(), page, keyword, startDate, endDate, sortType);
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortType", sortType);

        return "order/orderHistory";
    }

    // 5. 주문 취소
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/orders";
    }

    // 6. 주문내역 상세페이지
    @GetMapping("/order/{orderId}")
    public String orderDetailPage(@PathVariable("orderId") Long orderId,
                                  Principal principal,
                                  Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderDetail(orderId, principal.getName());

        int totalPrice = order.getOrderItems().stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("totalPrice", totalPrice);

        return "order/orderDetail";
    }

    //##################################################################################################################
    // [ 어드민 영역 ]

    // [ 주문 전체 조회 및 다중 조건 검색 (10개 단위 페이징) ]
    @GetMapping("/orders/admin/list")
    public String orderListPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String deliveryStatus,
            @RequestParam(required = false) String orderStatus,
            Model model) {

        Page<Order> paging = orderService.selectAdminOrderList(
                page, orderId, receiverName, receiverPhone, deliveryStatus, orderStatus);

        model.addAttribute("paging", paging);
        model.addAttribute("orderId", orderId != null ? orderId : "");
        model.addAttribute("receiverName", receiverName != null ? receiverName : "");
        model.addAttribute("receiverPhone", receiverPhone != null ? receiverPhone : "");
        model.addAttribute("deliveryStatus", deliveryStatus != null ? deliveryStatus : "");
        model.addAttribute("orderStatus", orderStatus != null ? orderStatus : "");

        return "order/selectOrderList";
    }

    // [ 배송 상태 변경 ]
    @PostMapping("/orders/admin/updateDeliveryStatus")
    public String updateDeliveryStatus(@RequestParam("orderId") Long orderId,
                                       @RequestParam("status") String status,
                                       @RequestHeader(value = "Referer", required = false) String referer) {
        orderService.updateDeliveryStatus(orderId, status);
        if (referer != null && referer.contains("/orders/admin/list")) {
            return "redirect:" + referer;
        }
        return "redirect:/orders/admin/list";
    }

    // [ 주문 상태 변경 ]
    @PostMapping("/orders/admin/updateOrderStatus")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status,
                                    @RequestHeader(value = "Referer", required = false) String referer) {
        orderService.updateOrderStatus(orderId, status);
        if (referer != null && referer.contains("/orders/admin/list")) {
            return "redirect:" + referer;
        }
        return "redirect:/orders/admin/list";
    }

    // [ 주문 상세 조회 ]
    @GetMapping("/orders/admin/selectOrderDetail")
    public String selectOrderDetail(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.selectOrderDetail(orderId);
        int totalPrice = order.getOrderItems().stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();
        model.addAttribute("order", order);
        model.addAttribute("totalPrice", totalPrice);
        return "order/adminOrderDetail";
    }

    // [ 주문 상세정보 수정 ]
    @PostMapping("/orders/admin/updateCustomerDetail")
    public String updateCustomerDetail(@ModelAttribute Order order,
                                       @RequestHeader(value = "Referer", required = false) String referer) {
        orderService.updateCustomerDetail(order);
        if (order != null && order.getId() != null) {
            return "redirect:/orders/admin/selectOrderDetail?orderId=" + order.getId();
        }
        if (referer != null && referer.contains("/orders/admin/list")) {
            return "redirect:" + referer;
        }
        return "redirect:/orders/admin/list";
    }

}