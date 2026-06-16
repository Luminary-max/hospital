package com.bear.hospital.service;

import com.bear.hospital.pojo.TriageRecord;
import java.util.HashMap;

public interface TriageRecordService {
    HashMap<String, Object> findAll(int pageNumber, int size);
}
