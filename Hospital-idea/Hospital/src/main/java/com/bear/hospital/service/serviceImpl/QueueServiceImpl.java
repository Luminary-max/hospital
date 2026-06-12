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
        int maxNum = queueMapper.getTodayMaxNumber(dId, today);
        String prefix = getDeptPrefix(dId);
        String qNumber = prefix + String.format("%03d", maxNum + 1);

        QueueNumber qn = new QueueNumber();
        qn.setOId(oId);
        qn.setPId(pId);
        qn.setDId(dId);
        qn.setQNumber(qNumber);
        qn.setQState(0);
        qn.setQCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        queueMapper.insert(qn);
        return qNumber;
    }

    private String getDeptPrefix(String dId) {
        // 根据科室获取前缀
        Doctor doc = doctorMapper.selectById(dId);
        if (doc == null || doc.getdSection() == null) return "A";
        String section = doc.getdSection();
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("神经内科", "N"); prefixMap.put("呼吸与危重症医学科", "N");
        prefixMap.put("内分泌科", "N"); prefixMap.put("消化内科", "N");
        prefixMap.put("心血管内科", "N"); prefixMap.put("肾内科", "N");
        prefixMap.put("发热门诊", "N");
        prefixMap.put("手足外科", "W"); prefixMap.put("普通外科", "W");
        prefixMap.put("肛肠外科", "W"); prefixMap.put("神经外科", "W");
        prefixMap.put("泌尿外科", "W"); prefixMap.put("骨科", "W");
        prefixMap.put("烧伤整形外科", "W");
        prefixMap.put("妇科", "F"); prefixMap.put("产科", "F");
        prefixMap.put("儿科", "P"); prefixMap.put("儿童保健科", "P");
        prefixMap.put("耳鼻咽喉科", "E"); prefixMap.put("眼科", "E"); prefixMap.put("口腔科", "E");
        prefixMap.put("中医科", "T");
        // 默认
        for (Map.Entry<String, String> e : prefixMap.entrySet()) {
            if (section.contains(e.getKey()) || e.getKey().contains(section)) {
                return e.getValue();
            }
        }
        return "A";
    }

    @Override
    @Transactional
    public QueueNumber callNext(String dId, Integer reQueueId) {
        String today = todayYmd();
        // 先标记正在叫号的为完成
        QueryWrapper<QueueNumber> currentWrapper = new QueryWrapper<>();
        currentWrapper.eq("d_id", dId).eq("q_state", 1).apply("q_create_time LIKE CONCAT({0}, '%')", today);
        QueueNumber current = queueMapper.selectOne(currentWrapper);
        if (current != null) {
            current.setQState(3);
            current.setQFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            queueMapper.updateById(current);
        }
        QueueNumber next;
        if (reQueueId != null) {
            // 重新排入已过号的患者（加到队列最前）
            next = queueMapper.selectById(reQueueId);
            if (next != null) {
                next.setQState(0);
                queueMapper.updateById(next);
            }
        }
        // 取下一个待叫号
        QueryWrapper<QueueNumber> nextWrapper = new QueryWrapper<>();
        nextWrapper.eq("d_id", dId).eq("q_state", 0).apply("q_create_time LIKE CONCAT({0}, '%')", today)
                .orderByAsc("q_id").last("LIMIT 1");
        next = queueMapper.selectOne(nextWrapper);
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
        return queueMapper.findByDoctorToday(dId, todayYmd());
    }

    @Override
    public QueueNumber findByPatientToday(int pId) {
        return queueMapper.findByPatientToday(pId, todayYmd());
    }

    @Override
    public List<Map<String, Object>> getDeptQueueStats() {
        // 按科室统计排队数据
        List<Map<String, Object>> result = new ArrayList<>();
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
