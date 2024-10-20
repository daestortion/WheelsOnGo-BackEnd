package com.respo.respo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.RequestFormEntity;

@Repository
public interface RequestFormRepository extends JpaRepository<RequestFormEntity, Integer> {
    // You can define custom query methods here if needed
    List<RequestFormEntity> findAllByUser_UserIdAndStatus(int userId, String status);
}