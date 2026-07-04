package com.bear.hospital.service;

import com.bear.hospital.pojo.RefundRequest;
import java.util.HashMap;

public interface RefundRequestService {
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    Boolean create(RefundRequest request);
    /** 退费审批前置校验，返回null表示通过，否则返回错误原因 */
    String validateBeforeApprove(int rfId);
    Boolean approve(int rfId, String approveOperator);
    Boolean reject(int rfId, String rejectReason);
}

