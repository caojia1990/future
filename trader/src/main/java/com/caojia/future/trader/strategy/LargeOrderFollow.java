package com.caojia.future.trader.strategy;

import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Buy;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Sell;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_OPT_LimitPrice;

import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderField;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.FuturesMarket;
import com.caojia.future.trader.bean.Position;
import com.caojia.future.trader.programTrading.Application;

public class LargeOrderFollow implements Runnable {

    static Logger logger = Logger.getLogger(LargeOrderFollow.class);
    
    private Application application;
    
    int orderRef;
    long tickCount = 0;
    long volumeAvg = 0;
    long volumeAll = 0;
    
    //缓存上一跳行情
    FuturesMarket lastMarket;
    
    public LargeOrderFollow(Application application){
        this.application = application;
    }
    
    @Override
    public void run() {
        
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
            if(market.getVolumeChange() >0 ){
                
                tickCount++;
                volumeAll += market.getVolumeChange();
                
                volumeAvg = volumeAll/tickCount;
                logger.info("平均成交量："+volumeAvg+", 计数器："+tickCount);
            }
            
            
            //有持仓
            if(application.getPosition() != null){
                
                Position position = application.getPosition();
                
                //计算持仓盈亏
                if(position.getDirection().equals("0")){
                    //买开
                    if(position.getPrice() < market.getBidPrice1()){
                        
                        if(market.getBidVolume1() >= market.getAskVolume1()/2){
                            //如果买一量的挂单大于卖一量的挂单，则继续持有多头合约
                            logger.info("买单支撑比卖单支撑强，继续持有多头");
                            continue;
                        }
                        //止盈
                        logger.info("止盈卖出平仓操作 ，成本价："+position.getPrice()+" ,当前买一价：" +market.getBidPrice1());
                        //卖平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID("cu1708");
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
                    }else if(position.getPrice() > market.getBidPrice1() + 20){
                        //止损 3跳
                        logger.info("止损卖出平仓操作 ，成本价："+position.getPrice()+" ,当前买一价：" +market.getBidPrice1());
                        //卖平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID("cu1708");
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
                            continue;
                        }
                        logger.info("止盈买入平仓操作 ，成本价："+position.getPrice()+" ,当前卖一价：" +market.getAskPrice1());
                      //买平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID("cu1708");
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
                    }else if (position.getPrice() < market.getAskPrice1()-20) {
                        logger.info("止损买入平仓操作 ，成本价："+position.getPrice()+" ,当前卖一价：" +market.getAskPrice1());
                        //止损
                      //买平
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        // 合约代码
                        inputOrderField.setInstrumentID("cu1708");
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
                    
                    if(market.getVolumeChange()/volumeAvg > 3  && tickCount > 20 && market.getOpenInterestChange() >0 ){
                        logger.info("成交量大于平局成交量3倍,策略下单");
                        CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                        
                        // 合约代码
                        inputOrderField.setInstrumentID("cu1708");
                        ///报单引用
                        inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                        
                        // 报单价格条件
                        inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        // 买卖方向    
                        if(market.getLastPrice() <= lastMarket.getBidPrice1() ){
                            //最新价小于等于上一跳的买一价，说明大单做空，跟空单
                            logger.info("最新价小于等于上一跳的买一价，说明大单做空，跟空单");
                            inputOrderField.setDirection(THOST_FTDC_D_Sell);
                            // 价格
                            inputOrderField.setLimitPrice(market.getBidPrice1());
                            
                        }else if (market.getLastPrice() >= lastMarket.getAskPrice1()) {
                            logger.info("最新价大于等于上一跳的卖一价，说明大单做多，跟多单");
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
            
            lastMarket = market;
            
        }

    }

}
