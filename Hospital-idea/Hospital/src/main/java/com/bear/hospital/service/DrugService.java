package com.bear.hospital.service;

import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.DrugPriceLog;
import java.util.HashMap;
import java.util.List;

public interface DrugService {
    HashMap<String, Object> findAllDrugs(int pageNumber, int size, String query, Integer typeFilter);
    Drug findDrug(String drId);
    Boolean reduceDrugNumber(String drId,int usedNumber);
    Boolean addDrug(Drug drug);
    Boolean deleteDrug(String drId);
    Boolean modifyDrug(Drug drug);
    List<DrugPriceLog> findPriceLogs(String drId);
    Boolean toggleDisabled(String drId);
}
