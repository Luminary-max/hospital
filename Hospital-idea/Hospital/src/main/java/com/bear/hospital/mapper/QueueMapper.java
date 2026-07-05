package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.QueueNumber;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface QueueMapper extends BaseMapper<QueueNumber> {
    @Select("SELECT q.*, p.p_name, d.d_name, d.d_section AS dept_name " +
            "FROM queue_number q " +
            "LEFT JOIN orders o ON q.o_id = o.o_id " +
            "LEFT JOIN patient p ON o.p_id = p.p_id " +
            "LEFT JOIN doctor d ON o.d_id = d.d_id " +
            "WHERE o.d_id = #{dId} AND DATE(q.q_create_time) = CURDATE() " +
            "ORDER BY q.q_id ASC")
    List<QueueNumber> findByDoctorToday(@Param("dId") String dId, @Param("today") String today);

    @Select("SELECT q.*, p.p_name, d.d_name, d.d_section AS dept_name " +
            "FROM queue_number q " +
            "LEFT JOIN orders o ON q.o_id = o.o_id " +
            "LEFT JOIN patient p ON o.p_id = p.p_id " +
            "LEFT JOIN doctor d ON o.d_id = d.d_id " +
            "WHERE o.p_id = #{pId} AND DATE(q.q_create_time) = CURDATE() " +
            "ORDER BY q.q_id DESC LIMIT 1")
    QueueNumber findByPatientToday(@Param("pId") int pId, @Param("today") String today);

    @Select("SELECT COUNT(*) FROM queue_number q " +
            "LEFT JOIN orders o ON q.o_id = o.o_id " +
            "WHERE o.d_id = #{dId} AND DATE(q.q_create_time) = CURDATE() AND q.q_state = 0")
    int countWaiting(@Param("dId") String dId, @Param("today") String today);

    @Update("UPDATE queue_number SET q_state = 2, q_call_time = NOW() " +
            "WHERE q_id = #{qId}")
    int skipNumber(@Param("qId") int qId);
}
