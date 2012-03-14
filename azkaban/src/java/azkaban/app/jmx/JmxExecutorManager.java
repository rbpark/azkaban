package azkaban.app.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import azkaban.jobcontrol.impl.jobs.locks.NamedPermitManager;
import azkaban.jobcontrol.impl.jobs.locks.ReadWriteLockManager;
import azkaban.jobs.JobExecutorManager;
import azkaban.jobs.JobExecutorManager.ExecutingJobAndInstance;

public class JmxExecutorManager implements JmxExecutorManagerMBean {
    private JobExecutorManager executorManager;
    private NamedPermitManager permitManager;
    private ReadWriteLockManager readWriteLockManager;
    
    public JmxExecutorManager(JobExecutorManager executorManager, NamedPermitManager permitManager, ReadWriteLockManager readWriteLockManager) {
        this.executorManager = executorManager;
        this.permitManager = permitManager;
        this.readWriteLockManager = readWriteLockManager;
    }
    
    @Override
    public List<String> viewExecutingJobNames() {
        ArrayList<String> jobNames = new ArrayList<String>();
        for ( ExecutingJobAndInstance instance: executorManager.getExecutingJobs()) {
            jobNames.add(instance.getExecutableFlow().getName());
        }
        return jobNames;
    }

    @Override
    public List<String> viewExecutingJobIds() {
        return executorManager.getExecutingListById();
    }

    @Override
    public List<String> viewExecutingJobIdsInternal() {
        ArrayList<String> jobNames = new ArrayList<String>();
        for ( ExecutingJobAndInstance instance: executorManager.getExecutingJobs()) {
            jobNames.add(instance.getExecutableFlow().getId());
        }
        return jobNames;
    }

    @Override
    public String killJobId(String jobId) {
        try {
            executorManager.cancel(jobId);
            if (executorManager.doesJobExistInExecutingList(jobId)) {
                executorManager.removeJobFromExecutingList(jobId);
                return "Had to manually remove job from list";
            }
            else {
                return "Job successfully killed";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String killJobByName(String jobName) {
        // TODO Auto-generated method stub
        try {
            executorManager.cancelAllJobsWithName(jobName);
            return "success";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }

    @Override
    public String removeJobFromExecutingList(String id) {
        executorManager.removeJobFromExecutingList(id);
        return executorManager.doesJobExistInExecutingList(id) ? "Failed to remove" : "Succeeded";
    }

    @Override
    public List<String> getAllNamedPermits() {
        return permitManager.getPermitNames();
    }

    @Override
    public String releasePermitByName(String name, int num) {
        boolean result = permitManager.releasePermitByName(name, num);
        return result ? "Permits released": "Permit doesnt exist";
    }

    @Override
    public List<String> getAllReadWriteLocks() {
        return readWriteLockManager.getLockNames();
    }

    @Override
    public Map<String, String> getReadWriteLockData(String name) {
        return readWriteLockManager.getReadWriteLockData(name);
    }

    @Override
    public String releaseWriteLock(String name) {
        if (readWriteLockManager.unlockWriteLock(name)) {
            return "success";
        }
        else {
            return "does not exist";
        }
    }

    @Override
    public String releaseReadLock(String name) {
        if (readWriteLockManager.unlockReadLock(name)) {
            return "success";
        }
        else {
            return "does not exist";
        }
    }
    
}