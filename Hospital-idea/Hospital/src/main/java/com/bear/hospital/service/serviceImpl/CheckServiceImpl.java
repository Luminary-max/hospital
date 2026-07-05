package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.CheckMapper;
import com.bear.hospital.mapper.OrderCheckMapper;
import com.bear.hospital.pojo.Checks;
import com.bear.hospital.pojo.OrderCheck;
import com.bear.hospital.pojo.Orders;
import com.bear.hospital.service.CheckService;
import com.bear.hospital.service.OrderService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("CheckService")
public class CheckServiceImpl implements CheckService {

    @Autowired
    private CheckMapper checkMapper;
    @Autowired
    private OrderCheckMapper orderCheckMapper;
    @Autowired
    private OrderService orderService;

    /**
     * 分页模糊查询所有检查信息
     */
    @Override
    public HashMap<String, Object> findAllChecks(int pageNumber, int size, String query) {
        Page<Checks> page = new Page<>(pageNumber, size);
        QueryWrapper<Checks> wrapper = new QueryWrapper<>();
        wrapper.like("ch_name", query);
        IPage<Checks> iPage = this.checkMapper.selectPage(page, wrapper);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("total", iPage.getTotal());       //总条数
        hashMap.put("size", iPage.getPages());       //总页数
        hashMap.put("pageNumber", iPage.getCurrent());//当前页
        hashMap.put("checks", iPage.getRecords()); //查询到的记录
        return hashMap;
    }
    /**
     * 根据id查找检查
     */
    @Override
    public Checks findCheck(String chId){
        return this.checkMapper.selectById(chId);
    }
    /**
     * 增加检查信息
     */
    @Override
    public Boolean addCheck(Checks checks){
        //如果账号已存在则返回false
        if (checks.getChId() != null) {
            Checks existing = this.checkMapper.selectById(checks.getChId());
            if (existing != null) return false;
        }
        this.checkMapper.insert(checks);
        return true;
    }
    /**
     * 删除检查信息
     */
    @Override
    public Boolean deleteCheck(String chId) {
        this.checkMapper.deleteById(chId);
        return true;
    }
    /**
     * 修改检查信息
     */
    @Override
    public Boolean modifyCheck(Checks checks) {
        int i = this.checkMapper.updateById(checks);
        System.out.println("影响行数："+i);
        return true;
    }

    // ========== Order Check operations ==========

    @Override
    public HashMap<String, Object> findOrderChecks(int pageNumber, int size, Integer emrId, Integer status) {
        Page<OrderCheck> page = new Page<>(pageNumber, size);
        QueryWrapper<OrderCheck> wrapper = new QueryWrapper<>();
        if (emrId != null) wrapper.eq("emr_id", emrId);
        if (status != null) wrapper.eq("oc_status", status);
        wrapper.orderByDesc("oc_id");
        IPage<OrderCheck> iPage = this.orderCheckMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("pages", iPage.getPages());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public Boolean createOrderCheck(int oId, String chId, String chName, Double chPrice) {
        // 通过 oId 查病历，拿到 emrId
        com.bear.hospital.mapper.EmrMapper emrMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.EmrMapper.class);
        com.bear.hospital.pojo.OutpatientEmr emr = emrMapper2.findByOrderId(oId);
        int emrId = (emr != null) ? emr.getEmrId() : oId;
        OrderCheck oc = new OrderCheck();
        oc.setEmrId(emrId);
        oc.setChId(chId);
        oc.setChName(chName);
        oc.setChPrice(chPrice);
        oc.setOcStatus(0); // 未缴费
        oc.setOcCreateTime(TodayUtil.getToday());
        int inserted = this.orderCheckMapper.insert(oc);
        return inserted > 0;
    }

    @Override
    public Boolean batchCreateOrderChecks(int oId, List<Map<String, Object>> items) {
        // 通过 oId 查病历，拿到 emrId
        com.bear.hospital.mapper.EmrMapper emrMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.EmrMapper.class);
        com.bear.hospital.pojo.OutpatientEmr emr = emrMapper2.findByOrderId(oId);
        int emrId = (emr != null) ? emr.getEmrId() : oId;
        if (items == null || items.isEmpty()) return false;
        boolean allSuccess = true;
        for (Map<String, Object> item : items) {
            String chId = (String) item.get("chId");
            String chName = (String) item.get("chName");
            Double chPrice = item.get("chPrice") != null ? Double.parseDouble(item.get("chPrice").toString()) : 0.0;
            boolean ok = createOrderCheck(oId, chId, chName, chPrice);
            if (!ok) allSuccess = false;
        }
        return allSuccess;
    }

    @Override
    public Boolean updateCheckResult(Integer ocId, String result, String attachment, String operator) {
        UpdateWrapper<OrderCheck> wrapper = new UpdateWrapper<>();
        wrapper.eq("oc_id", ocId)
            .set("oc_result", result)
            .set("oc_attachment", attachment)
            .set("oc_operator", operator)
            .set("oc_result_time", TodayUtil.getToday())
            .set("oc_status", 2); // 已完成
        return this.orderCheckMapper.update(null, wrapper) > 0;
    }

    @Override
    public Boolean updateCheckStatus(Integer ocId, Integer status) {
        UpdateWrapper<OrderCheck> wrapper = new UpdateWrapper<>();
        wrapper.eq("oc_id", ocId).set("oc_status", status);
        return this.orderCheckMapper.update(null, wrapper) > 0;
    }
}
