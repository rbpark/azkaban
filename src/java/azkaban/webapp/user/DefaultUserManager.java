package azkaban.webapp.user;

import azkaban.utils.Props;

public class DefaultUserManager implements UserManager {

	@Override
	public User getUser(String username, String password) {
		return new User(username);
	}

	@Override
	public void init(Props props) {
	}

}
