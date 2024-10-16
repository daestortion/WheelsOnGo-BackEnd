package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.OrderEntity;
import java.util.List;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.CarEntity;
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByUser(UserEntity user);
    List<OrderEntity> findByCar(CarEntity car); // Add this method
    List<OrderEntity> findAllByUser_UserIdAndIsPaid(int userId, boolean isPaid);

}
