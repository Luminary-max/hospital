package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.InventoryTransaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface InventoryTransactionMapper extends BaseMapper<InventoryTransaction> {

    @Select("SELECT it.dr_id, d.dr_name, SUM(ABS(it.it_quantity)) as total_consumed " +
            "FROM inventory_transaction it " +
            "LEFT JOIN drug d ON it.dr_id = d.dr_id " +
            "WHERE it.it_type = '发药' " +
            "GROUP BY it.dr_id, d.dr_name " +
            "ORDER BY total_consumed DESC LIMIT #{limit}")
    List<Map<String, Object>> selectConsumptionRanking(@Param("limit") int limit);
}
