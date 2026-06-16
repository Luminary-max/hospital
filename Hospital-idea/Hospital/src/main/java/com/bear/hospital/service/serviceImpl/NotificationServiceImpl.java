package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bear.hospital.mapper.NotificationMapper;
import com.bear.hospital.pojo.Notification;
import com.bear.hospital.service.NotificationService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service("NotificationService")
public class NotificationServiceImpl implements NotificationService {
    @Resource
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> findByPatient(Integer pId) {
        QueryWrapper<Notification> w = new QueryWrapper<>();
        w.eq("p_id", pId).orderByDesc("n_create_time");
        return this.notificationMapper.selectList(w);
    }

    @Override
    public HashMap<String, Object> findAll(int pageNumber, int size) {
        Page<Notification> page = new Page<>(pageNumber, size);
        IPage<Notification> iPage = this.notificationMapper.selectPage(page, null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", iPage.getTotal());
        map.put("records", iPage.getRecords());
        return map;
    }

    @Override
    public Boolean markRead(Integer nId) {
        Notification n = new Notification();
        n.setNId(nId);
        n.setNIsRead(1);
        return this.notificationMapper.updateById(n) > 0;
    }

    @Override
    public int unreadCount(Integer pId) {
        QueryWrapper<Notification> w = new QueryWrapper<>();
        w.eq("p_id", pId).eq("n_is_read", 0);
        return this.notificationMapper.selectCount(w);
    }
}
