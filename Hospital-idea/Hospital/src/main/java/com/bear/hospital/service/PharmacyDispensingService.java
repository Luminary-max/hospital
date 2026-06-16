package com.bear.hospital.service;

import com.bear.hospital.pojo.PharmacyDispensing;
import java.util.HashMap;

public interface PharmacyDispensingService {
    HashMap<String, Object> findAll(int pageNumber, int size, Integer status);
    Boolean dispense(int pdId, String dispenseBy);
}

