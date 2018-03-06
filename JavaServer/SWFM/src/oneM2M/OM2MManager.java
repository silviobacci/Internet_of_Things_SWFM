package oneM2M;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.*;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;

import resources.*;

@SuppressWarnings("unchecked")
public class OM2MManager {
	public static final int ACP = 1;
	public static final int AE = 2;
	public static final int CONTAINER = 3;
	public static final int CONTENT_INSTANCE = 4;
	public static final int CSE_BASE = 5;
	public static final int M2M_SERVICE_SUBSCRIPTION = 11;
	public static final int REMOTE_CSE = 16;
	public static final int SUBSCRIPTION = 23;
	
	public static final int WHOLE_RESOURCE = 1; 
	public static final int MODIFIED_ATTRIBUTES = 2; 
	public static final int REFERENCE_ONLY = 3;
	
	public static final String RESOURCE_TYPE_AE = "m2m:ae";
	public static final String RESOURCE_TYPE_CONTAINER = "m2m:cnt";
	public static final String RESOURCE_TYPE_CONTENT_INSTANCE = "m2m:cin";
	public static final String RESOURCE_TYPE_REMOTE_CSE = "m2m:csr";
	public static final String RESOURCE_TYPE_SUBSCRIPTION = "m2m:sub";
	public static final String RESOURCE_TYPE_URI_LIST = "m2m:uril";
	public static final String FILTER_USAGE = "fu=1";
	public static final String FILTER_RESOURCE_TYPE = "rty=";
	
	private static final int RESPONSE_STATUS_CODE = 265;
	private static final int CREATED_SUCCESSFULLY = 2001;
	private static final int CONTENT = 2000;

	private static final int portIN = 5683;
	private static final int portMN = 5684;

	private String inAddress;
	private String mnAddress;
	
	private String inCSE = "/SWFM-in-cse"; 
	private String mnCSE = "/SWFM-mn-cse"; 

	public OM2MManager(String ip) {
		inAddress = "coap://" + ip + ":" + portIN + "/~";
		mnAddress = "coap://" + ip + ":" + portMN + "/~";
	}
	
