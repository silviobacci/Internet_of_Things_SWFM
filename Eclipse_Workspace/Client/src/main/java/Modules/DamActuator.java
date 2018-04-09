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
	
	public String getDState() {
		return state.get(JSONParser.STATE).toString();
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
	
	public int getLat() {
		return ((Integer)state.get(JSONParser.GPSY)).intValue();
	}

	public int getLng() {
		return ((Integer)state.get(JSONParser.GPSX)).intValue();
	}
	
	public String getRi() {
		return this.ri;
	}
	
	public void setRi(String id) {
		this.ri= id;
	}
	
	public CoapClient getGpsConnection() {
		return gpsConnection;
	}
	
	public void setDam(boolean opened, String open) {
		state.put(JSONParser.STATE, false);
	
	}
	
	public void setClosed() {
		state.put(JSONParser.STATE, ModulesConstants.CLOSED);
	
	}
	
	public void setOpened() {
		state.put(JSONParser.STATE, ModulesConstants.OPEN);
	
	}
	
	
	public void updateState(String jsonPost) {
		HashMap<String, Object> tmp = JSONParser.getDamValues(jsonPost);
		
		for (String key: tmp.keySet()) 
				state.put(key, tmp.get(key));
		 //printState();
	}
	
	public boolean isOpened() {
		if(state.get(JSONParser.STATE).equals(ModulesConstants.OPEN))
			return true;
		return false;
	}
	
	public String getName() {
		return name;
	}
	
}