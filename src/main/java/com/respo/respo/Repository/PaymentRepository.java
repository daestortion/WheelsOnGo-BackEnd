package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.respo.respo.Entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer>{
 
}
