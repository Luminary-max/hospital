package com.bear.hospital.service;

import com.bear.hospital.pojo.BillingRecord;
import java.util.HashMap;
import java.util.List;

public interface BillingService {
    Boolean addBillingRecord(BillingRecord record);
    List<BillingRecord> findByOrderId(Integer oId);
    /** 按病历查找收费记录（正确业务流程） */
    List<BillingRecord> findByEmrId(Integer emrId);

    /**
     * Feature 9: 收费员日结统计
     */
    HashMap<String, Object> dailySummary(String date);
}
