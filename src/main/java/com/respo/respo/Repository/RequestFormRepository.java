package com.respo.respo.Repository;

import com.respo.respo.Entity.RequestFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestFormRepository extends JpaRepository<RequestFormEntity, Integer> {
    // You can define custom query methods here if needed
}
