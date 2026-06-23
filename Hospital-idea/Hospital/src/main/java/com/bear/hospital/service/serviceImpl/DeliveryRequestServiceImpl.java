package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.DeliveryRequestMapper;
import com.bear.hospital.pojo.DeliveryRequest;
import com.bear.hospital.service.DeliveryRequestService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("DeliveryRequestService")
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

    @Resource
    private DeliveryRequestMapper deliveryRequestMapper;

    @Override
    public DeliveryRequest create(DeliveryRequest request) {
        request.setDlPickupCode("PICK-" + System.currentTimeMillis());
        request.setDlStatus(0);
        request.setDlCreateTime(TodayUtil.getToday());
        deliveryRequestMapper.insert(request);
        return request;
    }

    @Override
    public List<DeliveryRequest> findByPatient(Integer pId) {
        QueryWrapper<DeliveryRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("p_id", pId).orderByDesc("dl_id");
        return deliveryRequestMapper.selectList(wrapper);
    }

    @Override
    public boolean pickup(Integer dlId) {
        UpdateWrapper<DeliveryRequest> wrapper = new UpdateWrapper<>();
        wrapper.eq("dl_id", dlId).eq("dl_status", 0)
            .set("dl_status", 1)
            .set("dl_pickup_time", TodayUtil.getToday());
        return deliveryRequestMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean cancel(Integer dlId) {
        UpdateWrapper<DeliveryRequest> wrapper = new UpdateWrapper<>();
        wrapper.eq("dl_id", dlId)
            .set("dl_status", 2);
        return deliveryRequestMapper.update(null, wrapper) > 0;
    }

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size, Integer status) {
        Page<DeliveryRequest> page = new Page<>(pageNumber, size);
        QueryWrapper<DeliveryRequest> wrapper = new QueryWrapper<>();
        if (status != null) wrapper.eq("dl_status", status);
        wrapper.orderByDesc("dl_id");
        IPage<DeliveryRequest> iPage = deliveryRequestMapper.selectPage(page, wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }
}
