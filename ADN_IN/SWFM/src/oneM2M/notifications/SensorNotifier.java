package oneM2M.notifications;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Sensor;

public class SensorNotifier extends ResourceNotifier {
	public SensorNotifier() {
		super();
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
			
		if(json.get(Sensor.LAT) == null && json.get(Sensor.THRESHOLD) == null && json.get(Sensor.SENSOR_IS_WORKING) == null && json.get(Sensor.WATER_LEVEL) == null)
			return false;
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ref, ae);
		
		json.put(JSMessage.SENSOR_ID, destination.getRi());
		json.put(JSMessage.SENSOR_NAME, destination.getRn());
		json.put(JSMessage.CONTENT, ((InstanceResource) resource));
		
		return json;
	}

	@Override
	public void sendNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		if(isSensor((InstanceResource) resource))
			notify(createNotification(ref, resource, ae, destination));
	}
}
