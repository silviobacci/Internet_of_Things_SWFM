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
	public static final String ACP_ADMIN = "admin:admin";;
	
	private static final int RESPONSE_STATUS_CODE = 265;
	private static final int POST_SUCCESSFULL = 2001;
	private static final int GET_SUCCESSFULL = 2000;
	private static final int DELETE_SUCCESSFULL = 2002;

	private static final int portIN = 5683;
	private static final int portMN = 5684;
	
	private static final int TIMEOUT = 5000;

	private String inAddress;
	private String mnAddress;
	
	private String inCSE = "/SWFM-in-cse"; 
	private String mnCSE = "/SWFM-mn-cse";
	
	public OM2MManager() {
		inAddress = "coap://127.0.0.1:" + portIN + "/~";
		mnAddress = "coap://127.0.0.1:" + portMN + "/~";
	}

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
	
	public String getCSE(boolean isMN) {
		if(isMN)
			return mnCSE;
		else
			return inCSE;
	}
	
	public JSONObject jsonAE(String api, String rn, boolean rr) {
		JSONObject jo = new JSONObject();
		jo.put("api", api);
		jo.put("rn", rn);
		jo.put("rr", rr);
		
		return jo;
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
	
	private boolean checkResource(String json, String type) {
		JSONObject jo = null;
		try {
			jo = (JSONObject) (JSONObject) JSONValue.parseWithException(json);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(jo == null || jo.get(type) == null)
			return false;
		
		return true;
	}
	
	private boolean isReference(String json) {
		return checkResource(json, RESOURCE_TYPE_REMOTE_CSE);
	}
	
	private boolean isAE(String json) {
		return checkResource(json, RESOURCE_TYPE_AE);
	}
	
	private boolean isContainer(String json) {
		return checkResource(json, RESOURCE_TYPE_CONTAINER);
	}
	
	private boolean isContentInstance(String json) {
		return checkResource(json, RESOURCE_TYPE_CONTENT_INSTANCE);
	}
	
	private boolean isSubscription(String json) {
		return checkResource(json, RESOURCE_TYPE_SUBSCRIPTION);
	}
	
	private CoapResponse postRequest(String address, String payload, int type) {
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256,ACP_ADMIN));
		req.getOptions().addOption(new Option(267,type));
		req.setPayload(payload);
		
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(TIMEOUT);
		
		return client.advanced(req);
	}
	
	public String createResource(boolean isMN, String id_father, String resource_type, int type, JSONObject body) {		
		String address = getAddress(isMN) + id_father;

		JSONObject payload = new JSONObject();
		payload.put(resource_type, body);
		
		CoapResponse res = postRequest(address, payload.toJSONString().replace("\\", ""), type);
		
		if(!checkResponse(res, POST_SUCCESSFULL))
			return null;
		
	    	return res.getResponseText();
	}
	
	public String createBridgedResource(boolean isMN, String csi, String id_father, String resource_type, int type, JSONObject body) {	
		String name = csi;
		if(id_father != null)
			name += id_father.substring(id_father.lastIndexOf("/"), id_father.length());
		
		return createResource(isMN, name, resource_type, type, body);
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
		Request req = getRequest();
		
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(TIMEOUT);
		
		return client.advanced(req);
	}
	
	private Request getRequest() {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, ACP_ADMIN));
		
		return req;
	}
	
	private String getResource(boolean isMN, String id) {
		String address = getAddress(isMN) + id;
		
		CoapResponse res = getRequest(address);
		
		if(!checkResponse(res, GET_SUCCESSFULL))
			return null;

    		return res.getResponseText();
	}
	
	private String getBridgedResource(boolean isMN, String csi, String id) {
		String name = csi;
		if(id != null)
			name += id.substring(id.lastIndexOf("/"), id.length());
		
		return getResource(isMN, name);
	}
	
	public ReferenceResource getReference(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		
		if(resource == null)
			return null;
		
		if(!isReference(resource))
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getAE(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		
		if(resource == null)
			return null;
		
		if(!isAE(resource))
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getContainer(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		
		if(resource == null)
			return null;
		
		if(!isContainer(resource))
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getContentInstance(boolean isMN, String la) {
		String resource = getResource(isMN, la);
		
		if(resource == null)
			return null;
		
		if(!isContentInstance(resource))
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getSubscription(boolean isMN, String id) {
		String resource = getResource(isMN, id);
		
		if(resource == null)
			return null;
		
		if(!isSubscription(resource))
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	public ReferenceResource getBridgedReference(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		
		if(resource == null)
			return null;
		
		if(!isReference(resource))
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getBridgedAE(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		
		if(resource == null)
			return null;
		
		if(!isAE(resource))
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getBridgedContainer(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		
		if(resource == null)
			return null;
		
		if(!isContainer(resource))
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getBridgedContentInstance(boolean isMN, String csi, String la) {
		String resource = getBridgedResource(isMN, csi, la);
		
		if(resource == null)
			return null;
		
		if(!isContentInstance(resource))
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getBridgedSubscription(boolean isMN, String csi, String id) {
		String resource = getBridgedResource(isMN, csi, id);
		
		if(resource == null)
			return null;
		
		if(!isSubscription(resource))
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	private Request deleteRequest() {
		Request req = new Request(Code.DELETE);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, ACP_ADMIN));
		
		return req;
	}
	
	private CoapResponse deleteRequest(String address) {
		Request req = deleteRequest();
		
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(TIMEOUT);
		
		return client.advanced(req);
	}
	
	private boolean deleteResource(boolean isMN, String id) {
		String address = getAddress(isMN) + id;
		
		CoapResponse res = deleteRequest(address);
		
		return checkResponse(res, DELETE_SUCCESSFULL);
	}
	
	private boolean deleteBridgedResource(boolean isMN, String csi, String id) {
		String name = csi;
		if(id != null)
			name += id.substring(id.lastIndexOf("/"), id.length());
		
		return deleteResource(isMN, name);
	}
	
	public boolean deleteReference(boolean isMN, String id) {
		return deleteResource(isMN, id);
	}
	
	public boolean deleteAE(boolean isMN, String id) {
		return deleteResource(isMN, id);
	}
	
	public boolean deleteContainer(boolean isMN, String id) {
		return deleteResource(isMN, id);
	}
	
	public boolean deleteContentInstance(boolean isMN, String la) {
		return deleteResource(isMN, la);
	}
	
	public boolean deleteSubscription(boolean isMN, String id) {
		return deleteResource(isMN, id);
	}
	
	public boolean deleteBridgedReference(boolean isMN, String csi, String id) {
		return deleteBridgedResource(isMN, csi, id);
	}
	
	public boolean deleteBridgedAE(boolean isMN, String csi, String id) {
		return deleteBridgedResource(isMN, csi, id);
	}
	
	public boolean deleteBridgedContainer(boolean isMN, String csi, String id) {
		return deleteBridgedResource(isMN, csi, id);
	}
	
	public boolean deleteBridgedContentInstance(boolean isMN, String csi, String la) {
		return deleteBridgedResource(isMN, csi, la);
	}
	
	public boolean deleteBridgedSubscription(boolean isMN, String csi, String id) {
		return deleteBridgedResource(isMN, csi, id);
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
		
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(TIMEOUT);
		
		CoapResponse res = client.advanced(req);
		
		if(!checkResponse(res, GET_SUCCESSFULL))
			return null;
		
		try {
			JSONObject discJSON = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			JSONArray json = (JSONArray) discJSON.get(RESOURCE_TYPE_URI_LIST);
			
			for(Object j : json) {
				String jo = (String) j;
				res = getRequest(getAddress(isMN) + jo);
				if (checkResponse(res, GET_SUCCESSFULL)){
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
	
	public ArrayList<OM2MResource> discovery(boolean isMN, Integer type, String filter) {
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(filter);
		return discovery(isMN, getAddress(isMN), type, filters);
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
	
	public ArrayList<OM2MResource> bridgedDiscovery(boolean isMN, String csi, Integer type, String filter) {
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(filter);
		return discovery(isMN, getAddress(isMN) + csi, type, filters);
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
		    if(!iter.next().getRi().contains(id))
		        iter.remove();
		}
		
		return r;
	}
	
	public ArrayList<OM2MResource> getResourcesByFatherId(ArrayList<OM2MResource> r, String id) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(!iter.next().getPi().contains(id))
		        iter.remove();
		}
		
		return r;
	}
}
