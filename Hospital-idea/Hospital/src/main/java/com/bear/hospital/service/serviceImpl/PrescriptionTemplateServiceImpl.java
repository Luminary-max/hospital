package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.PrescriptionTemplateMapper;
import com.bear.hospital.pojo.PrescriptionTemplate;
import com.bear.hospital.service.PrescriptionTemplateService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("PrescriptionTemplateService")
public class PrescriptionTemplateServiceImpl implements PrescriptionTemplateService {
    @Autowired
    private PrescriptionTemplateMapper prescriptionTemplateMapper;

    @Override
    public List<PrescriptionTemplate> findAll(String dId) {
        QueryWrapper<PrescriptionTemplate> wrapper = new QueryWrapper<>();
        if (dId != null && !dId.trim().isEmpty()) {
            wrapper.eq("d_id", dId);
        }
        wrapper.orderByDesc("pt_id");
        return prescriptionTemplateMapper.selectList(wrapper);
    }

    @Override
    public Boolean addPrescriptionTemplate(PrescriptionTemplate template) {
        template.setPtCreateTime(TodayUtil.getToday());
        return prescriptionTemplateMapper.insert(template) > 0;
    }

    @Override
    public Boolean modifyPrescriptionTemplate(PrescriptionTemplate template) {
        return prescriptionTemplateMapper.updateById(template) > 0;
    }

    @Override
    public Boolean deletePrescriptionTemplate(Integer ptId) {
        return prescriptionTemplateMapper.deleteById(ptId) > 0;
    }
}
