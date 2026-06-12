package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.PrescriptionMapper;
import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    public List<PrescriptionDetail> findByOrderId(int oId) {
        return prescriptionMapper.findByOrderId(oId);
    }

    @Override
    @Transactional
    public boolean savePrescriptions(int oId, List<PrescriptionDetail> details) {
        // 先删除旧处方
        QueryWrapper<PrescriptionDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("o_id", oId);
        prescriptionMapper.delete(wrapper);
        // 批量插入新处方
        for (PrescriptionDetail d : details) {
            d.setOId(oId);
            prescriptionMapper.insert(d);
        }
        return true;
    }
}
