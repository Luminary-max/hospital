package com.bear.hospital.controller;

import com.bear.hospital.pojo.QueueNumber;
import com.bear.hospital.service.QueueService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController {
    @Autowired
    private QueueService queueService;

    @GetMapping("/takeNumber")
    public ResponseData takeNumber(@RequestParam int oId, @RequestParam int pId, @RequestParam String dId) {
        String qNumber = queueService.takeNumber(oId, pId, dId);
        return qNumber != null ? ResponseData.success("取号成功", qNumber) : ResponseData.fail("取号失败");
    }

    @GetMapping("/callNext")
    public ResponseData callNext(@RequestParam String dId, @RequestParam(required = false) Integer reQueue) {
        QueueNumber qn = queueService.callNext(dId, reQueue);
        return qn != null ? ResponseData.success("呼叫成功", qn) : ResponseData.success("队列已空", null);
    }

    @GetMapping("/skipNumber")
    public ResponseData skipNumber(@RequestParam int qId) {
        boolean ok = queueService.skipNumber(qId);
        return ok ? ResponseData.success("已标记为过号") : ResponseData.fail("操作失败");
    }

    @GetMapping("/listByDoctor")
    public ResponseData listByDoctor(@RequestParam String dId) {
        List<QueueNumber> list = queueService.listByDoctorToday(dId);
        return ResponseData.success("查询成功", list);
    }

    @GetMapping("/listByPatient")
    public ResponseData listByPatient(@RequestParam int pId) {
        QueueNumber qn = queueService.findByPatientToday(pId);
        return ResponseData.success("查询成功", qn != null ? List.of(qn) : List.of());
    }

    @GetMapping("/deptStats")
    public ResponseData deptStats() {
        return ResponseData.success("查询成功", queueService.getDeptQueueStats());
    }
}
