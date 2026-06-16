package com.bear.hospital.controller;

import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.service.BillingService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("addRecord")
    @ResponseBody
    public ResponseData addRecord(@RequestBody BillingRecord record) {
        if (this.billingService.addBillingRecord(record))
            return ResponseData.success("添加收费记录成功");
        return ResponseData.fail("添加收费记录失败");
    }

    @RequestMapping("findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        List<BillingRecord> list = this.billingService.findByOrderId(oId);
        return ResponseData.success("查询成功", list);
    }
}
