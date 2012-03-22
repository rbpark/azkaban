package azkaban.app.jmx;

public interface CacheFlowManagerMBean {
    @DisplayName("OPERATION: getCacheSize")
    int getCacheSize();
    
    @DisplayName("OPERATION: purgeCache ")
    void purgeCache();
    
    @DisplayName("OPERATION: getTimeToIdle")
    long getTimeToIdle();
    
    @DisplayName("OPERATION: setTimeToIdle")
    String setTimeToIdle(
            @ParameterName("millisec : The idle age in millisec")
            long millisec
     );
    
    @DisplayName("OPERATION: getCleanInterval ")
    long getCleanInterval();
    
    @DisplayName("OPERATION: Set the clean interval ")
    String setCleanInterval(
            @ParameterName("interval : The interval in millisec")
            long interval
     );
}
