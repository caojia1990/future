package com.caojia.future.trader.main;

import org.hraink.futures.jctp.md.JCTPMdApi;

public class MarketApplication {

	//实盘行情
	//public static String frontAddr = "tcp://180.169.116.119:41213";
	//simnow 仿真
	public static String frontAddr = "tcp://180.168.146.187:10010";
	/** 行情API **/
	public static JCTPMdApi mdApi;
	public static MyMdSpi mdSpi;
	
	
	public static void main(String[] args) {
		System.out.println("===================service  start ...==================");
		//ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //classPathXmlApplicationContext.start();
        System.out.println("===================service  start  complete!==================");

        mdApi = JCTPMdApi.createFtdcTraderApi();
		
		//行情service
		//FutureMarketService marketService = (FutureMarketService) SpringContextUtil.getBean("futureMarketService");
		
		mdSpi = new MyMdSpi(mdApi);
		//注册spi
		mdApi.registerSpi(mdSpi);
		//注册前置机地址
		mdApi.registerFront(frontAddr);
		mdApi.Init();
		
		mdApi.Join();
		
//		TimeUnit.SECONDS.sleep(5);
		mdApi.Release();
	}
}
