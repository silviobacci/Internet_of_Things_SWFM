package unipi.iot.Client;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.json.simple.*;
import org.json.simple.parser.*;

public class WaterFlowSensor {

	private HashMap<String, Integer> sensorState;
	public int id;
	public String name;
	
	private final 	String name_string = "name";
	private static 	jParser parser;
	private final   CoapClient sensorConnection,gpsConnection;
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();
		/*jo.put("water_level", water_level);
		jo.put("water_speed", water_speed);
		jo.put("id", id);
		jo.put("name", name);
		*/
		return jo;
	}
	
	public CoapClient getSensorConnection() {
		return sensorConnection;
	}
	
	public CoapClient getGpsConnection() {
		return gpsConnection;
	}
	
	public boolean isOverflowed() {
		if(sensorState.get("w_l") != null && sensorState.get("w_t") != null )
			return (sensorState.get("w_t") - sensorState.get("w_l") < 0);
		else
			return false;
	}
	
	public int getLevel() {
		return sensorState.get("w_l");
	}
	
	
	public void printState() {
		System.out.print("updating "+name+":");
		for(String key: sensorState.keySet())
			System.out.print(key+":"+sensorState.get(key)+" ");
		System.out.println();
	}
	public WaterFlowSensor(ArrayList<String> prop, String n, String address) {
		sensorConnection = new CoapClient(address+ "Sensor");
		gpsConnection = new CoapClient(address+ "gps");
		
	
		sensorState = new HashMap<String,Integer>();
		for (String property: prop) {
			sensorState.put(property,0);
			
		}
		parser = jParser.getInstance(prop);
		
		name = n;
	
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Integer> tmp = parser.getSensorValues(jsonPost);
		System.out.println("update:"+this.name);
		for (String key: tmp.keySet()) {
			System.out.println("key updated:"+key);
			if(sensorState.containsKey(key)) 
				sensorState.put(key,tmp.get(key));		 
		
		}
		printState();
	}
	

	public String toString() {
		JSONObject jo = createJsonObject();
		return jo.toJSONString();
	}
	
	public String toParsedString() {
		JSONObject jo = createJsonObject();
		return JSONValue.escape(jo.toJSONString());
	}

}