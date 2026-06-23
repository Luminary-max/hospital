package com.bear.hospital.controller;

import com.bear.hospital.pojo.EmrTemplate;
import com.bear.hospital.service.EmrTemplateService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emrTemplate")
public class EmrTemplateController {
    @Autowired
    private EmrTemplateService emrTemplateService;

    @GetMapping("/findAll")
    public ResponseData findAll(@RequestParam(required = false) String dept) {
        return ResponseData.success("查询成功", emrTemplateService.findAll(dept));
    }

    @PostMapping("/add")
    public ResponseData add(@RequestBody EmrTemplate template) {
        if (emrTemplateService.addEmrTemplate(template)) {
            return ResponseData.success("添加成功");
        }
        return ResponseData.fail("添加失败");
    }

    @PostMapping("/modify")
    public ResponseData modify(@RequestBody EmrTemplate template) {
        if (emrTemplateService.modifyEmrTemplate(template)) {
            return ResponseData.success("修改成功");
        }
        return ResponseData.fail("修改失败");
    }

    @GetMapping("/delete")
    public ResponseData delete(@RequestParam Integer etId) {
        if (emrTemplateService.deleteEmrTemplate(etId)) {
            return ResponseData.success("删除成功");
        }
        return ResponseData.fail("删除失败");
    }
}
