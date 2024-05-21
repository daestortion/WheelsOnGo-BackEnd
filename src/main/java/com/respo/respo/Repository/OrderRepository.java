package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer>{

}
