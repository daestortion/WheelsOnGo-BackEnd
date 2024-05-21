package com.respo.respo.Repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.AdminEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer>{
    boolean existsByUsername(String username);
    Optional<AdminEntity> findByUsername(String username);
}
