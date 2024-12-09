package com.respo.respo.Repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Entity.OrderEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    List<PaymentEntity> findByOrder(OrderEntity order);
    List<PaymentEntity> findByOrderOrderId(int orderId);

}
