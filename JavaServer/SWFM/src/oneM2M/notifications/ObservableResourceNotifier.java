package oneM2M.notifications;

import java.util.Observer;

import org.json.simple.JSONObject;

import oneM2M.resources.ContainerResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.JSMessage;

public abstract class ObservableResourceNotifier {
	protected ReferenceResource reference;
	protected ObservableResource observable_resource;
	
	public ObservableResourceNotifier(ReferenceResource ref) {
		reference = ref;
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
	protected JSONObject createNotification(OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put(JSMessage.REFERENCE_ID, reference.getRi());
		json.put(JSMessage.AE_ID, ae.getRi());
		json.put(JSMessage.AE_NAME, ae.getRn());
		
		return json;
	}
	
	protected abstract JSONObject createNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination);
	
	abstract public void sendNotification(OM2MResource resource, int type, OM2MResource ae, ContainerResource destination); 
}
