package unipi.iot.Client;

import java.util.HashMap;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.JSONObject;

public class ADN {

	private static 	String inAddress = "127.0.0.1:5683/SWFM-in-cse";
	private static  String mnAddress = "127.0.0.1:5683/SWFM-mn-cse";

	public static String jsonStringAE(String api, String rn, String rr) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		
		return jo.toJSONString();
	}
	
	public static String jsonStringContainer( String rn) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);

		return jo.toJSONString();
	}
	
	public static String jsonStringSubscription(String nct, String rn, String nu) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo.toJSONString();

	}
	
	public static String jsonStringCI(String cnf, String con) {
		JSONObject jo = new JSONObject();
		jo.put("cnf",cnf);
		jo.put("con",con);
		
		return jo.toJSONString();
	}
	
	
	public static void createAE(boolean isMN, String json) {
		JSONObject obj = new JSONObject();
		Request req = new Request(Code.POST);
		String address;
		if(isMN)
			address = mnAddress;
		else
			address = inAddress;
		
		obj.put("m2m:ae",json);
		
	
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(267,2));
		req.getOptions().addOption(new Option(256,"admin:admin"));
		
		req.setPayload(obj.toJSONString());
    	new CoapClient(address).advanced(req);
		
		
	}
	
	public static void createContainer(boolean isMN , String json, String ae) {
		JSONObject obj = new JSONObject();
		Request req = new Request(Code.POST);
		String address;
		if(isMN)
			address = mnAddress+"/SWFM-mn-name/"+ae;
		else
			address = inAddress+"/SWFM-in-name/"+ae;

		
		obj.put("m2m:cnt",json);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(267,3));
		req.getOptions().addOption(new Option(256,"admin:admin"));
		
		req.setPayload(obj.toJSONString());
    	new CoapClient(address).advanced(req);
		
		
	}
	
	public static void createContentInstance(boolean isMN , String json, String ae, String container) {
		JSONObject obj = new JSONObject();
		Request req = new Request(Code.POST);
		String address;
		if(isMN)
			address = mnAddress+"/SWFM-mn-name/"+ae+container;
		else
			address = inAddress+"/SWFM-in-name/"+ae+container;
		
		obj.put("m2m:cin",json);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(267,4));
		req.getOptions().addOption(new Option(256,"admin:admin"));
		
		req.setPayload(obj.toJSONString());
    	new CoapClient(address).advanced(req);
		
		
	}
	
	public static void discovery(boolean isMN) {
		Request req = new Request(Code.GET);
		String address; 
		if(isMN)
			address = mnAddress;
		else
			address = inAddress;
		
		address += "/?fu=1";	
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
    	new CoapClient(address).advanced(req);
		
	}
	
	
	public static void discovery(boolean isMN, int type) {
		Request req = new Request(Code.GET);
		String address; 
		
		if(isMN)
			address = mnAddress;
		else
			address = inAddress;
		
		address += "/?fu=1&rty="+type;	
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
    	new CoapClient(address).advanced(req);
	}
	
	public static void createSubscription(boolean isMN , String json, String ae, String container) {
		JSONObject obj = new JSONObject();
		Request req = new Request(Code.POST);
		String address;
		if(isMN)
			address = mnAddress+"/SWFM-mn-name/"+ae+container;
		else
			address = inAddress+"/SWFM-in-name/"+ae+container;	
		
		obj.put("m2m:sub",json);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(267,4));
		req.getOptions().addOption(new Option(256,"admin:admin"));
		
		req.setPayload(obj.toJSONString());
    	new CoapClient(address).advanced(req);
		
		
	}
}
