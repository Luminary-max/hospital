package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.TriageRecordMapper;
import com.bear.hospital.pojo.TriageRecord;
import com.bear.hospital.service.TriageRecordService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;

@Service("TriageRecordService")
public class TriageRecordServiceImpl implements TriageRecordService {
    @Resource
    private TriageRecordMapper triageRecordMapper;

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
        triageRecord.setTCreateTime(TodayUtil.getToday());
        if (triageRecord.getTStatus() == null) triageRecord.setTStatus(0);
        if (triageRecord.getTSource() == null) triageRecord.setTSource("现场");
        boolean ok = this.triageRecordMapper.insert(triageRecord) > 0;
        // 分诊成功后推进该患者最新挂号订单状态到"已分诊"(oState=1)
        if (ok && triageRecord.getPId() != null) {
            try {
                com.bear.hospital.mapper.OrderMapper orderMapper = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.bear.hospital.pojo.Orders> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                wrapper.eq("p_id", triageRecord.getPId()).eq("o_state", 0).orderByDesc("o_id").last("LIMIT 1");
                com.bear.hospital.pojo.Orders order = orderMapper.selectOne(wrapper);
                if (order != null) {
                    order.setOState(1);
                    orderMapper.updateById(order);
                }
            } catch(Exception e) {
                System.err.println("分诊更新订单状态失败: " + e.getMessage());
            }
        }
        return ok;
    }

    @Override
    public Boolean updateTriage(TriageRecord triageRecord) {
        UpdateWrapper<TriageRecord> wrapper = new UpdateWrapper<>();
        wrapper.eq("t_id", triageRecord.getTId());
        if (triageRecord.getTLevel() != null) wrapper.set("t_level", triageRecord.getTLevel());
        if (triageRecord.getTStatus() != null) wrapper.set("t_status", triageRecord.getTStatus());
        if (triageRecord.getTNote() != null) wrapper.set("t_note", triageRecord.getTNote());
        if (triageRecord.getTTemperature() != null) wrapper.set("t_temperature", triageRecord.getTTemperature());
        if (triageRecord.getTBloodPressure() != null) wrapper.set("t_blood_pressure", triageRecord.getTBloodPressure());
        if (triageRecord.getTHeartRate() != null) wrapper.set("t_heart_rate", triageRecord.getTHeartRate());
        if (triageRecord.getTWeight() != null) wrapper.set("t_weight", triageRecord.getTWeight());
        if (triageRecord.getTChiefComplaint() != null) wrapper.set("t_chief_complaint", triageRecord.getTChiefComplaint());
        return this.triageRecordMapper.update(null, wrapper) > 0;
    }
}
