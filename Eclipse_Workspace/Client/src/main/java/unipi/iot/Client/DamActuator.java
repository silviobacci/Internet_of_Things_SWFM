package unipi.iot.Client;

import org.json.simple.*;
import org.json.simple.parser.*;

public class DamActuator {
	private final String state_string = "state_string";
	private final String id_string = "id";
	private final String name_string = "name";
	
	public boolean state;
	public int id;
	public String name;
	
	private JSONObject createJsonObject() {
		JSONObject jo = new JSONObject();
		jo.put(state_string, state);
		jo.put(id_string, id);
		jo.put(name_string, name);
		
		return jo;
	}
	
	public DamActuator(boolean s, int i, String n) {
		state = s;
		id = i;
		name = n;
	}
	
	public DamActuator(String s) throws ParseException {
		JSONObject jo = (JSONObject) JSONValue.parseWithException(s);
		
		state = ((Long) jo.get(state_string)).intValue() > 0 ? true : false;
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