package com.bear.hospital.controller;

import com.bear.hospital.pojo.TriageRecord;
import com.bear.hospital.service.TriageRecordService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("triage")
public class TriageRecordController {

    @Autowired
    private TriageRecordService triageRecordService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer level) {
        return ResponseData.success("查询成功", this.triageRecordService.findAll(pageNumber, size, status, level));
    }

    @RequestMapping("create")
    @ResponseBody
    public ResponseData create(@RequestBody TriageRecord triageRecord) {
        if (this.triageRecordService.createTriage(triageRecord))
            return ResponseData.success("创建分诊记录成功");
        return ResponseData.fail("创建分诊记录失败");
    }
}
