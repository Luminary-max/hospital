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

    @GetMapping("findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        return ResponseData.success("查询成功", invoiceRecordService.findByOrderId(oId));
    }

    @GetMapping("findByBilling")
    public ResponseData findByBilling(@RequestParam int brId) {
        return ResponseData.success("查询成功", invoiceRecordService.findByBillingId(brId));
    }

    @GetMapping("findByDate")
    public ResponseData findByDate(@RequestParam(required = false) String date) {
        if (date == null || date.isEmpty()) {
            date = TodayUtil.getTodayYmd();
        }
        return ResponseData.success("查询成功", invoiceRecordService.findByDate(date));
    }

    @GetMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", invoiceRecordService.findAll(pageNumber, size, status));
    }

    @GetMapping("void")
    public ResponseData voidInvoice(@RequestParam int invId, @RequestParam String reason,
        @RequestParam(required = false) String operator) {
        if (invoiceRecordService.voidInvoice(invId, operator != null ? operator : "管理员", reason))
            return ResponseData.success("发票已作废");
        return ResponseData.fail("作废失败");
    }
}
