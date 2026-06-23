package com.bear.hospital.controller;

import com.bear.hospital.pojo.DeliveryRequest;
import com.bear.hospital.service.DeliveryRequestService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("delivery")
public class DeliveryRequestController {

    @Autowired
    private DeliveryRequestService deliveryRequestService;

    @PostMapping("create")
    public ResponseData create(@RequestBody DeliveryRequest request) {
        DeliveryRequest result = deliveryRequestService.create(request);
        return ResponseData.success("创建送药申请成功", result);
    }

    @GetMapping("findByPatient")
    public ResponseData findByPatient(@RequestParam Integer pId) {
        return ResponseData.success("查询成功", deliveryRequestService.findByPatient(pId));
    }

    @PostMapping("pickup")
    public ResponseData pickup(@RequestParam Integer dlId) {
        if (deliveryRequestService.pickup(dlId))
            return ResponseData.success("取药成功");
        return ResponseData.fail("取药失败，取药记录状态不正确");
    }

    @PostMapping("cancel")
    public ResponseData cancel(@RequestParam Integer dlId) {
        if (deliveryRequestService.cancel(dlId))
            return ResponseData.success("取消成功");
        return ResponseData.fail("取消失败");
    }

    @GetMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", deliveryRequestService.findAll(pageNumber, size, status));
    }
}
