package com.example.dtwh.Scheduler.service;

import com.example.dtwh.Scheduler.model.FactKQXS;
import com.example.dtwh.Scheduler.repository.FactKQXSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// FactKQXSService.java
@Service
public class FactKQXSService {

    @Autowired
    private FactKQXSRepository factKQXSRepository;

    public List<FactKQXS> getFactKQXSByStatus(boolean statusData) {
        return factKQXSRepository.findByStatusData(statusData);
    }
}
