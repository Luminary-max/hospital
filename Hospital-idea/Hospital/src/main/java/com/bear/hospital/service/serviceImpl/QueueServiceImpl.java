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
            }
        }
        // 取下一个待叫号（按创建时间升序）— 通过 orders 关联找该医生名下排队
        if (!orderIds.isEmpty()) {
            QueryWrapper<QueueNumber> nextWrapper = new QueryWrapper<>();
            nextWrapper.in("o_id", orderIds).eq("q_state", 0).apply("DATE(q_create_time) = CURDATE()")
                    .orderByAsc("q_id").last("LIMIT 1");
            QueueNumber next = queueMapper.selectOne(nextWrapper);
            if (next != null) {
                next.setQState(1);
                next.setQCallTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                queueMapper.updateById(next);
            }
            return next;
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
        // 计算派生排队序号
        int idx = 1;
        for (QueueNumber qn : list) {
            if (qn.getQState() == 0 || qn.getQState() == 1) {
                qn.setQueueIndex(idx++);
            }
        }
        return list;
    }

    @Override
    public QueueNumber findByPatientToday(int pId) {
        QueueNumber qn = queueMapper.findByPatientToday(pId, todayYmd());
        // 前面等待人数已在 Mapper 层通过 orders JOIN 处理
        return qn;
    }

    @Override
    public List<Map<String, Object>> getDeptQueueStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        String today = todayYmd();
        // 按科室统计排队数据
        String[] depts = {"内科", "外科", "妇产科", "儿科", "五官科", "中医科", "康复医学科", "急诊科"};
        for (String dept : depts) {
            Map<String, Object> m = new HashMap<>();
            m.put("deptName", dept);
            m.put("waiting", 0);
            m.put("calling", "--");
            m.put("finished", 0);
            result.add(m);
        }
        return result;
    }
}
