package com.bear.hospital.controller;

import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.pojo.PrescriptionMaster;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.service.PharmacyDispensingService;
import com.bear.hospital.service.PrescriptionService;
import com.bear.hospital.utils.ResponseData;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private PharmacyDispensingService pharmacyDispensingService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        List<PrescriptionDetail> list = prescriptionService.findByOrderId(oId);
        return ResponseData.success("查询成功", list);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/save")
    public ResponseData save(@RequestBody Map<String, Object> params) {
        Object oIdObj = params.get("oId");
        Object rawListObj = params.get("details");
        if (oIdObj == null || rawListObj == null) {
            return ResponseData.fail("参数不完整");
        }
        int oId;
        try {
            oId = Integer.parseInt(oIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseData.fail("oId格式无效");
        }
        List<Map<String, Object>> rawList;
        try {
            rawList = (List<Map<String, Object>>) rawListObj;
        } catch (ClassCastException e) {
            return ResponseData.fail("details格式无效");
        }
        // 提取处方主表信息
        String dId = params.get("dId") != null ? params.get("dId").toString() : null;
        String diagnosis = params.get("pmDiagnosis") != null ? params.get("pmDiagnosis").toString() : null;

        List<PrescriptionDetail> details = new ArrayList<>();
        for (Map<String, Object> m : rawList) {
            PrescriptionDetail d = new PrescriptionDetail();
            d.setDrId((String) m.get("drId"));
            if (m.get("pdUsage") != null) d.setPdUsage(m.get("pdUsage").toString());
            if (m.get("pdRoute") != null) d.setPdRoute(m.get("pdRoute").toString());
            if (m.get("pdDosage") != null) d.setPdDosage(m.get("pdDosage").toString());
            if (m.get("pdFrequency") != null) d.setPdFrequency(m.get("pdFrequency").toString());
            if (m.get("pdTiming") != null) d.setPdTiming(m.get("pdTiming").toString());
            if (m.get("pdSkinTest") != null) d.setPdSkinTest(Integer.parseInt(m.get("pdSkinTest").toString()));
            if (m.get("pdTcmGroupNo") != null) d.setPdTcmGroupNo(m.get("pdTcmGroupNo").toString());
            if (m.get("pdDecoctionMethod") != null) d.setPdDecoctionMethod(m.get("pdDecoctionMethod").toString());
            if (m.get("pdDays") != null) d.setPdDays(Integer.parseInt(m.get("pdDays").toString()));
            if (m.get("pdQuantity") != null) d.setPdQuantity(Integer.parseInt(m.get("pdQuantity").toString()));
            if (m.get("pdNote") != null) d.setPdNote(m.get("pdNote").toString());
            if (m.get("drPrice") != null) d.setPdPrice(Double.parseDouble(m.get("drPrice").toString()));
            details.add(d);
        }
        // 使用新方法保存处方（含主表+明细+pmId关联）
        if (prescriptionService instanceof com.bear.hospital.service.serviceImpl.PrescriptionServiceImpl) {
            ((com.bear.hospital.service.serviceImpl.PrescriptionServiceImpl) prescriptionService)
                .savePrescriptions(oId, details, dId, diagnosis);
        } else {
            prescriptionService.savePrescriptions(oId, details);
        }
        // 自动生成发药记录（关联到处方明细）
        for (PrescriptionDetail d : details) {
            pharmacyDispensingService.createDispensing(oId, d.getDrId(), d.getPdQuantity());
        }
        return ResponseData.success("保存成功");
    }
}
