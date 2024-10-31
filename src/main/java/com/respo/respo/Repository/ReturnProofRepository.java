package com.respo.respo.Repository;

import com.respo.respo.Entity.ReturnProofEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnProofRepository extends JpaRepository<ReturnProofEntity, Integer> {
}
