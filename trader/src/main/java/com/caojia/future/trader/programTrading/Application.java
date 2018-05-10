package com.caojia.future.trader.programTrading;

import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_CC_Immediately;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_FCC_NotForceClose;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_OST_Canceled;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_OST_NoTradeNotQueueing;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_TC_IOC;
import static org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_FTDC_VC_AV;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_TE_RESUME_TYPE;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcDepthMarketDataField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcOrderField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspInfoField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcTradeField;
import org.hraink.futures.jctp.md.JCTPMdApi;
import org.hraink.futures.jctp.md.JCTPMdSpi;
import org.hraink.futures.jctp.trader.JCTPTraderApi;
import org.hraink.futures.jctp.trader.JCTPTraderSpi;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.CloseRelation;
import com.caojia.future.trader.bean.FutureChange;
import com.caojia.future.trader.bean.FuturesMarket;
import com.caojia.future.trader.bean.Position;
import com.caojia.future.trader.dao.CommonRedisDao;
import com.caojia.future.trader.service.FutureMarketService;
import com.caojia.future.trader.strategy.FollowLargeNoCut;
import com.caojia.future.trader.util.SpringContextUtil;

public class Application {
    
    static Logger logger = Logger.getLogger(Application.class);
    
    public static String BROKER_ID = "9999";
    public static String USER_ID = "105839";
    public static String PASSWORD = "caojiactp1";
    
    public static String BUY = "buy:";
    public static String SELL = "sell:";
    
    
    //行情地址
    public static String marketFront = "tcp://180.168.146.187:10010";
    /** 行情API **/
    static JCTPMdApi mdApi;
    static JCTPMdSpi mdSpi;
    
    //交易地址 
    //public static String tradeFront = "tcp://180.168.146.187:10000";
    public static String tradeFront = "tcp://180.168.146.187:10001";
    static JCTPTraderApi traderApi;
    static JCTPTraderSpi traderSpi;
    
    private static FutureMarketService marketService;
    private static CommonRedisDao commonRedisDao;
    
/*    int volume = 0;
    double openInterest = 0;*/
    
    Map<String , FutureChange> changeMap = new HashMap<String , FutureChange>();
    int orderRef = 0;

    AtomicInteger request = new AtomicInteger(0);
    
    
    //行情队列
    private static BlockingQueue<FuturesMarket> marketQueue;
    
    //持仓信息
    private Map<String, Position> positionMap= new HashMap<String, Position>();
    
