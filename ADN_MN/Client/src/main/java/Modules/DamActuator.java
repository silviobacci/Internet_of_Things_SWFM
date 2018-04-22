package Modules;

import java.util.HashMap;
import org.eclipse.californium.core.CoapClient;

import communication.LoWPANManager;
import unipi.iot.Client.Constants;
import unipi.iot.Client.JSONParser;

	public class DamActuator extends Module implements ModuleAPI {
	public static final String GPS		=	"gps";
	
	private final CoapClient damConnection,gpsConnection;
	private boolean controllable	= true;
	private boolean isFirstTime 	= true;
	private String 	cnf 			= Constants.AUTO_CHANGE;  

	public DamActuator( String n, String address){
		String id = n.substring(n.indexOf("_")+1, n.length());
		name = n;
		damConnection = new CoapClient(address + Constants.DAM+id);//.setTimeout(93000);
		gpsConnection = new CoapClient(address + GPS);//.setTimeout(93000);
	   
		state = new HashMap<String, Object>();
		state.put(JSONParser.STATE, Boolean.FALSE);
		state.put(JSONParser.GPSX, 0);
		state.put(JSONParser.GPSY, 0);		
	}
	
	public boolean isConnected() {
		return (damConnection.ping() && gpsConnection.ping());
	}
	
	public String getDState() {
		return state.get(JSONParser.STATE).toString();
	}
	
	public HashMap<String, Object> getState() {
		return state;
	}
	
	public void printState() {
		System.out.println(name+":"+state.get(JSONParser.STATE)+"coordinates:"+state.get(JSONParser.GPSX)+" "+state.get(JSONParser.GPSY));
		
	}
	
	public CoapClient getSDConnection() {
		return damConnection;
	}
	
	public int getLat() {
		return ((Integer)state.get(JSONParser.GPSX)).intValue();
	}

	public int getLng() {
		return ((Integer)state.get(JSONParser.GPSY)).intValue();
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
		state.put(JSONParser.STATE, Constants.CLOSED);
	}
	
	public void setOpened() {
		state.put(JSONParser.STATE, Constants.OPEN);
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Object> tmp = JSONParser.getDamValues(jsonPost);
		
		for (String key: tmp.keySet()) 
				state.put(key, tmp.get(key));
		
		LoWPANManager.getGUI().updateDam(name);
	}
	
	public boolean isOpened() {
		if(state.get(JSONParser.STATE).equals(Constants.OPEN))
			return true;
		return false;
	}
	
	public String getName() {
		return name;
	}

	public boolean isControllable() {
		return controllable;
	}

	public void setControllable(boolean controllable) {
		this.controllable = controllable;
	}

	public boolean isFirstTime() {
		return isFirstTime;
	}

	public void setFirstTime(boolean isFirstTime) {
		this.isFirstTime = isFirstTime;
	}

	public String getCnf() {
		return cnf;
	}

	public void setCnf(String cnf) {
		this.cnf = cnf;
	}
	
}