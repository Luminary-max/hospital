package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.mapper.PharmacyDispensingMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.service.DrugService;
import com.bear.hospital.service.InventoryService;
import com.bear.hospital.service.PharmacyDispensingService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;

@Service("PharmacyDispensingService")
public class PharmacyDispensingServiceImpl implements PharmacyDispensingService {
    @Resource
    private PharmacyDispensingMapper pharmacyDispensingMapper;
    @Resource
    private DrugMapper drugMapper;
    @Resource
    private InventoryService inventoryService;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status) {
        Page<PharmacyDispensing> page = new Page<>(pageNumber, size);
        QueryWrapper<PharmacyDispensing> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("pd_status", status);
        wrapper.orderByDesc("pd_id");
        IPage<PharmacyDispensing> iPage = this.pharmacyDispensingMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("pages", iPage.getPages());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public Boolean dispense(int pdId, String dispenseBy, DrugService drugService) {
        // 查出待发药记录
        PharmacyDispensing pd = this.pharmacyDispensingMapper.selectById(pdId);
        if (pd == null) return false;
        if (pd.getPdStatus() == null || pd.getPdStatus() != 0) return false;
        // 校验：关联订单必须已缴费（o_price_state=1），否则不可发药
        com.bear.hospital.mapper.OrderMapper orderMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
        com.bear.hospital.pojo.Orders order = orderMapper2.selectById(pd.getOId());
        if (order == null || order.getOPriceState() == null || order.getOPriceState() != 1) {
            return false; // 未缴费不可发药
        }
        Integer batchId = inventoryService.dispenseFefo(pd.getDrId(), pd.getPdQuantity(), dispenseBy,
            "DISPENSE-" + pdId);
        if (batchId == null) return false;
        // 更新发药状态 -> 待复核(1)
        UpdateWrapper<PharmacyDispensing> wrapper = new UpdateWrapper<>();
        wrapper.eq("pd_id", pdId)
            .set("pd_status", 1)
            .set("db_id", batchId)
            .set("pd_dispense_by", dispenseBy)
            .set("pd_dispense_time", TodayUtil.getToday());
        return this.pharmacyDispensingMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean review(int pdId, String reviewer) {
        PharmacyDispensing pd = this.pharmacyDispensingMapper.selectById(pdId);
        if (pd == null || pd.getPdStatus() == null || pd.getPdStatus() != 1) return false;
        UpdateWrapper<PharmacyDispensing> wrapper = new UpdateWrapper<>();
        wrapper.eq("pd_id", pdId)
            .set("pd_status", 2)
            .set("pd_review_by", reviewer)
            .set("pd_review_time", TodayUtil.getToday());
        return this.pharmacyDispensingMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean returnDrug(int pdId, String returnBy) {
        PharmacyDispensing pd = this.pharmacyDispensingMapper.selectById(pdId);
        if (pd == null || pd.getPdStatus() == null || pd.getPdStatus() != 2) return false;
        if (!inventoryService.returnStock(pd.getDrId(), pd.getDbId(), pd.getPdQuantity(), returnBy,
                "RETURN-" + pdId)) return false;
        UpdateWrapper<PharmacyDispensing> wrapper = new UpdateWrapper<>();
        wrapper.eq("pd_id", pdId).set("pd_status", 3)
            .set("pd_return_by", returnBy).set("pd_return_time", TodayUtil.getToday());
        return this.pharmacyDispensingMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean createDispensing(int oId, String drId, int quantity) {
        PharmacyDispensing pd = new PharmacyDispensing();
        pd.setOId(oId);
        pd.setDrId(drId);
        pd.setPdQuantity(quantity);
        pd.setPdStatus(0);
        pd.setPdCreateTime(TodayUtil.getToday());
        return this.pharmacyDispensingMapper.insert(pd) > 0;
    }

    @Override
    public PharmacyDispensing findById(int pdId) {
        return this.pharmacyDispensingMapper.selectById(pdId);
    }
}
