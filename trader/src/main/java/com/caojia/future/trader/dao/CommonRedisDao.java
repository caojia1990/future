package com.caojia.future.trader.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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
    
    /**
     * 获取hash
     * @param key
     * @param hashKey
     * @return
     */
    public String getHash(String key, String hashKey){
        
        return this.hashOperations.get(key, hashKey);
    }
    
    /**
     * 获取指定key的hash所有属性
     * @param key
     * @return
     */
    public List<String> getHashList(String key){
        
        //logger.info("从redis中获取key值为:"+key+"的持仓信息");
        
        List<String> list = this.hashOperations.multiGet(key, hashOperations.keys(key));
        
        //logger.debug("成功获取持仓信息"+list);
        return list;
    }
    
    public Set<String> getKeys(String key){
        
        return  this.hashOperations.keys(key);
    }
    
    /**
     * 删除hash
     * @param key
     * @param hashKey
     */
    public void deleteHash(String key, String hashKey){
        this.hashOperations.delete(key, hashKey);
    }
    
    
    public void deleteByKey(String key){
        
        this.redisTemplate.delete(key);
        
    }
    
    /**
     * 设置key值
     * @param key
     * @param value
     */
    public void setValueByKey(String key, String value){
        
        this.valueOperations.set(key, value);
    }
    
    /**
     * 获取key值
     * @param key
     * @return
     */
    public String getValueByKey(String key){
        return this.valueOperations.get(key);
    }
    
    /**
     * 原子操作
     * @param key
     * @param value
     */
    public void increamentByKey(String key, Long value){
        this.valueOperations.increment(key, value);
    }
    
    
}