    public void onRtnDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData){
        
        int volumeChange = 0;
        double openInterestChange = 0;
        FutureChange futureChange = this.getChange(pDepthMarketData.getInstrumentID());
        if(futureChange.getVolume() == 0){
            futureChange.setVolume(pDepthMarketData.getVolume());
            futureChange.setOpenInterest(pDepthMarketData.getOpenInterest());
        }else {
            volumeChange = pDepthMarketData.getVolume() - futureChange.getVolume();
            openInterestChange = pDepthMarketData.getOpenInterest() - futureChange.getOpenInterest();
            futureChange.setVolume(pDepthMarketData.getVolume());
            futureChange.setOpenInterest(pDepthMarketData.getOpenInterest());
        }
        
        FuturesMarket market = new FuturesMarket();
        
        market.setAskPrice1(pDepthMarketData.getAskPrice1());
        market.setAskVolume1(pDepthMarketData.getAskVolume1());
        market.setBidPrice1(pDepthMarketData.getBidPrice1());
        market.setBidVolume1(pDepthMarketData.getBidVolume1());
        market.setInstrumentID(pDepthMarketData.getInstrumentID());
        market.setLastPrice(pDepthMarketData.getLastPrice());
        market.setOpenInterest(pDepthMarketData.getOpenInterest());
        market.setVolume(pDepthMarketData.getVolume());
        market.setVolumeChange(volumeChange);
        market.setOpenInterestChange(openInterestChange);
        market.setTradeDate(pDepthMarketData.getTradingDay());
        market.setUpdateTime(pDepthMarketData.getUpdateTime());
        market.setUpdateMillisec(pDepthMarketData.getUpdateMillisec());
        try {
            marketQueue.put(market);
        } catch (InterruptedException e) {
            logger.error("推送行情失败",e);
        }
        
        marketService.saveFutureMarket(pDepthMarketData,volumeChange,(int)openInterestChange);
    }
    
    /**
     * 下单操作
     * @param inputOrderField
     * @return
     */
    public int reqOrderInsert(CThostFtdcInputOrderField inputOrderField){
        
        //期货公司代码
        inputOrderField.setBrokerID(BROKER_ID);
        //投资者代码
        inputOrderField.setInvestorID(USER_ID);
        // 用户代码
        inputOrderField.setUserID(USER_ID);
        // 组合投机套保标志
        inputOrderField.setCombHedgeFlag("1");
        // 有效期类型    不成交即撤单
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
        
        int result = traderApi.reqOrderInsert(inputOrderField, request.incrementAndGet());
        
        //预先缓存持仓信息，避免重复开仓
        if(inputOrderField.getCombOffsetFlag().equals("0")){
            
            Position position = new Position();
            position.setInstrumentID(inputOrderField.getInstrumentID());
            position.setDirection(String.valueOf(inputOrderField.getDirection()));
            position.setPrice(inputOrderField.getLimitPrice());
            position.setVolume(inputOrderField.getVolumeTotalOriginal());
            positionMap.put(inputOrderField.getInstrumentID(), position);
            
        }
        
        return result;
    }
    
    /**
     * 报单回报
     * @param pOrder
     */
    public void onRtnOrder(CThostFtdcOrderField pOrder) {
        
        if(pOrder.getCombOffsetFlag().equals("0") && (pOrder.getOrderStatus() == THOST_FTDC_OST_Canceled || THOST_FTDC_OST_NoTradeNotQueueing == pOrder.getOrderStatus())){
            logger.debug("开仓失败，报单状态："+pOrder.getOrderStatus()+", 报单信息："+pOrder.getStatusMsg());
            positionMap.remove(pOrder.getInstrumentID());
            
          //开仓失败后持仓数量减去
            if(pOrder.getDirection() == '0'){
                //买持量减去
                commonRedisDao.increamentByKey(BUY+pOrder.getInstrumentID(), (long) -pOrder.getVolumeTotalOriginal());
            }else {
                commonRedisDao.increamentByKey(SELL+pOrder.getInstrumentID(), (long) -pOrder.getVolumeTotalOriginal());
            }
        }else if (!pOrder.getCombOffsetFlag().equals("0") && (pOrder.getOrderStatus() == THOST_FTDC_OST_Canceled || THOST_FTDC_OST_NoTradeNotQueueing == pOrder.getOrderStatus())) {
            logger.debug("平仓失败，报单状态："+pOrder.getOrderStatus()+", 报单信息："+pOrder.getStatusMsg());
            
            String tradeID = commonRedisDao.getHash(CloseRelation.CLOSE_RELATION+pOrder.getInstrumentID(), pOrder.getOrderRef());
            String jsonStr = commonRedisDao.getHash(Position.POSITION+pOrder.getInstrumentID(), tradeID);
            Position position = JSON.parseObject(jsonStr, Position.class);
            position.setStatus("0");
            commonRedisDao.cacheHash(Position.POSITION+pOrder.getInstrumentID(), tradeID, JSON.toJSONString(position));
            //删除平仓对应关系
            commonRedisDao.deleteHash(CloseRelation.CLOSE_RELATION+pOrder.getInstrumentID(), pOrder.getOrderRef());
            
           //开仓失败后持仓数量加上
            if(pOrder.getDirection() == '0'){
                //卖持量加上
                commonRedisDao.increamentByKey(SELL+pOrder.getInstrumentID(), (long) pOrder.getVolumeTotalOriginal());
            }else {
                //买持量加上
                commonRedisDao.increamentByKey(BUY+pOrder.getInstrumentID(), (long) pOrder.getVolumeTotalOriginal());
            }
        }
    }
    
    public void onRspOrderInsert(CThostFtdcInputOrderField pInputOrder,
            CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if(pInputOrder.getCombOffsetFlag().equals("0")){
            //开仓失败
            positionMap.remove(pInputOrder.getInstrumentID());
            
            //开仓失败后持仓数量减去
            if(pInputOrder.getDirection() == '0'){
                //买持量减去
                commonRedisDao.increamentByKey(BUY+pInputOrder.getInstrumentID(), (long) -pInputOrder.getVolumeTotalOriginal());
            }else {
                commonRedisDao.increamentByKey(SELL+pInputOrder.getInstrumentID(), (long) -pInputOrder.getVolumeTotalOriginal());
            }
        }else {
            //平仓失败
            String tradeID = commonRedisDao.getHash(CloseRelation.CLOSE_RELATION+pInputOrder.getInstrumentID(), pInputOrder.getOrderRef());
            String jsonStr = commonRedisDao.getHash(Position.POSITION+pInputOrder.getInstrumentID(), tradeID);
            Position position = JSON.parseObject(jsonStr, Position.class);
            position.setStatus("0");
            commonRedisDao.cacheHash(Position.POSITION+pInputOrder.getInstrumentID(), tradeID, JSON.toJSONString(position));
            
            commonRedisDao.deleteHash(CloseRelation.CLOSE_RELATION+pInputOrder.getInstrumentID(), pInputOrder.getOrderRef());
            
            //开仓失败后持仓数量加上
            if(pInputOrder.getDirection() == '0'){
                //卖持量加上
                commonRedisDao.increamentByKey(SELL+pInputOrder.getInstrumentID(), (long) pInputOrder.getVolumeTotalOriginal());
            }else {
                //买持量加上
                commonRedisDao.increamentByKey(BUY+pInputOrder.getInstrumentID(), (long) pInputOrder.getVolumeTotalOriginal());
            }
        }
    }
    
    
    public void onRtnTrade(CThostFtdcTradeField pTrade){
        logger.debug("成交信息："+JSON.toJSONString(pTrade));
        if(pTrade.getOffsetFlag() == '0'){
            logger.info("已成交，成交价："+pTrade.getPrice());
            
            Position position = new Position();
            position.setInstrumentID(pTrade.getInstrumentID());
            position.setDirection(String.valueOf(pTrade.getDirection()));
            position.setTradeID(pTrade.getTradeID());
            position.setPrice(pTrade.getPrice());
            position.setVolume(pTrade.getVolume());
            position.setTradeDate(pTrade.getTradingDay());
            
            positionMap.put(pTrade.getInstrumentID(), position);
            
            //第二天可能成交编号重复
            logger.debug("缓存持仓信息："+JSON.toJSONString(position));
            commonRedisDao.cacheHash(Position.POSITION+position.getInstrumentID(), position.getTradeID(), JSON.toJSONString(position));
            
        }else {
            logger.info("已平仓，成交价："+pTrade.getPrice());
            positionMap.put(pTrade.getInstrumentID(), null);
            //获取对应的开仓成交编号
            String tradeID = commonRedisDao.getHash(CloseRelation.CLOSE_RELATION+pTrade.getInstrumentID(), pTrade.getOrderRef());
            logger.debug("获取平仓编号："+pTrade.getTradeID()+"对应的开仓成交编号："+tradeID);
            //删除持仓
            if(tradeID != null){
                commonRedisDao.deleteHash(Position.POSITION+pTrade.getInstrumentID(), tradeID);
                logger.debug("删除持仓："+tradeID);
                //删除平仓对应关系
                commonRedisDao.deleteHash(CloseRelation.CLOSE_RELATION+pTrade.getInstrumentID(), pTrade.getOrderRef());
            }
            
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
        commonRedisDao = (CommonRedisDao) SpringContextUtil.getBean("commonRedisDao");
        
        final Application application = new Application();
        marketQueue = new LinkedBlockingDeque<FuturesMarket>(); 
        
        Thread market = new Thread(new MarketThread(application));
        market.start();
        
        Thread trade = new Thread(new TradeThread(application));
        trade.start();
        
        
        Thread strategy = new Thread(new FollowLargeNoCut(application));
        strategy.start();
        
        market.join();
        
        trade.join();
        
        classPathXmlApplicationContext.close();
    }


    public BlockingQueue<FuturesMarket> getMarketQueue() {
        return marketQueue;
    }

    public Map<String, Position> getPositionMap() {
        return positionMap;
    }

    public FutureChange getChange(String instrument){
        
        FutureChange change = this.changeMap.get(instrument);
        if(change == null){
            change = new FutureChange();
            changeMap.put(instrument, change);
        }
        return change;
    }
    

}
