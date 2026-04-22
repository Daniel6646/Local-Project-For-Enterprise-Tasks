package com.debuglab.repository;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.debuglab.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    org.springframework.data.domain.Page<OrderEntity> findByStatus(String status, Pageable pageable);

}
