package oneM2M.notifications;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.Alert;
import servlets.assets.oneM2M.constants.JSMessage;

public class AlertNotifier extends ResourceNotifier {
	public AlertNotifier() {
		super();
	}
	
	private boolean isAlert(InstanceResource inst) {
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
			
		if(json.get(Alert.RISK_LEVEL) == null)
			return false;
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		JSONObject json = createNotification(ref, ae);
		json.put(JSMessage.CONTENT, ((InstanceResource) resource));
		
		return json;
	}
	
	@Override
	public void sendNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination) {
		if(isAlert((InstanceResource) resource))
			notify(createNotification(ref, resource, ae, destination));
	}
}
