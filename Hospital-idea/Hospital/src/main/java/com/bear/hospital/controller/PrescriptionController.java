package com.bear.hospital.controller;

import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.service.PrescriptionService;
import com.bear.hospital.utils.ResponseData;
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

    @GetMapping("/findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        List<PrescriptionDetail> list = prescriptionService.findByOrderId(oId);
        return ResponseData.success("查询成功", list);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/save")
    public ResponseData save(@RequestBody Map<String, Object> params) {
        int oId = Integer.parseInt(params.get("oId").toString());
        List<Map<String, Object>> rawList = (List<Map<String, Object>>) params.get("details");
        List<PrescriptionDetail> details = new ArrayList<>();
        for (Map<String, Object> m : rawList) {
            PrescriptionDetail d = new PrescriptionDetail();
            d.setDrId((String) m.get("drId"));
            if (m.get("pdUsage") != null) d.setPdUsage(m.get("pdUsage").toString());
            if (m.get("pdDosage") != null) d.setPdDosage(m.get("pdDosage").toString());
            if (m.get("pdFrequency") != null) d.setPdFrequency(m.get("pdFrequency").toString());
            if (m.get("pdDays") != null) d.setPdDays(Integer.parseInt(m.get("pdDays").toString()));
            if (m.get("pdQuantity") != null) d.setPdQuantity(Integer.parseInt(m.get("pdQuantity").toString()));
            if (m.get("pdNote") != null) d.setPdNote(m.get("pdNote").toString());
            if (m.get("drPrice") != null) d.setPdPrice(Double.parseDouble(m.get("drPrice").toString()));
            details.add(d);
        }
        prescriptionService.savePrescriptions(oId, details);
        return ResponseData.success("保存成功");
    }
}
