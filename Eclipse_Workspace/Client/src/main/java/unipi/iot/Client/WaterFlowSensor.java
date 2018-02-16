package unipi.iot.Client;

import java.util.ArrayList;

import org.json.simple.*;
import org.json.simple.parser.*;

public class WaterFlowSensor {
	private int	w_l;
	private int w_t;
	private int toReach;
	private int evolution;
	
	private final 	ArrayList<String> properties;
	private final 	String name_string = "name";
	private static 	jParser parser;
	
	public int water_level;
	public int water_speed;
	public int id;
	public String name;
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();
		jo.put("water_level", water_level);
		jo.put("water_speed", water_speed);
		jo.put("id", id);
		jo.put("name", name);
		
		return jo;
	}
	
	public WaterFlowSensor(String n) {
		//initialization
		
		
		parser = jParser.getInstance();
		name = n;
	}
	
	public WaterFlowSensor(String s) throws ParseException {
		JSONObject jo = (JSONObject) JSONValue.parseWithException(s);
		
		System.out.println("TEMP: " + jo.get("temperature"));
		
		water_level = ((Long) jo.get(water_level_string)).intValue();
		water_speed = ((Long) jo.get(water_speed_string)).intValue();
		id = ((Long) jo.get(id_string)).intValue();
		name = (String) jo.get(name_string);
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