package oneM2M.subscriptions;

import org.json.simple.JSONObject;

import oneM2M.INManager;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MConstants;

public class INSubscriptionResource extends SubscriptionServerResource {	
	public INSubscriptionResource(String name, int port) {
		super(name, port);
		getAttributes().setTitle(name);
	}
	
	private void createMNReference(JSONObject jo) {
		ReferenceResource notified = new ReferenceResource(jo);
		
		INManager.createMNReference(notified, SERVER_PORT);
	}
	
	protected void handleNotify(JSONObject notified) {
		if(notified == null)
			return;
		
		JSONObject REMOTE_CSE = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_REMOTE_CSE);
		
		if(REMOTE_CSE != null) createMNReference(REMOTE_CSE);
	}
}