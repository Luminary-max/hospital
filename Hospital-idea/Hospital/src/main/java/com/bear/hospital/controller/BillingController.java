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

    /** 按病历查找收费记录（正确业务流程） */
    @RequestMapping("findByEmr")
    public ResponseData findByEmr(@RequestParam int emrId) {
        List<BillingRecord> list = this.billingService.findByEmrId(emrId);
        return ResponseData.success("查询成功", list);
    }

    /**
     * Feature 9: 收费员日结统计
     */
    @RequestMapping("dailySummary")
    public ResponseData dailySummary(@RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = com.bear.hospital.utils.TodayUtil.getTodayYmd();
        }
        return ResponseData.success("查询成功", this.billingService.dailySummary(date));
    }

    /**
     * 日结统计别名（前端使用）
     */
    @RequestMapping("dailySettlement")
    public ResponseData dailySettlement(@RequestParam(required = false) String date) {
        return dailySummary(date);
    }
}
