package com.bear.hospital.service.serviceImpl;

import com.bear.hospital.mapper.ArrangeMapper;
import com.bear.hospital.pojo.Arrange;
import com.bear.hospital.service.ArrangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;

@Service("ArrangeService")
public class ArrangeServiceImpl implements ArrangeService {
    @Autowired
    private ArrangeMapper arrangeMapper;
    @Autowired
    private JedisPool jedisPool;//redis连接池

    /**
     * 根据日期查询排班信息
     */
    @Override
    public List<Arrange> findByTime(String arTime, String dSection) {
        return this.arrangeMapper.findByTime(arTime, dSection);
    }
    /**
     * 增加排班信息
     */
    public Boolean addArrange(Arrange arrange){
        Arrange arrange1 = this.arrangeMapper.selectById(arrange.getArId());
        if (arrange1 == null) {
            // Redis操作（可选，无Redis不影响排班）
            try {
                Jedis jedis = jedisPool.getResource();
                HashMap<String, String> map = new HashMap<>();
                map.put("eTOn","40");
                map.put("nTOt","40");
                map.put("tTOe","40");
                map.put("fTOf","40");
                map.put("fTOs","40");
                map.put("sTOs","40");
                jedis.hmset(arrange.getArId(), map);
                jedis.expire(arrange.getArId(), 604800);
                jedis.close();
            } catch(Exception e) {
                // Redis不可用时忽略，不影响排班功能
            }
            this.arrangeMapper.insert(arrange);
            return true;
        }
        return false;
    }

    /**
     * 删除排班信息
     */
    public Boolean deleteArrange(String arId){
        Arrange arrange = this.arrangeMapper.selectById(arId);
        if (arrange != null) {
            try {
                Jedis jedis = jedisPool.getResource();
                jedis.del(arId);
                jedis.close();
            } catch(Exception e) {
                // Redis不可用时忽略
            }
            // 先删 arrangement（外键依赖），再删 arrange
            try {
                com.bear.hospital.mapper.ArrangeMapper arrangementMapper = com.bear.hospital.spring.SpringContextHolder.getBean(com.bear.hospital.mapper.ArrangeMapper.class);
                arrangementMapper.deleteById(arId);
            } catch(Exception e) {
                // arrangement 表可能不存在或有其他问题，不影响主删除
            }
            this.arrangeMapper.deleteById(arId);
            return true;
        }
        return false;
    }

}
