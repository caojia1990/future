package com.caojia.future.trader.strategy;

import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_CC_Immediately;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Buy;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_D_Sell;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_FCC_NotForceClose;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_OPT_LimitPrice;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_TC_IOC;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_VC_AV;

import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_TE_RESUME_TYPE;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcDepthMarketDataField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcTradeField;
import org.hraink.futures.jctp.md.JCTPMdApi;
import org.hraink.futures.jctp.md.JCTPMdSpi;
import org.hraink.futures.jctp.trader.JCTPTraderApi;
import org.hraink.futures.jctp.trader.JCTPTraderSpi;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caojia.future.trader.service.FutureMarketService;
import com.caojia.future.trader.util.SpringContextUtil;

public class Application {
    
    static Logger logger = Logger.getLogger(Application.class);
    
    //行情地址
    public static String marketFront = "tcp://180.168.146.187:10010";
    /** 行情API **/
    static JCTPMdApi mdApi;
    static JCTPMdSpi mdSpi;
    
    //交易地址 
    public static String tradeFront = "tcp://180.168.146.187:10000";
    static JCTPTraderApi traderApi;
    static JCTPTraderSpi traderSpi;
    
    private static FutureMarketService marketService;
    
    int volume = 0;
    double openInterest = 0;
    long tickCount = 0;
    long volumeAvg = 0;
    long volumeAll = 0;
    int orderRef = 0;
    int requestId = 0;
    
    private JSONObject json;
    
