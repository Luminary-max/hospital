package com.bear.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bear.hospital.pojo.Bed;

public interface BedMapper extends BaseMapper<Bed> {
    /**
     * 统计今天留观人数
     */
    int bedPeople(String bStart);
}
