package oneM2M.subscriptions;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import oneM2M.INManager;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MConstants;
import oneM2M.utilities.OM2MUtilities;

public class MNSubscriptionResource extends SubscriptionServerResource {	
	private ReferenceResource reference;
	
	public MNSubscriptionResource(String name, int port, ReferenceResource ref) {
		super(name, port);
		
		reference = ref;
		
		getAttributes().setTitle(name);
	}
	
	private ContainerResource findDestination(OM2MResource notified) {
		ArrayList<String> filters = OM2MUtilities.createFilters(reference.getRi(), notified.getPi());
		ArrayList<OM2MResource> copiedContainer = INManager.getManager().discoverer.discoveryContainer(filters);
		
		if(copiedContainer == null || copiedContainer.isEmpty() || copiedContainer.size() != 1)
			return null;
		
		return (ContainerResource) copiedContainer.get(0);
	}
	
	private void createAE(JSONObject jo) {
		AEResource notified = new AEResource(jo);
		
		ArrayList<String> filters = OM2MUtilities.createFilters(reference.getRi());
		ArrayList<OM2MResource> copiedAE = INManager.getManager().discoverer.discoveryAE(filters);
		
		if(copiedAE == null || copiedAE.isEmpty() || copiedAE.size() != 1)
			return;
		
		AEResource destination = (AEResource) copiedAE.get(0);
		
		INManager.createAE(reference, notified, destination, SERVER_PORT);
	}
	
	private void createContainer(JSONObject jo) {
		ContainerResource notified = new ContainerResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		INManager.createContainer(reference, notified, destination, SERVER_PORT);
	}
	
	private void createContentInstance(JSONObject jo) {
		InstanceResource notified = new InstanceResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		INManager.createInstance(reference, notified, destination);
	}
	
	public synchronized void handleNotify(JSONObject notified) {
		JSONObject AE = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_AE);
		JSONObject CONTAINER = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_CONTAINER);
		JSONObject CONTENT_INSTANCE = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE);
		
		if(AE != null) createAE(AE);
		if(CONTAINER != null) createContainer(CONTAINER);
		if(CONTENT_INSTANCE != null) createContentInstance(CONTENT_INSTANCE);
	}
}