package com.caojia.future.trader.programTrading;


import org.apache.log4j.Logger;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderActionField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInputOrderField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInstrumentCommissionRateField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInstrumentField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInstrumentMarginRateField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInvestorPositionDetailField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcInvestorPositionField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcOrderField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcQryInstrumentCommissionRateField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcQryInstrumentField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcQryInvestorPositionDetailField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcReqUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspInfoField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcRspUserLoginField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcSettlementInfoConfirmField;
import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcTradeField;
import org.hraink.futures.jctp.trader.JCTPTraderApi;
import org.hraink.futures.jctp.trader.JCTPTraderSpi;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.InstrumentInfo;
import com.caojia.future.trader.dao.InstrumentInfoRedisDaoImpl;
import com.caojia.future.trader.util.SpringContextUtil;

/**
 * Custom TraderSpi
 * 
 * @author Hraink E-mail:Hraink@Gmail.com
 * @version 2013-1-25 下午11:46:13
 */
public class MyTraderSpi extends JCTPTraderSpi {

    static Logger logger = Logger.getLogger(MyTraderSpi.class);
    
	JCTPTraderApi traderApi;
	private Application application;
	int nRequestID = 0;
	
	//中证
	String brokerId = "9999";
	String userId = "090985";
	String password = "caojiactp";
	
	private InstrumentInfoRedisDaoImpl instrumentInfoRedisDao;
	
	public MyTraderSpi(JCTPTraderApi traderApi) {
		this.traderApi = traderApi;
	}
	
	public MyTraderSpi(JCTPTraderApi traderApi, Application application) {
	    this.traderApi = traderApi;
	    this.application = application;
	}
	public void onFrontConnected() {
		System.out.println("前置机连接");
		CThostFtdcReqUserLoginField userLoginField = new CThostFtdcReqUserLoginField();
		
		userLoginField.setBrokerID(brokerId);
		userLoginField.setUserID(userId);
		userLoginField.setPassword(password);
		
		traderApi.reqUserLogin(userLoginField, 112);
		
		instrumentInfoRedisDao = (InstrumentInfoRedisDaoImpl) SpringContextUtil.getBean("instrumentInfoRedisDao");
		
	}
	
	@Override
	public void onRspUserLogin(CThostFtdcRspUserLoginField pRspUserLogin,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("TradingDay:" + traderApi.getTradingDay());
		System.out.println(pRspInfo.getErrorID());
		System.out.println(pRspUserLogin.getLoginTime());
		System.out.println(pRspUserLogin.getCZCETime());
		System.out.println(pRspUserLogin.getDCETime());
		System.out.println(pRspUserLogin.getFFEXTime());
		System.out.println(pRspUserLogin.getSHFETime());
		System.out.println(pRspUserLogin.getMaxOrderRef());
		
		//查询持仓明细
		CThostFtdcQryInvestorPositionDetailField positionField = new CThostFtdcQryInvestorPositionDetailField();
		positionField.setBrokerID(brokerId);
		positionField.setInstrumentID("cu1703");
		positionField.setInvestorID(userId);
		//traderApi.reqQryInvestorPositionDetail(positionField, ++nRequestID);
		
		
		//确认结算单
		CThostFtdcSettlementInfoConfirmField confirmField = new CThostFtdcSettlementInfoConfirmField();
		confirmField.setBrokerID(brokerId);
		confirmField.setInvestorID(userId);
		traderApi.reqSettlementInfoConfirm(confirmField, ++nRequestID);
		
		//查询合约信息
		CThostFtdcQryInstrumentField pQryInstrument = new CThostFtdcQryInstrumentField();
		//traderApi.reqQryInstrument(pQryInstrument, ++nRequestID);

	}
	
	//报单回报
	@Override
	public void onRtnOrder(CThostFtdcOrderField pOrder) {
		System.out.println(pOrder.getStatusMsg());
		
	}
	
	//报单响应
	@Override
	public void onRspOrderInsert(CThostFtdcInputOrderField pInputOrder,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		logger.error("报单失败："+JSON.toJSONString(pRspInfo));
	}
	
	@Override
	public void onRspOrderAction(
			CThostFtdcInputOrderActionField pInputOrderAction,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		logger.error(pRspInfo.getErrorMsg());
	}
	
	//成交回报
	@Override
	public void onRtnTrade(CThostFtdcTradeField pTrade) {
		//System.out.println("成交"+pTrade.getInstrumentID());
		application.onRtnTrade(pTrade);
		
	}
	
