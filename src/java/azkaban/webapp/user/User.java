package azkaban.webapp.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class User {
	private final String userid;
	private String error = null;
	private Exception e = null;
	private Set<String> groups = new HashSet<String>();
	
	public User(String userid) {
		this.userid = userid;
	}
	
	public String getUserId() {
		return userid;
	}

	public Set<String> getGroups() {
		return groups;
	}

	public void clearGroup() {
		groups.clear();
	}
	
	public void addGroup(String name) {
		groups.add(name);
	}
	
	public void setGroups(Collection<String> groups) {
		this.groups = new HashSet<String>(groups);
	}


	public String toString() {
		String groupStr = "[";
		for (String group: groups) {
			groupStr += group + ",";
		}
		groupStr += "]";
		return userid + ": " + groupStr;
	}
	
	public void setError(String error, Exception e) {
		this.error = error;
		this.e = e;
	}
	
	public String getErrorMsg() {
		return this.error;
	}
	
	public Exception getError() {
		return e;
	}
}
