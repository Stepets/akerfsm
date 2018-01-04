package pt.gwtfsm.server;

import pt.gwtfsm.client.GreetingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String getConfig(String param) throws Exception {
		Thread.sleep(3000);
		return "Your Config";
	}

	public String getData(String param) throws Exception {
		Thread.sleep(3000);
		return "Your Data";
	}
}
