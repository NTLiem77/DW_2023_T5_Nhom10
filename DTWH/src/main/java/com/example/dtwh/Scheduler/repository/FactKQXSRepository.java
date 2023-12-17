package com.example.dtwh.Scheduler.repository;

import com.example.dtwh.Scheduler.model.FactKQXS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// FactKQXSRepository.java
public interface FactKQXSRepository extends JpaRepository<FactKQXS, Long> {

    List<FactKQXS> findByStatusData(boolean statusData);
}
