package oneM2M.notifications;

import java.util.ArrayList;
import java.util.Observer;

import org.json.simple.JSONObject;

import oneM2M.INManager;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.subscriptions.SubscriptionServerResource;
import oneM2M.utilities.OM2MConstants;
import oneM2M.utilities.OM2MUtilities;

public class MNSubscriptionResource extends SubscriptionServerResource {	
	private ReferenceResource reference;
	private MarkerNotifier marker; 
	private SensorNotifier sensor; 
	private DamNotifier dam; 
	private HistoryNotifier history; 
	
	public MNSubscriptionResource(String name, int port, ReferenceResource ref) {
		super(name, port);
		
		reference = ref;
		
		marker = new MarkerNotifier(reference);
		sensor = new SensorNotifier(reference);
		dam = new DamNotifier(reference);
		history = new HistoryNotifier(reference);
		
		getAttributes().setTitle(name);
	}
	
	public void addMarkerObserver(Observer ob) {
		marker.addObserver(ob);
	}
	
	public void addSensorObserver(Observer ob) {
		sensor.addObserver(ob);
	}
	
	public void addDamObserver(Observer ob) {
		dam.addObserver(ob);
	}
	
	public void addHistoryObserver(Observer ob) {
		history.addObserver(ob);
	}
	
	public void deleteMarkerObserver(Observer ob) {
		marker.deleteObserver(ob);
	}
	
	public void deleteSensorObserver(Observer ob) {
		sensor.deleteObserver(ob);
	}
	
	public void deleteDamObserver(Observer ob) {
		dam.deleteObserver(ob);
	}
	
	public void deleteHistoryObserver(Observer ob) {
		history.deleteObserver(ob);
	}
	
	private ContainerResource findAEfromContainer(ContainerResource cnt) {
		AEResource copiedMN = INManager.getManager().getter.getAE(cnt.getPi());
		
		while(copiedMN == null) {
			cnt = INManager.getManager().getter.getContainer(cnt.getPi());
			copiedMN = INManager.getManager().getter.getAE(cnt.getPi());
		}
		
		return cnt;
	}
	
	private ContainerResource findDestination(OM2MResource notified) {
		ArrayList<String> filters = OM2MUtilities.createFilters(notified.getPi(), reference.getRi());
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
		
		ContainerResource ae = INManager.createAE(reference, notified, destination, SERVER_PORT);
		
		marker.sendNotification(OM2MConstants.AE, ae);
	}
	
	private void createContainer(JSONObject jo) {
		ContainerResource notified = new ContainerResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		ContainerResource createdContainer = INManager.createContainer(reference, notified, destination, SERVER_PORT);
		
		ContainerResource ae = findAEfromContainer(destination);
		
		destination = INManager.getManager().getter.getContainer(destination.getPi());
		
		marker.sendNotification(destination, OM2MConstants.CONTAINER, ae);
		
		sensor.sendNotification(createdContainer, OM2MConstants.CONTAINER, ae, destination);
		
		history.sendNotification(createdContainer, OM2MConstants.CONTAINER, ae, destination);
		
		dam.sendNotification(createdContainer, OM2MConstants.CONTAINER, ae);
	}
	
	private void createContentInstance(JSONObject jo) {
		InstanceResource notified = new InstanceResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		InstanceResource createdInstance = INManager.createInstance(reference, notified, destination);
		
		ContainerResource ae = findAEfromContainer(destination);
		
		destination = INManager.getManager().getter.getContainer(destination.getPi());
		
		marker.sendNotification(createdInstance, OM2MConstants.CONTENT_INSTANCE, ae);
		
		sensor.sendNotification(createdInstance, OM2MConstants.CONTENT_INSTANCE, ae, destination);
		
		history.sendNotification(createdInstance, OM2MConstants.CONTENT_INSTANCE, ae, destination);
		
		dam.sendNotification(createdInstance, OM2MConstants.CONTENT_INSTANCE, ae, destination);
	}
	
	public void handleNotify(JSONObject notified) {
		JSONObject AE = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_AE);
		JSONObject CONTAINER = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_CONTAINER);
		JSONObject CONTENT_INSTANCE = (JSONObject) notified.get(OM2MConstants.RESOURCE_TYPE_CONTENT_INSTANCE);
		
		if(AE != null) createAE(AE);
		if(CONTAINER != null) createContainer(CONTAINER);
		if(CONTENT_INSTANCE != null) createContentInstance(CONTENT_INSTANCE);
	}
}