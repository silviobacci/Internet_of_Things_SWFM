package unipi.iot.Client;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class JSONParser {
	public static final String WL 		= "w_l";
	public static final String EVO 		= "evo";
	public static final String WT 		= "w_t";
	public static final String MIN 		= "min";
	public static final String MAX		= "max";
	public static final String GPSX		= "gps_x";
	public static final String GPSY 	= "gps_y";
	public static final String STATE 	= "state";
	public static final String CLOSED	= "closed";
	public static final String OPEN		= "open";
	public static final String IPBR		= "ip_br";
	public static final String PORTBR	= "port_br";
	public static final String IPMN		= "ip_mn";
	public static final String PORTMN	= "port_mn";
	public static final String LAT		= "lat";
	public static final String LNG		= "lng";
	public static final String NAME		= "name";

	private static ArrayList<String> jProperties = new ArrayList<String>();
	
	private JSONParser() {}
	
	public static void setConfProperties() {
		jProperties =new ArrayList<String>();
		
		jProperties.add(IPBR);
		jProperties.add(PORTBR);
		jProperties.add(IPMN);
		jProperties.add(PORTMN);
		jProperties.add(LAT);
		jProperties.add(LNG);
		jProperties.add(NAME);
	}
	
	public static void setSensorProperties() {
		jProperties =new ArrayList<String>();
		
		jProperties.add(WL);
		jProperties.add(EVO);
		jProperties.add(WT);
		jProperties.add(GPSX);
		jProperties.add(GPSY);
		jProperties.add(MIN);
		jProperties.add(MAX);
	}
	
	public static void setDamProperties() {
		jProperties =new ArrayList<String>();
		
		jProperties.add(STATE);
		jProperties.add(GPSX);
		jProperties.add(GPSY);
	}
	
	public static void setProperties(ArrayList<String> prop) {
		jProperties = prop;
	}
	
	public ArrayList<String> getProperties(){
		return jProperties;
	}
	
	public static HashMap<String, Object> getSensorValues(String toParse) {
		setSensorProperties();
		JSONObject jo= new JSONObject();
		HashMap<String, Object> propVal;
		
		try {
			jo = (JSONObject) JSONValue.parseWithException( toParse);
		
		} catch (ParseException e) {
			System.out.println("Parsing exception while parsing:"+toParse);
			e.printStackTrace();
		}
		
		propVal = new HashMap<String, Object>();
		
		for (String property: jProperties) {
			if(jo.get(property) != null)
				propVal.put(property, new Integer ( ( (Long) jo.get(property) ).intValue()) );
		
		}
		return propVal;
	}
	
	public static HashMap<String, Object> getDamValues(String toParse) {
		setDamProperties();
		JSONObject jo= new JSONObject();
		HashMap<String, Object> propVal;
		
		try {
			jo = (JSONObject) JSONValue.parseWithException( toParse); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		propVal = new HashMap<String, Object>();
		
		for (String property: jProperties) {
			if(jo.get(property) != null && property != JSONParser.STATE)
				propVal.put(property, new Integer ( ( (Long) jo.get(property) ).intValue()));
			
			else if(jo.get(property) != null)
				propVal.put(property,jo.get(property));
		
		}
		return propVal;

	}

	public static HashMap<String, Object> getValues(String toParse,ArrayList<String> prop) {
		setProperties(prop);
		JSONObject jo= new JSONObject();
		HashMap<String, Object> propVal;
		
		try {

			jo = (JSONObject) JSONValue.parseWithException( toParse);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		propVal = new HashMap<String, Object>();
		
		for (String property: jProperties) {
			if(jo.get(property) != null)
				propVal.put(property,  jo.get(property)+"");
	
		}
		return propVal;

	}

	public static HashMap<String, Object> getConfValues(String toParse) {
		setConfProperties();
		JSONObject jo= new JSONObject();
		HashMap<String, Object> propVal;
		
		try {
			jo = (JSONObject) JSONValue.parseWithException( toParse); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		propVal = new HashMap<String, Object>();
		
		for (String property: jProperties) {
			if(jo.get(property) != null)
				propVal.put(property,  jo.get(property)+"");
		
		}
		return propVal;

	}
	




}
