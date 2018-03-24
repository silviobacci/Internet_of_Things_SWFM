package Modules;

import java.util.HashMap;
import org.eclipse.californium.core.CoapClient;
import org.json.simple.*;

import unipi.iot.Client.JSONParser;

public class WaterFlowSensor extends Module implements ModuleAPI{

	public int getLat() {
		return ((Integer)state.get(JSONParser.GPSX)).intValue();
	}

	public int getLng() {
		return ((Integer)state.get(JSONParser.GPSX)).intValue();
	}

	public CoapClient getSDConnection() {
		return sdConnection;
	}
	
	public CoapClient getGpsConnection() {
		return gpsConnection;
	}

	public HashMap<String, Object> getState() {
		return state;
	}
	
	public String getRi() {
		return this.ri;
	}
	
	public void setRi(String id) {
		this.ri= id;
	}
	
	public boolean isOverflowed() {
		if(state.get(JSONParser.WL) != null && state.get(JSONParser.WL) != null )
			return (((Integer)state.get(JSONParser.WT)).intValue() - ((Integer)state.get(JSONParser.WL)).intValue() < 0);
		else
			return false;
	}
	
	public int getLevel() {
		return ((Integer)state.get(JSONParser.WL)).intValue();
	}
	
	public int getThreshold() {
		return ((Integer)state.get(JSONParser.WT)).intValue();
	}
	
	public int getMin() {
		return ((Integer)state.get(JSONParser.MIN)).intValue();
	}
	
	public int getMax() {
		return ((Integer)state.get(JSONParser.MAX)).intValue();
	}
	
	public void printState() {
		System.out.print("State of "+name+":");
		for(String key: state.keySet())
			System.out.print(key+":"+state.get(key)+" ");
		System.out.println();
	}
	
	public WaterFlowSensor( String n, String address) {
		sdConnection = new CoapClient(address + ModulesConstants.SENSOR);
		gpsConnection = new CoapClient(address + ModulesConstants.GPS);
		
		state = new HashMap<String,Object>();
		state.put(JSONParser.WL,0);
		state.put(JSONParser.WT,0);
		state.put(JSONParser.EVO,0);
		state.put(JSONParser.GPSX,0);
		state.put(JSONParser.GPSY,0);
		state.put(JSONParser.MIN,0);
		state.put(JSONParser.MAX,0);	
		
		name = n;
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Object> tmp = JSONParser.getSensorValues(jsonPost);
		
		for (String key: tmp.keySet()) 
				state.put(key,tmp.get(key));		 
		//printState();
	}
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();

		return jo;
	}

	public String toString() {
		JSONObject jo = createJsonObject();
		return jo.toJSONString();
	}
	
	public String toParsedString() {
		JSONObject jo = createJsonObject();
		return JSONValue.escape(jo.toJSONString());
	}

	public String getName() {
		return name;
	}

}