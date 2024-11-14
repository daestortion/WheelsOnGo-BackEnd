package com.respo.respo.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    List<PaymentEntity> findByOrder(OrderEntity order);
    boolean existsByReferenceNumber(String referenceNumber);
}
