package azkaban.project;
import java.util.HashMap;

import azkaban.flow.Flow;
import azkaban.permission.Permission;

public class Project {
    private final String name;
    private long lastUpdateTime;
    private int uploadVersion;
    private HashMap<String, Flow> flows;
    
    public Project(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getUploadVersion() {
        return uploadVersion;
    }

    public void setUploadVersion(int uploadVersion) {
        this.uploadVersion = uploadVersion;
    }

    
}
