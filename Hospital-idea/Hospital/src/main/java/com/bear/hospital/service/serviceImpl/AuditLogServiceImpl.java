package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.AuditLogMapper;
import com.bear.hospital.pojo.AuditLog;
import com.bear.hospital.service.AuditLogService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;

@Service("AuditLogService")
public class AuditLogServiceImpl implements AuditLogService {
    @Resource
    private AuditLogMapper auditLogMapper;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size) {
        Page<AuditLog> page = new Page<>(pageNumber, size);
        IPage<AuditLog> iPage = this.auditLogMapper.selectPage(page, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }
}
