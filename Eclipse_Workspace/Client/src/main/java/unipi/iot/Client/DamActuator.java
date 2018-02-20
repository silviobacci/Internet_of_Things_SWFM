package unipi.iot.Client;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.*;
import org.json.simple.parser.*;

public class DamActuator {
	private boolean isOpen=false;
	private final String name;
	
	private final CoapClient connection;
	private String state;
	private static 	jParser parser;

	
	public void printState() {
		System.out.println(name+" is currently "+state);
		
	}
	
	public CoapClient getConnection() {
		return connection;
	}
	
	public void setClosed() {
		isOpen=false;
		state= "closed";
	}
	
	public void setOpened() {
		isOpen= true; 
		state = "open";
	}
	public void damControl(String jsonPost) {
		HashMap<String, String> tmp = parser.getDamValues(jsonPost);
		
		for (String key: tmp.keySet()) {
			System.out.println(key+"  "+tmp.get(key));
			if(key == "dam") {
				if(tmp.get(key) == "open")
					isOpen = true;
				else
					isOpen = false;
				state = tmp.get(key);
			
			}
		}
		printState();
	}
	
	public boolean isOpened() {
		return isOpen;
	}
	
	
	public DamActuator(ArrayList<String> prop, String n, String address){
		parser = jParser.getInstance(prop);
		name = n;
		connection = new CoapClient(address);
		isOpen = false;
		state = "closed";
	}
	
	/*public String toString() {
		//JSONObject jo = createJsonObjec)t();
		return jo.toJSONString();
	}
	
	public String toParsedString() {
		JSONObject jo = createJsonObject();
		return JSONValue.escape(jo.toJSONString());
	}*/
}