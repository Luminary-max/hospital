package com.bear.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DrugBatchMapper;
import com.bear.hospital.mapper.DrugMapper;
import com.bear.hospital.pojo.Drug;
import com.bear.hospital.pojo.DrugBatch;
import com.bear.hospital.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("drugBatch")
public class DrugBatchController {

    @Autowired
    private DrugBatchMapper drugBatchMapper;
    @Autowired
    private DrugMapper drugMapper;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
        @RequestParam(required = false) String query) {
        Page<DrugBatch> page = new Page<>(pageNumber, size);
        QueryWrapper<DrugBatch> wrapper = new QueryWrapper<>();
        if (query != null && !query.trim().isEmpty()) {
            wrapper.like("db_batch_no", query)
                .or().like("dr_id", query)
                .or().like("db_supplier", query);
        }
        wrapper.orderByDesc("db_id");
        IPage<DrugBatch> iPage = this.drugBatchMapper.selectPage(page, wrapper);
        // Enrich with drug name
        List<DrugBatch> records = iPage.getRecords();
        if (records != null && !records.isEmpty()) {
            List<String> drIds = records.stream().map(DrugBatch::getDrId).collect(Collectors.toList());
            if (!drIds.isEmpty()) {
                List<Drug> drugs = drugMapper.selectBatchIds(drIds);
                Map<String, String> drNameMap = drugs.stream()
                    .collect(Collectors.toMap(Drug::getDrId, Drug::getDrName, (a, b) -> a));
                for (DrugBatch batch : records) {
                    batch.setDbDrugName(drNameMap.get(batch.getDrId()));
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", records);
        return ResponseData.success("查询成功", map);
    }
}
