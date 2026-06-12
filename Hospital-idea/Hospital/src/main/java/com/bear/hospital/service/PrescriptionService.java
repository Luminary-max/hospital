package com.bear.hospital.service;

import com.bear.hospital.pojo.PrescriptionDetail;
import java.util.List;

public interface PrescriptionService {
    List<PrescriptionDetail> findByOrderId(int oId);
    boolean savePrescriptions(int oId, List<PrescriptionDetail> details);
}
