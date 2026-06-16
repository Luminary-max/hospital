package com.bear.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugBatchMapper;
import com.bear.hospital.pojo.DrugBatch;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("drugBatch")
public class DrugBatchController {

    @Autowired
    private DrugBatchMapper drugBatchMapper;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size) {
        Page<DrugBatch> page = new Page<>(pageNumber, size);
        QueryWrapper<DrugBatch> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("db_id");
        IPage<DrugBatch> iPage = this.drugBatchMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return ResponseData.success("查询成功", map);
    }
}
