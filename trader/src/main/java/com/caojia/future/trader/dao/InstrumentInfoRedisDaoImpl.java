package com.caojia.future.trader.dao;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caojia.future.trader.bean.InstrumentInfo;

@Component("instrumentInfoRedisDao")
public class InstrumentInfoRedisDaoImpl {
    
    static Logger logger = Logger.getLogger(InstrumentInfoRedisDaoImpl.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    
    public void saveInstrument(InstrumentInfo instrumentInfo){
        
        valueOperations.set(instrumentInfo.getInstrumentID(), JSON.toJSONString(instrumentInfo));
        
        /*hashOperations.put(instrumentInfo.getInstrumentID(), InstrumentInfo.VOLUME_MULTIPLE, instrumentInfo.getVolumeMultiple());
        hashOperations.put(instrumentInfo.getInstrumentID(), InstrumentInfo.PRICE_TICK, instrumentInfo.getPriceTick());*/
    }
    
    public InstrumentInfo getInstrument(String instrumentId){
        
        String json = valueOperations.get(instrumentId);
        return JSONObject.parseObject(json, InstrumentInfo.class);
        
    }
    
    
    public void removeInstrument(String instrumentId){
        this.redisTemplate.delete(instrumentId);
    }
}
