package oneM2M.notifications;

import java.util.Observer;

import org.json.simple.JSONObject;

import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.JSMessage;

public abstract class ResourceNotifier {
	protected ObservableResource observable_resource;
	
	public ResourceNotifier() {
		observable_resource = new ObservableResource();
	}

	public void addObserver(Observer ob) {
		observable_resource.addObserver(ob);
	}
	
	public void deleteObserver(Observer ob) {
		observable_resource.deleteObserver(ob);
	}
	
	protected void notify(Object o) {
		observable_resource.notify(o);
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject createNotification(ReferenceResource reference, OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put(JSMessage.REFERENCE_ID, reference.getRi());
		json.put(JSMessage.AE_ID, ae.getRi());
		json.put(JSMessage.AE_NAME, ae.getRn());
		
		return json;
	}
	
	protected abstract JSONObject createNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination);
	
	abstract public void sendNotification(ReferenceResource ref, InstanceResource resource, ContainerResource ae, ContainerResource destination); 
}
