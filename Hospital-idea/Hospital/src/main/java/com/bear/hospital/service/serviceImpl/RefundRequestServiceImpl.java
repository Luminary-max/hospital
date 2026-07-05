package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.BillingMapper;
import com.bear.hospital.mapper.OrderCheckMapper;
import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.mapper.PharmacyDispensingMapper;
import com.bear.hospital.mapper.RefundRequestMapper;
import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.pojo.OrderCheck;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.pojo.PrescriptionDetail;
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
    private BillingMapper billingMapper;
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

        // 通过 brId 找到缴费记录，再通过缴费记录找到订单
        BillingRecord br = null;
        if (request.getBrId() != null) {
            br = billingMapper.selectById(request.getBrId());
        }
        if (br == null) return "关联缴费记录不存在";

        // 验证1: 订单必须已缴费 — 通过 emrId → outpatient_emr → o_id 找到订单
        if (br.getEmrId() != null) {
            com.bear.hospital.mapper.EmrMapper emrMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.EmrMapper.class);
            com.bear.hospital.pojo.OutpatientEmr emr = emrMapper2.selectById(br.getEmrId());
            if (emr != null) {
                Orders order = com.bear.hospital.spring.SpringContextHolder.getBean(OrderMapper.class).selectById(emr.getOId());
                if (order == null) return "关联订单不存在";
                if (order.getOPriceState() == null || order.getOPriceState() != 1) {
                    return "订单未缴费，无法退费";
                }
            }
        }

        // 验证2: 药费退费 — 检查发药状态
        if (br.getPmId() != null) {
            // 通过 pmId 找到处方明细，检查发药状态
            com.bear.hospital.mapper.PrescriptionMapper prescMapper = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.PrescriptionMapper.class);
            java.util.List<PrescriptionDetail> details = prescMapper.selectList(
                new QueryWrapper<PrescriptionDetail>().eq("pm_id", br.getPmId())
            );
            for (PrescriptionDetail detail : details) {
                QueryWrapper<PharmacyDispensing> pdWrapper = new QueryWrapper<>();
                pdWrapper.eq("presc_detail_id", detail.getPdId());
                java.util.List<PharmacyDispensing> dispensingList = pharmacyDispensingMapper.selectList(pdWrapper);
                for (PharmacyDispensing pd : dispensingList) {
                    if (pd.getPdStatus() != null && pd.getPdStatus() == 1) {
                        return "药品正在复核中，无法退费。请等待复核完成后退药再退费";
                    }
                    if (pd.getPdStatus() != null && pd.getPdStatus() == 2) {
                        return "药品已发药，必须先退药后才能退费";
                    }
                }
            }
        }

        // 验证3: 检查费退费 — 检查 ocId 状态
        if (br.getOcId() != null) {
            OrderCheck oc = orderCheckMapper.selectById(br.getOcId());
            if (oc != null && (oc.getOcStatus() == 2 || oc.getOcStatus() == 3)) {
                return "检查已执行完成或处于异常状态，无法退费";
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
