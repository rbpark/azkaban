package azkaban.webapp.session;

import azkaban.utils.Props;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class SessionCache {
	private static final int MAX_NUM_SESSIONS = 10000;
	private static final int SESSION_TIME_TO_LIVE = 86400;
	private CacheManager manager = CacheManager.create();
	private Cache cache;
	
	public SessionCache(Props props) {
		CacheConfiguration config = new CacheConfiguration();
		config.setName("sessionCache");
		config.setMaxEntriesLocalHeap(props.getInt("max.num.sessions", MAX_NUM_SESSIONS));
		config.setTimeToLiveSeconds(props.getInt("session.time.to.live", SESSION_TIME_TO_LIVE));
		config.eternal(false);
		config.diskPersistent(false);
		config.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
		
		cache = new Cache(config);
		manager.addCache(cache);
	}

	public Session getSession(String id) {
		Element elem = cache.get(id);
		if (elem == null) {
			return null;
		}
		
		return (Session)elem.getObjectValue();
	}
	
	public void addSession(String id, Session session) {
		Element elem = new Element(id, session);
		cache.put(elem);
	}
}