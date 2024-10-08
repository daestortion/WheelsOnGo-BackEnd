package com.respo.respo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.ChatEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {
    Optional<ChatEntity> findByReport_ReportId(int reportId);
}
