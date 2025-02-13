package com.example.order.domain.repository;

import com.example.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyId(String idempotencyId);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = 'CALCULATED' ORDER BY o.creationDate ASC")
    List<Order> findCalculatedOrders(int limit);
}
