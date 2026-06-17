package com.bear.hospital.service;

import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.service.DrugService;
import java.util.HashMap;

public interface PharmacyDispensingService {
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    Boolean dispense(int pdId, String dispenseBy, DrugService drugService);
    Boolean createDispensing(int oId, String drId, int quantity);
}

