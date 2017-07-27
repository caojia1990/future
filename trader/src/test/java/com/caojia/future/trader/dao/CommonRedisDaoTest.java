package com.caojia.future.trader.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.Position;

@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class CommonRedisDaoTest extends AbstractJUnit4SpringContextTests{
    
    @Autowired
    private CommonRedisDao commonRedisDao;
    
    @Test
    public void cacheHash(){
        
        for(int i =0 ; i < 10 ; i++){
            
            Position position = new Position();
            position.setInstrumentID("cu1709");
            position.setDirection("0");
            position.setOrderSystemId("123123"+i);
            position.setPrice(50100.00);
            position.setVolume(1);
            position.setOrderRef(i+"");
            
            this.commonRedisDao.cacheHash(position.getInstrumentID(),
                    position.getOrderSystemId(), JSON.toJSONString(position));
            
        }
        
        
    }

}
