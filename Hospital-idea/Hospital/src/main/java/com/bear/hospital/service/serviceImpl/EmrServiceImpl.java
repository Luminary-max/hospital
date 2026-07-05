package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.EmrMapper;
import com.bear.hospital.mapper.AuditLogMapper;
import com.bear.hospital.pojo.AuditLog;
import com.bear.hospital.pojo.OutpatientEmr;
import com.bear.hospital.service.EmrService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class EmrServiceImpl implements EmrService {
    @Autowired
    private EmrMapper emrMapper;
    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public OutpatientEmr saveEmr(OutpatientEmr emr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        // 检查是否已有病历
        OutpatientEmr existing = emrMapper.findByOrderId(emr.getOId());
        if (existing != null) {
            emr.setEmrId(existing.getEmrId());
            emr.setUpdateTime(now);
            emrMapper.updateById(emr);
            return emrMapper.findByOrderId(emr.getOId());
        }
        emr.setCreateTime(now);
        emr.setUpdateTime(now);
        emrMapper.insert(emr);
        return emrMapper.findByOrderId(emr.getOId());
    }

    @Override
    public OutpatientEmr findByOrderId(int oId) {
        return emrMapper.findByOrderId(oId);
    }

    @Override
    public OutpatientEmr findById(int emrId) {
        return emrMapper.selectById(emrId);
    }

    @Override
    public List<OutpatientEmr> findByPatientId(int pId) {
        return emrMapper.findByPatientId(pId);
    }

    @Override
    public boolean updateEmr(OutpatientEmr emr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        emr.setUpdateTime(sdf.format(new Date()));
        boolean success = emrMapper.updateById(emr) > 0;
        if (success && emr.getEmrId() > 0) {
            AuditLog log = new AuditLog();
            log.setAlUserId(String.valueOf(emr.getDId()));
            log.setAlUserRole("doctor");
            log.setAlAction("EMR_UPDATE");
            log.setAlTarget("emr_id=" + emr.getEmrId() + ",o_id=" + emr.getOId());
            log.setAlDetail("病历内容已修改");
            log.setAlCreateTime(TodayUtil.getToday());
            auditLogMapper.insert(log);
        }
        return success;
    }

    @Override
    public OutpatientEmr copyFromHistory(int emrId, int newOId) {
        OutpatientEmr old = emrMapper.selectById(emrId);
        if (old == null) return null;
        OutpatientEmr fresh = new OutpatientEmr();
        fresh.setOId(newOId);
        fresh.setPId(old.getPId());
        fresh.setDId(old.getDId());
        fresh.setChiefComplaint(old.getChiefComplaint());
        fresh.setPresentIllness(old.getPresentIllness());
        fresh.setPastHistory(old.getPastHistory());
        fresh.setPhysicalExam(old.getPhysicalExam());
        fresh.setDiagnosis(old.getDiagnosis());
        fresh.setTreatmentPlan(old.getTreatmentPlan());
        fresh.setAllergyHistory(old.getAllergyHistory());
        fresh.setMedicalAdvice(old.getMedicalAdvice());
        fresh.setFollowUpSuggest(old.getFollowUpSuggest());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        fresh.setCreateTime(now);
        fresh.setUpdateTime(now);
        emrMapper.insert(fresh);
        return emrMapper.findByOrderId(newOId);
    }
}
