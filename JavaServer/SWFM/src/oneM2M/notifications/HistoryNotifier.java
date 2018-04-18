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
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Sensor;

public class HistoryNotifier extends ObservableResourceNotifier {
	private final String SENSORS_CONTAINER = "SENSORS";

	public HistoryNotifier(ReferenceResource ref) {
		super(ref);
	}
	
	private boolean isSensorsContainer(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		if(cnt.getRn().equals(SENSORS_CONTAINER))
			return true;
		
		return true;
	}
	
	private boolean isSensor(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		return isSensorsContainer(INManager.getManager().getter.getContainer(cnt.getPi()));
	}
	
	private boolean isSensor(InstanceResource inst) {
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
			
		if(json.get(Sensor.WATER_LEVEL) != null)
			return true;
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ae);
		
		if(type == OM2MConstants.CONTENT_INSTANCE) {
			json.put(JSMessage.SENSOR_ID, destination.getRi());
			json.put(JSMessage.SENSOR_NAME, destination.getRn());
			json.put(JSMessage.CONTENT, ((InstanceResource) resource));
		}	
		else
			json.put(JSMessage.SENSOR_ID, resource.getRi());
		
		return json;
	}
	
	protected JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae) {
		return createNotification(resource, type, ae, null);
	}

	@Override
	public void sendNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination) {
		switch(type) {
			case OM2MConstants.CONTAINER:
				if(isSensor((ContainerResource) resource))
					notify(createNotification(resource, OM2MConstants.CONTAINER, ae));
		
				break;
			case OM2MConstants.CONTENT_INSTANCE:
				if(isSensor((InstanceResource) resource))
					notify(createNotification(resource, OM2MConstants.CONTENT_INSTANCE, ae, destination));
				break;
		}
	}
}
