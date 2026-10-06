package com.myapp.repository;

import com.myapp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 회원 주문 내역 검색
    @Query(value = "select distinct o from orders o " +
            "join o.orderItems oi " +
            "join oi.product p " +
            "where o.orderCustomer.customerId = :customerId " +
            "and (:keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))) " +
            "and (:startDate is null or o.orderDate >= :startDate) " +
            "and (:endDate is null or o.orderDate < :endDate)",
            countQuery = "select count(distinct o) from orders o " +
                    "join o.orderItems oi " +
                    "join oi.product p " +
                    "where o.orderCustomer.customerId = :customerId " +
                    "and (:keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))) " +
                    "and (:startDate is null or o.orderDate >= :startDate) " +
                    "and (:endDate is null or o.orderDate < :endDate)")
    Page<Order> searchOrders(@Param("customerId") String customerId,
                             @Param("keyword") String keyword,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             Pageable pageable);

    // 특정 고객의 모든 주문 내역 조회 (최신순)
    // join fetch를 붙여서 orderItems와 product까지 한 번에 로딩
    @Query("select distinct o from orders o " +
            "join fetch o.orderItems oi " +
            "join fetch oi.product " +
            "where o.orderCustomer.customerId = :customerId " +
            "order by o.orderDate desc")
    List<Order> findOrdersByCustomerId(@Param("customerId") String customerId);

    // 총 주문 건수 조회
    @Query("SELECT count(o) FROM orders o WHERE o.orderCustomer.customerId = :customerId")
    Long countOrder(@Param("customerId") String customerId);

    // 어드민 영역 - 전체 주문 조회 페이징 + 검색 쿼리문
    @Query(value = "select distinct o from orders o " +
            "left join o.delivery d " +
            "where (:orderId is null or o.id = :orderId) " +
            "and (:receiverName is null or lower(d.receiverName) like lower(concat('%', :receiverName, '%'))) " +
            "and (:receiverPhone is null or d.receiverPhone like concat('%', :receiverPhone, '%')) " +
            "and (:deliveryStatus is null or d.status = :deliveryStatus)" +
            "and (:orderStatus is null or o.status = :orderStatus)",
            countQuery = "select count(distinct o) from orders o " +
            "left join o.delivery d " +
            "where (:orderId is null or o.id = :orderId) " +
            "and (:receiverName is null or lower(d.receiverName) like lower(concat('%', :receiverName, '%'))) " +
            "and (:receiverPhone is null or d.receiverPhone like concat('%', :receiverPhone, '%')) " +
            "and (:deliveryStatus is null or d.status = :deliveryStatus)" +
            "and (:orderStatus is null or o.status = :orderStatus)")
    Page<Order> searchAdminOrders(@Param("orderId") Long orderId,
                                  @Param("receiverName") String receiverName,
                                  @Param("receiverPhone") String receiverPhone,
                                  @Param("deliveryStatus") String deliveryStatus,
                                  @Param("orderStatus") String orderStatus,
                                  Pageable pageable);
}