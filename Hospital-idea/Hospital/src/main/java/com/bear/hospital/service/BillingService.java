package com.bear.hospital.service;

import com.bear.hospital.pojo.BillingRecord;
import java.util.HashMap;
import java.util.List;

public interface BillingService {
    Boolean addBillingRecord(BillingRecord record);
    List<BillingRecord> findByOrderId(Integer oId);

    /**
     * Feature 9: 收费员日结统计
     */
    HashMap<String, Object> dailySummary(String date);
}
