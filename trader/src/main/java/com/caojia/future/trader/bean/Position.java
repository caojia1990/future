package com.caojia.future.trader.bean;

/**
 * 持仓明细
 * @author caojia
 */
public class Position {
    
    public static final String POSITION = "position:";
    
    /**
     * 合约
     */
    private String instrumentID;
    
    /**
     * 持仓方向
     */
    private String direction;
    
    /**
     * 成交价格
     */
    private Double price;
    
    /**
     * 成交手数
     */
    private Integer volume;
    
    
    /**
     * 成交编号
     */
    private String tradeID;
    
    /**
     * 成交日期
     */
    private String tradeDate;
    
    /**
     * 状态 0：持仓   1：平仓中
     */
    private String status = "0";
    

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    
}
