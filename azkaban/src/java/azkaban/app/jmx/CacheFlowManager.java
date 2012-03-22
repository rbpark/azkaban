package azkaban.app.jmx;

import azkaban.flow.CachingFlowManager;

public class CacheFlowManager implements CacheFlowManagerMBean {
    private CachingFlowManager manager;
    
    public CacheFlowManager(CachingFlowManager manager) {
        this.manager = manager;
    }
    
    @Override
    public int getCacheSize() {
        return this.manager.getCacheSize();
    }

    @Override
    public long getCleanIntervalMillisec() {
        return this.manager.getCleanInterval();
    }
    
    @Override
    public void setCleanIntervalMillisec(long interval) {
        this.manager.setCleanInterval(interval);
    }
    
    @Override
    public long getTimeToIdleMillisec() {
        return this.manager.getTimeToIdle();
    }
    
    @Override
    public void setTimeToIdleMillisec(long millisec) {
        this.manager.setTimeToIdleMillisec(millisec);
    }
    
    @Override
    public void purgeCache() {
        this.manager.evictFinishedIdleFlows();
    }

}
