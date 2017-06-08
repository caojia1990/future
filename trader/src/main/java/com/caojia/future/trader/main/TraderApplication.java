package com.caojia.future.trader.main;

import java.util.concurrent.TimeUnit;

import org.hraink.futures.ctp.thostftdcuserapidatatype.ThostFtdcUserApiDataTypeLibrary.THOST_TE_RESUME_TYPE;
import org.hraink.futures.jctp.trader.JCTPTraderApi;
import org.hraink.futures.jctp.trader.JCTPTraderSpi;

public class TraderApplication {
	/** 前置机地址 **/
	//static String frontAddr = "tcp://180.169.116.120:41205";
	//simnow 仿真
	static String frontAddr = "tcp://180.168.146.187:10000";
	/** 行情API **/
	static JCTPTraderApi traderApi;
	static JCTPTraderSpi traderSpi;
	
	public static void main(String[] args) throws InterruptedException {
	    
	    String dataPath = "ctpdata/test/";
        
//      traderApi = JCTPTraderApi.createFtdcTraderApi();
        traderApi = JCTPTraderApi.createFtdcTraderApi(dataPath);

        traderSpi = new MyTraderSpi(traderApi);
        
        //注册traderpi
        traderApi.registerSpi(traderSpi);
        //注册公有流
        traderApi.subscribePublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_RESTART);
        //注册私有流
        traderApi.subscribePrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_RESTART);
        //注册前置机地址
        traderApi.registerFront(frontAddr);
        
        traderApi.init();
        traderApi.join();
        TimeUnit.SECONDS.sleep(2);
        //回收api和JCTP
        traderApi.release();
    }
	
	
}
