package com.caojia.future.trader.service;

import org.hraink.futures.ctp.thostftdcuserapistruct.CThostFtdcDepthMarketDataField;

public interface FutureMarketService {
	
	/**
	 * 保存期货行情
	 * @param dataField
	 */
	public void saveFutureMarket(CThostFtdcDepthMarketDataField dataField,int volumeChange,int openInterestChange);
}
