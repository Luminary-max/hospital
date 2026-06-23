package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugCategoryMapper;
import com.bear.hospital.pojo.DrugCategory;
import com.bear.hospital.service.DrugCategoryService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("DrugCategoryService")
public class DrugCategoryServiceImpl implements DrugCategoryService {
    @Resource
    private DrugCategoryMapper drugCategoryMapper;

    @Override
    public HashMap<String, Object> findAllDrugCategories(int pageNumber, int size, String query) {
        Page<DrugCategory> page = new Page<>(pageNumber, size);
        QueryWrapper<DrugCategory> wrapper = new QueryWrapper<>();
        if (query != null && !query.trim().isEmpty()) {
            wrapper.like("dc_name", query).or().like("dc_code", query);
        }
        wrapper.orderByAsc("dc_sort", "dc_id");
        IPage<DrugCategory> iPage = this.drugCategoryMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("pages", iPage.getPages());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public List<DrugCategory> listAll() {
        QueryWrapper<DrugCategory> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("dc_sort", "dc_id");
        return this.drugCategoryMapper.selectList(wrapper);
    }

    @Override
    public Boolean addDrugCategory(DrugCategory drugCategory) {
        return this.drugCategoryMapper.insert(drugCategory) > 0;
    }

    @Override
    public Boolean modifyDrugCategory(DrugCategory drugCategory) {
        return this.drugCategoryMapper.updateById(drugCategory) > 0;
    }

    @Override
    public Boolean deleteDrugCategory(Integer dcId) {
        return this.drugCategoryMapper.deleteById(dcId) > 0;
    }
}
