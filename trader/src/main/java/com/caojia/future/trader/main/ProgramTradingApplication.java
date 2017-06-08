package com.caojia.future.trader.main;

import org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_TE_RESUME_TYPE;
import org.hraink.futures.jctp.md.JCTPMdApi;
import org.hraink.futures.jctp.trader.JCTPTraderApi;
import org.hraink.futures.jctp.trader.JCTPTraderSpi;

public class ProgramTradingApplication {

  //simnow 仿真
    public static String marketfrontAddr = "tcp://180.168.146.187:10010";
    /** 行情API **/
    public static JCTPMdApi mdApi;
    public static MyMdSpi mdSpi;
    
  //simnow 仿真
    static String tradefrontAddr = "tcp://180.168.146.187:10000";
    /** 行情API **/
    static JCTPTraderApi traderApi;
    static JCTPTraderSpi traderSpi;
    
    
    public static void main(String[] args) {
        
        Thread market = new Thread(new MarketThread());
        market.setDaemon(true);
        market.start();
        
        Thread trade = new Thread(new TradeThread());
        trade.setDaemon(true);
        trade.start();
        
        try {
            market.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 行情线程
     * @author caoji_000
     *
     */
    public static class MarketThread implements Runnable{

        @Override
        public void run() {
            mdApi = JCTPMdApi.createFtdcTraderApi();
            
            mdSpi = new MyMdSpi(mdApi);
            //注册spi
            mdApi.registerSpi(mdSpi);
            //注册前置机地址
            mdApi.registerFront(marketfrontAddr);
            mdApi.Init();
            
            mdApi.Join();
            
            mdApi.Release();
            
        }
    }
    
    /**
     * 交易线程
     * @author caoji_000
     *
     */
    public static class TradeThread implements Runnable{

        @Override
        public void run() {
            String dataPath = "ctpdata/test/";
            
            traderApi = JCTPTraderApi.createFtdcTraderApi(dataPath);

            traderSpi = new MyTraderSpi(traderApi);
            
            //注册traderpi
            traderApi.registerSpi(traderSpi);
            //注册公有流
            traderApi.subscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_RESTART);
            //注册私有流
            traderApi.subscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_RESTART);
            //注册前置机地址
            traderApi.registerFront(tradefrontAddr);
            
            traderApi.init();
            traderApi.join();
            //回收api和JCTP
            traderApi.release();
            
        }
        
    }
}
