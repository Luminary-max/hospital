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
        // 新增分诊时默认设为"已分诊"状态
        triageRecord.setTStatus(1);
        if (triageRecord.getTSource() == null) triageRecord.setTSource("现场");
        boolean ok = this.triageRecordMapper.insert(triageRecord) > 0;
        // 分诊成功后推进该患者最新挂号订单状态到"已分诊"(oState=1)
        if (ok && triageRecord.getPId() != null) {
            try {
                com.bear.hospital.mapper.OrderMapper orderMapper = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
                // 先找该指定医生下该患者最新的待诊订单
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.bear.hospital.pojo.Orders> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                if (triageRecord.getDId() != null) {
                    wrapper.eq("p_id", triageRecord.getPId()).eq("d_id", triageRecord.getDId());
                } else {
                    wrapper.eq("p_id", triageRecord.getPId());
                }
                wrapper.eq("o_state", 0).orderByDesc("o_id").last("LIMIT 1");
                com.bear.hospital.pojo.Orders order = orderMapper.selectOne(wrapper);
                // 如果找不到该医生下的待诊订单，看看该患者今天有没有挂这个医生的号（o_state=1）
                if (order == null && triageRecord.getDId() != null) {
                    wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                    wrapper.eq("p_id", triageRecord.getPId()).eq("d_id", triageRecord.getDId())
                        .eq("o_state", 1).orderByDesc("o_id").last("LIMIT 1");
                    order = orderMapper.selectOne(wrapper);
                }
                // 仍然找不到，自动创建一个挂号订单
                if (order == null && triageRecord.getDId() != null) {
                    try {
                        com.bear.hospital.pojo.Orders newOrder = new com.bear.hospital.pojo.Orders();
                        newOrder.setPId(triageRecord.getPId());
                        newOrder.setDId(triageRecord.getDId());
                        newOrder.setOState(1);
                        newOrder.setOPriceState(0);
                        newOrder.setOStart(TodayUtil.getTodayYmd() + " " +
                            new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()));
                        // 使用 RandomUtil 生成订单ID（与 OrderServiceImpl.addOrder 一致）
                        newOrder.setOId(com.bear.hospital.utils.RandomUtil.randomOid(triageRecord.getPId()));
                        int inserted = orderMapper.insert(newOrder);
                        if (inserted > 0) {
                            order = orderMapper.selectOne(
                                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.bear.hospital.pojo.Orders>()
                                    .eq("p_id", triageRecord.getPId()).eq("d_id", triageRecord.getDId())
                                    .orderByDesc("o_id").last("LIMIT 1")
                            );
                        }
                    } catch(Exception e2) {
                        System.err.println("分诊自动创建订单失败: " + e2.getMessage());
                    }
                }
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
