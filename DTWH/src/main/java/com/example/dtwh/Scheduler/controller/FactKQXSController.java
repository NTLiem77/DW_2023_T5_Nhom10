package com.example.dtwh.Scheduler.controller;

import com.example.dtwh.Scheduler.model.FactKQXS;
import com.example.dtwh.Scheduler.service.FactKQXSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FactKQXSController {

    @Autowired
    private FactKQXSService factKQXSService;

    @GetMapping("/fact-kqxs-true")
    public List<FactKQXS> getTrueStatusData() {
        return factKQXSService.getFactKQXSByStatus(true);
    }
}