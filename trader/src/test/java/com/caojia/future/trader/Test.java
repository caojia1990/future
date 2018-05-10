package com.caojia.future.trader;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.caojia.future.trader.bean.InstrumentInfo;

public class Test {

    public static void main(String[] args) {
        
        List<InstrumentInfo> list = new ArrayList<>();
        {
            InstrumentInfo info = new InstrumentInfo();
            info.setVolumeMultiple(10);
            info.setCloseRatioByMoney(5);
            info.setExchangeID("DECE");
            info.setInstrumentID("m1805");
            info.setInstrumentName("豆粕");
            info.setProductID("豆粕");
            list.add(info);
        }
        {
            InstrumentInfo info = new InstrumentInfo();
            info.setVolumeMultiple(10);
            info.setCloseRatioByMoney(5);
            info.setExchangeID("DECE");
            info.setInstrumentID("m1809");
            info.setInstrumentName("豆粕1909");
            info.setProductID("豆粕");
            list.add(info);
        }
        System.out.println(JSON.toJSONString(list,true));
    }
}