    //上一跳的买一价
    private double lastBidPrice;
    //上一跳的卖一价
    private double lastAskPrice;
    
    
    public void cu1707(CThostFtdcDepthMarketDataField pDepthMarketData){
        
        
        if(json != null){
            
            logger.info("最近一笔成交信息："+JSON.toJSONString(json));
            //计算持仓盈亏
            if(json.getString("direction").equals("0")){
                //买开
                if(json.getDouble("price")<pDepthMarketData.getBidPrice1()){
                    //止盈
                    logger.info("止盈卖出平仓操作 ，成本价："+json.getDouble("price")+" ,当前买一价：" +pDepthMarketData.getBidPrice1());
                    //卖平
                    CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                    //期货公司代码
                    inputOrderField.setBrokerID("9999");
                    //投资者代码
                    inputOrderField.setInvestorID("090985");
                    // 合约代码
                    inputOrderField.setInstrumentID("cu1708");
                    ///报单引用
                    inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                    // 用户代码
                    inputOrderField.setUserID("090985");
                    // 报单价格条件
                    inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        
                    inputOrderField.setDirection(THOST_FTDC_D_Sell);
                    // 组合开平标志
                    inputOrderField.setCombOffsetFlag("3");
                    // 组合投机套保标志
                    inputOrderField.setCombHedgeFlag("1");
                    // 价格
                    inputOrderField.setLimitPrice(pDepthMarketData.getBidPrice1());
                    // 数量
                    inputOrderField.setVolumeTotalOriginal(1);
                    // 有效期类型
                    inputOrderField.setTimeCondition(THOST_FTDC_TC_IOC);
                    // GTD日期
                    inputOrderField.setGTDDate("");
                    // 成交量类型
                    inputOrderField.setVolumeCondition(THOST_FTDC_VC_AV);
                    // 最小成交量
                    inputOrderField.setMinVolume(0);
                    // 触发条件
                    inputOrderField.setContingentCondition(THOST_FTDC_CC_Immediately);
                    // 止损价
                    inputOrderField.setStopPrice(0);
                    // 强平原因
                    inputOrderField.setForceCloseReason(THOST_FTDC_FCC_NotForceClose);
                    // 自动挂起标志
                    inputOrderField.setIsAutoSuspend(0);
                    
                    traderApi.reqOrderInsert(inputOrderField, ++requestId);
                }else if(json.getDouble("price") > pDepthMarketData.getBidPrice1() + 20){
                    //止损 3跳
                    logger.info("止损卖出平仓操作 ，成本价："+json.getDouble("price")+" ,当前买一价：" +pDepthMarketData.getBidPrice1());
                    //卖平
                    CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                    //期货公司代码
                    inputOrderField.setBrokerID("9999");
                    //投资者代码
                    inputOrderField.setInvestorID("090985");
                    // 合约代码
                    inputOrderField.setInstrumentID("cu1708");
                    ///报单引用
                    inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                    // 用户代码
                    inputOrderField.setUserID("090985");
                    // 报单价格条件
                    inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        
                    inputOrderField.setDirection(THOST_FTDC_D_Sell);
                    // 组合开平标志
                    inputOrderField.setCombOffsetFlag("3");
                    // 组合投机套保标志
                    inputOrderField.setCombHedgeFlag("1");
                    // 价格
                    inputOrderField.setLimitPrice(pDepthMarketData.getBidPrice1());
                    // 数量
                    inputOrderField.setVolumeTotalOriginal(1);
                    // 有效期类型
                    inputOrderField.setTimeCondition(THOST_FTDC_TC_IOC);
                    // GTD日期
                    inputOrderField.setGTDDate("");
                    // 成交量类型
                    inputOrderField.setVolumeCondition(THOST_FTDC_VC_AV);
                    // 最小成交量
                    inputOrderField.setMinVolume(0);
                    // 触发条件
                    inputOrderField.setContingentCondition(THOST_FTDC_CC_Immediately);
                    // 止损价
                    inputOrderField.setStopPrice(0);
                    // 强平原因
                    inputOrderField.setForceCloseReason(THOST_FTDC_FCC_NotForceClose);
                    // 自动挂起标志
                    inputOrderField.setIsAutoSuspend(0);
                    
                    traderApi.reqOrderInsert(inputOrderField, ++requestId);
                }
            }else {
              //卖开
                if(json.getDouble("price")>pDepthMarketData.getAskPrice1()){
                    logger.info("止盈买入平仓操作 ，成本价："+json.getDouble("price")+" ,当前卖一价：" +pDepthMarketData.getAskPrice1());
                  //买平
                    CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                    //期货公司代码
                    inputOrderField.setBrokerID("9999");
                    //投资者代码
                    inputOrderField.setInvestorID("090985");
                    // 合约代码
                    inputOrderField.setInstrumentID("cu1708");
                    ///报单引用
                    inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                    // 用户代码
                    inputOrderField.setUserID("090985");
                    // 报单价格条件
                    inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        
                    inputOrderField.setDirection(THOST_FTDC_D_Buy);
                    // 组合开平标志
                    inputOrderField.setCombOffsetFlag("3");
                    // 组合投机套保标志
                    inputOrderField.setCombHedgeFlag("1");
                    // 价格
                    inputOrderField.setLimitPrice(pDepthMarketData.getAskPrice1());
                    // 数量
                    inputOrderField.setVolumeTotalOriginal(1);
                    // 有效期类型
                    inputOrderField.setTimeCondition(THOST_FTDC_TC_IOC);
                    // GTD日期
                    inputOrderField.setGTDDate("");
                    // 成交量类型
                    inputOrderField.setVolumeCondition(THOST_FTDC_VC_AV);
                    // 最小成交量
                    inputOrderField.setMinVolume(0);
                    // 触发条件
                    inputOrderField.setContingentCondition(THOST_FTDC_CC_Immediately);
                    // 止损价
                    inputOrderField.setStopPrice(0);
                    // 强平原因
                    inputOrderField.setForceCloseReason(THOST_FTDC_FCC_NotForceClose);
                    // 自动挂起标志
                    inputOrderField.setIsAutoSuspend(0);
                    
                    traderApi.reqOrderInsert(inputOrderField, ++requestId);
                }else if (json.getDouble("price") < pDepthMarketData.getAskPrice1()-20) {
                    logger.info("止损买入平仓操作 ，成本价："+json.getDouble("price")+" ,当前卖一价：" +pDepthMarketData.getAskPrice1());
                    //止损
                  //买平
                    CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                    //期货公司代码
                    inputOrderField.setBrokerID("9999");
                    //投资者代码
                    inputOrderField.setInvestorID("090985");
                    // 合约代码
                    inputOrderField.setInstrumentID("cu1708");
                    ///报单引用
                    inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                    // 用户代码
                    inputOrderField.setUserID("090985");
                    // 报单价格条件
                    inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                        
                    inputOrderField.setDirection(THOST_FTDC_D_Buy);
                    // 组合开平标志
                    inputOrderField.setCombOffsetFlag("3");
                    // 组合投机套保标志
                    inputOrderField.setCombHedgeFlag("1");
                    // 价格
                    inputOrderField.setLimitPrice(pDepthMarketData.getAskPrice1());
                    // 数量
                    inputOrderField.setVolumeTotalOriginal(1);
                    // 有效期类型
                    inputOrderField.setTimeCondition(THOST_FTDC_TC_IOC);
                    // GTD日期
                    inputOrderField.setGTDDate("");
                    // 成交量类型
                    inputOrderField.setVolumeCondition(THOST_FTDC_VC_AV);
                    // 最小成交量
                    inputOrderField.setMinVolume(0);
                    // 触发条件
                    inputOrderField.setContingentCondition(THOST_FTDC_CC_Immediately);
                    // 止损价
                    inputOrderField.setStopPrice(0);
                    // 强平原因
                    inputOrderField.setForceCloseReason(THOST_FTDC_FCC_NotForceClose);
                    // 自动挂起标志
                    inputOrderField.setIsAutoSuspend(0);
                    
                    traderApi.reqOrderInsert(inputOrderField, ++requestId);
                }
            }
        }
        
        int volumeChange = 0;
        double openInterestChange = 0;
        if(volume == 0){
            volume = pDepthMarketData.getVolume();
            openInterest = pDepthMarketData.getOpenInterest();
        }else {
            volumeChange = pDepthMarketData.getVolume() - volume;
            openInterestChange = pDepthMarketData.getOpenInterest() - openInterest;
            logger.info("时间："+pDepthMarketData.getUpdateTime()+" "+pDepthMarketData.getUpdateMillisec()+"： 上次成交总量："+volume+" ; 本次成交总量："+pDepthMarketData.getVolume()+" ; 成交量："+volumeChange+" ; 增仓数量："+openInterestChange);
            volume = pDepthMarketData.getVolume();
            openInterest = pDepthMarketData.getOpenInterest();
            if(volumeChange >0 ){
                tickCount++;
                volumeAll += volumeChange;
                
                volumeAvg = volumeAll/tickCount;
                logger.info("平均成交量："+volumeAvg+", 计数器："+tickCount);
                if(volumeChange/volumeAvg > 3 && tickCount > 20 ){
                    if(json != null){
                        return;
                    }
                    logger.info("本次成交量大于平均成交量3倍,策略下单");
                    CThostFtdcInputOrderField inputOrderField=new CThostFtdcInputOrderField();
                    //期货公司代码
                    inputOrderField.setBrokerID("9999");
                    //投资者代码
                    inputOrderField.setInvestorID("090985");
                    // 合约代码
                    inputOrderField.setInstrumentID("cu1708");
                    ///报单引用
                    inputOrderField.setOrderRef(String.valueOf((orderRef++)));
                    // 用户代码
                    inputOrderField.setUserID("090985");
                    // 报单价格条件
                    inputOrderField.setOrderPriceType(THOST_FTDC_OPT_LimitPrice);
                    // 买卖方向    
                    if(pDepthMarketData.getLastPrice() <= lastBidPrice){
                        //最新价小于等于上一跳的买一价，说明大单做空，跟空单
                        inputOrderField.setDirection(THOST_FTDC_D_Sell);
                    }else if (pDepthMarketData.getLastPrice() >= pDepthMarketData.getAskPrice1()) {
                        //最新价大于等于上一跳的卖一价，说明大单做多，跟多单
                        inputOrderField.setDirection(THOST_FTDC_D_Buy);
                    }
                    // 组合开平标志
                    inputOrderField.setCombOffsetFlag("0");
                    // 组合投机套保标志
                    inputOrderField.setCombHedgeFlag("1");
                    // 价格
                    inputOrderField.setLimitPrice(pDepthMarketData.getLastPrice());
                    // 数量
                    inputOrderField.setVolumeTotalOriginal(1);
                    // 有效期类型
                    inputOrderField.setTimeCondition(THOST_FTDC_TC_IOC);
                    // GTD日期
                    inputOrderField.setGTDDate("");
                    // 成交量类型
                    inputOrderField.setVolumeCondition(THOST_FTDC_VC_AV);
                    // 最小成交量
                    inputOrderField.setMinVolume(0);
                    // 触发条件
                    inputOrderField.setContingentCondition(THOST_FTDC_CC_Immediately);
                    // 止损价
                    inputOrderField.setStopPrice(0);
                    // 强平原因
                    inputOrderField.setForceCloseReason(THOST_FTDC_FCC_NotForceClose);
                    // 自动挂起标志
                    inputOrderField.setIsAutoSuspend(0);
                    
                    traderApi.reqOrderInsert(inputOrderField, ++requestId);
                    
                }
            }
        }
        //买一价
        lastBidPrice = pDepthMarketData.getBidPrice1();
        //卖一价
        lastAskPrice = pDepthMarketData.getAskPrice1();
        
        marketService.saveFutureMarket(pDepthMarketData,volumeChange,(int)openInterestChange);
    }
    
    
    public void position(CThostFtdcTradeField pTrade){
        logger.debug("成交信息："+JSON.toJSONString(pTrade));
        if(pTrade.getOffsetFlag() == '0'){
            logger.info("已成交，成交价："+pTrade.getPrice());
            //this.pTrade = pTrade;
            
            this.json = JSON.parseObject(JSON.toJSONString(pTrade));
        }else {
            logger.info("已平仓，成交价："+pTrade.getPrice());
            this.json = null; 
        }
    }
    
