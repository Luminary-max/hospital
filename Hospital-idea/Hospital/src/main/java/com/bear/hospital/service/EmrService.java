package com.bear.hospital.service;

import com.bear.hospital.pojo.OutpatientEmr;
import java.util.List;

public interface EmrService {
    OutpatientEmr saveEmr(OutpatientEmr emr);
    OutpatientEmr findByOrderId(int oId);
    OutpatientEmr findById(int emrId);
    List<OutpatientEmr> findByPatientId(int pId);
    boolean updateEmr(OutpatientEmr emr);
    OutpatientEmr copyFromHistory(int emrId, int newOId);
}
