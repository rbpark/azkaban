package azkaban.webapp.session;

public class Session {
	private final String user;
	
	public Session(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
