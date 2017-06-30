package com.caojia.future.trader.strategy;

import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Buy;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Sell;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_OPT_LimitPrice;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderField;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.FutureRecord;
import com.caojia.future.trader.bean.FuturesMarket;
import com.caojia.future.trader.bean.InstrumentInfo;
import com.caojia.future.trader.bean.Position;
import com.caojia.future.trader.dao.InstrumentInfoRedisDaoImpl;
import com.caojia.future.trader.programTrading.Application;
import com.caojia.future.trader.util.SpringContextUtil;

public class LargeOrderFollow implements Runnable {

    static Logger logger = Logger.getLogger(LargeOrderFollow.class);
    
    private Application application;
    
    private InstrumentInfoRedisDaoImpl instrumentInfoRedisDao;
    
    int orderRef;
/*    long tickCount = 0;
    long volumeAvg = 0;
    long volumeAll = 0;*/
    
    //缓存上一跳行情
    //FuturesMarket lastMarket;
    
    Map<String, FuturesMarket> lastMarketMap = new HashMap<String, FuturesMarket>();
    Map<String, FutureRecord> futureRecordMap = new HashMap<String, FutureRecord>();
    Map<String, Double> tickPriceMap = new HashMap<String, Double>();
    
    public LargeOrderFollow(Application application){
        this.application = application;
    }
    
