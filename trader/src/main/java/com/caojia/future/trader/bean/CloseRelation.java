package com.caojia.future.trader.bean;

public class CloseRelation {

    /**
     * 平仓单orderRef
     */
    private String closeOrderRef;
    
    /**
     * 开仓单orderRef
     */
    private String openOrderRef;

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
     * 开仓单orderRef
     */
    public String getOpenOrderRef() {
        return openOrderRef;
    }

    /**
     * 开仓单orderRef
     */
    public void setOpenOrderRef(String openOrderRef) {
        this.openOrderRef = openOrderRef;
    }
    
    
}
