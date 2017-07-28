package com.caojia.future.trader.bean;

/**
 * 平仓对应关系
 * @author caojia
 */
public class CloseRelation {
    
    public static final String CLOSE_RELATION = "closeRelation:";

    /**
     * 平仓单orderRef
     */
    private String closeOrderRef;
    
    /**
     * 开仓成交编号
     */
    private String openTradeID;

    /**
     * 平仓单orderRef
     */
    public String getCloseOrderRef() {
        return closeOrderRef;
    }

    /**
     * 平仓单orderRef
     */
    public void setCloseOrderRef(String closeOrderRef) {
        this.closeOrderRef = closeOrderRef;
    }

    /**
     * 开仓单成交编号
     */
    public String getOpenTradeID() {
        return openTradeID;
    }

    /**
     * 开仓单成交编号
     */
    public void setOpenTradeID(String openTradeID) {
        this.openTradeID = openTradeID;
    }

    
}
