package com.bear.hospital.service;

import com.bear.hospital.pojo.DeliveryRequest;
import java.util.HashMap;
import java.util.List;

public interface DeliveryRequestService {
    DeliveryRequest create(DeliveryRequest request);
    List<DeliveryRequest> findByPatient(Integer pId);
    boolean pickup(Integer dlId);
    boolean cancel(Integer dlId);
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
}
