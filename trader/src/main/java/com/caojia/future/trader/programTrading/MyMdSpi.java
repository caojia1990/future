package com.caojia.future.trader.programTrading;

import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcDepthMarketDataField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcReqUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspInfoField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcSpecificInstrumentField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcUserLogoutField;
import org.hraink.futures.jctp.md.JCTPMdApi;
import org.hraink.futures.jctp.md.JCTPMdSpi;

import com.caojia.future.trader.util.FileUtil;

public class MyMdSpi extends JCTPMdSpi {
	private JCTPMdApi mdApi;
	
	
	//private FutureMarketService futureMarketService;
	
	private Application application;
	
	int volume = 0;
	
    double openInterest = 0;
    
	public MyMdSpi(JCTPMdApi mdApi) {
		this.mdApi = mdApi;
	}
	
	public MyMdSpi(JCTPMdApi mdApi,Application application) {
	    this.mdApi = mdApi;
	    
	    this.application = application;
	}
	
	/*public MyMdSpi(JCTPMdApi mdApi,FutureMarketService futureMarketService) {
		this.mdApi = mdApi;
		this.futureMarketService = futureMarketService;
	}*/
	
	@Override
	public void onFrontConnected() {
		System.out.println("准备登陆");
		//登陆
		CThostFtdcReqUserLoginField userLoginField = new CThostFtdcReqUserLoginField();
		userLoginField.setBrokerID("9999");
		userLoginField.setUserID("090985");
		userLoginField.setPassword("caojiactp");
		
		mdApi.reqUserLogin(userLoginField, 112);
		System.out.println("登陆完成");
	}
	
	@Override
	public void onRspUserLogin(CThostFtdcRspUserLoginField pRspUserLogin, CThostFtdcRspInfoField pRspInfo, int nRequestID,
			boolean bIsLast) {
		System.out.println("登录回调");
		System.out.println(pRspUserLogin.getLoginTime());
		//订阅
		int subResult = -1;
		
		/*String s = FileUtil.read("C:\\合约test.txt");
		String s1 = s.replaceAll("\r\n", "");
		String[] ss = s1.split(",");*/
		
		subResult = mdApi.subscribeMarketData("cu1708","cu1709","al1708","rb1710");
		System.out.println(subResult == 0 ? "订阅成功" : "订阅失败");
	}

	@Override
	public void onRtnDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData) {
		/*System.out.print(pDepthMarketData.getUpdateTime() + " " + pDepthMarketData.getUpdateMillisec() + "   ");
		System.out.println(pDepthMarketData.getInstrumentID()+": "+JSON.toJSONString(pDepthMarketData));*/
		
		int volumeChange = 0;
		double openInterestChange = 0;
		
		application.onRtnDepthMarketData(pDepthMarketData);
		
		/*if(pDepthMarketData.getInstrumentID().equals("cu1707")){
		    if(volume == 0){
		        volume = pDepthMarketData.getVolume();
		        openInterest = pDepthMarketData.getOpenInterest();
		    }else {
		        volumeChange = pDepthMarketData.getVolume() - volume;
		        System.out.println("上次成交量："+volume);
		        System.out.println("本次成交量："+pDepthMarketData.getVolume());
		        System.out.println("成交量："+volumeChange);
		        openInterestChange = pDepthMarketData.getOpenInterest() - openInterest;
		        System.out.println("增仓量量："+volumeChange);
		        volume = pDepthMarketData.getVolume();
		        openInterest = pDepthMarketData.getOpenInterest();
		    }
		    //保存行情
		    if(futureMarketService != null){
		        System.out.println(JSON.toJSONString(pDepthMarketData));
		        this.futureMarketService.saveFutureMarket(pDepthMarketData,volumeChange,(int)openInterestChange);
		    }
		}*/
	}
//	
	@Override
	public void onRspSubMarketData(CThostFtdcSpecificInstrumentField pSpecificInstrument, CThostFtdcRspInfoField pRspInfo, int nRequestID,
			boolean bIsLast) {
		
		System.out.println("订阅回报:" + bIsLast +" : "+ pRspInfo.getErrorID()+":"+pRspInfo.getErrorMsg());
		System.out.println("InstrumentID:" + pSpecificInstrument.getInstrumentID());
	}
	
	@Override
	public void onHeartBeatWarning(int nTimeLapse) {
	}
	
	@Override
	public void onFrontDisconnected(int nReason) {
	}
	
	@Override
	public void onRspError(CThostFtdcRspInfoField pRspInfo, int nRequestID,
			boolean bIsLast) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onRspUnSubMarketData(
			CThostFtdcSpecificInstrumentField pSpecificInstrument,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onRspUserLogout(CThostFtdcUserLogoutField pUserLogout,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		// TODO Auto-generated method stub
	}


}