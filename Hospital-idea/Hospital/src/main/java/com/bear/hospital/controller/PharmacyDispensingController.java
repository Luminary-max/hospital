package com.bear.hospital.controller;

import com.bear.hospital.service.DrugService;
import com.bear.hospital.service.PharmacyDispensingService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pharmacy")
public class PharmacyDispensingController {

    @Autowired
    private PharmacyDispensingService pharmacyDispensingService;
    @Autowired
    private DrugService drugService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", this.pharmacyDispensingService.findAll(pageNumber, size, status));
    }

    @RequestMapping("dispense")
    public ResponseData dispense(@RequestParam int pdId, @RequestParam String dispenseBy) {
        // 发药同时扣减库存
        if (this.pharmacyDispensingService.dispense(pdId, dispenseBy, drugService))
            return ResponseData.success("发药成功");
        return ResponseData.fail("发药失败，库存不足");
    }
}
