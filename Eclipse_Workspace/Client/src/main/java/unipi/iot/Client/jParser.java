package unipi.iot.Client;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class jParser {
	private static jParser instance;
	private static ArrayList<String> jProperties;
	
	private jParser() {}
	
	private jParser(ArrayList<String> prop) {
		jProperties = prop;
	}
	
	public jParser getInstance(ArrayList<String> prop) {
		if( instance == null)
			instance = new jParser(prop);
		
		return instance; 
			
	}
	
	public HashMap<String,Integer> getValues(ArrayList <String> properties, String toParse) {
		JSONObject jo= new JSONObject();;
		try {
			jo = (JSONObject) JSONValue.parseWithException(toParse);
		} catch (ParseException e) {
			System.out.println("Parsing exception!");
			e.printStackTrace();
		}
		HashMap<String, Integer> propVal = new HashMap<String, Integer>();
		for (String property: properties)
			propVal.put(property,(Integer) jo.get(property) );
		
		return propVal;

	}

	




}
