package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.EmrMapper;
import com.bear.hospital.pojo.OutpatientEmr;
import com.bear.hospital.service.EmrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class EmrServiceImpl implements EmrService {
    @Autowired
    private EmrMapper emrMapper;

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
    public List<OutpatientEmr> findByPatientId(int pId) {
        return emrMapper.findByPatientId(pId);
    }

    @Override
    public boolean updateEmr(OutpatientEmr emr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        emr.setUpdateTime(sdf.format(new Date()));
        return emrMapper.updateById(emr) > 0;
    }
}
