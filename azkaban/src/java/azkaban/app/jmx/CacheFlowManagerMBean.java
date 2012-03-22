package azkaban.app.jmx;

public interface CacheFlowManagerMBean {
    @DisplayName("OPERATION: getCacheSize")
    public int getCacheSize();
    
    @DisplayName("OPERATION: purgeCache")
    public void purgeCache();

    @DisplayName("OPERATION: getCleanIntervalMillisec")
    public long getCleanIntervalMillisec();
    
    @DisplayName("OPERATION: setCleanIntervalMillisec")
    public void setCleanIntervalMillisec(long interval);
    
    @DisplayName("OPERATION: getTimeToIdleMillisec")
    public long getTimeToIdleMillisec();
    
    @DisplayName("OPERATION: setTimeToIdleMillisec")
    public void setTimeToIdleMillisec(long millisec);
}
