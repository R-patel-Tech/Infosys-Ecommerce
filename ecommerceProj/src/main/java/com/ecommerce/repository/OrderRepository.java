package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("""
        select distinct o
        from Order o
        left join fetch o.orderItems oi
        left join fetch oi.product
        where o.user.userId = :userId
        order by o.orderDate desc
    """)
    List<Order> findOrderHistoryByUserId(@Param("userId") Integer userId);
}
