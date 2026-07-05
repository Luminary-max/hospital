package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.BillingMapper;
import com.bear.hospital.mapper.EmrMapper;
import com.bear.hospital.mapper.InvoiceRecordMapper;
import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.pojo.InvoiceRecord;
import com.bear.hospital.pojo.OutpatientEmr;
import com.bear.hospital.service.InvoiceRecordService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service("InvoiceRecordService")
public class InvoiceRecordServiceImpl implements InvoiceRecordService {

    @Resource
    private InvoiceRecordMapper invoiceRecordMapper;
    @Resource
    private BillingMapper billingMapper;
    @Resource
    private EmrMapper emrMapper;

    @Override
    public List<InvoiceRecord> findByOrderId(Integer oId) {
        // 通过 oId → outpatient_emr → billing_record → invoice_record 链
        QueryWrapper<OutpatientEmr> emrWrapper = new QueryWrapper<>();
        emrWrapper.eq("o_id", oId).orderByDesc("create_time").last("LIMIT 1");
        OutpatientEmr emr = emrMapper.selectOne(emrWrapper);
        if (emr == null) return java.util.Collections.emptyList();
        // 找到该病历下的所有缴费记录 brId
        QueryWrapper<BillingRecord> brWrapper = new QueryWrapper<>();
        brWrapper.eq("emr_id", emr.getEmrId());
        List<BillingRecord> brs = billingMapper.selectList(brWrapper);
        if (brs.isEmpty()) return java.util.Collections.emptyList();
        List<Integer> brIds = brs.stream().map(BillingRecord::getBrId).collect(Collectors.toList());
        QueryWrapper<InvoiceRecord> wrapper = new QueryWrapper<>();
        wrapper.in("br_id", brIds).orderByAsc("inv_id");
        return this.invoiceRecordMapper.selectList(wrapper);
    }

    @Override
    public List<InvoiceRecord> findByBillingId(Integer brId) {
        QueryWrapper<InvoiceRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("br_id", brId).orderByAsc("inv_id");
        return this.invoiceRecordMapper.selectList(wrapper);
    }

    @Override
    public List<InvoiceRecord> findByDate(String date) {
        QueryWrapper<InvoiceRecord> wrapper = new QueryWrapper<>();
        wrapper.like("inv_create_time", date).orderByAsc("inv_id");
        return this.invoiceRecordMapper.selectList(wrapper);
    }

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status) {
        Page<InvoiceRecord> page = new Page<>(pageNumber, size);
        QueryWrapper<InvoiceRecord> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("inv_status", status);
        wrapper.orderByDesc("inv_id");
        IPage<InvoiceRecord> iPage = this.invoiceRecordMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public boolean voidInvoice(int invId, String operator, String reason) {
        UpdateWrapper<InvoiceRecord> wrapper = new UpdateWrapper<>();
        wrapper.eq("inv_id", invId)
            .set("inv_status", -1)
            .set("inv_cancel_reason", reason)
            .set("inv_operator", operator)
            .set("inv_cancel_time", TodayUtil.getToday());
        return this.invoiceRecordMapper.update(null, wrapper) > 0;
    }
}

