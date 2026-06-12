package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.OutpatientEmr;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmrMapper extends BaseMapper<OutpatientEmr> {
    @Select("SELECT e.*, p.p_name, d.d_name, o.o_start " +
            "FROM outpatient_emr e " +
            "LEFT JOIN patient p ON e.p_id = p.p_id " +
            "LEFT JOIN doctor d ON e.d_id = d.d_id " +
            "LEFT JOIN orders o ON e.o_id = o.o_id " +
            "WHERE e.p_id = #{pId} ORDER BY e.create_time DESC")
    List<OutpatientEmr> findByPatientId(@Param("pId") int pId);

    @Select("SELECT e.*, p.p_name, d.d_name, o.o_start " +
            "FROM outpatient_emr e " +
            "LEFT JOIN patient p ON e.p_id = p.p_id " +
            "LEFT JOIN doctor d ON e.d_id = d.d_id " +
            "LEFT JOIN orders o ON e.o_id = o.o_id " +
            "WHERE e.o_id = #{oId}")
    OutpatientEmr findByOrderId(@Param("oId") int oId);
}
