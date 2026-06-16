package com.bear.hospital.service;

import com.bear.hospital.pojo.BillingRecord;
import java.util.List;

public interface BillingService {
    Boolean addBillingRecord(BillingRecord record);
    List<BillingRecord> findByOrderId(Integer oId);
}
