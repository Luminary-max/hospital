package com.bear.hospital.controller;

import com.bear.hospital.pojo.DrugBatch;
import com.bear.hospital.service.InventoryService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("inventory")
public class InventoryController {
    @Resource private InventoryService inventoryService;

    @GetMapping("dashboard")
    public ResponseData dashboard(@RequestParam(defaultValue = "90") int expiryDays) {
        return ResponseData.success("库存预警查询成功", inventoryService.dashboard(expiryDays));
    }

    @GetMapping("transactions")
    public ResponseData transactions(@RequestParam int pageNumber, @RequestParam int size,
                                     @RequestParam(required = false) String drId) {
        return ResponseData.success("库存流水查询成功", inventoryService.findTransactions(pageNumber, size, drId));
    }

    @PostMapping("receive")
    public ResponseData receive(@RequestBody Map<String, Object> params) {
        DrugBatch batch = new DrugBatch();
        batch.setDrId(text(params, "drId"));
        batch.setDbBatchNo(text(params, "dbBatchNo"));
        batch.setDbExpireDate(text(params, "dbExpireDate"));
        batch.setDbQuantity(integer(params, "dbQuantity"));
        batch.setDbPurchasePrice(decimal(params, "dbPurchasePrice"));
        batch.setDbSupplier(text(params, "dbSupplier"));
        if (inventoryService.receive(batch, text(params, "operator"), text(params, "note")))
            return ResponseData.success("药品入库成功");
        return ResponseData.fail("入库失败，请检查药品、批号和数量");
    }

    @PostMapping("adjust")
    public ResponseData adjust(@RequestBody Map<String, Object> params) {
        Integer dbId = integer(params, "dbId");
        int quantity = integer(params, "quantity") == null ? 0 : integer(params, "quantity");
        if (inventoryService.adjust(text(params, "drId"), dbId, quantity, text(params, "type"),
                text(params, "operator"), text(params, "note")))
            return ResponseData.success("库存调整成功");
        return ResponseData.fail("库存调整失败，库存不能小于零");
    }

    /**
     * 报损过期批次
     */
    @PostMapping("writeOff")
    public ResponseData writeOff(@RequestBody Map<String, Object> params) {
        Integer dbId = integer(params, "dbId");
        String operator = params.get("operator") != null ? params.get("operator").toString() : "管理员";
        String note = params.get("note") != null ? params.get("note").toString() : null;
        if (dbId == null) return ResponseData.fail("请指定批次ID");
        if (inventoryService.writeOffExpired(dbId, operator, note))
            return ResponseData.success("报损成功");
        return ResponseData.fail("报损失败，批次不存在或数量已为零");
    }

    /**
     * Feature 6: 药品消耗排行
     */
    @GetMapping("consumptionRanking")
    public ResponseData consumptionRanking(@RequestParam(defaultValue = "10") int limit,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        return ResponseData.success("查询成功", inventoryService.drugConsumptionRanking(limit, startDate, endDate));
    }

    private String text(Map<String, Object> map, String key) {
        return map.get(key) == null ? null : map.get(key).toString();
    }
    private Integer integer(Map<String, Object> map, String key) {
        return map.get(key) == null || map.get(key).toString().isEmpty() ? null : Integer.valueOf(map.get(key).toString());
    }
    private Double decimal(Map<String, Object> map, String key) {
        return map.get(key) == null || map.get(key).toString().isEmpty() ? null : Double.valueOf(map.get(key).toString());
    }
}
