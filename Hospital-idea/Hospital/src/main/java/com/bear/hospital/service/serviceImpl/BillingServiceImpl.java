package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.BillingMapper;
import com.bear.hospital.pojo.BillingRecord;
import com.bear.hospital.service.BillingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("BillingService")
public class BillingServiceImpl implements BillingService {

    @Resource
    private BillingMapper billingMapper;

    @Override
    public Boolean addBillingRecord(BillingRecord record) {
        return this.billingMapper.insert(record) > 0;
    }

    @Override
    public List<BillingRecord> findByOrderId(Integer oId) {
        QueryWrapper<BillingRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("o_id", oId).orderByAsc("br_id");
        return this.billingMapper.selectList(wrapper);
    }
}
