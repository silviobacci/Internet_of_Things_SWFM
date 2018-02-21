package unipi.iot.Client;

import java.util.HashMap;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.JSONObject;

public class Onem2mManager {

	private static 	String inAddress = "127.0.0.1:5683/~/SWFM-in-cse";
	private static  String mnAddress = "127.0.0.1:5684/~/SWFM-mn-cse";
	private static  String inName = "SWFM-in-name"; 
	private static  String mnName = "SWFM-mn-name"; 

	public Onem2mManager() {}
	
	private static String mnAddress(boolean isMN) {
		if(isMN)
			return mnAddress;
		else
			return inAddress;
	}
	
	public Onem2mManager(String inA, String mnA, String inN, String mnN) {
		inAddress = inA;
		mnAddress = mnA;
		inName = inN;
		mnName = mnN;
	}
	
	public static JSONObject jsonAE(String api, String rn, String rr) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		
		return jo;
	}
	
	public static JSONObject jsonContainer( String rn) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);

		return jo;
	}
	
	public static JSONObject jsonSubscription(String nct, String rn, String nu) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo;
	}
	
	public static JSONObject jsonCI(String cnf, String con) {
		JSONObject jo = new JSONObject();
		jo.put("cnf",cnf);
		jo.put("con",con);
		
		return jo;
	}
	
	private static Request postRequest(int type) {
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256,"admin:admin"));
		req.getOptions().addOption(new Option(267,type));
		
		return req; 	
	}
	
	
	public static void createAE(boolean isMN, JSONObject json) {		
		String address = mnAddress(isMN);
		JSONObject payload = new JSONObject();
		payload.put("m2m:ae",json);
		
		Request req = postRequest(2);
		req.setPayload(payload.toJSONString());
		
    	CoapResponse res = new CoapClient(address) .advanced(req);
    	System.out.println("resAE:"+res.getResponseText());
    		
	}
	
	
	public static void createNestedContainer(boolean isMN , JSONObject json, String ae, String cnt) {
		String address = mnAddress(isMN)+"/"+mnName+"/"+ae+"/"+cnt;
		JSONObject payload = new JSONObject();
		payload.put("m2m:cnt",json);
		
		Request req = postRequest(3);
		req.setPayload(payload.toJSONString());
    	
		new CoapClient(address).advanced(req);
		
	}
	
	public static void createContainer(boolean isMN , JSONObject json, String ae) {
		JSONObject payload = new JSONObject();
		payload.put("m2m:cnt",json);
		String address = mnAddress(isMN)+"/"+mnName+"/"+ae;
		
		Request req = postRequest(3);
		req.setPayload(payload.toJSONString());

		System.out.println("insta:"+new CoapClient(address).advanced(req).getResponseText());
	}
	
	public static void createContentInstance(boolean isMN , JSONObject json, String ae, String container) {
		JSONObject payload = new JSONObject();
		payload.put("m2m:cin",json);
		String address = mnAddress(isMN)+"/"+mnName+"/"+ae+"/"+container;
		
		Request req = postRequest(4);
		
		req.setPayload(payload.toJSONString());
    	System.out.println("insta:"+new CoapClient(address).advanced(req).getResponseText());
		
		
	}
	
	public static Request getRequest() {
		Request req = new Request(Code.GET);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		
		return req; 
	}
	
	public static String getContentInstance(boolean isMN, String container) {
		String address = mnAddress(isMN);
		Request req = getRequest();

    	return new CoapClient(address).advanced(req).getResponseText();
	}
	
	public void getResource(boolean isMN, String path) {	
		
		String address= mnAddress(isMN);
		if(path.contains(address))
			path = path.substring(path.indexOf(address)+address.length(), path.length());
		
		address += "/"+path;
		System.out.println("get_addres:"+address);
		Request req = getRequest();
		
		System.out.println("get_cnt"+ new CoapClient(address).advanced(req).getResponseText());		
		
	}
	
	public static String discovery(boolean isMN) {
		Request req = getRequest();
		String address = mnAddress(isMN);
		
		req.getOptions().addUriQuery("fu=1");

		System.out.println("URI: "+req.getURI());
    	return new CoapClient(address).advanced(req).getResponseText();
		
	}
	
	public static String discovery(boolean isMN, int type, String filter) {
		Request req = getRequest();
		String address = mnAddress(isMN);
	
		req.getOptions().addUriQuery("fu=1");
		req.getOptions().addUriQuery("rty="+type);
		
		if(filter != null)
			req.getOptions().addUriPath(filter);
		
    	return new CoapClient(address).advanced(req).getResponseText();
	}
	
	public static void createSubscription(boolean isMN , JSONObject json, String ae, String container) {
		JSONObject payload = new JSONObject();
		payload.put("m2m:sub",json);
		Request req = postRequest(4);
		String address = mnAddress(isMN)+"/"+mnName+"/"+ae+container;
		
		req.setPayload(payload.toJSONString());
    	
		new CoapClient(address).advanced(req);	
	}
}
