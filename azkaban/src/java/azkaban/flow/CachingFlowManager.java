/*
 * Copyright 2010 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.flow;

import azkaban.common.utils.Props;
import azkaban.flow.ExecutableFlow;
import azkaban.flow.Flow;
import azkaban.jobs.Status;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A FlowManager that caches ExecutableFlows.
 *
 * That is, if "createNewExecutableFlow()" generated a Flow with id 2, subsequent calls to loadExecutableFlow(2) would
 * return the same instance.
 */
public class CachingFlowManager implements FlowManager
{
    private static final Logger log = Logger.getLogger(CachingFlowManager.class);

    private final FlowManager baseManager;
//    private CacheManager manager = CacheManager.create();
//    private Cache cache;
    private long cleanInterval = 60000;
    private long timeToIdle = 300;
    private long nextCleanTime = 0;
    private ConcurrentHashMap<String, Element> cache = new ConcurrentHashMap<String,Element>();
    
    private class Element {
        private FlowExecutionHolder holder;
        private final long createTime;
        private long lastAccessTime;
        
        public Element(FlowExecutionHolder holder) {
            this.holder = holder;
            createTime = System.currentTimeMillis();
            lastAccessTime = createTime;
        }
        
        public long getCreateTime() {
            return createTime;
        }
        
        public long getLastAccessTime() {
            return lastAccessTime;
        }
        
        public void touch() {
            lastAccessTime = System.currentTimeMillis();
        }
        
        public FlowExecutionHolder getValue() {
            return holder;
        }
        
        public boolean isComplete() {
            switch(holder.getFlow().getStatus()) {
                case SUCCEEDED:
                case COMPLETED:
                case FAILED:
                    return true;
                default:
                    return false;
            }
        }
    }
    
    public CachingFlowManager(FlowManager baseManager, final int cacheSize, final long timeToIdle)
    {
        this.baseManager = baseManager;
        log.info("Creating Flow cache of size " + cacheSize + " and tti of " + timeToIdle);
        this.timeToIdle = timeToIdle;
        cleanInterval = timeToIdle;
    }

    public long getCleanInterval() {
        return cleanInterval;
    }
    
    public void setCleanInterval(long millisec) {
        cleanInterval = millisec;
    }
    
    public long getTimeToIdle() {
        return timeToIdle;
    }
    
    public void setTimeToIdleMillisec(long millisec) {
        timeToIdle = millisec;
    }
    
    
    private void cleanCache() {
        if(nextCleanTime < System.currentTimeMillis()) {
            evictFinishedIdleFlows();
            nextCleanTime = System.currentTimeMillis() + cleanInterval;
        }
    }
    
    public void evictFinishedIdleFlows() {
        ArrayList<String> toRemove = new ArrayList<String>();
        for (Map.Entry<String, Element> entry: cache.entrySet()) {
            Element element = entry.getValue();
            
             if(element.isComplete() && (System.currentTimeMillis() - element.getLastAccessTime()) > timeToIdle) {
                 toRemove.add(entry.getKey());
             }
        }
        
        
        log.info("Evicted from CachingFlowManager: " + toRemove.size());
        for (String key: toRemove) {
            cache.remove(key);
        }
    }
    
    public boolean hasFlow(String name)
    {
        return baseManager.hasFlow(name);
    }

    public Flow getFlow(String name)
    {
        return baseManager.getFlow(name);
    }

    public Collection<Flow> getFlows()
    {
        return baseManager.getFlows();
    }

    public Set<String> getRootFlowNames()
    {
        return baseManager.getRootFlowNames();
    }

    @Override
    public Iterator<Flow> iterator()
    {
        return baseManager.iterator();
    }

    public int getCacheSize() {
        return cache.size();
    }
    
    public ExecutableFlow createNewExecutableFlow(String name)
    {
        final ExecutableFlow retVal = baseManager.createNewExecutableFlow(name);

        if (retVal == null) {
            return null;
        }

        return new WrappingExecutableFlow(retVal){
            @Override
            public void execute(Props parentProperties, FlowCallback callback) {
                String id = getId();
                if (!cache.containsKey(id)) {
                    addToCache(new FlowExecutionHolder(retVal, parentProperties));
                }
                
                super.execute(parentProperties, callback);
            }
        };
    }

    public long getNextId()
    {
        return baseManager.getNextId();
    }

    public long getCurrMaxId()
    {
        return baseManager.getCurrMaxId();
    }

    public FlowExecutionHolder saveExecutableFlow(FlowExecutionHolder holder)
    {
        return baseManager.saveExecutableFlow(holder);
    }

    public FlowExecutionHolder loadExecutableFlow(long id)
    {
        Element elem = cache.get(id);
        if (elem != null) {
            elem.touch();
            return elem.getValue();
        }
        
        final FlowExecutionHolder retVal = baseManager.loadExecutableFlow(id);
        addToCache(retVal);

        return retVal;
    }

    public void reload()
    {
        baseManager.reload();
    }

    private void addToCache(FlowExecutionHolder retVal)
    {
        cleanCache();
        if (retVal == null || retVal.getFlow() == null) {
            return;
        }

        Element element = new Element(retVal);
        cache.put(retVal.getFlow().getId(), element);
    }

	@Override
	public List<String> getFolders() {
		return baseManager.getFolders();
	}

	@Override
	public List<String> getRootNamesByFolder(String folder) {
		return baseManager.getRootNamesByFolder(folder);
	}
	
}
