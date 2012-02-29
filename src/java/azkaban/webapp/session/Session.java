package azkaban.webapp.session;

import azkaban.webapp.user.User;

public class Session {
	private final User user;
	private final String id;
	
	public Session(String id, User user) {
		this.user = user;
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public String getId() {
		return id;
	}
}
