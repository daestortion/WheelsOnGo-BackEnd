package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.respo.respo.Entity.ActivityLogEntity;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Integer> {
    List<ActivityLogEntity> findByActionContaining(String action);
}