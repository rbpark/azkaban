package azkaban.webapp.session;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import azkaban.utils.Props;

public class JNDIHelper {
	private final String jndiProviderUrl;
	private final String jndiSecurityAuthentication;
	private final String jndiInitialContextFactory;
	private final String jndiSecurityPrincipalPattern;
	
	public JNDIHelper(Props props) {
		jndiProviderUrl = props.get("jndi.provider.url");
		jndiInitialContextFactory = props.getString("jndi.initial.context.factory", "com.sun.jndi.ldap.LdapCtxFactory");
		jndiSecurityAuthentication = props.getString("jndi.security.authentication", "simple");
		jndiSecurityPrincipalPattern = props.getString("jndi.security.principal.pattern", "${username}");
	}
	
	@SuppressWarnings("unchecked")
	public void getUser(String user, String password) {
		String jndiSecurityPrincipal = jndiSecurityPrincipalPattern.replaceAll("\\$\\{username\\}", user);
		System.out.println("Using the following: " + jndiSecurityPrincipal);
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, jndiInitialContextFactory);
		env.put(Context.PROVIDER_URL, jndiProviderUrl);
		env.put(Context.SECURITY_AUTHENTICATION, jndiSecurityAuthentication);
		env.put(Context.SECURITY_PRINCIPAL, jndiSecurityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		try {
			DirContext ctx = new InitialDirContext(env);
//			ctx.list("cn");
//			System.out.println(ctx.getAttributes("cn"));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}