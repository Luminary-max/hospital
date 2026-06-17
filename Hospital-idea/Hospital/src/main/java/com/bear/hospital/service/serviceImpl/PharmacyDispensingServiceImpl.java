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
        // 扣减库存
        boolean ok = drugService.reduceDrugNumber(pd.getDrId(), pd.getPdQuantity());
        if (!ok) return false;
        // 更新发药状态
        UpdateWrapper<PharmacyDispensing> wrapper = new UpdateWrapper<>();
        wrapper.eq("pd_id", pdId)
            .set("pd_status", 1)
            .set("pd_dispense_by", dispenseBy)
            .set("pd_dispense_time", TodayUtil.getToday());
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
}
