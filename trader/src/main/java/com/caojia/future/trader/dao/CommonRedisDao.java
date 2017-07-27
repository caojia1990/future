package com.caojia.future.trader.dao;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component("commonRedisDao")
public class CommonRedisDao {
    
    static Logger logger = Logger.getLogger(CommonRedisDao.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOperations;
    
    /**
     * 缓存hash
     * @param key
     * @param hashKey
     * @param jsonStr
     */
    public void cacheHash(String key, String hashKey, String jsonStr){
        
        this.hashOperations.put(key, hashKey, jsonStr);
    }
    
    public void getHash(){
        
    }
    
    /**
     * 删除hash
     * @param key
     * @param hashKey
     */
    public void deleteHash(String key, String hashKey){
        this.hashOperations.delete(key, hashKey);
    }
}
