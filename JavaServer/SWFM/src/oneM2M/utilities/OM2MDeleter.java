package oneM2M.utilities;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;

public class OM2MDeleter {
	private String IP_ADDRESS;
	
	protected void setIP_ADDRESS(String ip) {
		IP_ADDRESS = ip;
	}
	
	private Request deleteRequest() {
		Request req = new Request(Code.DELETE);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, OM2MConstants.ACP_ADMIN));
		
		return req;
	}
	
	private CoapResponse deleteRequest(String address) {
		Request req = deleteRequest();
		
		CoapClient client = new CoapClient(address);
		
		client.setTimeout(OM2MConstants.TIMEOUT);
		
		return client.advanced(req);
	}
	
	private boolean deleteResource(String id) {
		String address = IP_ADDRESS + id;
		
		CoapResponse res = deleteRequest(address);
		
		return OM2MUtilities.checkResponse(res, OM2MConstants.DELETE_SUCCESSFULL);
	}
	
	private boolean deleteBridgedResource(String csi, String id) {
		String name = csi;
		if(id != null)
			name += id.substring(id.lastIndexOf("/"), id.length());
		
		return deleteResource(name);
	}
	
	public synchronized boolean deleteReference(String id) {
		return deleteResource(id);
	}
	
	public synchronized boolean deleteAE(String id) {
		return deleteResource(id);
	}
	
	public synchronized boolean deleteContainer(String id) {
		return deleteResource(id);
	}
	
	public synchronized boolean deleteContentInstance(String la) {
		return deleteResource(la);
	}
	
	public synchronized boolean deleteSubscription(String id) {
		return deleteResource(id);
	}
	
	public synchronized boolean deleteBridgedReference(String csi, String id) {
		return deleteBridgedResource(csi, id);
	}
	
	public synchronized boolean deleteBridgedAE(String csi, String id) {
		return deleteBridgedResource(csi, id);
	}
	
	public synchronized boolean deleteBridgedContainer(String csi, String id) {
		return deleteBridgedResource(csi, id);
	}
	
	public synchronized boolean deleteBridgedContentInstance(String csi, String la) {
		return deleteBridgedResource(csi, la);
	}
	
	public synchronized boolean deleteBridgedSubscription(String csi, String id) {
		return deleteBridgedResource(csi, id);
	}
}
