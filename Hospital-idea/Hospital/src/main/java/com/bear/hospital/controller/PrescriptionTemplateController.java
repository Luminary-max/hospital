package com.bear.hospital.controller;

import com.bear.hospital.pojo.PrescriptionTemplate;
import com.bear.hospital.service.PrescriptionTemplateService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescriptionTemplate")
public class PrescriptionTemplateController {
    @Autowired
    private PrescriptionTemplateService prescriptionTemplateService;

    @GetMapping("/findAll")
    public ResponseData findAll(@RequestParam(required = false) String dId) {
        return ResponseData.success("查询成功", prescriptionTemplateService.findAll(dId));
    }

    @PostMapping("/add")
    public ResponseData add(@RequestBody PrescriptionTemplate template) {
        if (prescriptionTemplateService.addPrescriptionTemplate(template)) {
            return ResponseData.success("添加成功");
        }
        return ResponseData.fail("添加失败");
    }

    @PostMapping("/modify")
    public ResponseData modify(@RequestBody PrescriptionTemplate template) {
        if (prescriptionTemplateService.modifyPrescriptionTemplate(template)) {
            return ResponseData.success("修改成功");
        }
        return ResponseData.fail("修改失败");
    }

    @GetMapping("/delete")
    public ResponseData delete(@RequestParam Integer ptId) {
        if (prescriptionTemplateService.deletePrescriptionTemplate(ptId)) {
            return ResponseData.success("删除成功");
        }
        return ResponseData.fail("删除失败");
    }
}
