package com.bear.hospital.controller;

import com.bear.hospital.service.TriageRecordService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("triage")
public class TriageRecordController {

    @Autowired
    private TriageRecordService triageRecordService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size) {
        return ResponseData.success("查询成功", this.triageRecordService.findAll(pageNumber, size));
    }
}
