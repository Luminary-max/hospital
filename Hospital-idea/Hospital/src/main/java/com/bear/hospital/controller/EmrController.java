package com.bear.hospital.controller;

import com.bear.hospital.pojo.OutpatientEmr;
import com.bear.hospital.service.EmrService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emr")
public class EmrController {
    @Autowired
    private EmrService emrService;

    @PostMapping("/save")
    public ResponseData save(@RequestBody OutpatientEmr emr) {
        OutpatientEmr result = emrService.saveEmr(emr);
        return result != null ? ResponseData.success("保存成功", result) : ResponseData.fail("保存失败");
    }

    @GetMapping("/findByOrder")
    public ResponseData findByOrder(@RequestParam int oId) {
        OutpatientEmr emr = emrService.findByOrderId(oId);
        return ResponseData.success("查询成功", emr);
    }

    @GetMapping("/findByPatient")
    public ResponseData findByPatient(@RequestParam int pId) {
        List<OutpatientEmr> list = emrService.findByPatientId(pId);
        return ResponseData.success("查询成功", list);
    }
}
