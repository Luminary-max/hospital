package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.mapper.DrugPriceLogMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.DrugPriceLog;
import com.bear.hospital.service.DrugService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("DrugService")
public class DrugServiceImpl implements DrugService {
    @Autowired
    private DrugMapper drugMapper;
    @Autowired
    private DrugPriceLogMapper drugPriceLogMapper;
    @Override
    public HashMap<String, Object> findAllDrugs(int pageNumber, int size, String query, Integer typeFilter){
        Page<Drug> page = new Page<>(pageNumber, size);
        QueryWrapper<Drug> wrapper = new QueryWrapper<>();
        if (typeFilter != null && typeFilter > 0) {
            wrapper.eq("dr_type", typeFilter);
        }
        if (query != null && !query.isEmpty()) {
            wrapper.and(w -> w.like("dr_name", query)
                .or().like("dr_generic_name", query)
                .or().like("dr_pinyin", query)
                .or().like("dr_spec", query)
                .or().like("dr_manufacturer", query));
        }
        IPage<Drug> iPage = this.drugMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());
        hashMap.put("size", iPage.getPages());
        hashMap.put("pageNumber", iPage.getCurrent());
        hashMap.put("drugs", iPage.getRecords());
        return hashMap;
    }
    @Override
    public Drug findDrug(String drId){
        return this.drugMapper.selectById(drId);
    }
    @Override
    public Boolean reduceDrugNumber(String drId,int usedNumber){
        Drug drug = this.drugMapper.selectById(drId);
        if(drug.getDrNumber() < usedNumber) return false;
        drug.setDrNumber(drug.getDrNumber()-usedNumber);
        this.drugMapper.updateById(drug);
        return true;
    }
    public Boolean addDrug(Drug drug){
        if (drug.getDrId() != null) {
            Drug existing = this.drugMapper.selectById(drug.getDrId());
            if (existing != null) return false;
        }
        this.drugMapper.insert(drug);
        return true;
    }
    @Override
    public Boolean deleteDrug(String drId) {
        this.drugMapper.deleteById(drId);
        return true;
    }
    @Override
    public Boolean modifyDrug(Drug drug) {
        Drug old = this.drugMapper.selectById(drug.getDrId());
        if (old != null && old.getDrPrice() != drug.getDrPrice()) {
            DrugPriceLog log = new DrugPriceLog();
            log.setDrId(drug.getDrId());
            log.setOldPrice(old.getDrPrice());
            log.setNewPrice(drug.getDrPrice());
            log.setChangeReason("管理员修改价格");
            log.setOperator("管理员");
            log.setCreateTime(TodayUtil.getToday());
            drugPriceLogMapper.insert(log);
        }
        int i = this.drugMapper.updateById(drug);
        System.out.println("影响行数："+i);
        return true;
    }
    public List<DrugPriceLog> findPriceLogs(String drId) {
        QueryWrapper<DrugPriceLog> wrapper = new QueryWrapper<>();
        if (drId != null && !drId.trim().isEmpty()) {
            wrapper.eq("dr_id", drId);
        }
        wrapper.orderByDesc("dpl_id");
        return drugPriceLogMapper.selectList(wrapper);
    }
    @Override
    public Boolean toggleDisabled(String drId) {
        Drug drug = this.drugMapper.selectById(drId);
        if (drug == null) return false;
        Integer current = drug.getDrDisabled();
        drug.setDrDisabled((current != null && current == 1) ? 0 : 1);
        return this.drugMapper.updateById(drug) > 0;
    }
}
