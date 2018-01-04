package pt.gwtfsm.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String getConfig(String param) throws Exception;
	String getData(String param) throws Exception;
}
