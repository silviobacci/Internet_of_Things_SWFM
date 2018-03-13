package Modules;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.json.simple.*;
import org.json.simple.parser.*;

import unipi.iot.Client.JSONParser;

public class WaterFlowSensor extends Module implements ModuleAPI{

	

	
	public CoapClient getSDConnection() {
		return sdConnection;
	}
	
	public CoapClient getGpsConnection() {
		return gpsConnection;
	}
	public HashMap<String, Object> getState() {
		return state;
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
	
	
	public void printState() {
		System.out.print("updating "+name+":");
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
		
		
		name = n;
	
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Object> tmp = JSONParser.getSensorValues(jsonPost);
		for (String key: tmp.keySet()) {
			//System.out.println(key+":"+tmp.get(key));
				//if(!state.get( key).equals(tmp.get(key)))
					//System.out.println("Sensor"+name+"->updated value:"+key+ "    new:"+tmp.get(key)+"   old:"+state.get(key) );
				state.put(key,tmp.get(key));		 
			
		
		}
		
		//printState();
	}
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();
		/*jo.put("water_level", water_level);
		jo.put("water_speed", water_speed);
		jo.put("id", id);
		jo.put("name", name);
		*/
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