package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.OrderMapper;
import com.bear.hospital.mapper.TriageRecordMapper;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.pojo.TriageRecord;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.service.TriageRecordService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;

@Service("TriageRecordService")
public class TriageRecordServiceImpl implements TriageRecordService {
    @Resource
    private TriageRecordMapper triageRecordMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderService orderService;

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status, Integer level) {
        Page<TriageRecord> page = new Page<>(pageNumber, size);
        QueryWrapper<TriageRecord> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("t_status", status);
        if (level != null) wrapper.eq("t_level", level);
        wrapper.orderByDesc("t_id");
        IPage<TriageRecord> iPage = this.triageRecordMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public Boolean createTriage(TriageRecord triageRecord) {
        // Set create time
        triageRecord.setTCreateTime(TodayUtil.getToday());
        // Set default status if not provided
        if (triageRecord.getTStatus() == null) triageRecord.setTStatus(0);
        int inserted = this.triageRecordMapper.insert(triageRecord);
        if (inserted <= 0) return false;
        // Update order state to 已分诊(1)
        if (triageRecord.getOId() != null) {
            orderService.updateOrderState(triageRecord.getOId(), Orders.STATE_TRIAGED);
        }
        return true;
    }

    @Override
    public Boolean updateTriage(TriageRecord triageRecord) {
        UpdateWrapper<TriageRecord> wrapper = new UpdateWrapper<>();
        wrapper.eq("t_id", triageRecord.getTId());
        if (triageRecord.getTLevel() != null) wrapper.set("t_level", triageRecord.getTLevel());
        if (triageRecord.getTStatus() != null) wrapper.set("t_status", triageRecord.getTStatus());
        if (triageRecord.getTNote() != null) wrapper.set("t_note", triageRecord.getTNote());
        if (triageRecord.getTChiefComplaint() != null) wrapper.set("t_chief_complaint", triageRecord.getTChiefComplaint());
        if (triageRecord.getTTemperature() != null) wrapper.set("t_temperature", triageRecord.getTTemperature());
        if (triageRecord.getTBloodPressure() != null) wrapper.set("t_blood_pressure", triageRecord.getTBloodPressure());
        if (triageRecord.getTHeartRate() != null) wrapper.set("t_heart_rate", triageRecord.getTHeartRate());
        if (triageRecord.getTWeight() != null) wrapper.set("t_weight", triageRecord.getTWeight());
        return this.triageRecordMapper.update(null, wrapper) > 0;
    }
}
