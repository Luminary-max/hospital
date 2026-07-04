package com.bear.hospital.service;

import com.bear.hospital.pojo.InvoiceRecord;
import java.util.HashMap;
import java.util.List;

public interface InvoiceRecordService {
    List<InvoiceRecord> findByOrderId(Integer oId);
    List<InvoiceRecord> findByBillingId(Integer brId);
    List<InvoiceRecord> findByDate(String date);
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    boolean voidInvoice(int invId, String operator, String reason);
}
