package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugBatchMapper;
import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.mapper.DispensingBatchDetailMapper;
import com.bear.hospital.mapper.InventoryTransactionMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.DrugBatch;
import com.bear.hospital.pojo.DispensingBatchDetail;
import com.bear.hospital.pojo.InventoryTransaction;
import com.bear.hospital.service.InventoryService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Resource private DrugMapper drugMapper;
    @Resource private DrugBatchMapper drugBatchMapper;
    @Resource private InventoryTransactionMapper transactionMapper;
    @Resource private DispensingBatchDetailMapper dispensingBatchDetailMapper;

    @Override
    public HashMap<String, Object> dashboard(int expiryDays) {
        LocalDate today = LocalDate.now();
        QueryWrapper<Drug> lowWrapper = new QueryWrapper<>();
        lowWrapper.apply("dr_number <= COALESCE(dr_min_stock, 20)").orderByAsc("dr_number");
        // Expiring: use per-drug drWarnDays when available, else fall back to the passed expiryDays
        List<DrugBatch> allBatches = drugBatchMapper.selectList(
            new QueryWrapper<DrugBatch>().gt("db_quantity", 0));
        List<DrugBatch> expiringBatches = new ArrayList<>();
        for (DrugBatch batch : allBatches) {
            if (batch.getDbExpireDate() == null) continue;
            LocalDate expireDate = LocalDate.parse(batch.getDbExpireDate());
            // Determine warn days for this drug
            Drug drug = drugMapper.selectById(batch.getDrId());
            int warnDays = (drug != null && drug.getDrWarnDays() != null && drug.getDrWarnDays() > 0)
                ? drug.getDrWarnDays() : expiryDays;
            if (!expireDate.isAfter(today.plusDays(warnDays))) {
                expiringBatches.add(batch);
            }
        }
        expiringBatches.sort((a, b) -> a.getDbExpireDate().compareTo(b.getDbExpireDate()));
        QueryWrapper<DrugBatch> expiredWrapper = new QueryWrapper<>();
        expiredWrapper.gt("db_quantity", 0).lt("db_expire_date", today.toString());
        HashMap<String, Object> data = new HashMap<>();
        data.put("lowStock", drugMapper.selectList(lowWrapper));
        data.put("expiringBatches", expiringBatches);
        data.put("expiredCount", drugBatchMapper.selectCount(expiredWrapper));
        data.put("lowStockCount", drugMapper.selectCount(lowWrapper));
        return data;
    }

    @Override
    public HashMap<String, Object> findTransactions(int pageNumber, int size, String drId) {
        QueryWrapper<InventoryTransaction> wrapper = new QueryWrapper<>();
        if (drId != null && !drId.trim().isEmpty()) wrapper.eq("dr_id", drId.trim());
        wrapper.orderByDesc("it_id");
        IPage<InventoryTransaction> page = transactionMapper.selectPage(new Page<>(pageNumber, size), wrapper);
        HashMap<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("records", page.getRecords());
        return data;
    }

    @Override
    @Transactional
    public boolean receive(DrugBatch batch, String operator, String note) {
        if (batch == null || batch.getDrId() == null || batch.getDbQuantity() == null || batch.getDbQuantity() <= 0) return false;
        Drug drug = drugMapper.selectById(batch.getDrId());
        if (drug == null) return false;
        // Check upper stock limit
        int newTotal = drug.getDrNumber() + batch.getDbQuantity();
        int maxStock = (drug.getDrMaxStock() != null && drug.getDrMaxStock() > 0) ? drug.getDrMaxStock() : 9999;
        if (newTotal > maxStock) return false;
        int before = drug.getDrNumber();
        batch.setDbId(null);
        batch.setDbCreateTime(TodayUtil.getToday());
        drugBatchMapper.insert(batch);
        drug.setDrNumber(newTotal);
        drugMapper.updateById(drug);
        record(batch.getDrId(), batch.getDbId(), "入库", batch.getDbQuantity(), before,
            drug.getDrNumber(), batch.getDbBatchNo(), operator, note);
        return true;
    }

    @Override
    @Transactional
    public boolean adjust(String drId, Integer dbId, int quantity, String type, String operator, String note) {
        if (quantity == 0 || type == null) return false;
        Drug drug = drugMapper.selectById(drId);
        if (drug == null || drug.getDrNumber() + quantity < 0) return false;
        DrugBatch batch = dbId == null ? null : drugBatchMapper.selectById(dbId);
        if (batch != null && (batch.getDbQuantity() + quantity < 0 || !drId.equals(batch.getDrId()))) return false;
        int before = drug.getDrNumber();
        drug.setDrNumber(before + quantity);
        drugMapper.updateById(drug);
        if (batch != null) {
            batch.setDbQuantity(batch.getDbQuantity() + quantity);
            drugBatchMapper.updateById(batch);
        }
        record(drId, dbId, type, quantity, before, drug.getDrNumber(), null, operator, note);
        return true;
    }

    @Override
    @Transactional
    public Integer dispenseFefo(String drId, int quantity, String operator, String reference) {
        Drug drug = drugMapper.selectById(drId);
        if (drug == null || quantity <= 0 || drug.getDrNumber() < quantity) return null;
        QueryWrapper<DrugBatch> wrapper = new QueryWrapper<>();
        wrapper.eq("dr_id", drId).gt("db_quantity", 0)
            .ge("db_expire_date", LocalDate.now().toString())
            .orderByAsc("db_expire_date", "db_id");
        List<DrugBatch> batches = drugBatchMapper.selectList(wrapper);
        int available = batches.stream().mapToInt(b -> b.getDbQuantity() == null ? 0 : b.getDbQuantity()).sum();
        if (!batches.isEmpty() && available < quantity) return null;
        int before = drug.getDrNumber();
        int remaining = quantity;
        Integer firstBatchId = 0;
        for (DrugBatch batch : batches) {
            if (remaining <= 0) break;
            int used = Math.min(remaining, batch.getDbQuantity());
            if (firstBatchId == 0) firstBatchId = batch.getDbId();
            batch.setDbQuantity(batch.getDbQuantity() - used);
            drugBatchMapper.updateById(batch);
            DispensingBatchDetail allocation = new DispensingBatchDetail();
            allocation.setPdReference(reference);
            allocation.setDrId(drId);
            allocation.setDbId(batch.getDbId());
            allocation.setDbdQuantity(used);
            allocation.setDbdReturned(0);
            allocation.setDbdCreateTime(TodayUtil.getToday());
            dispensingBatchDetailMapper.insert(allocation);
            remaining -= used;
        }
        drug.setDrNumber(before - quantity);
        drugMapper.updateById(drug);
        record(drId, firstBatchId, "发药", -quantity, before, drug.getDrNumber(), reference, operator, "按有效期优先出库");
        return firstBatchId;
    }

    @Override
    @Transactional
    public boolean returnStock(String drId, Integer dbId, int quantity, String operator, String reference) {
        if (quantity <= 0) return false;
        Drug drug = drugMapper.selectById(drId);
        if (drug == null) return false;
        int before = drug.getDrNumber();
        drug.setDrNumber(before + quantity);
        drugMapper.updateById(drug);
        String sourceReference = reference == null ? null : reference.replace("RETURN-", "DISPENSE-");
        QueryWrapper<DispensingBatchDetail> allocationWrapper = new QueryWrapper<>();
        allocationWrapper.eq("pd_reference", sourceReference).eq("dbd_returned", 0);
        List<DispensingBatchDetail> allocations = dispensingBatchDetailMapper.selectList(allocationWrapper);
        for (DispensingBatchDetail allocation : allocations) {
            DrugBatch batch = drugBatchMapper.selectById(allocation.getDbId());
            if (batch != null) {
                batch.setDbQuantity(batch.getDbQuantity() + allocation.getDbdQuantity());
                drugBatchMapper.updateById(batch);
            }
            allocation.setDbdReturned(1);
            dispensingBatchDetailMapper.updateById(allocation);
        }
        if (allocations.isEmpty() && dbId != null && dbId > 0) {
            DrugBatch batch = drugBatchMapper.selectById(dbId);
            if (batch != null) {
                batch.setDbQuantity(batch.getDbQuantity() + quantity);
                drugBatchMapper.updateById(batch);
            }
        }
        record(drId, dbId, "退药", quantity, before, drug.getDrNumber(), reference, operator, null);
        return true;
    }

    @Override
    @Transactional
    public boolean writeOffExpired(Integer dbId, String operator, String note) {
        DrugBatch batch = drugBatchMapper.selectById(dbId);
        if (batch == null || batch.getDbQuantity() == null || batch.getDbQuantity() <= 0) return false;
        Drug drug = drugMapper.selectById(batch.getDrId());
        if (drug == null) return false;
        int before = drug.getDrNumber();
        int batchQty = batch.getDbQuantity();
        batch.setDbQuantity(0);
        drugBatchMapper.updateById(batch);
        drug.setDrNumber(before - batchQty);
        drugMapper.updateById(drug);
        String ref = "WRITEOFF-" + dbId;
        record(batch.getDrId(), dbId, "报损", -batchQty, before, drug.getDrNumber(), ref, operator,
            note != null ? note : "过期报损");
        return true;
    }

    private void record(String drId, Integer dbId, String type, int quantity, int before,
                        int after, String reference, String operator, String note) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setDrId(drId);
        tx.setDbId(dbId);
        tx.setItType(type);
        tx.setItQuantity(quantity);
        tx.setItBeforeQuantity(before);
        tx.setItAfterQuantity(after);
        tx.setItReference(reference);
        tx.setItOperator(operator);
        tx.setItNote(note);
        tx.setItCreateTime(TodayUtil.getToday());
        transactionMapper.insert(tx);
    }

    @Override
    public List<java.util.Map<String, Object>> drugConsumptionRanking(int limit, String startDate, String endDate) {
        List<java.util.Map<String, Object>> raw = transactionMapper.selectConsumptionRanking(limit > 0 ? limit : 10);
        // Filter by date range in Java if provided
        if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
            java.util.List<java.util.Map<String, Object>> filtered = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> row : raw) {
                String drId = (String) row.get("dr_id");
                // Re-query with date filter per drug
                StringBuilder dateSql = new StringBuilder();
                dateSql.append("SELECT SUM(ABS(it.it_quantity)) as total FROM inventory_transaction it ");
                dateSql.append("WHERE it.it_type = '发药' AND it.dr_id = '").append(drId).append("' ");
                if (startDate != null && !startDate.isEmpty()) {
                    dateSql.append("AND it.it_create_time >= '").append(startDate).append("' ");
                }
                if (endDate != null && !endDate.isEmpty()) {
                    dateSql.append("AND it.it_create_time <= '").append(endDate).append(" 23:59:59' ");
                }
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InventoryTransaction> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                qw.last(dateSql.toString());
                java.util.Map<String, Object> result = transactionMapper.selectMaps(qw).stream().findFirst().orElse(null);
                if (result != null && result.get("total") != null) {
                    row.put("total_consumed", result.get("total"));
                    filtered.add(row);
                }
            }
            filtered.sort((a, b) -> {
                Number ta = (Number) a.get("total_consumed");
                Number tb = (Number) b.get("total_consumed");
                return tb.intValue() - ta.intValue();
            });
            return filtered.subList(0, Math.min(limit, filtered.size()));
        }
        return raw;
    }
}
