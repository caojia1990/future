package com.caojia.future.trader.dao;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.Position;
import com.google.common.base.Stopwatch;

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
            position.setTradeID("123123"+i);
            position.setPrice(50100.00);
            position.setVolume(1);
            position.setTradeDate("20170728");
            
            this.commonRedisDao.cacheHash(Position.POSITION+position.getInstrumentID(),
                    position.getTradeID(), JSON.toJSONString(position));
        }
        
    }
    
    @Test
    public void deleteHash(){
        this.commonRedisDao.deleteHash(Position.POSITION+"cu1709", "1231230");
    }
    
    
    @Test
    public void getHashList(){
        
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> list = this.commonRedisDao.getHashList("cu1709");
        System.out.println("耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS)+"ms");
        
        System.out.println(list);
    }
    
    @Test
    public void deleteByKey(){
        this.commonRedisDao.deleteByKey(Position.POSITION+"cu1709");
    }

    @Test
    public void getKeysTest(){
        System.out.println(this.commonRedisDao.getKeys(Position.POSITION+"cu1709"));
    }
    
    @Test
    public void getValueByKey(){
        Long buy = Long.parseLong(this.commonRedisDao.getValueByKey("buy:cu1801"));
        System.out.println("买："+buy);
        
        Long sell = Long.parseLong(this.commonRedisDao.getValueByKey("sell:cu1801"));
        System.out.println("卖："+sell);
    }
    
    @Test
    public void setValueByKey(){
        this.commonRedisDao.setValueByKey("buy:cu1801", "12");
    }
    
    @Test
    public void increament(){
        this.commonRedisDao.increamentByKey("buy:cu1801", -5L);
    }
}
