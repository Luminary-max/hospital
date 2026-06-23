package com.bear.hospital.service;

import com.bear.hospital.pojo.DrugCategory;
import java.util.HashMap;
import java.util.List;

public interface DrugCategoryService {
    HashMap<String, Object> findAllDrugCategories(int pageNumber, int size, String query);
    List<DrugCategory> listAll();
    Boolean addDrugCategory(DrugCategory drugCategory);
    Boolean modifyDrugCategory(DrugCategory drugCategory);
    Boolean deleteDrugCategory(Integer dcId);
}
