package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String eMail);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    List<OrderEntity> findAllByUserId(int userId); // Method to find all orders by userId
}