    public static class MarketThread implements Runnable {
        
        private Application application;
        
        
        public MarketThread(Application application){
            this.application = application;
        }
        

        public void run() {
            mdApi = JCTPMdApi.createFtdcTraderApi();
            
            mdSpi = new MyMdSpi(mdApi,application);
            //注册spi
            mdApi.registerSpi(mdSpi);
            //注册前置机地址
            mdApi.registerFront(marketFront);
            mdApi.Init();
            
            mdApi.Join();
            
            mdApi.Release();
            
        }
        
    }
    
    public static class TradeThread implements Runnable {
        private Application application;
        
        public TradeThread(Application application){
            this.application = application;
        }
        

        public void run() {
            String dataPath = "ctpdata/test/";
            
//          traderApi = JCTPTraderApi.createFtdcTraderApi();
            traderApi = JCTPTraderApi.createFtdcTraderApi(dataPath);

            traderSpi = new MyTraderSpi(traderApi,application);
            
            //注册traderpi
            traderApi.registerSpi(traderSpi);
            //注册公有流
            traderApi.subscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
            //注册私有流
            traderApi.subscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
            //注册前置机地址
            traderApi.registerFront(tradeFront);
            
            traderApi.init();
            traderApi.join();
            //回收api和JCTP
            traderApi.release();
            
        }
        
    }
    
    public static void main(String[] args) throws InterruptedException {
        
        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        classPathXmlApplicationContext.start();
      //行情service
        marketService = (FutureMarketService) SpringContextUtil.getBean("futureMarketService");
        
        final Application application = new Application();
        
        Thread market = new Thread(new MarketThread(application));
        market.start();
        
        Thread trade = new Thread(new TradeThread(application));
        trade.start();
        
        market.join();
        
        trade.join();
    }

}
