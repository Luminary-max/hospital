package com.bear.hospital.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bear.hospital.mapper.DoctorMapper;
import com.bear.hospital.mapper.QueueMapper;
import com.bear.hospital.pojo.Doctor;
import com.bear.hospital.pojo.QueueNumber;
import com.bear.hospital.service.QueueService;
import com.bear.hospital.utils.TodayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueueServiceImpl implements QueueService {
    @Autowired
    private QueueMapper queueMapper;
    @Autowired
    private DoctorMapper doctorMapper;

    private String todayYmd() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    @Override
    @Transactional
    public String takeNumber(int oId) {
        String today = todayYmd();
        QueueNumber qn = new QueueNumber();
        qn.setOId(oId);
        qn.setQState(0);
        qn.setQCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        queueMapper.insert(qn);
        // 返回排队序号（基于该订单医生当天等待人数+1）
        // 通过 orders 表找到医生
        com.bear.hospital.mapper.OrderMapper orderMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
        com.bear.hospital.pojo.Orders order = orderMapper2.selectById(oId);
        String dId = order != null ? order.getdId() : "";
        int index = queueMapper.countWaiting(dId, today) + 1;
        return String.valueOf(index);
    }

    @Override
    @Transactional
    public QueueNumber callNext(String dId, Integer reQueueId) {
        String today = todayYmd();
        // 通过 orders 关联找到该医生当前叫号并标记完成
        com.bear.hospital.mapper.OrderMapper orderMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.OrderMapper.class);
        List<Integer> orderIds = orderMapper2.selectList(
            new QueryWrapper<com.bear.hospital.pojo.Orders>()
                .eq("d_id", dId)
                .select("o_id")
        ).stream().map(com.bear.hospital.pojo.Orders::getOId).collect(java.util.stream.Collectors.toList());

        if (!orderIds.isEmpty()) {
            // 把当前正在就诊的（q_state=1）标记为已完成
            QueueNumber current2 = queueMapper.selectOne(
                new QueryWrapper<QueueNumber>()
                    .in("o_id", orderIds)
                    .eq("q_state", 1)
                    .apply("DATE(q_create_time) = CURDATE()")
                    .last("LIMIT 1")
            );
            if (current2 != null) {
                current2.setQState(3);
                current2.setQFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                queueMapper.updateById(current2);
            }
        }
        // 重新排入已过号患者
        if (reQueueId != null) {
            QueueNumber re = queueMapper.selectById(reQueueId);
            if (re != null) {
                re.setQState(0);
                queueMapper.updateById(re);
                return re; // 直接返回重新排入的记录
            }
        }
        // 取下一个待叫号 — 优先从 queue_number 找 q_state=0 的
        if (!orderIds.isEmpty()) {
            QueryWrapper<QueueNumber> nextWrapper = new QueryWrapper<>();
            nextWrapper.in("o_id", orderIds).eq("q_state", 0).apply("DATE(q_create_time) = CURDATE()")
                    .orderByAsc("q_id").last("LIMIT 1");
            QueueNumber next = queueMapper.selectOne(nextWrapper);
            if (next != null) {
                next.setQState(1);
                next.setQCallTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                queueMapper.updateById(next);
                return next;
            }
            // queue_number 中没有等待的，从 orders 中找没有排队号的待诊订单，自动创建排队记录
            List<com.bear.hospital.pojo.Orders> pendingOrders = orderMapper2.selectList(
                new QueryWrapper<com.bear.hospital.pojo.Orders>()
                    .eq("d_id", dId)
                    .in("o_state", 0, 1, 4)
                    .apply("DATE(o_start) = CURDATE()")
                    .orderByAsc("o_id")
            );
            // 也找没有排队号的已分诊订单（o_state=1且无时间范围限制）
            if (pendingOrders.isEmpty()) {
                List<com.bear.hospital.pojo.Orders> triagedOrders = orderMapper2.selectList(
                    new QueryWrapper<com.bear.hospital.pojo.Orders>()
                        .eq("d_id", dId).eq("o_state", 1)
                        .orderByAsc("o_id")
                );
                for (com.bear.hospital.pojo.Orders to : triagedOrders) {
                    QueueNumber existing = queueMapper.selectOne(
                        new QueryWrapper<QueueNumber>().eq("o_id", to.getOId())
                    );
                    if (existing == null) {
                        pendingOrders.add(to);
                    }
                }
            }
            for (com.bear.hospital.pojo.Orders order : pendingOrders) {
                QueueNumber existing = queueMapper.selectOne(
                    new QueryWrapper<QueueNumber>().eq("o_id", order.getOId())
                );
                if (existing == null) {
                    // 自动创建排队记录并叫号
                    QueueNumber newQn = new QueueNumber();
                    newQn.setOId(order.getOId());
                    newQn.setQState(1); // 直接设为已叫号
                    newQn.setQCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    newQn.setQCallTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    queueMapper.insert(newQn);
                    return newQn;
                }
            }
        }
        return null;
    }

    @Override
    public boolean skipNumber(int qId) {
        return queueMapper.skipNumber(qId) > 0;
    }

    @Override
    public List<QueueNumber> listByDoctorToday(String dId) {
        List<QueueNumber> list = queueMapper.findByDoctorToday(dId, todayYmd());
        // 计算派生排队序号，处理没有排队号的订单（qState == null → 设为0）
        int idx = 1;
        for (QueueNumber qn : list) {
            if (qn.getQState() == null) {
                qn.setQState(0); // 未取号的订单默认为等待中
            }
            if (qn.getQState() == 0 || qn.getQState() == 1) {
                qn.setQueueIndex(idx++);
            }
        }
        return list;
    }

    @Override
    public QueueNumber findByPatientToday(int pId) {
        QueueNumber qn = queueMapper.findByPatientToday(pId, todayYmd());
        if (qn != null) {
            // 没有排队号的设为等待中
            if (qn.getQState() == null) {
                qn.setQState(0);
            }
            // 计算前面人数和排队序号
            String dId = qn.getDId();
            if (dId != null) {
                List<QueueNumber> todayList = queueMapper.findByDoctorToday(dId, todayYmd());
                int idx = 0;
                for (QueueNumber item : todayList) {
                    if (item.getQState() == null || item.getQState() == 0) {
                        idx++;
                        if (item.getOId() == qn.getOId()) {
                            qn.setQueueIndex(idx);
                            qn.setAheadCount(idx - 1);
                            break;
                        }
                    }
                }
            }
        }
        return qn;
    }

    @Override
    public List<Map<String, Object>> getDeptQueueStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            // 直接用自定义SQL（QueryWrapper不支持多表JOIN）
            List<Map<String, Object>> stats = queueMapper.selectDeptQueueStats();
            com.bear.hospital.mapper.DoctorMapper doctorMapper2 = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.DoctorMapper.class);
            List<com.bear.hospital.pojo.Doctor> allDocs = doctorMapper2.selectList(null);
            Set<String> allDepts = new java.util.LinkedHashSet<>();
            for (com.bear.hospital.pojo.Doctor d : allDocs) {
                if (d.getdSection() != null) allDepts.add(d.getdSection());
            }
            if (allDepts.isEmpty()) {
                Collections.addAll(allDepts, "内科", "外科", "妇产科", "儿科", "五官科", "中医科", "康复医学科", "急诊科");
            }
            Map<String, Map<String, Object>> statsMap = new HashMap<>();
            for (Map<String, Object> row : stats) {
                String dept = (String) row.get("deptName");
                if (dept != null) statsMap.put(dept, row);
            }
            for (String dept : allDepts) {
                Map<String, Object> m = statsMap.get(dept);
                if (m == null) {
                    m = new HashMap<>();
                    m.put("deptName", dept);
                    m.put("waiting", 0);
                    m.put("calling", "--");
                    m.put("finished", 0);
                }
                result.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String[] depts = {"内科", "外科", "妇产科", "儿科", "五官科", "中医科", "康复医学科", "急诊科"};
            for (String dept : depts) {
                Map<String, Object> m = new HashMap<>();
                m.put("deptName", dept);
                m.put("waiting", 0);
                m.put("calling", "--");
                m.put("finished", 0);
                result.add(m);
            }
        }
        return result;
    }
}
