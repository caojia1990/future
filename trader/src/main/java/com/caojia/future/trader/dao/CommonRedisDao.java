package com.caojia.future.trader.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
     * 获取指定key的hash所有属性
     * @param key
     * @return
     */
    public List<String> getHashList(String key){
        Long size = hashOperations.size(key);
        
        ScanOptions options = ScanOptions.scanOptions().count(size).build();
        Cursor<Entry<String, String>> cursor = this.hashOperations.scan(key, options);
        
        List<String> list = new ArrayList<String>();
        
        while(cursor.hasNext()){
            Entry<String, String> entry = cursor.next();
            list.add(entry.getValue());
        }
        return list;
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
}
