package com.bear.hospital.service;

import com.bear.hospital.pojo.AuditLog;
import java.util.HashMap;

public interface AuditLogService {
    HashMap<String, Object> findAll(int pageNumber, int size);
}
