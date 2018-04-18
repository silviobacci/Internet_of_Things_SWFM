package oneM2M.notifications;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManager;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MConstants;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Marker;

public class MarkerNotifier extends ObservableResourceNotifier {
	private final String DAMS_CONTAINER = "DAMS";
	private final String SENSORS_CONTAINER = "SENSORS";

	public MarkerNotifier(ReferenceResource ref) {
		super(ref);
	}
	
	private boolean isMarker(ContainerResource cnt) {
		if(cnt == null || cnt.getRn().equals(DAMS_CONTAINER) || cnt.getRn().equals(SENSORS_CONTAINER))
			return false;
		
		AEResource copiedMN = INManager.getManager().getter.getAE(cnt.getPi());

		if(copiedMN == null)
			return false;
		
		return true;
	}
	
	private boolean isMarker(InstanceResource inst) {
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
			
		if(json.get(Marker.RISK_MESSAGE) == null && json.get(Marker.LAT) == null)
			return false;
		
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ae);
		
		if(type == OM2MConstants.CONTENT_INSTANCE)
			json.put(JSMessage.CONTENT, ((InstanceResource) resource));
		
		return json;
	}

	@Override
	public void sendNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		switch(type) {
			case OM2MConstants.AE:
				if(ae != null)
					notify(createNotification(resource, type, ae, destination));
				break;
			case OM2MConstants.CONTAINER:
				if(isMarker((ContainerResource) resource))
					notify(createNotification(resource, type, ae, destination));
				break;
			case OM2MConstants.CONTENT_INSTANCE:
				if(isMarker((InstanceResource) resource))
					notify(createNotification(resource, type, ae, destination));
				break;
		}
	}
	
	public void sendNotification(OM2MResource resource, int type, OM2MResource ae) {
		sendNotification(resource, type, ae, null);
	}

	public void sendNotification(int type, OM2MResource ae) {
		sendNotification(null, type, ae, null);
	}
}
