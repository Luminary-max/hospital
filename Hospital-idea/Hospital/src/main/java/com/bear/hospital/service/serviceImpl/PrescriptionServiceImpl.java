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
    public boolean savePrescriptions(int emrId, List<PrescriptionDetail> details) {
        return savePrescriptions(emrId, details, null, null);
    }

    @Transactional
    public boolean savePrescriptions(int emrId, List<PrescriptionDetail> details, String dId, String diagnosis) {
        // 删除该病历下的旧处方（先删发药记录引用，再删明细，最后删主表）
        QueryWrapper<PrescriptionMaster> pmWrapper = new QueryWrapper<>();
        pmWrapper.eq("emr_id", emrId);
        java.util.List<PrescriptionMaster> oldMasters = prescriptionMasterMapper.selectList(pmWrapper);
        for (PrescriptionMaster old : oldMasters) {
            // 先删除关联的发药记录
            com.bear.hospital.mapper.PharmacyDispensingMapper pdMapper =
                com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.PharmacyDispensingMapper.class);
            java.util.List<com.bear.hospital.pojo.PrescriptionDetail> oldDetails = prescriptionMapper.selectList(
                new QueryWrapper<com.bear.hospital.pojo.PrescriptionDetail>().eq("pm_id", old.getPmId()));
            for (com.bear.hospital.pojo.PrescriptionDetail od : oldDetails) {
                pdMapper.delete(new QueryWrapper<com.bear.hospital.pojo.PharmacyDispensing>().eq("presc_detail_id", od.getPdId()));
            }
            prescriptionMapper.delete(new QueryWrapper<com.bear.hospital.pojo.PrescriptionDetail>().eq("pm_id", old.getPmId()));
        }
        prescriptionMasterMapper.delete(pmWrapper);

        // 创建处方主表
        PrescriptionMaster master = new PrescriptionMaster();
        master.setEmrId(emrId);
        master.setDId(dId);
        master.setPmDiagnosis(diagnosis);
        master.setPmType("西药");
        master.setPmStatus(0);
        master.setPmCreateTime(TodayUtil.getToday());
        prescriptionMasterMapper.insert(master);

        // 批量插入新处方明细
        for (PrescriptionDetail d : details) {
            d.setPmId(master.getPmId());
            prescriptionMapper.insert(d);
        }
        return true;
    }
}
