package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.OrderCheckMapper;
import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.mapper.PharmacyDispensingMapper;
import com.bear.hospital.mapper.RefundRequestMapper;
import com.bear.hospital.pojo.OrderCheck;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.pojo.RefundRequest;
import com.bear.hospital.service.RefundRequestService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service("RefundRequestService")
public class RefundRequestServiceImpl implements RefundRequestService {

    @Resource
    private RefundRequestMapper refundRequestMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderCheckMapper orderCheckMapper;
    @Resource
    private PharmacyDispensingMapper pharmacyDispensingMapper;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status) {
        Page<RefundRequest> page = new Page<>(pageNumber, size);
        QueryWrapper<RefundRequest> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("rf_status", status);
        }
        wrapper.orderByDesc("rf_create_time");
        IPage<RefundRequest> iPage = this.refundRequestMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());
        hashMap.put("pages", iPage.getPages());
        hashMap.put("pageNumber", iPage.getCurrent());
        hashMap.put("records", iPage.getRecords());
        return hashMap;
    }

    @Override
    public Boolean create(RefundRequest request) {
        request.setRfCreateTime(TodayUtil.getToday());
        request.setRfStatus(RefundRequest.STATUS_PENDING);
        return this.refundRequestMapper.insert(request) > 0;
    }

    @Override
    public String validateBeforeApprove(int rfId) {
        RefundRequest request = this.refundRequestMapper.selectById(rfId);
        if (request == null) return "退费申请不存在";

        // 验证1: 订单必须已缴费
        Orders order = orderMapper.selectById(request.getOId());
        if (order == null) return "关联订单不存在";
        if (order.getOPriceState() == null || order.getOPriceState() != 1) {
            return "订单未缴费，无法退费";
        }

        // 验证2: 如果有检查项(ocId)，检查状态不能是已完成或异常
        if (request.getOcId() != null) {
            OrderCheck oc = orderCheckMapper.selectById(request.getOcId());
            if (oc != null && (oc.getOcStatus() == 2 || oc.getOcStatus() == 3)) {
                return "检查已执行完成或处于异常状态，无法退费";
            }
        }

        // 验证3: 必须确认所有药品已退药后才能退费
        // 有任何药品处于 已发药(status=2) 或 待复核(status=1) 状态，均拒绝退费
        QueryWrapper<PharmacyDispensing> pdWrapper = new QueryWrapper<>();
        pdWrapper.eq("o_id", request.getOId());
        java.util.List<PharmacyDispensing> dispensingList = pharmacyDispensingMapper.selectList(pdWrapper);
        for (PharmacyDispensing pd : dispensingList) {
            if (pd.getPdStatus() != null && pd.getPdStatus() == 1) {
                return "药品正在复核中，无法退费。请等待复核完成后退药再退费";
            }
            if (pd.getPdStatus() != null && pd.getPdStatus() == 2) {
                return "药品已发药，必须先退药后才能退费";
            }
        }

        return null; // null means validation passed
    }

    @Override
    public Boolean approve(int rrId, String approveOperator) {
        // 前置校验
        String error = validateBeforeApprove(rrId);
        if (error != null) return false;

        UpdateWrapper<RefundRequest> wrapper = new UpdateWrapper<>();
        wrapper.eq("rf_id", rrId)
                .set("rf_status", RefundRequest.STATUS_APPROVED)
                .set("rf_approve_time", TodayUtil.getToday())
                .set("rf_approver", approveOperator);
        return this.refundRequestMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean reject(int rrId, String rejectReason) {
        UpdateWrapper<RefundRequest> wrapper = new UpdateWrapper<>();
        wrapper.eq("rf_id", rrId)
                .set("rf_status", RefundRequest.STATUS_REJECTED)
                .set("rf_approve_time", TodayUtil.getToday());
        if (rejectReason != null && !rejectReason.isEmpty()) {
            RefundRequest request = this.refundRequestMapper.selectById(rrId);
            if (request != null) {
                String updatedReason = request.getRfReason() != null ? request.getRfReason() + " | 驳回原因: " + rejectReason : "驳回原因: " + rejectReason;
                wrapper.set("rf_reason", updatedReason);
            }
        }
        return this.refundRequestMapper.update(null, wrapper) > 0;
    }
}
