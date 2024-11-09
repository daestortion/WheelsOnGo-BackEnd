package com.respo.respo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByUser(UserEntity user);
    List<OrderEntity> findByCar(CarEntity car); // Add this method
}