    @Override
    public void run() {
        
        instrumentInfoRedisDao = (InstrumentInfoRedisDaoImpl) SpringContextUtil.getBean("instrumentInfoRedisDao");
        
        while(true){
            
            FuturesMarket market = null;
            
            try {
                market = application.getMarketQueue().take();
            } catch (InterruptedException e) {
                logger.error("线程中断",e);
            }
            
            if(market == null){
                continue;
            }
            
            logger.debug(market.getUpdateTime()+" "+market.getUpdateMillisec()+"接收到行情："+JSON.toJSONString(market));
            
            //计算平均成交量
            long avg = 0;
            FutureRecord record = this.getFutureRecord(market.getInstrumentID());
            if(market.getVolumeChange() >0 ){
                
                /*tickCount++;
                volumeAll += market.getVolumeChange();
                volumeAvg = volumeAll/tickCount;*/
                
                
                record.tickIncrease();
                record.addVolume(market.getVolumeChange());
                avg = record.avg();
                
                
                logger.info("平均成交量："+avg+", 计数器："+record.getTickCount());
            }
            
            
            //有持仓
            if(application.getPositionMap().get(market.getInstrumentID()) != null){
                
                Position position = application.getPositionMap().get(market.getInstrumentID());
                
                //计算持仓盈亏
                if(position.getDirection().equals("0")){
                    //买开
                    if(position.getPrice() < market.getBidPrice1()){
                        
                        if(market.getBidVolume1() >= market.getAskVolume1()/2){
                            //如果买一量的挂单大于卖一量的挂单，则继续持有多头合约
                            logger.info("买单支撑比卖单支撑强，继续持有多头");
                            lastMarketMap.put(market.getInstrumentID(), market);
                            continue;
                        }
                        //止盈
                        logger.info("止盈卖出平仓操作 ，成本价："+position.getPrice()+" ,当前买一价：" +market.getBidPrice1());
                        //卖平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID(market.getInstrumentID());
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                            
                        inputOrderField.setDirection(THOST_FTDC_D_Sell);
                        // 组合开平标志
                        inputOrderField.setCombOffsetFlag("3");
                        // 价格
                        inputOrderField.setLimitPrice(market.getBidPrice1());
                        // 数量
                        inputOrderField.setVolumeTotalOriginal(1);
                        
                        application.reqOrderInsert(inputOrderField);
                    }else if(position.getPrice() > market.getBidPrice1() + getTickPrice(market.getInstrumentID(), 2)){
                        //止损 3跳
                        logger.info("止损卖出平仓操作 ，成本价："+position.getPrice()+" ,当前买一价：" +market.getBidPrice1());
                        //卖平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID(market.getInstrumentID());
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                            
                        inputOrderField.setDirection(THOST_FTDC_D_Sell);
                        // 组合开平标志
                        inputOrderField.setCombOffsetFlag("3");
                        // 价格
                        inputOrderField.setLimitPrice(market.getBidPrice1());
                        // 数量
                        inputOrderField.setVolumeTotalOriginal(1);
                        
                       application.reqOrderInsert(inputOrderField);
                    }
                }else {
                  //卖开
                    
                    if(position.getPrice() > market.getAskPrice1()){
                        
                        if(market.getAskVolume1() >= market.getBidVolume1()/2){
                            logger.info("卖单支撑比买单支撑强，继续持有空头");
                            lastMarketMap.put(market.getInstrumentID(), market);
                            continue;
                        }
                        logger.info("止盈买入平仓操作 ，成本价："+position.getPrice()+" ,当前卖一价：" +market.getAskPrice1());
                      //买平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID(market.getInstrumentID());
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                            
                        inputOrderField.setDirection(THOST_FTDC_D_Buy);
                        // 组合开平标志
                        inputOrderField.setCombOffsetFlag("3");
                        // 价格
                        inputOrderField.setLimitPrice(market.getAskPrice1());
                        // 数量
                        inputOrderField.setVolumeTotalOriginal(1);
                        
                        application.reqOrderInsert(inputOrderField);
                    }else if (position.getPrice() < market.getAskPrice1()-getTickPrice(market.getInstrumentID(), 2)) {
                        logger.info("止损买入平仓操作 ，成本价："+position.getPrice()+" ,当前卖一价：" +market.getAskPrice1());
                        //止损
                      //买平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID(market.getInstrumentID());
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                            
                        inputOrderField.setDirection(THOST_FTDC_D_Buy);
                        // 组合开平标志
                        inputOrderField.setCombOffsetFlag("3");
                        // 价格
                        inputOrderField.setLimitPrice(market.getAskPrice1());
                        // 数量
                        inputOrderField.setVolumeTotalOriginal(1);
                        
                        application.reqOrderInsert(inputOrderField);
                    }
                }
            
            }else {
              //下单
                if(market.getVolumeChange() >0 ){
                    
                    if(market.getVolumeChange()/avg > 3  && record.getTickCount() > 20 && market.getOpenInterestChange() >0 ){
                        logger.info("成交量大于平局成交量3倍,策略下单");
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        
                        // 合约代码
                        inputOrderField.setInstrumentID(market.getInstrumentID());
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        // 买卖方向    
                        if(market.getLastPrice().doubleValue() <= lastMarketMap.get(market.getInstrumentID()).getBidPrice1().doubleValue() ){
                            
                            if(market.getLastPrice().doubleValue() == lastMarketMap.get(market.getInstrumentID()).getBidPrice1().doubleValue()){
                                //如果最新价等于上一跳的买一价
                                if( (lastMarketMap.get(market.getInstrumentID()).getBidVolume1() - market.getBidVolume1()) <= (lastMarketMap.get(market.getInstrumentID()).getAskVolume1() - market.getBidVolume1()) ){
                                    //如果买一量支撑消减比卖一量慢  则不跟空单  继续观望
                                    logger.debug("多头优势，不跟空单");
                                    lastMarketMap.put(market.getInstrumentID(), market);
                                    continue;
                                }
                            }else {
                                logger.debug("合约"+market.getInstrumentID()+"价格已往下跳动，不再跟单"+"最新价："+market.getLastPrice()+", 上一跳买一价："+lastMarketMap.get(market.getInstrumentID()).getBidPrice1());
                                lastMarketMap.put(market.getInstrumentID(), market);
                                continue;
                            }
                            //最新价小于等于上一跳的买一价，说明大单做空，跟空单
                            logger.info("最新价等于上一跳的买一价，说明大单做空，跟空单");
                            inputOrderField.setDirection(THOST_FTDC_D_Sell);
                            // 价格
                            inputOrderField.setLimitPrice(market.getBidPrice1());
                            
                        }else if (market.getLastPrice().doubleValue() >= lastMarketMap.get(market.getInstrumentID()).getAskPrice1().doubleValue()) {
                            
                            if(market.getLastPrice().doubleValue() == lastMarketMap.get(market.getInstrumentID()).getAskPrice1().doubleValue()){
                              //如果最新价等于上一跳的卖一价
                                if( (lastMarketMap.get(market.getInstrumentID()).getBidVolume1() - market.getBidPrice1()) >= (lastMarketMap.get(market.getInstrumentID()).getAskPrice1() - market.getBidVolume1()) ){
                                    //如果买一量支撑消减比卖一量快  说明做空优势大  继续观望
                                    logger.debug("空头优势，不跟多单");
                                    lastMarketMap.put(market.getInstrumentID(), market);
                                    continue;
                                }
                            }else {
                                logger.debug("合约"+market.getInstrumentID()+"价格已往上跳动，不再跟单"+"最新价："+market.getLastPrice()+",上一跳卖一价： "+lastMarketMap.get(market.getInstrumentID()).getAskPrice1());
                                lastMarketMap.put(market.getInstrumentID(), market);
                                continue;
                            }
                            
                            logger.info("最新价等于上一跳的卖一价，说明大单做多，跟多单");
                            //最新价大于等于上一跳的卖一价，说明大单做多，跟多单
                            inputOrderField.setDirection(THOST_FTDC_D_Buy);
                            // 价格
                            inputOrderField.setLimitPrice(market.getAskPrice1());
                        }
                        // 组合开平标志
                        inputOrderField.setCombOffsetFlag("0");
                        
                        // 数量
                        inputOrderField.setVolumeTotalOriginal(1);
                        
                        application.reqOrderInsert(inputOrderField);
                        
                    }
                }
            }
            
            //lastMarket = market;
            lastMarketMap.put(market.getInstrumentID(), market);
            
        }

    }
    
    public FutureRecord getFutureRecord(String instrument){
        
        FutureRecord futureRecord = this.futureRecordMap.get(instrument);
        if(futureRecord == null){
            futureRecord = new FutureRecord();
            futureRecordMap.put(instrument, futureRecord);
        }
        return futureRecord;
    }

    public double getTickPrice(String instrumentId, int tick){
        
        Double tickPrice = this.tickPriceMap.get(instrumentId);
        if(tickPrice == null){
            InstrumentInfo info = this.instrumentInfoRedisDao.getInstrument(instrumentId);
            tickPrice = info.getPriceTick();
            tickPriceMap.put(instrumentId, tickPrice);
        }
        return tickPrice*tick;
    }
}
