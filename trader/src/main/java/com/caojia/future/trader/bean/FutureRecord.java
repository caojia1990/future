package com.caojia.future.trader.bean;

/**
 * 期货行情步数
 * @author caojia
 *
 */
public class FutureRecord {
    
    private long tickCount = 0;
    
    private long volumeAvg = 0;
    
    private long volumeAll = 0;
    
    public void tickIncrease(){
        ++tickCount;
    }
    
    public void addVolume(long change){
        volumeAll += change;
    }
    public long avg(){
        return volumeAll/tickCount;
    }

    public long getTickCount() {
        return tickCount;
    }

    public void setTickCount(long tickCount) {
        this.tickCount = tickCount;
    }

    public long getVolumeAvg() {
        return volumeAvg;
    }

    public void setVolumeAvg(long volumeAvg) {
        this.volumeAvg = volumeAvg;
    }

    public long getVolumeAll() {
        return volumeAll;
    }

    public void setVolumeAll(long volumeAll) {
        this.volumeAll = volumeAll;
    }
    
    
    

}
