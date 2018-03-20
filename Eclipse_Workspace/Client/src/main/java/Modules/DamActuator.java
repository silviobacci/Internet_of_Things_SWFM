package Modules;

import java.util.HashMap;
import org.eclipse.californium.core.CoapClient;


import unipi.iot.Client.JSONParser;

	public class DamActuator extends Module implements ModuleAPI {
	
	private final CoapClient damConnection,gpsConnection;
	
	public DamActuator( String n, String address){
		
		name = n;
		damConnection = new CoapClient(address + ModulesConstants.DAM);
		gpsConnection = new CoapClient(address + ModulesConstants.GPS);
		state = new HashMap<String, Object>();
		state.put(JSONParser.STATE, Boolean.FALSE);
		state.put(JSONParser.GPSX, 0);
		state.put(JSONParser.GPSY, 0);
		
	}
	
	public HashMap<String, Object> getState() {
		return state;
	}
	
	public void printState() {
		System.out.println(name+"coordinates:"+state.get(JSONParser.GPSX)+" "+state.get(JSONParser.GPSY));
		
	}
	
	public CoapClient getSDConnection() {
		return damConnection;
	}
	
	public CoapClient getGpsConnection() {
		return gpsConnection;
	}
	
	public void setClosed() {
		System.out.println(this.name+" closed");
		state.put(JSONParser.STATE, false);
		
	}
	
	public void setDam(boolean opened, String open) {
		state.put(JSONParser.STATE, false);
	
	}
	
	public void setOpened() {
		System.out.println(this.name+" opened");
		state.put(JSONParser.STATE, true);
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Object> tmp = JSONParser.getDamValues(jsonPost);
		
		for (String key: tmp.keySet()) {
			
			if(key == JSONParser.STATE) {
				
				if(tmp.get(key) == JSONParser.OPEN)
					setOpened();
				else
					setClosed();
			
			}else 
				state.put(key, tmp.get(key));
		}
		 //printState();
	}
	
	public boolean isOpened() {
		return   (Boolean) state.get(JSONParser.STATE);
	}
	
	public String getName() {
		return name;
	}
	
}