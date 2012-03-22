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
    public long getCleanInterval() {
        return this.manager.getCleanInterval();
    }
    
    @Override
    public String setCleanInterval(long interval) {
        this.manager.setCleanInterval(interval);
        return "success";
    }
    
    @Override
    public long getTimeToIdle() {
        return this.manager.getTimeToIdle();
    }
    
    @Override
    public String setTimeToIdle(long millisec) {
        this.manager.setTimeToIdleMillisec(millisec);
        return "success";
    }
    
    @Override
    public void purgeCache() {
        this.manager.evictFinishedIdleFlows();
    }

}
