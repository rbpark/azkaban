package azkaban.webapp.user;

import azkaban.utils.Props;

public interface UserManager {
	public void init(Props props);
	
	public User getUser(String username, String password);
}
