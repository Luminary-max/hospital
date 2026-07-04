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
    public String takeNumber(int oId, int pId, String dId) {
        String today = todayYmd();
        QueueNumber qn = new QueueNumber();
        qn.setOId(oId);
        qn.setPId(pId);
        qn.setDId(dId);
        qn.setQState(0);
        qn.setQCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        queueMapper.insert(qn);
        // 返回排队序号（基于该医生当天等待人数+1）
        int index = queueMapper.countWaiting(dId, today) + 1;
        return String.valueOf(index);
    }

    @Override
    @Transactional
    public QueueNumber callNext(String dId, Integer reQueueId) {
        String today = todayYmd();
        // 先标记正在叫号的为完成
        QueryWrapper<QueueNumber> currentWrapper = new QueryWrapper<>();
        currentWrapper.eq("d_id", dId).eq("q_state", 1).apply("DATE(q_create_time) = CURDATE()");
        QueueNumber current = queueMapper.selectOne(currentWrapper);
        if (current != null) {
            current.setQState(3);
            current.setQFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            queueMapper.updateById(current);
        }
        // 重新排入已过号患者
        if (reQueueId != null) {
            QueueNumber re = queueMapper.selectById(reQueueId);
            if (re != null) {
                re.setQState(0);
                queueMapper.updateById(re);
            }
        }
        // 取下一个待叫号（按创建时间升序）
        QueryWrapper<QueueNumber> nextWrapper = new QueryWrapper<>();
        nextWrapper.eq("d_id", dId).eq("q_state", 0).apply("DATE(q_create_time) = CURDATE()")
                .orderByAsc("q_id").last("LIMIT 1");
        QueueNumber next = queueMapper.selectOne(nextWrapper);
        if (next != null) {
            next.setQState(1);
            next.setQCallTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            queueMapper.updateById(next);
        }
        return next;
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
        if (qn != null) {
            // 计算前面等待人数
            QueryWrapper<QueueNumber> ahead = new QueryWrapper<>();
            ahead.eq("d_id", qn.getDId())
                .eq("q_state", 0)
                .lt("q_id", qn.getQId())
                .apply("DATE(q_create_time) = CURDATE()");
            int aheadCount = queueMapper.selectCount(ahead);
            qn.setQueueIndex(aheadCount + 1);
        }
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
