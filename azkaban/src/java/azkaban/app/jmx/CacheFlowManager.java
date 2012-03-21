package azkaban.app.jmx;

import azkaban.flow.CachingFlowManager;

public class CacheFlowManager implements CacheFlowManagerMBean {
    private CachingFlowManager manager;
    
    public CacheFlowManager(CachingFlowManager manager) {
        this.manager = manager;
    }
    
    @Override
    public int getCacheSize() {
        return this.manager.getCache().getSize();
    }

    @Override
    public void purgeCache() {
        this.manager.getCache().evictExpiredElements();
    }

}
