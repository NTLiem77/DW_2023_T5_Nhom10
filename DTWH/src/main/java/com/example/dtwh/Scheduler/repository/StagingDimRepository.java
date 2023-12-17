package com.example.dtwh.Scheduler.repository;

import com.example.dtwh.Scheduler.model.StagingDim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagingDimRepository extends JpaRepository<StagingDim, Long> {
    // Custom queries if needed
}
