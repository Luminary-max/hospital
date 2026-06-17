package com.bear.hospital.controller;

import com.bear.hospital.service.AuditLogService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auditLog")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size) {
        return ResponseData.success("查询成功", this.auditLogService.findAll(pageNumber, size));
    }
}
