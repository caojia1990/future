package com.caojia.future.trader.bean;

/**
 * 合约属性  保存在redis
 * @author caojia
 *
 */
public class InstrumentInfo {
    
    
    public final static String VOLUME_MULTIPLE = "volumeMultiple";

    public final static String PRICE_TICK = "priceTick";
    
    public final static String OPEN_RATIO_BY_MONEY = "OpenRatioByMoney";
    
    public final static String OPEN_RATIO_BY_VOLUME = "OpenRatioByVolume";
    
    public final static String CLOSE_RATIO_BY_MONEY = "CloseRatioByMoney";
    
    public final static String CLOSE_RATIO_BY_VOLUME = "CloseRatioByVolume";
    
    public final static String CLOSE_TODAY_RATIO_BY_MONEY = "CloseTodayRatioByMoney";
    
    public final static String CLOSE_TODAY_RATIO_BY_VOLUME = "CloseTodayRatioByVolume";
    
    private String instrumentID;
    
    private String exchangeID;
    
    private String instrumentName;
    
    private String exchangeInstID;
    
    private String productID;
    
    private int maxMarketOrderVolume;
    
    private int MinMarketOrderVolume;
    
    private int maxLimitOrderVolume;
    
    private int MinLimitOrderVolume;
    
    private int volumeMultiple;
    
    private double priceTick;
    
    private double longMarginRatio;
    
    private double shortMarginRatio;

    /**---------------------------------------------------------------------------**/
    
    //开仓手续费比例(按金额)
    private double OpenRatioByMoney;
    
    //开仓手续费比例(按手数)
    private double OpenRatioByVolume;
    
    //平仓手续费比例(按金额)
    private double CloseRatioByMoney;
    
    //平仓手续费比例(按手数)
    private double CloseRatioByVolume;
    
    //平今手续费比例(按金额)
    private double CloseTodayRatioByMoney;
    
    //平今手续费比例(按手数)
    private double CloseTodayRatioByVolume;
    
    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public String getExchangeID() {
        return exchangeID;
    }

    public void setExchangeID(String exchangeID) {
        this.exchangeID = exchangeID;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getExchangeInstID() {
        return exchangeInstID;
    }

    public void setExchangeInstID(String exchangeInstID) {
        this.exchangeInstID = exchangeInstID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getMaxMarketOrderVolume() {
        return maxMarketOrderVolume;
    }

    public void setMaxMarketOrderVolume(int maxMarketOrderVolume) {
        this.maxMarketOrderVolume = maxMarketOrderVolume;
    }

    public int getMinMarketOrderVolume() {
        return MinMarketOrderVolume;
    }

    public void setMinMarketOrderVolume(int minMarketOrderVolume) {
        MinMarketOrderVolume = minMarketOrderVolume;
    }

    public int getMaxLimitOrderVolume() {
        return maxLimitOrderVolume;
    }

    public void setMaxLimitOrderVolume(int maxLimitOrderVolume) {
        this.maxLimitOrderVolume = maxLimitOrderVolume;
    }

    public int getMinLimitOrderVolume() {
        return MinLimitOrderVolume;
    }

    public void setMinLimitOrderVolume(int minLimitOrderVolume) {
        MinLimitOrderVolume = minLimitOrderVolume;
    }

    public int getVolumeMultiple() {
        return volumeMultiple;
    }

    public void setVolumeMultiple(int volumeMultiple) {
        this.volumeMultiple = volumeMultiple;
    }

    public double getPriceTick() {
        return priceTick;
    }

    public void setPriceTick(double priceTick) {
        this.priceTick = priceTick;
    }

    public double getOpenRatioByMoney() {
        return OpenRatioByMoney;
    }

    public void setOpenRatioByMoney(double openRatioByMoney) {
        OpenRatioByMoney = openRatioByMoney;
    }

    public double getOpenRatioByVolume() {
        return OpenRatioByVolume;
    }

    public void setOpenRatioByVolume(double openRatioByVolume) {
        OpenRatioByVolume = openRatioByVolume;
    }

    public double getCloseRatioByMoney() {
        return CloseRatioByMoney;
    }

    public void setCloseRatioByMoney(double closeRatioByMoney) {
        CloseRatioByMoney = closeRatioByMoney;
    }

    public double getCloseRatioByVolume() {
        return CloseRatioByVolume;
    }

    public void setCloseRatioByVolume(double closeRatioByVolume) {
        CloseRatioByVolume = closeRatioByVolume;
    }

    public double getCloseTodayRatioByMoney() {
        return CloseTodayRatioByMoney;
    }

    public void setCloseTodayRatioByMoney(double closeTodayRatioByMoney) {
        CloseTodayRatioByMoney = closeTodayRatioByMoney;
    }

    public double getCloseTodayRatioByVolume() {
        return CloseTodayRatioByVolume;
    }

    public void setCloseTodayRatioByVolume(double closeTodayRatioByVolume) {
        CloseTodayRatioByVolume = closeTodayRatioByVolume;
    }

    public double getLongMarginRatio() {
        return longMarginRatio;
    }

    public void setLongMarginRatio(double longMarginRatio) {
        this.longMarginRatio = longMarginRatio;
    }

    public double getShortMarginRatio() {
        return shortMarginRatio;
    }

    public void setShortMarginRatio(double shortMarginRatio) {
        this.shortMarginRatio = shortMarginRatio;
    }
    
    
}
