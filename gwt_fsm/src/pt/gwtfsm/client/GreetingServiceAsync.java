package pt.gwtfsm.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync {
	void getConfig(String param, AsyncCallback<String> callback);
	void getData(String param, AsyncCallback<String> callback);
}
