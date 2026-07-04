package com.bear.hospital.controller;

import com.bear.hospital.service.RefundRequestService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("refundRequest")
public class RefundRequestController {
    @Resource
    private RefundRequestService refundRequestService;

    /**
     * Feature 7: 分页查询退费申请
     */
    @GetMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
                                @RequestParam(required = false) Integer status) {
        return ResponseData.success("查询成功", refundRequestService.findAll(pageNumber, size, status));
    }

    /**
     * Feature 7: 创建退费申请
     */
    @PostMapping("create")
    public ResponseData create(@RequestBody com.bear.hospital.pojo.RefundRequest request) {
        return refundRequestService.create(request)
                ? ResponseData.success("退费申请提交成功，等待审核")
                : ResponseData.fail("提交失败");
    }

    /**
     * Feature 7: 审核通过退费（含校验）
     */
    @PostMapping("approve")
    public ResponseData approve(@RequestParam int rfId, @RequestParam String approver) {
        // 前置校验
        String error = refundRequestService.validateBeforeApprove(rfId);
        if (error != null) {
            return ResponseData.fail(error);
        }
        return refundRequestService.approve(rfId, approver)
                ? ResponseData.success("退费已批准")
                : ResponseData.fail("审批失败");
    }

    /**
     * Feature 7: 驳回退费申请
     */
    @PostMapping("reject")
    public ResponseData reject(@RequestParam int rfId, @RequestParam(required = false) String reason) {
        return refundRequestService.reject(rfId, reason)
                ? ResponseData.success("退费已拒绝")
                : ResponseData.fail("操作失败");
    }
}

