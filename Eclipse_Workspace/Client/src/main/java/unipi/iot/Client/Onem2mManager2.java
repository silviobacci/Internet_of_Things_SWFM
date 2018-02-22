package unipi.iot.Client;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;

import java.util.ArrayList;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.Resource;

public class Onem2mManager2 {
	private String inAddress = "127.0.0.1:5683/~/SWFM-in-cse";
	private String mnAddress = "127.0.0.1:5684/~/SWFM-mn-cse";
	private String inName = "SWFM-in-name"; 
	private String mnName = "SWFM-mn-name"; 

	public Onem2mManager2() {}
	
	public Onem2mManager2(String inA, String mnA, String inN, String mnN) {
		inAddress = inA;
		mnAddress = mnA;
		inName = inN;
		mnName = mnN;
	}
	
	private String getAddress(boolean isMN) {
		if(isMN)
			return mnAddress;
		else
			return inAddress;
	}
	
	private String getName(boolean isMN) {
		if(isMN)
			return mnName;
		else
			return inName;
	}
	
	public JSONObject jsonAE(String api, String rn, boolean rr) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		
		return jo;
	}
	
	public JSONObject jsonContainer(String rn) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);

		return jo;
	}
	
	public JSONObject jsonSubscription(String nct, String rn, String nu) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, String con) {
		JSONObject jo = new JSONObject();
		jo.put("cnf",cnf);
		jo.put("con",con);
		
		return jo;
	}
	
	private CoapResponse postRequest(String address, String payload, int type) {
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256,"admin:admin"));
		req.getOptions().addOption(new Option(267,type));
		req.setPayload(payload);
		
    	return new CoapClient(address).advanced(req);
	}
	
	
	public AEResource createAE(boolean isMN, JSONObject ae) {		
		String address = getAddress(isMN);
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:ae",ae);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 2);

    	System.out.println("resAE:"+res.getResponseText());	
    	
    	return new AEResource(res.getResponseText());
	}
	
	public ContainerResource createContainer(boolean isMN, Resource father, JSONObject cnt) {
		String address = getAddress(isMN)+"/"+father.getRi();
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:cnt",cnt);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 3);

		System.out.println("resContainer:"+res.getResponseText());
		
		return new ContainerResource(res.getResponseText());
	}
	
	public InstanceResource createContentInstance(boolean isMN , Resource father, JSONObject inst) {
		String address = getAddress(isMN)+"/"+father.getRi();
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:cin",inst);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 4);
		
		System.out.println("resContentInstance:"+res.getResponseText());
		
		return new InstanceResource(res.getResponseText());
	}
	
	public CoapResponse getRequest(String address) {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		
		return new CoapClient(address).advanced(req); 
	}
	
	public Request getRequest() {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		
		return req;
	}
	
	public InstanceResource getContentInstance(boolean isMN, InstanceResource who) {
		String address = getAddress(isMN)+"/"+who.getRi();
		
		CoapResponse res = getRequest(address);
		
		System.out.println("getContentInstance:"+res.getResponseText());

    	return new InstanceResource(res.getResponseText());
	}
	
	public ContainerResource getContainer(boolean isMN, ContainerResource who) {
		String address = getAddress(isMN)+"/"+who.getRi();
		
		CoapResponse res = getRequest(address);
		
		System.out.println("getContainer:"+res.getResponseText());

    	return new ContainerResource(res.getResponseText());
	}
	
	public AEResource getContentInstance(boolean isMN, AEResource who) {
		String address = getAddress(isMN)+"/"+who.getRi();
		
		CoapResponse res = getRequest(address);
		
		System.out.println("getAE:"+res.getResponseText());

    	return new AEResource(res.getResponseText());
	}

	public ArrayList<Resource> discovery(boolean isMN) {
		String address = getAddress(isMN);
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		
		Request req = getRequest();
		
		req.getOptions().addUriQuery("fu=1");
		
		CoapResponse res = new CoapClient(address).advanced(req);
		
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) created.get("m2m:uril");
			
			for(Object jo : json) {
				if(((Integer) ((JSONObject) jo).get("ty")) == 2)
					resources.add(new AEResource((JSONObject) jo));
				if(((Integer) ((JSONObject) jo).get("ty")) == 3)
					resources.add(new ContainerResource((JSONObject) jo));
				if(((Integer) ((JSONObject) jo).get("ty")) == 4)
					resources.add(new InstanceResource((JSONObject) jo));
			}
		} catch (ParseException e) {
			return null;
		}

		return resources;
	}
	
	
	public ArrayList<Resource> discovery(boolean isMN, int type, String filter) {
		String address = getAddress(isMN);
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		
		Request req = getRequest();
		
		req.getOptions().addUriQuery("fu=1");
		req.getOptions().addUriQuery("rty="+type);
		
		if(filter != null)
			req.getOptions().addUriQuery(filter);
		
		CoapResponse res = new CoapClient(address).advanced(req);
		
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) created.get("m2m:uril");
			
			for(Object jo : json) {
				if(((Integer) ((JSONObject) jo).get("ty")) == 2)
					resources.add(new AEResource((JSONObject) jo));
				if(((Integer) ((JSONObject) jo).get("ty")) == 3)
					resources.add(new ContainerResource((JSONObject) jo));
				if(((Integer) ((JSONObject) jo).get("ty")) == 4)
					resources.add(new InstanceResource((JSONObject) jo));
			}
		} catch (ParseException e) {
			return null;
		}

		return resources;
	}
	
	public void createSubscription(boolean isMN, Resource father, JSONObject sub) {
		String address = getAddress(isMN)+"/"+father.getRi();
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:sub",sub);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 1);
		
		System.out.println("subscription: " + res.getResponseText());
	}
}
