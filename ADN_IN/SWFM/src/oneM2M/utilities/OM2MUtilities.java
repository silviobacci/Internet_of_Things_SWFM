package oneM2M.utilities;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.resources.OM2MResource;

public class OM2MUtilities {
	private static final int MAX_NUMBER_ATTEMPTS = 5;
	public static ArrayList<String> createFilters(String... label) {
		ArrayList<String> labels = new ArrayList<String>();
		
		for (String l : label)
			labels.add(OM2MConstants.FILTER_LABEL + l);

		return labels;
	}
	
	public static ArrayList<String> addLimitFilter(ArrayList<String> filters, int limit) {
		filters.add(OM2MConstants.FILTER_LIMIT + limit);

		return filters;
	}
	
	public static ArrayList<String> createLabels(String... label) {
		ArrayList<String> labels = new ArrayList<String>();
		
		for (String l : label)
			labels.add(l);

		return labels;
	}
	
	public static ArrayList<OM2MResource> filterByName(ArrayList<OM2MResource> r, String name) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(iter.next().getRn().equals(name))
		        iter.remove();
		}
		
		return r;
	}
	
	public static ArrayList<OM2MResource> getResourcesByName(ArrayList<OM2MResource> r, String name) {
		Iterator<OM2MResource> iter = r.iterator();
		while(iter.hasNext()) {
		    if(!iter.next().getRn().equals(name))
		        iter.remove();
		}
		
		return r;
	}
	
	public static ArrayList<String> getResourcesById(ArrayList<String> r, String id) {
		Iterator<String> iter = r.iterator();
		while(iter.hasNext()) {
		    if(!iter.next().contains(id))
		        iter.remove();
		}
		
		return r;
	}
	
	protected static boolean checkResponse(CoapResponse res, int code) {
		if(res == null)
			return false;
		
		Option responseCode = null;
		for(Option opt : res.getOptions().asSortedList()) {
		      if(opt.getNumber() == OM2MConstants.RESPONSE_STATUS_CODE) {
		    	  	responseCode = opt;
		    	  	break;
		      }
		}
		
		if(responseCode != null && responseCode.getIntegerValue() == code)
			return true;
		
		return false;
	}
	
	private static boolean checkResource(String json, String type) {
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
	
	protected static boolean isReference(String json) {
		return checkResource(json, OM2MConstants.RESOURCE_TYPE_REMOTE_CSE);
	}
	
	protected static boolean isAE(String json) {
		return checkResource(json, OM2MConstants.RESOURCE_TYPE_AE);
	}
	
	protected static boolean isContainer(String json) {
		return checkResource(json, OM2MConstants.RESOURCE_TYPE_CONTAINER);
	}
	
	protected static boolean isContentInstance(String json) {
		return checkResource(json, OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE);
	}
	
	protected static boolean isSubscription(String json) {
		return checkResource(json, OM2MConstants.RESOURCE_TYPE_SUBSCRIPTION);
	}
	
	protected static CoapResponse executeRequest(String address, Request req) {
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(OM2MConstants.TIMEOUT);
		
		CoapResponse res;
		
		int attempts = 0;
		
		do {
			res = client.advanced(req);
			if(res == null)
				attempts++;
		} while((req.isTimedOut() || res == null) && attempts < MAX_NUMBER_ATTEMPTS);
		
		return res;
	}
	
	protected static CoapResponse postRequest(String address, String payload, int type) {
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, OM2MConstants.ACP_ADMIN));
		req.getOptions().addOption(new Option(267, type));
		req.setPayload(payload);
		
		return executeRequest(address, req);
	}
}
