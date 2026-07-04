package com.bear.hospital.controller;

import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.mapper.PrescriptionMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.service.DrugService;
import com.bear.hospital.service.PharmacyDispensingService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pharmacy")
public class PharmacyDispensingController {

    @Autowired
    private PharmacyDispensingService pharmacyDispensingService;
    @Autowired
    private DrugService drugService;
    @Autowired
    private DrugMapper drugMapper;
    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", this.pharmacyDispensingService.findAll(pageNumber, size, status));
    }

    @RequestMapping("dispense")
    public ResponseData dispense(@RequestParam int pdId, @RequestParam String dispenseBy) {
        // 发药同时扣减库存，状态变为待复核(1)
        if (this.pharmacyDispensingService.dispense(pdId, dispenseBy, drugService))
            return ResponseData.success("发药成功，等待复核");
        return ResponseData.fail("发药失败，库存不足");
    }
    @RequestMapping("review")
    public ResponseData review(@RequestParam int pdId, @RequestParam String reviewer) {
        if (this.pharmacyDispensingService.review(pdId, reviewer))
            return ResponseData.success("复核通过，发药完成");
        return ResponseData.fail("复核失败，仅待复核记录可复核");
    }
    @RequestMapping("returnDrug")
    public ResponseData returnDrug(@RequestParam int pdId, @RequestParam String returnBy) {
        if (this.pharmacyDispensingService.returnDrug(pdId, returnBy))
            return ResponseData.success("退药成功");
        return ResponseData.fail("退药失败，仅已发药记录可以退药");
    }

    @RequestMapping("printGuide")
    public ResponseData printGuide(@RequestParam int pdId) {
        PharmacyDispensing pd = pharmacyDispensingService.findById(pdId);
        if (pd == null) return ResponseData.fail("发药记录不存在");
        // 通过处方明细获取药品和订单信息
        PrescriptionDetail detail = prescriptionMapper.selectById(pd.getPrescDetailId());
        if (detail == null) return ResponseData.fail("处方明细不存在");
        Drug drug = drugMapper.selectById(detail.getDrId());
        if (drug == null) return ResponseData.fail("药品信息不存在");
        Map<String, Object> guide = new HashMap<>();
        guide.put("drugName", drug.getDrName());
        guide.put("dosage", detail.getPdDosage());
        guide.put("frequency", detail.getPdFrequency());
        guide.put("route", detail.getPdRoute());
        guide.put("timing", detail.getPdTiming());
        guide.put("usage", detail.getPdUsage());
        guide.put("contraindications", drug.getDrContraindication());
        guide.put("storage", drug.getDrStorage());
        guide.put("adverseReactions", drug.getDrAdverseReaction());
        guide.put("oId", detail.getOId());
        guide.put("quantity", pd.getPdQuantity());
        return ResponseData.success("查询成功", guide);
    }
}
