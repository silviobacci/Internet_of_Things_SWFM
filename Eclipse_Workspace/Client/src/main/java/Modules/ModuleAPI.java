package Modules;

import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;

public interface ModuleAPI {
	public void updateState(String jsonPost);
	public void printState();
	public CoapClient getSDConnection();
	public CoapClient getGpsConnection();
	public HashMap<String, Object> getState();
	public String getName();
	public int getLat();
	public int getLng();

	
}
