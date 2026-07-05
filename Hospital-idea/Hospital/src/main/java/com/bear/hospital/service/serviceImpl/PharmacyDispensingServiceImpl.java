package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.mapper.PharmacyDispensingMapper;
import com.bear.hospital.mapper.PrescriptionMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.PharmacyDispensing;
import com.bear.hospital.pojo.PrescriptionDetail;
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
    private PrescriptionMapper prescriptionMapper;
    @Resource
    private InventoryService inventoryService;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status, String query) {
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

    private PrescriptionDetail getDetailByPrescDetailId(Integer prescDetailId) {
        if (prescDetailId == null) return null;
        return prescriptionMapper.selectById(prescDetailId);
    }

    @Override
    public Boolean dispense(int pdId, String dispenseBy, DrugService drugService) {
        PharmacyDispensing pd = this.pharmacyDispensingMapper.selectById(pdId);
        if (pd == null) return false;
        if (pd.getPdStatus() == null || pd.getPdStatus() != 0) return false;
        // 通过处方明细获取用药信息
        PrescriptionDetail detail = getDetailByPrescDetailId(pd.getPrescDetailId());
        if (detail == null) return false;
        // 校验：关联订单必须已缴费
        com.bear.hospital.mapper.PrescriptionMasterMapper pmMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.PrescriptionMasterMapper.class);
        com.bear.hospital.pojo.PrescriptionMaster pm = pmMapper2.selectById(detail.getPmId());
        if (pm == null) return false;
        com.bear.hospital.mapper.EmrMapper emrMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.EmrMapper.class);
        com.bear.hospital.pojo.OutpatientEmr emr = emrMapper2.selectById(pm.getEmrId());
        if (emr == null) return false;
        // 通过 oId 查找订单
        com.bear.hospital.mapper.OrderMapper orderMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
        com.bear.hospital.pojo.Orders order = orderMapper2.selectById(emr.getOId());
        if (order == null || order.getOPriceState() == null || (order.getOPriceState() != 1 && order.getOPriceState() != 5)) {
            return false;
        }
        Integer batchId = inventoryService.dispenseFefo(detail.getDrId(), pd.getPdQuantity(), dispenseBy,
            "DISPENSE-" + pdId);
        if (batchId == null) return false;
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
        PrescriptionDetail detail = getDetailByPrescDetailId(pd.getPrescDetailId());
        if (detail == null) return false;
        if (!inventoryService.returnStock(detail.getDrId(), pd.getDbId(), pd.getPdQuantity(), returnBy,
                "RETURN-" + pdId)) return false;
        UpdateWrapper<PharmacyDispensing> wrapper = new UpdateWrapper<>();
        wrapper.eq("pd_id", pdId).set("pd_status", 3)
            .set("pd_return_by", returnBy).set("pd_return_time", TodayUtil.getToday());
        return this.pharmacyDispensingMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean createDispensing(Integer prescDetailId, Integer quantity) {
        PharmacyDispensing pd = new PharmacyDispensing();
        pd.setPrescDetailId(prescDetailId);
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
