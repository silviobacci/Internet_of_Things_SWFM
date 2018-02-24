package oneM2M;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;

import resources.*;

public class OM2MManager {
	private static final int RESPONSE_STATUS_CODE = 265;
	private static final int CREATED_SUCCESSFULLY = 2001;
	private static final int CONTENT = 2000;

	private String inAddress = "coap://127.0.0.1:5683/~";
	private String mnAddress = "coap://127.0.0.1:5684/~";
	private String inCSE = "/SWFM-in-cse"; 
	private String mnCSE = "/SWFM-mn-cse"; 
	private String inName = "/SWFM-in-name"; 
	private String mnName = "/SWFM-mn-name"; 

	public OM2MManager() {}
	
	public OM2MManager(String inA, String mnA, String inN, String mnN) {
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
	
	private String getCSE(boolean isMN) {
		if(isMN)
			return mnCSE;
		else
			return inCSE;
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
	
	public JSONObject jsonSubscription(String rn, String nu, int nct) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, Object con) {
		JSONObject jo = new JSONObject();
		jo.put("cnf",cnf);
		jo.put("con",con);
		
		return jo;
	}
	
	private boolean checkResponse(CoapResponse res, int code) {
		Option responseCode = null;
		for(Option opt : res.getOptions().asSortedList()) {
		      if(opt.getNumber() == RESPONSE_STATUS_CODE) {
		    	  responseCode = opt;
		    	  break;
		      }
		}
		
		if(responseCode != null && responseCode.getIntegerValue() == code)
			return true;
		
		return false;
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
		String address = getAddress(isMN) + getCSE(isMN);
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:ae",ae);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 2);

		if(!checkResponse(res, CREATED_SUCCESSFULLY))
			return null;
		
    	System.out.println("resAE:"+res.getResponseText());	
    	
    	return new AEResource(res.getResponseText());
	}
	
	public ContainerResource createContainer(boolean isMN, Resource father, JSONObject cnt) {
		String address = getAddress(isMN) + father.getRi();
		System.out.println("Address: " + address);
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:cnt",cnt);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 3);

		if(!checkResponse(res, CREATED_SUCCESSFULLY))
			return null;
		
		System.out.println("resContainer:"+res.getResponseText());
		
		return new ContainerResource(res.getResponseText());
	}
	
	public InstanceResource createContentInstance(boolean isMN , Resource father, JSONObject inst) {
		String address = getAddress(isMN) + father.getRi();
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:cin",inst);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 4);
		
		if(!checkResponse(res, CREATED_SUCCESSFULLY))
			return null;
		
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
		String address = getAddress(isMN) + who.getRi();
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		System.out.println("getContentInstance:"+res.getResponseText());

		return new InstanceResource(res.getResponseText());
	}
	
	public ContainerResource getContainer(boolean isMN, ContainerResource who) {
		String address = getAddress(isMN) + who.getRi();
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		System.out.println("getContainer:"+res.getResponseText());

    		return new ContainerResource(res.getResponseText());
	}
	
	public AEResource getContentInstance(boolean isMN, AEResource who) {
		String address = getAddress(isMN) + who.getRi();
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		System.out.println("getAE:"+res.getResponseText());

    	return new AEResource(res.getResponseText());
	}

	public ArrayList<Resource> discovery(boolean isMN) {
		return discovery(isMN, null, null);
	}
	
	public ArrayList<Resource> discovery(boolean isMN, Integer type, ArrayList<String> filter) {
		String address = getAddress(isMN);
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		
		Request req = getRequest();
		
		req.getOptions().addUriQuery("fu=1");
		
		if(type != null)
			req.getOptions().addUriQuery("rty="+type.intValue());
		
		if(filter != null)
			for(String s : filter)
				req.getOptions().addUriQuery(s);
		
		CoapResponse res = new CoapClient(address).advanced(req);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		try {
			JSONObject discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) discJSON.get("m2m:uril");
			
			for(Object j : json) {
				String jo = (String) j;
				res = getRequest(address + jo);
				if (checkResponse(res, CONTENT)){
					discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
					
					if(discJSON.get("m2m:ae") != null)
						resources.add(new AEResource((JSONObject) discJSON.get("m2m:ae")));
					if(discJSON.get("m2m:cnt") != null)
						resources.add(new ContainerResource((JSONObject) discJSON.get("m2m:cnt")));
					if(discJSON.get("m2m:cin") != null)
						resources.add(new InstanceResource((JSONObject) discJSON.get("m2m:cin")));
					if(discJSON.get("m2m:csr") != null)
						resources.add(new ReferenceResource((JSONObject) discJSON.get("m2m:csr")));
				}
			}
		} catch (ParseException e) {
			return null;
		}
		
		if(resources.isEmpty())
			return null;

		return resources;
	}
	
	public ArrayList<Resource> bridgedDiscovery(boolean isMN, String csi) {
		return bridgedDiscovery(isMN, csi, null, null);
	}
	
	public ArrayList<Resource> bridgedDiscovery(boolean isMN, String csi, Integer type, ArrayList<String> filter) {
		String address = getAddress(isMN) + csi;

		Request req = getRequest();
				
		req.getOptions().addUriQuery("fu=1");
				
		if(type != null)
			req.getOptions().addUriQuery("rty="+type.intValue());
		
		if(filter != null)
			for(String s : filter)
				req.getOptions().addUriQuery(s);
		
		CoapResponse res = new CoapClient(address).advanced(req);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		ArrayList<Resource> resources = new ArrayList<Resource>();
		
		try {
			JSONObject discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) discJSON.get("m2m:uril");
			
			for(Object j : json) {
				String jo = (String) j;
				res = getRequest(getAddress(isMN) + jo);
				if(checkResponse(res, CONTENT)){
					System.out.println(res.getResponseText());
					discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
					
					if(discJSON.get("m2m:ae") != null)
						resources.add(new AEResource((JSONObject) discJSON.get("m2m:ae")));
					if(discJSON.get("m2m:cnt") != null)
						resources.add(new ContainerResource((JSONObject) discJSON.get("m2m:cnt")));
					if(discJSON.get("m2m:cin") != null)
						resources.add(new InstanceResource((JSONObject) discJSON.get("m2m:cin")));
					if(discJSON.get("m2m:csr") != null)
						resources.add(new ReferenceResource((JSONObject) discJSON.get("m2m:csr")));
				}
			}
		} catch (ParseException e) {
			return null;
		}

		if(resources.isEmpty())
			return null;
		
		return resources;
	}
	
	public void createSubscription(boolean isMN, Resource father, JSONObject sub) {
		String address = getAddress(isMN)+father.getRi();
		
		JSONObject payload = new JSONObject();
		payload.put("m2m:sub",sub);
		
		CoapResponse res = postRequest(address, payload.toJSONString(), 1);
		
		System.out.println("subscription: " + res.getResponseText());
	}
}
