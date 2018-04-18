package oneM2M.notifications;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManager;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MConstants;
import servlets.assets.oneM2M.constants.Dam;
import servlets.assets.oneM2M.constants.JSMessage;

public class DamNotifier extends ObservableResourceNotifier {
	private final String DAMS_CONTAINER = "DAMS";

	public DamNotifier(ReferenceResource ref) {
		super(ref);
	}
	
	private boolean isDamsContainer(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		if(cnt.getRn().equals(DAMS_CONTAINER))
			return true;
		
		return true;
	}
	
	private boolean isDam(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		return isDamsContainer(INManager.getManager().getter.getContainer(cnt.getPi()));
	}
	
	private boolean isDam(InstanceResource inst) {
		if(inst == null)
			return false;
		
		JSONObject json = null;
		
		try {
			json = (JSONObject) JSONValue.parseWithException(inst.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
			
		if(json.get(Dam.LAT) == null && json.get(Dam.DAM_IS_WORKING) == null && json.get(Dam.STATE) == null)
			return false;
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ae);
		
		if(type == OM2MConstants.CONTAINER && resource != null) {
			json.put(JSMessage.DAM_ID, resource.getRi());
			json.put(JSMessage.DAM_NAME, resource.getRn());
		}
			
		
		if(type == OM2MConstants.CONTENT_INSTANCE) {
			json.put(JSMessage.DAM_ID, destination.getRi());
			json.put(JSMessage.DAM_NAME, destination.getRn());
			json.put(JSMessage.CONTENT, ((InstanceResource) resource));
			json.put(JSMessage.CREATION_TIME, ((InstanceResource) resource).getCt());
		}
		
		return json;
	}
	
	private JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae) {
		return createNotification(resource, type, ae, null);
	}
	
	private JSONObject createNotification(int type, OM2MResource ae) {
		return createNotification(null, type, ae, null);
	}

	@Override
	public void sendNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		switch(type) {
			case OM2MConstants.CONTAINER:
				if(isDamsContainer((ContainerResource) resource))
					notify(createNotification(OM2MConstants.CONTAINER, ae));
				
				if(isDam((ContainerResource) resource))
					notify(createNotification(resource, OM2MConstants.CONTAINER, ae));
				break;
			case OM2MConstants.CONTENT_INSTANCE:
				if(isDam((InstanceResource) resource))
					notify(createNotification(resource, OM2MConstants.CONTENT_INSTANCE, ae, destination));
				break;
		}
	}
	
	public void sendNotification(OM2MResource resource, int type, OM2MResource ae) {
		sendNotification(resource, type, ae, null);
	}
}
