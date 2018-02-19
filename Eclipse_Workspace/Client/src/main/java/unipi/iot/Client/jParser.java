package unipi.iot.Client;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class jParser {
	private static jParser instance;
	private ArrayList<String> jProperties;
	
	private jParser() {}
	
	private jParser(ArrayList<String> prop) {
		jProperties = prop;
	}
	
	public static jParser getInstance(ArrayList<String> prop) {
		return new jParser(prop);
			
	}
	
	public ArrayList<String> getProperties(){
		return jProperties;
	}
	
	public HashMap<String, Integer> getSensorValues(String toParse) {
		JSONObject jo= new JSONObject();
		try {
			jo = (JSONObject) JSONValue.parseWithException( toParse); //"{\"to_reach\":200,\"evolution\":1}");
		} catch (ParseException e) {
			System.out.println("Parsing exception while parsing:"+toParse);
			e.printStackTrace();
		}
		HashMap<String, Integer> propVal = new HashMap<String, Integer>();
		for (String property: jProperties) {
			if(jo.get(property) != null)
			propVal.put(property, new Integer ( ( (Long) jo.get(property) ).intValue()) );
		
		}
		return propVal;

	}
	
	public HashMap<String, String> getDamValues(String toParse) {
		JSONObject jo= new JSONObject();
		try {
		//	System.out.println(""+toParse);
			jo = (JSONObject) JSONValue.parseWithException( toParse); //"{\"to_reach\":200,\"evolution\":1}");
		} catch (ParseException e) {
			System.out.println("Parsing exception while parsing:"+toParse);
			e.printStackTrace();
		}
		HashMap<String, String> propVal = new HashMap<String, String>();
		for (String property: jProperties) {
			if(jo.get(property) != null)
			propVal.put(property,  jo.get(property)+"");
		
		}
		return propVal;

	}

	




}
