package com.caojia.future.trader.programTrading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.caojia.future.trader.bean.FuturesMarket;
import com.caojia.future.trader.bean.Position;
import com.caojia.future.trader.service.FutureMarketService;
import com.caojia.future.trader.strategy.LargeOrderFollow;
import com.caojia.future.trader.util.SpringContextUtil;

public class Application {
    
    static Logger logger = Logger.getLogger(Application.class);
    
    public String instrument = "rb1710";
    
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
    int orderRef = 0;

    AtomicInteger request = new AtomicInteger(0);
    
    
    //行情队列
    private static BlockingQueue<FuturesMarket> marketQueue;
    
    //持仓信息
    private Position position;
    
    public void onRtnDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData){
        
        int volumeChange = 0;
        double openInterestChange = 0;
        if(volume == 0){
            volume = pDepthMarketData.getVolume();
            openInterest = pDepthMarketData.getOpenInterest();
        }else {
            volumeChange = pDepthMarketData.getVolume() - volume;
            openInterestChange = pDepthMarketData.getOpenInterest() - openInterest;
            volume = pDepthMarketData.getVolume();
            openInterest = pDepthMarketData.getOpenInterest();
            
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
        
        return traderApi.reqOrderInsert(inputOrderField, request.incrementAndGet());
    }
    
    
    public void onRtnTrade(CThostFtdcTradeField pTrade){
        logger.debug("成交信息："+JSON.toJSONString(pTrade));
        if(pTrade.getOffsetFlag() == '0'){
            logger.info("已成交，成交价："+pTrade.getPrice());
            
            Position position = new Position();
            position.setInstrumentID(pTrade.getInstrumentID());
            position.setDirection(String.valueOf(pTrade.getDirection()));
            position.setOrderRef(pTrade.getOrderRef());
            position.setPrice(pTrade.getPrice());
            position.setVolume(pTrade.getVolume());
            this.position = position;
            
        }else {
            logger.info("已平仓，成交价："+pTrade.getPrice());
            this.position = null;
            //queue.clear();
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
        marketQueue = new LinkedBlockingDeque<FuturesMarket>(); 
        
        Thread market = new Thread(new MarketThread(application));
        market.start();
        
        Thread trade = new Thread(new TradeThread(application));
        trade.start();
        
        
        Thread strategy = new Thread(new LargeOrderFollow(application));
        strategy.start();
        
        market.join();
        
        trade.join();
        
        classPathXmlApplicationContext.close();
    }


    public BlockingQueue<FuturesMarket> getMarketQueue() {
        return marketQueue;
    }


    public void setMarketQueue(BlockingQueue<FuturesMarket> marketQueue) {
        Application.marketQueue = marketQueue;
    }


    public Position getPosition() {
        return position;
    }


    public void setPosition(Position position) {
        this.position = position;
    }
    
    

}
