package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.PrescriptionDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PrescriptionMapper extends BaseMapper<PrescriptionDetail> {
    @Select("SELECT pd.*, d.dr_name " +
            "FROM prescription_detail pd " +
            "LEFT JOIN drug d ON pd.dr_id = d.dr_id " +
            "LEFT JOIN prescription_master pm ON pd.pm_id = pm.pm_id " +
            "LEFT JOIN outpatient_emr e ON pm.emr_id = e.emr_id " +
            "WHERE e.o_id = #{oId}")
    List<PrescriptionDetail> findByOrderId(@Param("oId") int oId);
}
