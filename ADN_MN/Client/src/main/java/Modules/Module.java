package Modules;

import java.util.HashMap;
import java.util.Observable;

import org.eclipse.californium.core.CoapClient;

import unipi.iot.Client.Constants;

public class Module extends Observable {
	
	protected HashMap<String, Object> state;
	protected String name;
	protected CoapClient sdConnection,gpsConnection;
	protected String ri;
	protected String subID; 
	
	public String getsubID() {
		return subID;
	}
	
	public void setsubID(String id) {
		subID = id;
	}
	
	public String getRi() {
		return ri;
	}
	
	public String getName() {
		return name;
	}
	public boolean isSensor() {
		if(name.contains(Constants.SENSOR))
			return true;
		return false; 
	}
}
