package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.RefundRequestMapper;
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
    public Boolean approve(int rrId, String approveOperator) {
        UpdateWrapper<RefundRequest> wrapper = new UpdateWrapper<>();
        wrapper.eq("rf_id", rrId)
                .set("rf_status", RefundRequest.STATUS_APPROVED)
                .set("rf_approve_time", TodayUtil.getToday())
                .set("rf_approve_operator", approveOperator);
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

