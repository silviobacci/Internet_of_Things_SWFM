package oneM2M.utilities;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;

import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.resources.SubscriptionResource;

public class OM2MGetter {
	private String IP_ADDRESS;
	
	protected void setIP_ADDRESS(String ip) {
		IP_ADDRESS = ip;
	}
	
	protected static Request getRequest() {
		Request req = new Request(Code.GET);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, OM2MConstants.ACP_ADMIN));
		
		return req;
	}
	
	protected static CoapResponse getRequest(String address) {
		Request req = getRequest();
		
		return OM2MUtilities.executeRequest(address, req);
	}
	
	private String getResource(String id) {
		String address = IP_ADDRESS + id;
		
		CoapResponse res = getRequest(address);
		
		if(!OM2MUtilities.checkResponse(res, OM2MConstants.GET_SUCCESSFULL))
			return null;

    		return res.getResponseText();
	}
	
	private String getBridgedResource(String csi, String id) {
		String name = csi;
		if(id != null)
			name += id.substring(id.lastIndexOf("/"), id.length());
		
		return getResource(name);
	}
	
	public ReferenceResource getReference(String id) {
		String resource = getResource(id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isReference(resource))
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getAE(String id) {
		String resource = getResource(id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isAE(resource))
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getContainer(String id) {
		String resource = getResource(id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isContainer(resource))
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getContentInstance(String la) {
		String resource = getResource(la);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isContentInstance(resource))
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getSubscription(String id) {
		String resource = getResource(id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isSubscription(resource))
			return null;
		
		return new SubscriptionResource(resource);
	}
	
	public ReferenceResource getBridgedReference(String csi, String id) {
		String resource = getBridgedResource(csi, id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isReference(resource))
			return null;
		
		return new ReferenceResource(resource);
	}
	
	public AEResource getBridgedAE(String csi, String id) {
		String resource = getBridgedResource(csi, id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isAE(resource))
			return null;
		
		return new AEResource(resource);
	}
	
	public ContainerResource getBridgedContainer(String csi, String id) {
		String resource = getBridgedResource(csi, id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isContainer(resource))
			return null;
		
		return new ContainerResource(resource);
	}
	
	public InstanceResource getBridgedContentInstance(String csi, String la) {
		String resource = getBridgedResource(csi, la);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isContentInstance(resource))
			return null;
		
		return new InstanceResource(resource);
	}
	
	public SubscriptionResource getBridgedSubscription(String csi, String id) {
		String resource = getBridgedResource(csi, id);
		
		if(resource == null)
			return null;
		
		if(!OM2MUtilities.isSubscription(resource))
			return null;
		
		return new SubscriptionResource(resource);
	}
}
