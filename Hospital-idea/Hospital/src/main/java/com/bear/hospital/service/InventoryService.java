package com.bear.hospital.service;

import com.bear.hospital.pojo.DrugBatch;
import java.util.HashMap;

public interface InventoryService {
    HashMap<String, Object> dashboard(int expiryDays);
    HashMap<String, Object> findTransactions(int pageNumber, int size, String drId);
    boolean receive(DrugBatch batch, String operator, String note);
    boolean adjust(String drId, Integer dbId, int quantity, String type, String operator, String note);
    Integer dispenseFefo(String drId, int quantity, String operator, String reference);
    boolean returnStock(String drId, Integer dbId, int quantity, String operator, String reference);

    /**
     * 报损过期批次 — 将批次数量归零，扣减药品总库存
     */
    boolean writeOffExpired(Integer dbId, String operator, String note);

    /**
     * 药品消耗排行
     */
    java.util.List<java.util.Map<String, Object>> drugConsumptionRanking(int limit, String startDate, String endDate);
}

