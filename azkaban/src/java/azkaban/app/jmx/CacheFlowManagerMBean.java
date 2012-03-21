package azkaban.app.jmx;

public interface CacheFlowManagerMBean {
    @DisplayName("OPERATION: getCacheSize")
    public int getCacheSize();
    
    @DisplayName("OPERATION: purgeCache")
    public void purgeCache();
}
