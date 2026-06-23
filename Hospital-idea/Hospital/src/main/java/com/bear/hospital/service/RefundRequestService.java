package com.bear.hospital.service;

import com.bear.hospital.pojo.RefundRequest;
import java.util.HashMap;

public interface RefundRequestService {
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    Boolean create(RefundRequest request);
    Boolean approve(int rfId, String approveOperator);
    Boolean reject(int rfId, String rejectReason);
}

