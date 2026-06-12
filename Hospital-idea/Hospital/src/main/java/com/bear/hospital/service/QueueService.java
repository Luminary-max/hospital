package com.bear.hospital.service;

import com.bear.hospital.pojo.QueueNumber;
import java.util.List;
import java.util.Map;

public interface QueueService {
    String takeNumber(int oId, int pId, String dId);
    QueueNumber callNext(String dId, Integer reQueueId);
    boolean skipNumber(int qId);
    List<QueueNumber> listByDoctorToday(String dId);
    QueueNumber findByPatientToday(int pId);
    List<Map<String, Object>> getDeptQueueStats();
}
