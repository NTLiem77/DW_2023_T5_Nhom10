package com.example.dtwh.Scheduler.controller;

import com.example.dtwh.Scheduler.model.CrawlingResult;
import com.example.dtwh.Scheduler.service.DataCrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/data-processing")
public class DataProcessingController {

    @Autowired
    private DataCrawlingService dataCrawlingService;

    @GetMapping("/process-data")
    @ResponseBody
    public String processData() {
        try {
            CrawlingResult result = dataCrawlingService.crawlAndSaveData();

            String fileName = "D:\\dtwh\\DTWH\\data_csv\\" + result.getFileName();
            String dateFileName = "D:\\dtwh\\DTWH\\date_csv\\" + result.getDateFile();

            dataCrawlingService.loadCSVDataToStagingDim(fileName);
            dataCrawlingService.loadCSVDataToDategDim(dateFileName);
            dataCrawlingService.processStagingData();
            dataCrawlingService.stagingToDwh();
            dataCrawlingService.processDateData();
            return "Data processing completed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing data: " + e.getMessage();
        }
    }
}
