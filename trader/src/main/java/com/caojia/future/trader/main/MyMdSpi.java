package com.caojia.future.trader.main;

import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcDepthMarketDataField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcReqUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspInfoField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcSpecificInstrumentField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcUserLogoutField;
import org.hraink.futures.jctp.md.JCTPMdApi;
import org.hraink.futures.jctp.md.JCTPMdSpi;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.util.FileUtil;

public class MyMdSpi extends JCTPMdSpi {
	private JCTPMdApi mdApi;
	
	//private FutureMarketService futureMarketService;
	
	public MyMdSpi(JCTPMdApi mdApi) {
		this.mdApi = mdApi;
	}
	
	/*public MyMdSpi(JCTPMdApi mdApi,FutureMarketService futureMarketService) {
		this.mdApi = mdApi;
		this.futureMarketService = futureMarketService;
	}
	
	public MyMdSpi(JCTPMdApi mdApi,JCTPCallBack callBack) {
		this.mdApi = mdApi;
		this.callBack = callBack;
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
		
		/*String s = FileUtil.read("C:\\Users\\BHQH-CXYWB\\Desktop\\合约test.txt");
		String s1 = s.replaceAll("\r\n", "");
		String[] ss = s1.split(",");*/
		
		subResult = mdApi.subscribeMarketData("cu1706");
		System.out.println(subResult == 0 ? "订阅成功" : "订阅失败");
	}

	@Override
	public void onRtnDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData) {
		System.out.print(pDepthMarketData.getUpdateTime() + " " + pDepthMarketData.getUpdateMillisec() + "   ");
		//System.out.println(pDepthMarketData.getInstrumentID()+": "+JSON.toJSONString(pDepthMarketData));
		
		StringBuffer sb = new StringBuffer();
		sb.append("合约："+pDepthMarketData.getInstrumentID());
		sb.append("  最新价："+pDepthMarketData.getLastPrice());
		sb.append(" 买一价："+pDepthMarketData.getBidPrice1());
		sb.append(" 买一量:"+pDepthMarketData.getBidVolume1());
		sb.append(" 卖一价格:"+pDepthMarketData.getAskPrice1());
		sb.append(" 卖一量："+pDepthMarketData.getAskVolume1());
		sb.append(" 成交量："+pDepthMarketData.getVolume());
		sb.append(" 持仓量："+pDepthMarketData.getOpenInterest());
		System.out.println(sb.toString());
		
		//回调转发socket
		/*if(callBack != null){
			callBack.execute(pDepthMarketData);
		}
		//保存行情
		if(futureMarketService != null){
			System.out.println(JSON.toJSONString(pDepthMarketData));
			this.futureMarketService.saveFutureMarket(pDepthMarketData);
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

	/*public JCTPCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(JCTPCallBack callBack) {
		this.callBack = callBack;
	}

	public FutureMarketService getFutureMarketService() {
		return futureMarketService;
	}

	public void setFutureMarketService(FutureMarketService futureMarketService) {
		this.futureMarketService = futureMarketService;
	}*/
	

}