	@Override
	public void onRspQryInvestorPositionDetail(
			CThostFtdcInvestorPositionDetailField pInvestorPositionDetail,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("持仓明细查询回调");
	}
	
	
	@Override
	public void onRspQryInvestorPosition(
			CThostFtdcInvestorPositionField pInvestorPosition,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("持仓查询回调");
	}

   /**
     * 投资者结算结果确认响应
     * @param pSettlementInfoConfirm
     * @param pRspInfo
     * @param nRequestID
     * @param bIsLast
     */
	@Override
	public void onRspSettlementInfoConfirm(
			CThostFtdcSettlementInfoConfirmField pSettlementInfoConfirm,
			CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		logger.info("确认结算单："+JSON.toJSONString(pSettlementInfoConfirm));
	}
	
	@Override
	public void onRspError(CThostFtdcRspInfoField pRspInfo, int nRequestID,
			boolean bIsLast) {
	    logger.error("错误回调");
	}
	@Override
	public void onErrRtnOrderInsert(CThostFtdcInputOrderField pInputOrder,
			CThostFtdcRspInfoField pRspInfo) {
	    logger.error("报单录入错误回调："+JSON.toJSONString(pRspInfo));
	}
	
	/**
     * 请求查询合约保证金率响应
     * @param pInstrumentMarginRate
     * @param pRspInfo
     * @param nRequestID
     * @param bIsLast
     */
	@Override
	public void onRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField pInstrumentMarginRate,
	        CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	    
	}
	
	/**
     * 请求查询合约响应
     * @param pInstrument
     * @param pRspInfo
     * @param nRequestID
     * @param bIsLast
     */
	@Override
    public void onRspQryInstrument(CThostFtdcInstrumentField pInstrument, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	    
	    InstrumentInfo info = new InstrumentInfo();
	    info.setInstrumentID(pInstrument.getInstrumentID());
	    info.setVolumeMultiple(pInstrument.getVolumeMultiple());
	    info.setPriceTick(pInstrument.getPriceTick());
	    info.setExchangeID(pInstrument.getExchangeID());
	    info.setExchangeInstID(pInstrument.getExchangeInstID());
	    info.setProductID(pInstrument.getProductID());
	    info.setMaxMarketOrderVolume(pInstrument.getMaxMarketOrderVolume());
	    info.setMaxLimitOrderVolume(pInstrument.getMaxLimitOrderVolume());
	    info.setMinMarketOrderVolume(pInstrument.getMinMarketOrderVolume());
	    info.setMinLimitOrderVolume(pInstrument.getMinLimitOrderVolume());
	    info.setLongMarginRatio(pInstrument.getLongMarginRatio());
	    info.setShortMarginRatio(pInstrument.getShortMarginRatio());
	    instrumentInfoRedisDao.saveInstrument(info);
	    
	  /*//查询合约手续费
        CThostFtdcQryInstrumentCommissionRateField pQryInstrumentCommissionRate = new CThostFtdcQryInstrumentCommissionRateField();
        pQryInstrumentCommissionRate.setBrokerID(brokerId);
        pQryInstrumentCommissionRate.setInvestorID(userId);
        pQryInstrumentCommissionRate.setInstrumentID(pInstrument.getProductID());
        traderApi.reqQryInstrumentCommissionRate(pQryInstrumentCommissionRate, ++nRequestID);*/
	}
	
	   /**
     * 请求查询合约手续费率响应
     * @param pInstrumentCommissionRate
     * @param pRspInfo
     * @param nRequestID
     * @param bIsLast
     */
    public void onRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField pInstrumentCommissionRate, 
            CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        
        InstrumentInfo info = new InstrumentInfo();
        info.setInstrumentID(pInstrumentCommissionRate.getInstrumentID());
        info.setOpenRatioByMoney(pInstrumentCommissionRate.getOpenRatioByMoney());
        info.setOpenRatioByVolume(pInstrumentCommissionRate.getOpenRatioByVolume());
        info.setCloseRatioByMoney(pInstrumentCommissionRate.getCloseRatioByMoney());
        info.setCloseRatioByVolume(pInstrumentCommissionRate.getCloseRatioByVolume());
        info.setCloseTodayRatioByMoney(pInstrumentCommissionRate.getCloseTodayRatioByMoney());
        info.setCloseTodayRatioByVolume(pInstrumentCommissionRate.getCloseTodayRatioByVolume());
        //instrumentInfoRedisDao.saveInstrumentCommision(info);
    }
	

}
