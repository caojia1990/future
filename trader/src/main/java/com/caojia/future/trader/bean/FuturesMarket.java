package com.caojia.future.trader.bean;

/**
 * 期货行情
 * @author caojia
 */
public class FuturesMarket {
    
    /**
     * 合约代码
     */
    private String instrumentID;
    
    /**
     * 最新价
     */
    private Double lastPrice;

    /**
     * 总成交量
     */
    private Integer volume;
    
    /**
     * 本次成交量
     */
    private Integer volumeChange;
    
    /**
     * 持仓量
     */
    private Double openInterest;
    
    /**
     * 本次增仓量
     */
    private Double openInterestChange;
    
    /**
     * 买一价
     */
    private Double bidPrice1;
    
    /**
     * 买一量
     */
    private Integer bidVolume1;
    
    /**
     * 卖一价
     */
    private Double askPrice1;
    
    /**
     * 卖一量
     */
    private Integer askVolume1;
    
    /**
     * 交易日
     */
    private String tradeDate;
    
    private String updateTime;
    
    private int updateMillisec;
    
    

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Double getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(Double openInterest) {
        this.openInterest = openInterest;
    }

    public Double getBidPrice1() {
        return bidPrice1;
    }

    public void setBidPrice1(Double bidPrice1) {
        this.bidPrice1 = bidPrice1;
    }

    public Integer getBidVolume1() {
        return bidVolume1;
    }

    public void setBidVolume1(Integer bidVolume1) {
        this.bidVolume1 = bidVolume1;
    }

    public Double getAskPrice1() {
        return askPrice1;
    }

    public void setAskPrice1(Double askPrice1) {
        this.askPrice1 = askPrice1;
    }

    public Integer getAskVolume1() {
        return askVolume1;
    }

    public void setAskVolume1(Integer askVolume1) {
        this.askVolume1 = askVolume1;
    }

    public Integer getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(Integer volumeChange) {
        this.volumeChange = volumeChange;
    }

    public Double getOpenInterestChange() {
        return openInterestChange;
    }

    public void setOpenInterestChange(Double openInterestChange) {
        this.openInterestChange = openInterestChange;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getUpdateMillisec() {
        return updateMillisec;
    }

    public void setUpdateMillisec(int updateMillisec) {
        this.updateMillisec = updateMillisec;
    }
    
    
}
