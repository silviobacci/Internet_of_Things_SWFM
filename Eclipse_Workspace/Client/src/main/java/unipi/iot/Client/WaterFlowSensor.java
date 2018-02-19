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
	private final   CoapClient connection;
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();
		/*jo.put("water_level", water_level);
		jo.put("water_speed", water_speed);
		jo.put("id", id);
		jo.put("name", name);
		*/
		return jo;
	}
	
	public CoapClient getConnection() {
		return connection;
	}
	
	public boolean isOverflowed() {
		if(sensorState.get("w_l") != null && sensorState.get("w_t") != null )
			return (sensorState.get("w_t") - sensorState.get("w_l") < 0);
		else
			return false;
	}
	
	
	public void printState() {
		System.out.print(name+":");
		for(String key: sensorState.keySet())
			System.out.print(key+":"+sensorState.get(key)+" ");
		System.out.println();
	}
	public WaterFlowSensor(ArrayList<String> prop, String n, String address) {
		connection = new CoapClient(address);
		sensorState = new HashMap<String,Integer>();
		for (String property: prop) {
			sensorState.put(property,null);
			
		}
		parser = jParser.getInstance(prop);
		name = n;
	
	}
	
	public void updateState(String jsonPost) {
		HashMap<String, Integer> tmp = parser.getSensorValues(jsonPost);
		
		for (String key: tmp.keySet()) {
			
			if(sensorState.containsKey(key)) 
				sensorState.put(key,tmp.get(key));		 
		
		}
		printState();
	}
	
	/*public WaterFlowSensor(String s) throws ParseException {
		JSONObject jo = (JSONObject) JSONValue.parseWithException(s);
		
		System.out.println("TEMP: " + jo.get("temperature"));
		
		water_level = ((Long) jo.get(water_level_string)).intValue();
		water_speed = ((Long) jo.get(water_speed_string)).intValue();
		id = ((Long) jo.get(id_string)).intValue();
		name = (String) jo.get(name_string);
	}*/
	
	public String toString() {
		JSONObject jo = createJsonObject();
		return jo.toJSONString();
	}
	
	public String toParsedString() {
		JSONObject jo = createJsonObject();
		return JSONValue.escape(jo.toJSONString());
	}

}