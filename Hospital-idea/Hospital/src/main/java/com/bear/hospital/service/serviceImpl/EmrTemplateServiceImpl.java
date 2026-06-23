package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.EmrTemplateMapper;
import com.bear.hospital.pojo.EmrTemplate;
import com.bear.hospital.service.EmrTemplateService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("EmrTemplateService")
public class EmrTemplateServiceImpl implements EmrTemplateService {
    @Autowired
    private EmrTemplateMapper emrTemplateMapper;

    @Override
    public List<EmrTemplate> findAll(String dept) {
        QueryWrapper<EmrTemplate> wrapper = new QueryWrapper<>();
        if (dept != null && !dept.trim().isEmpty()) {
            wrapper.eq("et_dept", dept);
        }
        wrapper.orderByDesc("et_id");
        return emrTemplateMapper.selectList(wrapper);
    }

    @Override
    public Boolean addEmrTemplate(EmrTemplate template) {
        template.setEtCreateTime(TodayUtil.getToday());
        return emrTemplateMapper.insert(template) > 0;
    }

    @Override
    public Boolean modifyEmrTemplate(EmrTemplate template) {
        return emrTemplateMapper.updateById(template) > 0;
    }

    @Override
    public Boolean deleteEmrTemplate(Integer etId) {
        return emrTemplateMapper.deleteById(etId) > 0;
    }
}
