package com.caojia.future.trader.bean;

/**
 * 持仓明细
 * @author caojia
 */
public class Position {
    
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
     * 报单引用
     */
    private String orderRef;

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

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }
    
    
}
