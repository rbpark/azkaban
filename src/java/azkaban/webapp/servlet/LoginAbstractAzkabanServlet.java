package azkaban.webapp.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import azkaban.webapp.session.Session;

/**
 * Abstract Servlet that handles auto login when the session hasn't been
 * verified.
 */
public abstract class LoginAbstractAzkabanServlet extends
		AbstractAzkabanServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AbstractAzkabanServlet.class.getName());
	private static final String SESSION_ID_NAME = "azkaban.session.id";
	// This is set really high because its the session cache that really takes care of it.
	private static final int MAX_SESSION_AGE_IN_SEC = 86400;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Set session id
		//Session session = getSessionFromRequest(req);
		Session session = null;
		if (session != null) {
			handleGet(req, resp);
		}
		else {
			handleLogin(req, resp);
		}
	}

	private Session getSessionFromRequest(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();
		String sessionId = null;
		for(Cookie cookie : cookies) {
			if (SESSION_ID_NAME.equals(cookie.getName())) {
				sessionId = cookie.getValue();
			}
		}
		
		if (sessionId == null) {
			return null;
		}
		else {
			return getApplication().getSessionCache().getSession(sessionId);
		}
	}
	
	private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleLogin(req, resp, null);
	}
	
	private void handleLogin(HttpServletRequest req, HttpServletResponse resp, String errorMsg)
			throws ServletException, IOException {
		
		 Page page = newPage(req, resp, "azkaban/webapp/servlet/velocity/login.vm");
	     if (errorMsg != null) {
	    	 page.add("errorMsg", errorMsg);
	     }
		 
		 page.render();
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(hasParam(req, "action")) {
			String action = getParam(req, "action");
			if (action.equals("login")) {
				if (hasParam(req, "username") && hasParam(req, "password")) {
					String username = getParam(req, "username");
					String password = getParam(req, "password");	
					// Validate
					Session session = new Session(username);
					getApplication().getSessionCache().addSession(UUID.randomUUID().toString(), session);
					handleGet(req, resp);
				}
				else {
					handleLogin(req, resp, "Enter username and password");
				}
			}
			else {
				handlePost(req, resp);
			}
		}
		else {
			handlePost(req, resp);
		}
	}
	
	protected abstract void handleGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException;

	protected abstract void handlePost(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException;
}