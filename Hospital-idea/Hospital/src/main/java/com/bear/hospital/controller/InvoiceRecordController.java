package com.bear.hospital.controller;

import com.bear.hospital.service.InvoiceRecordService;
import com.bear.hospital.utils.ResponseData;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("invoiceRecord")
public class InvoiceRecordController {
    @Resource
    private InvoiceRecordService invoiceRecordService;

    /**
     * Feature 8: 根据订单查询发票
     */
    @GetMapping("findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        return ResponseData.success("查询成功", invoiceRecordService.findByOrderId(oId));
    }

    /**
     * Feature 8: 根据缴费记录查询发票
     */
    @GetMapping("findByBilling")
    public ResponseData findByBilling(@RequestParam int brId) {
        return ResponseData.success("查询成功", invoiceRecordService.findByBillingId(brId));
    }

    /**
     * Feature 8: 根据日期查询发票
     */
    @GetMapping("findByDate")
    public ResponseData findByDate(@RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = TodayUtil.getTodayYmd();
        }
        return ResponseData.success("查询成功", invoiceRecordService.findByDate(date));
    }
}
