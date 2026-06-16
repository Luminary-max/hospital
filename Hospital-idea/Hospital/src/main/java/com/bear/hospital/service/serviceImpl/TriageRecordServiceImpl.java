package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.TriageRecordMapper;
import com.bear.hospital.pojo.TriageRecord;
import com.bear.hospital.service.TriageRecordService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;

@Service("TriageRecordService")
public class TriageRecordServiceImpl implements TriageRecordService {
    @Resource
    private TriageRecordMapper triageRecordMapper;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size) {
        Page<TriageRecord> page = new Page<>(pageNumber, size);
        IPage<TriageRecord> iPage = this.triageRecordMapper.selectPage(page, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }
}
