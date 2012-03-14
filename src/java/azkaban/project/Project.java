package azkaban.project;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import azkaban.flow.Flow;

public class Project {
    private final String name;
    private String uploaderName;
    private long lastUpdateTime;
    private int uploadVersion = 1;
    private HashMap<String, Flow> flows;

    private ReentrantLock lock = new ReentrantLock();
    private String lockUser;

    public Project(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean obtainLock(String user) {
        if (lock.tryLock()) {
            lockUser = user;
            return true;
        }
        return false;
    }

    public void releaseLock() {
        lockUser = null;
        lock.unlock();
    }

    public String getLockUser() {
        return lockUser;
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

    public void clearFlows() {
        flows = new HashMap<String, Flow>();
    }

    public void addFlows(Flow flow) {
        flows.put(flow.getId(), flow);
    }

    public Flow getFlow(String id) {
        return flows.get(id);
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderName() {
        return uploaderName;
    }
}
