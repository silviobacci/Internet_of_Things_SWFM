package oneM2M.utilities;

import java.util.ArrayList;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.resources.SubscriptionResource;

public class OM2MDiscoverer {
	private String IP_ADDRESS;
	private String CSE_ID;
	
	protected void setIP_ADDRESS(String ip) {
		IP_ADDRESS = ip;
	}
	
	protected void setCSE_ID(String cse_id) {
		CSE_ID = cse_id;
	}
	
	private ArrayList<String> discoveryUril(String id, Integer type, ArrayList<String> filter) {
		String address = IP_ADDRESS + id;
		
		Request req = OM2MGetter.getRequest();
		
		req.getOptions().addUriQuery(OM2MConstants.FILTER_USAGE);
		
		if(type != null)
			req.getOptions().addUriQuery(OM2MConstants.FILTER_RESOURCE_TYPE + type.intValue());
		
		if(filter != null)
			for(String s : filter)
				req.getOptions().addUriQuery(s);
		
		CoapResponse res = OM2MUtilities.executeRequest(address, req);
		
		if(!OM2MUtilities.checkResponse(res, OM2MConstants.GET_SUCCESSFULL))
			return null;
		
		JSONArray jarray = null;
		
		try {
			JSONObject json = (JSONObject) JSONValue.parseWithException(res.getResponseText());
			if(json != null) jarray = (JSONArray) json.get(OM2MConstants.RESOURCE_TYPE_URI_LIST);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for(Object jo : jarray)
			if(jo instanceof String) 
				ret.add((String) jo);
		
		return ret;
	}
	
	private ArrayList<OM2MResource> discovery(String id, Integer type, ArrayList<String> filter) {
		ArrayList<String> uril = discoveryUril(id, type, filter);
		
		if(uril == null || uril.isEmpty())
			return null;
		
		ArrayList<OM2MResource> resources = new ArrayList<OM2MResource>();
		
		for(String uri : uril) {
			CoapResponse res = OM2MGetter.getRequest(IP_ADDRESS + uri);
			if (OM2MUtilities.checkResponse(res, OM2MConstants.GET_SUCCESSFULL)){
				JSONObject json = null;
				
				try {
					json = (JSONObject) JSONValue.parseWithException(res.getResponseText());
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				
				if(json != null) {
					if(json.get(OM2MConstants.RESOURCE_TYPE_REMOTE_CSE) != null)
						resources.add(new ReferenceResource((JSONObject) json.get(OM2MConstants.RESOURCE_TYPE_REMOTE_CSE)));
					if(json.get(OM2MConstants.RESOURCE_TYPE_AE) != null)
						resources.add(new AEResource((JSONObject) json.get(OM2MConstants.RESOURCE_TYPE_AE)));
					if(json.get(OM2MConstants.RESOURCE_TYPE_CONTAINER) != null)
						resources.add(new ContainerResource((JSONObject) json.get(OM2MConstants.RESOURCE_TYPE_CONTAINER)));
					if(json.get(OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE) != null)
						resources.add(new InstanceResource((JSONObject) json.get(OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE)));
					if(json.get(OM2MConstants.RESOURCE_TYPE_SUBSCRIPTION) != null)
						resources.add(new SubscriptionResource((JSONObject) json.get(OM2MConstants.RESOURCE_TYPE_SUBSCRIPTION)));
				}
			}
		}
		
		if(resources.isEmpty())
			return null;

		return resources;
	}
	
	private ArrayList<String> bridgedDiscoveryUril(String csi, String id, Integer type, ArrayList<String> filter) {
		String actual_id = csi;
		if(id != null) actual_id += id;
		return discoveryUril(actual_id, type, filter);
	}
	
	private ArrayList<OM2MResource> bridgedDiscovery(String csi, String id, Integer type, ArrayList<String> filter) {
		String actual_id = csi;
		if(id != null) actual_id += id;
		return discovery(actual_id, type, filter);
	}
	
	public ArrayList<OM2MResource> discoveryAE() {
		return discovery(CSE_ID, OM2MConstants.AE, null);
	}
	
	public ArrayList<OM2MResource> discoveryAE(ArrayList<String> filter) {
		return discovery(CSE_ID, OM2MConstants.AE, filter);
	}
	
	public ArrayList<OM2MResource> discoveryAE(String id) {
		return discovery(id, OM2MConstants.AE, null);
	}
	
	public ArrayList<OM2MResource> discoveryAE(String id, ArrayList<String> filter) {
		return discovery(id, OM2MConstants.AE, filter);
	}
	
	public ArrayList<String> discoveryAErUril() {
		return discoveryUril(CSE_ID, OM2MConstants.AE, null);
	}
	
	public ArrayList<String> discoveryAErUril(ArrayList<String> filter) {
		return discoveryUril(CSE_ID, OM2MConstants.AE, filter);
	}
	
	public ArrayList<String> discoveryAErUril(String id) {
		return discoveryUril(id, OM2MConstants.AE, null);
	}
	
	public ArrayList<String> discoveryAErUril(String id, ArrayList<String> filter) {
		return discoveryUril(id, OM2MConstants.AE, filter);
	}
	
	public ArrayList<OM2MResource> discoveryContainer() {
		return discovery(CSE_ID, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<OM2MResource> discoveryContainer(ArrayList<String> filter) {
		return discovery(CSE_ID, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<OM2MResource> discoveryContainer(String id) {
		return discovery(id, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<OM2MResource> discoveryContainer(String id, ArrayList<String> filter) {
		return discovery(id, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<String> discoveryContainerUril() {
		return discoveryUril(CSE_ID, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<String> discoveryContainerUril(ArrayList<String> filter) {
		return discoveryUril(CSE_ID, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<String> discoveryContainerUril(String id) {
		return discoveryUril(id, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<String> discoveryContainerUril(String id, ArrayList<String> filter) {
		return discoveryUril(id, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<OM2MResource> discoveryContentInstance() {
		return discovery(CSE_ID, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<OM2MResource> discoveryContentInstance(ArrayList<String> filter) {
		return discovery(CSE_ID, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<OM2MResource> discoveryContentInstance(String id) {
		return discovery(id, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<OM2MResource> discoveryContentInstance(String id, ArrayList<String> filter) {
		return discovery(id, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<String> discoveryContentInstancerUril() {
		return discoveryUril(CSE_ID, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<String> discoveryContentInstancerUril(ArrayList<String> filter) {
		return discoveryUril(CSE_ID, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<String> discoveryContentInstancerUril(String id) {
		return discoveryUril(id, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<String> discoveryContentInstancerUril(String id, ArrayList<String> filter) {
		return discoveryUril(id, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<OM2MResource> discoveryRemoteCSE() {
		return discovery(CSE_ID, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<OM2MResource> discoveryRemoteCSE(ArrayList<String> filter) {
		return discovery(CSE_ID, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<OM2MResource> discoveryRemoteCSE(String id) {
		return discovery(id, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<OM2MResource> discoveryRemoteCSE(String id, ArrayList<String> filter) {
		return discovery(id, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<String> discoveryRemoteCSEUril() {
		return discoveryUril(CSE_ID, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<String> discoveryRemoteCSEUril(ArrayList<String> filter) {
		return discoveryUril(CSE_ID, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<String> discoveryRemoteCSEUril(String id) {
		return discoveryUril(id, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<String> discoveryRemoteCSEUril(String id, ArrayList<String> filter) {
		return discoveryUril(id, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<OM2MResource> discoverySubscription() {
		return discovery(CSE_ID, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<OM2MResource> discoverySubscription(ArrayList<String> filter) {
		return discovery(CSE_ID, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<OM2MResource> discoverySubscription(String id) {
		return discovery(id, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<OM2MResource> discoverySubscription(String id, ArrayList<String> filter) {
		return discovery(id, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<String> discoverySubscriptionUril() {
		return discoveryUril(CSE_ID, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<String> discoverySubscriptionUril(ArrayList<String> filter) {
		return discoveryUril(CSE_ID, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<String> discoverySubscriptionUril(String id) {
		return discoveryUril(id, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<String> discoverySubscriptionUril(String id, ArrayList<String> filter) {
		return discoveryUril(id, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryAE(String csi) {
		return bridgedDiscovery(csi, null, OM2MConstants.AE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryAE(String csi, ArrayList<String> filter) {
		return bridgedDiscovery(csi, null, OM2MConstants.AE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryAE(String csi, String id) {
		return bridgedDiscovery(csi, id, OM2MConstants.AE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryAE(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscovery(csi, id, OM2MConstants.AE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryAErUril(String csi) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.AE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryAErUril(String csi, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.AE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryAErUril(String csi, String id) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.AE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryAErUril(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.AE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContainer(String csi) {
		return bridgedDiscovery(csi, null, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContainer(String csi, ArrayList<String> filter) {
		return bridgedDiscovery(csi, null, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContainer(String csi, String id) {
		return bridgedDiscovery(csi, id, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContainer(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscovery(csi, id, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryContainerUril(String csi) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<String> bridgedDiscoveryContainerUril(String csi, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryContainerUril(String csi, String id) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.CONTAINER, null);
	}
	
	public ArrayList<String> bridgedDiscoveryContainerUril(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.CONTAINER, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContentInstance(String csi) {
		return bridgedDiscovery(csi, null, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContentInstance(String csi, ArrayList<String> filter) {
		return bridgedDiscovery(csi, null, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContentInstance(String csi, String id) {
		return bridgedDiscovery(csi, id, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryContentInstance(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscovery(csi, id, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryContentInstanceUril(String csi) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryContentInstanceUril(String csi, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryContentInstanceUril(String csi, String id) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.CONTENT_INSTANCE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryContentInstanceUril(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.CONTENT_INSTANCE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryRemoteCSE(String csi) {
		return bridgedDiscovery(csi, null, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryRemoteCSE(String csi, ArrayList<String> filter) {
		return bridgedDiscovery(csi, null, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryRemoteCSE(String csi, String id) {
		return bridgedDiscovery(csi, id, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoveryRemoteCSE(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscovery(csi, id, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryRemoteCSEUril(String csi) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryRemoteCSEUril(String csi, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<String> bridgedDiscoveryRemoteCSEUril(String csi, String id) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.REMOTE_CSE, null);
	}
	
	public ArrayList<String> bridgedDiscoveryRemoteCSEUril(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.REMOTE_CSE, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoverySubscription(String csi) {
		return bridgedDiscovery(csi, null, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoverySubscription(String csi, ArrayList<String> filter) {
		return bridgedDiscovery(csi, null, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoverySubscription(String csi, String id) {
		return bridgedDiscovery(csi, id, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<OM2MResource> bridgedDiscoverySubscription(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscovery(csi, id, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<String> bridgedDiscoverySubscriptionUril(String csi) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<String> bridgedDiscoverySubscriptionUril(String csi, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, null, OM2MConstants.SUBSCRIPTION, filter);
	}
	
	public ArrayList<String> bridgedDiscoverySubscriptionUril(String csi, String id) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.SUBSCRIPTION, null);
	}
	
	public ArrayList<String> bridgedDiscoverySubscriptionUril(String csi, String id, ArrayList<String> filter) {
		return bridgedDiscoveryUril(csi, id, OM2MConstants.SUBSCRIPTION, filter);
	}
}
