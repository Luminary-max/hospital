package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.PrescriptionMapper;
import com.bear.hospital.mapper.PrescriptionMasterMapper;
import com.bear.hospital.pojo.PrescriptionDetail;
import com.bear.hospital.pojo.PrescriptionMaster;
import com.bear.hospital.service.PrescriptionService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    @Autowired
    private PrescriptionMapper prescriptionMapper;
    @Autowired
    private PrescriptionMasterMapper prescriptionMasterMapper;

    @Override
    public List<PrescriptionDetail> findByOrderId(int oId) {
        return prescriptionMapper.findByOrderId(oId);
    }

    @Override
    @Transactional
    public boolean savePrescriptions(int oId, List<PrescriptionDetail> details) {
        return savePrescriptions(oId, details, null, null);
    }

    @Transactional
    public boolean savePrescriptions(int oId, List<PrescriptionDetail> details, String dId, String diagnosis) {
        // 先删除旧处方
        QueryWrapper<PrescriptionDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("o_id", oId);
        prescriptionMapper.delete(wrapper);

        // 创建处方主表
        PrescriptionMaster master = new PrescriptionMaster();
        master.setOId(oId);
        master.setDId(dId);
        master.setPmDiagnosis(diagnosis);
        master.setPmType("西药");
        master.setPmStatus(0);
        master.setPmCreateTime(TodayUtil.getToday());
        prescriptionMasterMapper.insert(master);

        // 批量插入新处方明细（带pmId）
        for (PrescriptionDetail d : details) {
            d.setOId(oId);
            d.setPmId(master.getPmId());
            prescriptionMapper.insert(d);
        }
        return true;
    }
}
