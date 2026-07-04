package com.bear.hospital.service;

import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.service.DrugService;
import java.util.HashMap;

public interface PharmacyDispensingService {
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    Boolean dispense(int pdId, String dispenseBy, DrugService drugService);
    Boolean review(int pdId, String reviewer);
    Boolean returnDrug(int pdId, String returnBy);
    Boolean createDispensing(Integer prescDetailId, Integer quantity);
    PharmacyDispensing findById(int pdId);
}

