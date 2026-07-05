package com.bear.hospital.controller;

import com.bear.hospital.pojo.TriageRecord;
import com.bear.hospital.service.TriageRecordService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("triage")
public class TriageRecordController {

    @Autowired
    private TriageRecordService triageRecordService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) Integer level,
        @RequestParam(required = false) String query) {
        return ResponseData.success("查询成功", this.triageRecordService.findAll(pageNumber, size, status, level));
    }

    @RequestMapping("create")
    @ResponseBody
    public ResponseData create(@RequestBody TriageRecord triageRecord) {
        if (this.triageRecordService.createTriage(triageRecord))
            return ResponseData.success("创建分诊记录成功");
        return ResponseData.fail("创建分诊记录失败");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResponseData update(@RequestBody TriageRecord triageRecord) {
        if (this.triageRecordService.updateTriage(triageRecord))
            return ResponseData.success("更新分诊记录成功");
        return ResponseData.fail("更新分诊记录失败");
    }
}
