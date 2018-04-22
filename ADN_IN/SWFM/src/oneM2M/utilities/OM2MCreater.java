package oneM2M.utilities;

import org.eclipse.californium.core.CoapResponse;
import org.json.simple.JSONObject;

import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.SubscriptionResource;

@SuppressWarnings("unchecked")
public class OM2MCreater {
	private String IP_ADDRESS;
	private String CSE_ID;
	
	protected void setIP_ADDRESS(String ip) {
		IP_ADDRESS = ip;
	}
	
	protected void setCSE_ID(String cse_id) {
		CSE_ID = cse_id;
	}
	
	private String createResource(String id_father, String resource_type, int type, JSONObject body) {		
		String address = IP_ADDRESS + id_father;

		JSONObject payload = new JSONObject();
		payload.put(resource_type, body);
		
		CoapResponse res = OM2MUtilities.postRequest(address, payload.toJSONString().replace("\\", ""), type);
		
		if(!OM2MUtilities.checkResponse(res, OM2MConstants.POST_SUCCESSFULL))
			return null;
		
	    	return res.getResponseText();
	}
	
	public String createBridgedResource(String csi, String id_father, String resource_type, int type, JSONObject body) {	
		String name = csi;
		if(id_father != null)
			name += id_father.substring(id_father.lastIndexOf("/"), id_father.length());
		
		return createResource(name, resource_type, type, body);
	}
	
	public AEResource createAE(JSONObject body) {		
		String resource = createResource(CSE_ID, OM2MConstants.RESOURCE_TYPE_AE, OM2MConstants.AE, body);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource createContainer(String id_father, JSONObject body) {
		String resource = createResource(id_father, OM2MConstants.RESOURCE_TYPE_CONTAINER, OM2MConstants.CONTAINER, body);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource createContentInstance(String id_father, JSONObject body) {
		String resource = createResource(id_father, OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE, OM2MConstants.CONTENT_INSTANCE, body);

		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource createSubscription(String id_father, JSONObject body) {
		String resource = createResource(id_father, OM2MConstants.RESOURCE_TYPE_SUBSCRIPTION, OM2MConstants.SUBSCRIPTION, body);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	public AEResource createBridgedAE(String csi, String cse_id, JSONObject body) {	
		String resource = createBridgedResource(csi, cse_id, OM2MConstants.RESOURCE_TYPE_AE, OM2MConstants.AE, body);
		if(resource == null)
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource createBridgedContainer(String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(csi, id_father, OM2MConstants.RESOURCE_TYPE_CONTAINER, OM2MConstants.CONTAINER, body);
		if(resource == null)
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource createBridgedContentInstance(String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(csi, id_father, OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE, OM2MConstants.CONTENT_INSTANCE, body);
		if(resource == null)
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource createBridgedSubscription(String csi, String id_father, JSONObject body) {
		String resource = createBridgedResource(csi, id_father, OM2MConstants.RESOURCE_TYPE_SUBSCRIPTION, OM2MConstants.SUBSCRIPTION, body);
		if(resource == null)
			return null;
		
		return new SubscriptionResource(resource);
	}
}
