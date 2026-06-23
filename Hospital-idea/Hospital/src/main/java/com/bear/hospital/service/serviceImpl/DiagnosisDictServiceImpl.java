package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.DiagnosisDictMapper;
import com.bear.hospital.pojo.DiagnosisDict;
import com.bear.hospital.service.DiagnosisDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DiagnosisDictService")
public class DiagnosisDictServiceImpl implements DiagnosisDictService {
    @Autowired
    private DiagnosisDictMapper diagnosisDictMapper;

    @Override
    public List<DiagnosisDict> findAll(String query) {
        QueryWrapper<DiagnosisDict> wrapper = new QueryWrapper<>();
        if (query != null && !query.trim().isEmpty()) {
            wrapper.and(w -> w.like("dd_name", query)
                    .or().like("dd_pinyin", query)
                    .or().like("dd_code", query));
        }
        wrapper.orderByAsc("dd_sort");
        return diagnosisDictMapper.selectList(wrapper);
    }

    @Override
    public Boolean addDiagnosisDict(DiagnosisDict dict) {
        return diagnosisDictMapper.insert(dict) > 0;
    }

    @Override
    public Boolean modifyDiagnosisDict(DiagnosisDict dict) {
        return diagnosisDictMapper.updateById(dict) > 0;
    }

    @Override
    public Boolean deleteDiagnosisDict(Integer ddId) {
        return diagnosisDictMapper.deleteById(ddId) > 0;
    }
}
