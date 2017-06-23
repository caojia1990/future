package com.caojia.future.trader.dao;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.stereotype.Component;

import com.caojia.future.trader.bean.InstrumentInfo;

@Component("instrumentInfoRedisDao")
public class InstrumentInfoRedisDaoImpl {
    
    static Logger logger = Logger.getLogger(InstrumentInfoRedisDaoImpl.class);
    
    HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Resource(name="redisTemplate")
    private HashOperations<String, byte[], byte[]> hashOperations;
    
    public void saveInstrument(InstrumentInfo instrumentInfo){
        
        Map<byte[], byte[]> mappedHash = mapper.toHash(instrumentInfo);
        hashOperations.putAll(instrumentInfo.getInstrumentID(),mappedHash);
    }
}
