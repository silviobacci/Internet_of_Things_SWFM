package oneM2M.notifications;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Marker;

public class MarkerNotifier extends ResourceNotifier {
	public MarkerNotifier() {
		super();
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
			
		if(json.get(Marker.LAT) == null)
			return false;
		
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ref, ae);
		json.put(JSMessage.CONTENT, ((InstanceResource) resource));
		
		return json;
	}
	
	protected JSONObject createNotification(ReferenceResource ref, OM2MResource resource, int type, OM2MResource ae) {
		return createNotification(ref, resource, type, ae);
	}

	@Override
	public void sendNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		if(isMarker((InstanceResource) resource))
			notify(createNotification(ref, resource, ae, destination));
	}
}
