package azkaban.webapp.session;

import azkaban.webapp.user.User;

public class Session {
	private final User user;
	
	public Session(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
