package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.QueueNumber;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface QueueMapper extends BaseMapper<QueueNumber> {
    @Select("SELECT DISTINCT o.o_id, o.p_id AS p_id, o.o_state AS o_state, q.q_id, q.q_state, q.q_create_time, q.q_call_time, q.q_finish_time, " +
            "p.p_name, d.d_name, d.d_section AS dept_name, tr.t_level AS t_level " +
            "FROM orders o " +
            "LEFT JOIN queue_number q ON o.o_id = q.o_id " +
            "LEFT JOIN patient p ON o.p_id = p.p_id " +
            "LEFT JOIN doctor d ON o.d_id = d.d_id " +
            "LEFT JOIN triage_record tr ON tr.p_id = o.p_id AND DATE(tr.t_create_time) = CURDATE() " +
            "WHERE o.d_id = #{dId} AND DATE(o.o_start) = CURDATE() AND o.o_state IN (0,1,4) " +
            "ORDER BY tr.t_level DESC, COALESCE(q.q_id, o.o_id) ASC")
    List<QueueNumber> findByDoctorToday(@Param("dId") String dId, @Param("today") String today);

    @Select("SELECT o.o_id, q.q_id, q.q_state, q.q_create_time, q.q_call_time, q.q_finish_time, " +
            "p.p_name, d.d_name, d.d_section AS dept_name, d.d_id AS d_id " +
            "FROM orders o " +
            "LEFT JOIN queue_number q ON o.o_id = q.o_id " +
            "LEFT JOIN patient p ON o.p_id = p.p_id " +
            "LEFT JOIN doctor d ON o.d_id = d.d_id " +
            "WHERE o.p_id = #{pId} AND DATE(o.o_start) = CURDATE() " +
            "ORDER BY COALESCE(q.q_id, o.o_id) DESC LIMIT 1")
    QueueNumber findByPatientToday(@Param("pId") int pId, @Param("today") String today);

    @Select("SELECT COUNT(*) FROM queue_number q " +
            "LEFT JOIN orders o ON q.o_id = o.o_id " +
            "WHERE o.d_id = #{dId} AND DATE(q.q_create_time) = CURDATE() AND q.q_state = 0")
    int countWaiting(@Param("dId") String dId, @Param("today") String today);

    @Update("UPDATE queue_number SET q_state = 2, q_call_time = NOW() " +
            "WHERE q_id = #{qId}")
    int skipNumber(@Param("qId") int qId);

    @Select("SELECT d.d_section AS deptName, " +
            "COUNT(CASE WHEN q.q_state = 0 THEN 1 END) AS waiting, " +
            "COUNT(CASE WHEN q.q_state = 1 THEN 1 END) AS calling, " +
            "COUNT(CASE WHEN q.q_state = 3 THEN 1 END) AS finished " +
            "FROM queue_number q " +
            "LEFT JOIN orders o ON q.o_id = o.o_id " +
            "LEFT JOIN doctor d ON o.d_id = d.d_id " +
            "WHERE DATE(q.q_create_time) = CURDATE() " +
            "GROUP BY d.d_section")
    List<Map<String, Object>> selectDeptQueueStats();
}