	public OM2MManager(String ip, String inA, String mnA) {
		inAddress = "coap://" + ip + ":" + portIN + "/~";
		mnAddress = "coap://" + ip + ":" + portMN + "/~";
		inAddress = inA;
		mnAddress = mnA;
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
	
	public JSONObject jsonAE(String api, String rn, boolean rr, String lbl) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	public JSONObject jsonAE(String api, String rn, boolean rr, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	public JSONObject jsonContainer(String rn, String lbl) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("lbl", lbl);

		return jo;
	}
	
	public JSONObject jsonContainer(String rn, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("lbl", lbl);

		return jo;
	}
	
	public JSONObject jsonSubscription(String rn, String nu, int nct) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, Object con, String lbl, String rn) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("cnf", cnf);
		jo.put("con", con);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, Object con, ArrayList<String> lbl, String rn) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("cnf", cnf);
		jo.put("con", con);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, Object con, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		jo.put("cnf", cnf);
		jo.put("con", con);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	public JSONObject jsonCI(String cnf, Object con, String lbl) {
		JSONObject jo = new JSONObject();
		jo.put("cnf", cnf);
		jo.put("con", con);
		jo.put("lbl", lbl);
		
		return jo;
	}
	
	private boolean checkResponse(CoapResponse res, int code) {
		if(res == null)
			return false;
		
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
	
	public String createResource(boolean isMN, String id_father, String resource_type, int type, JSONObject body) {		
		String address = getAddress(isMN) + id_father;

		JSONObject payload = new JSONObject();
		payload.put(resource_type, body);
		
		CoapResponse res = postRequest(address, payload.toJSONString().replace("\\", ""), type);
		
		if(!checkResponse(res, CREATED_SUCCESSFULLY))
			return null;
		
	    	return res.getResponseText();
	}
	
	public String createBridgedResource(boolean isMN, String csi, String id_father, String resource_type, int type, JSONObject body) {		
		return createResource(isMN, csi + id_father, resource_type, type, body);
	}
	
	public AEResource createAE(boolean isMN, JSONObject body) {		
		String resource = createResource(isMN, getCSE(isMN), RESOURCE_TYPE_AE, AE, body);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource createContainer(boolean isMN, String id_father, JSONObject body) {
		String resource = createResource(isMN, id_father, RESOURCE_TYPE_CONTAINER, CONTAINER, body);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource createContentInstance(boolean isMN, String id_father, JSONObject body) {
		String resource = createResource(isMN, id_father, RESOURCE_TYPE_CONTENT_INSTANCE, CONTENT_INSTANCE, body);
		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource createSubscription(boolean isMN, String id_father, JSONObject body) {
		String resource = createResource(isMN, id_father, RESOURCE_TYPE_SUBSCRIPTION, SUBSCRIPTION, body);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	public AEResource createBridgedAE(boolean isMN, String csi, JSONObject body) {	
		String resource = createBridgedResource(isMN, csi, getCSE(isMN), RESOURCE_TYPE_AE, AE, body);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource createBridgedContainer(boolean isMN, String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(isMN, csi, id_father, RESOURCE_TYPE_CONTAINER, CONTAINER, body);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource createBridgedContentInstance(boolean isMN, String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(isMN, csi, id_father, RESOURCE_TYPE_CONTENT_INSTANCE, CONTENT_INSTANCE, body);
		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource createBridgedSubscription(boolean isMN, String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(isMN, csi, id_father, RESOURCE_TYPE_SUBSCRIPTION, SUBSCRIPTION, body);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	private CoapResponse getRequest(String address) {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		
		return new CoapClient(address).advanced(req); 
	}
	
	private Request getRequest() {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		
		return req;
	}
	
	private String getResource(boolean isMN, String id) {
		String address = getAddress(isMN) + id;
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, CONTENT))
			return null;

    		return res.getResponseText();
	}
	
	private String getBridgedResource(boolean isMN, String csi, String id) {
		String address = getAddress(isMN) + csi + id;
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, CONTENT))
			return null;

    		return res.getResponseText();
	}
	
	public ReferenceResource getReference(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		if(resource == null)
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getAE(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getContainer(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getContentInstance(boolean isMN, String la) {
		String resource = getResource(isMN, la);
		
		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getSubscription(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	public ReferenceResource getBridgedReference(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		if(resource == null)
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getBridgedAE(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getBridgedContainer(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getBridgedContentInstance(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id + "/la");
		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getBridgedSubscription(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	private ArrayList<OM2MResource> discovery(boolean isMN, String address, Integer type, ArrayList<String> filter) {
		ArrayList<OM2MResource> resources = new ArrayList<OM2MResource>();
		
		Request req = getRequest();
		
		req.getOptions().addUriQuery(FILTER_USAGE);
		
		if(type != null)
			req.getOptions().addUriQuery(FILTER_RESOURCE_TYPE + type.intValue());
		
		if(filter != null)
			for(String s : filter)
				req.getOptions().addUriQuery(s);
		
		CoapResponse res = new CoapClient(address).advanced(req);
		
		if(!checkResponse(res, CONTENT))
			return null;
		
		try {
			JSONObject discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) discJSON.get(RESOURCE_TYPE_URI_LIST);
			
			for(Object j : json) {
				String jo = (String) j;
				res = getRequest(getAddress(isMN) + jo);
				if (checkResponse(res, CONTENT)){
					discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
					
					if(discJSON.get(RESOURCE_TYPE_AE) != null)
						resources.add(new AEResource((JSONObject) discJSON.get(RESOURCE_TYPE_AE)));
					if(discJSON.get(RESOURCE_TYPE_CONTAINER) != null)
						resources.add(new ContainerResource((JSONObject) discJSON.get(RESOURCE_TYPE_CONTAINER)));
					if(discJSON.get(RESOURCE_TYPE_CONTENT_INSTANCE) != null)
						resources.add(new InstanceResource((JSONObject) discJSON.get(RESOURCE_TYPE_CONTENT_INSTANCE)));
					if(discJSON.get(RESOURCE_TYPE_REMOTE_CSE) != null)
						resources.add(new ReferenceResource((JSONObject) discJSON.get(RESOURCE_TYPE_REMOTE_CSE)));
				}
			}
		} catch (ParseException e) {
			return null;
		}
		
		if(resources.isEmpty())
			return null;

		return resources;
	}
	
	public ArrayList<OM2MResource> discovery(boolean isMN) {
		return discovery(isMN, getAddress(isMN), null, null);
	}
	
	public ArrayList<OM2MResource> discovery(boolean isMN, Integer type) {
		return discovery(isMN, getAddress(isMN), type, null);
	}
	
	public ArrayList<OM2MResource> discovery(boolean isMN, Integer type, ArrayList<String> filter) {
		return discovery(isMN, getAddress(isMN), type, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscovery(boolean isMN, String csi) {
		return discovery(isMN, getAddress(isMN) + csi, null, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscovery(boolean isMN, String csi, Integer type) {
		return discovery(isMN, getAddress(isMN) + csi, type, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscovery(boolean isMN, String csi, Integer type, ArrayList<String> filter) {
		return discovery(isMN, getAddress(isMN) + csi, type, filter);
	}
	
	public ArrayList<OM2MResource> filterByName(ArrayList<OM2MResource> r, String name) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(iter.next().getRn().equals(name))
		        iter.remove();
		}
		
		return r;
	}
	
	public ArrayList<OM2MResource> getResourcesByName(ArrayList<OM2MResource> r, String name) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(!iter.next().getRn().equals(name))
		        iter.remove();
		}
		
		return r;
	}
	
	public ArrayList<OM2MResource> getResourcesById(ArrayList<OM2MResource> r, String id) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(!iter.next().getRi().equals(id))
		        iter.remove();
		}
		
		return r;
	}
}
