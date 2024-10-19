package com.respo.respo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.ChatEntity;
import com.respo.respo.Entity.UserEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Integer> {
    Optional<ChatEntity> findByReport_ReportId(int reportId);
        List<ChatEntity> findAllByUsersContaining(UserEntity user);

}
