package com.bear.hospital.controller;

import com.bear.hospital.pojo.DiagnosisDict;
import com.bear.hospital.service.DiagnosisDictService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnosisDict")
public class DiagnosisDictController {
    @Autowired
    private DiagnosisDictService diagnosisDictService;

    @GetMapping("/findAll")
    public ResponseData findAll(@RequestParam(required = false) String query) {
        return ResponseData.success("查询成功", diagnosisDictService.findAll(query));
    }

    @PostMapping("/add")
    public ResponseData add(@RequestBody DiagnosisDict dict) {
        if (diagnosisDictService.addDiagnosisDict(dict)) {
            return ResponseData.success("添加成功");
        }
        return ResponseData.fail("添加失败");
    }

    @PostMapping("/modify")
    public ResponseData modify(@RequestBody DiagnosisDict dict) {
        if (diagnosisDictService.modifyDiagnosisDict(dict)) {
            return ResponseData.success("修改成功");
        }
        return ResponseData.fail("修改失败");
    }

    @GetMapping("/delete")
    public ResponseData delete(@RequestParam Integer ddId) {
        if (diagnosisDictService.deleteDiagnosisDict(ddId)) {
            return ResponseData.success("删除成功");
        }
        return ResponseData.fail("删除失败");
    }
}
