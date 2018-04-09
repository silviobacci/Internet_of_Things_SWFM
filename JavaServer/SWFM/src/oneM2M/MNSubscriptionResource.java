package oneM2M;

import java.util.ArrayList;
import java.util.Observer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;
import resources.ReferenceResource;

public class MNSubscriptionResource extends CoapResource {	
	private int SERVER_PORT;
	private boolean IN;
	
	private ReferenceResource reference;
	private ObservableResource marker_resource; 
	private ObservableResource sensor_resource; 
	private ObservableResource dam_resource; 
	private ObservableResource history_resource; 
	
	private INManager mng;
	
	public MNSubscriptionResource(INManager m, ReferenceResource r, String ip, String name, int port, boolean in) {
		super(name);
		mng = m;
		reference = r;
		marker_resource = new ObservableResource();
		sensor_resource = new ObservableResource();
		dam_resource = new ObservableResource();
		history_resource = new ObservableResource();
		SERVER_PORT = port;
		IN = in;
		getAttributes().setTitle(name);
	}
	
	public void addMarkerObserver(Observer ob) {
		marker_resource.addObserver(ob);
	}
	
	public void addSensorObserver(Observer ob) {
		sensor_resource.addObserver(ob);
	}
	
	public void addDamObserver(Observer ob) {
		dam_resource.addObserver(ob);
	}
	
	public void addHistoryObserver(Observer ob) {
		history_resource.addObserver(ob);
	}
	
	public void deleteMarkerObserver(Observer ob) {
		marker_resource.deleteObserver(ob);
	}
	
	public void deleteSensorObserver(Observer ob) {
		sensor_resource.deleteObserver(ob);
	}
	
	public void deleteDamObserver(Observer ob) {
		dam_resource.deleteObserver(ob);
	}
	
	public void deleteHistoryObserver(Observer ob) {
		history_resource.deleteObserver(ob);
	}
	
	private boolean isNotify(CoapExchange exchange) {
		JSONObject json;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get("m2m:sgn");
			json = (JSONObject) json.get("m2m:nev");
			
			return json != null;
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createMarkerNotification(OM2MResource resource, int type, OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put("reference_id", reference.getRi());
		json.put("ae_id", ae.getRi());
		json.put("ae_name", ae.getRn());
		
		if(type == OM2MManager.CONTENT_INSTANCE)
			json.put("content", ((InstanceResource) resource).getCon());
		
		return json;
	}
	
	private JSONObject createMarkerNotification(int type, OM2MResource ae) {
		return createMarkerNotification(null, type, ae);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createSensorNotification(OM2MResource resource, int type, OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put("reference_id", reference.getRi());
		json.put("ae_id", ae.getRi());
		json.put("ae_name", ae.getRn());
		
		
		if(type == OM2MManager.CONTENT_INSTANCE) {
			json.put("sensor_id", resource.getPi());
			json.put("content", ((InstanceResource) resource).getCon());
		}
		else
			if(resource != null) json.put("sensor_id", resource.getRi());
		
		return json;
	}
	
	private JSONObject createSensorNotification(int type, OM2MResource ae) {
		return createSensorNotification(null, type, ae);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createDamNotification(OM2MResource resource, int type, OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put("reference_id", reference.getRi());
		json.put("ae_id", ae.getRi());
		json.put("ae_name", ae.getRn());
		
		
		if(type == OM2MManager.CONTENT_INSTANCE) {
			json.put("dam_id", resource.getPi());
			json.put("content", ((InstanceResource) resource).getCon());
		}
		else
			if(resource != null) json.put("dam_id", resource.getRi());
		
		return json;
	}
	
	private JSONObject createDamNotification(int type, OM2MResource ae) {
		return createDamNotification(null, type, ae);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createHistoryNotification(OM2MResource resource, int type, OM2MResource ae) {
		JSONObject json = new JSONObject();
		json.put("reference_id", reference.getRi());
		json.put("ae_id", ae.getRi());
		json.put("ae_name", ae.getRn());
		json.put("sensor_id", resource.getPi());
		
		if(type == OM2MManager.CONTENT_INSTANCE)
			json.put("content", ((InstanceResource) resource).getCon());
		
		return json;
	}
	
	private boolean isMarker(ContainerResource cnt) {
		if(cnt == null || cnt.getRn().equals("DAMS") || cnt.getRn().equals("SENSORS"))
			return false;
		
		AEResource copiedMN = mng.mng.getAE(IN, cnt.getPi());

		if(copiedMN == null)
			return false;
		
		return true;
	}
	
	private boolean isMarker(InstanceResource inst) {
		if(inst == null)
			return false;
		
		if(!(inst.getCon() instanceof JSONObject))
			return false;
		
		JSONObject json = null;
		
		try {
			json = (JSONObject) JSONValue.parseWithException(inst.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
			
		if(json.get("message") == null && json.get("LNG_AE") == null)
			return false;
		
		return true;
	}
	
	private boolean isAllSensor(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		if(cnt.getRn().equals("SENSORS"))
			return true;
		
		return true;
	}
	
	private boolean isSensor(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		return isAllSensor(mng.mng.getContainer(IN, cnt.getPi()));
	}
	
	private boolean isSensor(InstanceResource inst) {
		if(inst == null)
			return false;
		
		if(inst.getCon() instanceof Integer)
			return true;
		
		if(!(inst.getCon() instanceof JSONObject))
			return false;
		
		JSONObject json = null;
		
		try {
			json = (JSONObject) JSONValue.parseWithException(inst.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
			
		if(json.get("LNG_SENSOR") == null && json.get("TH") == null)
			return false;
		
		return true;
	}
	
	private boolean isAllDam(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		if(cnt.getRn().equals("DAMS"))
			return true;
		
		return true;
	}
	
	private boolean isDam(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		return isAllDam(mng.mng.getContainer(IN, cnt.getPi()));
	}
	
	private boolean isDam(InstanceResource inst) {
		if(inst == null)
			return false;
		
		if(inst.getCon() instanceof Boolean)
			return true;
		
		if(!(inst.getCon() instanceof JSONObject))
			return false;
		
		JSONObject json = null;
		
		try {
			json = (JSONObject) JSONValue.parseWithException(inst.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
			
		if(json.get("LNG_DAM") == null)
			return false;
		
		return true;
	}
	
	private ContainerResource findAEfromContainer(ContainerResource cnt) {
		AEResource copiedMN = mng.mng.getAE(IN, cnt.getPi());
		
		while(copiedMN == null) {
			cnt = mng.mng.getContainer(IN, cnt.getPi());
			copiedMN = mng.mng.getAE(IN, cnt.getPi());
		}
		
		return cnt;
	}
	
	private ContainerResource findDestination(OM2MResource notified) {
		ArrayList<String> filters = new ArrayList<String>();
		filters.add("lbl=" + notified.getPi());
		filters.add("lbl=" + reference.getRi());
		ArrayList<OM2MResource> copiedContainer = mng.mng.discovery(IN, OM2MManager.CONTAINER, filters);
		
		if(copiedContainer == null || copiedContainer.isEmpty() || copiedContainer.size() != 1)
			return null;
		
		return (ContainerResource) copiedContainer.get(0);
	}
	
	private void createAE(JSONObject jo) {
		AEResource notified = new AEResource(jo);
		
		String filter = "lbl=" + reference.getRi();
		ArrayList<OM2MResource> copiedAE = mng.mng.discovery(IN, OM2MManager.AE, filter);
		
		if(copiedAE == null || copiedAE.isEmpty() || copiedAE.size() != 1)
			return;
		
		AEResource destination = (AEResource) copiedAE.get(0);
		
		ContainerResource ae = mng.createAE(reference, notified, destination, SERVER_PORT);
		
		if(ae != null)
			marker_resource.notify(createMarkerNotification(OM2MManager.AE, ae));
	}
	
	private void createContainer(JSONObject jo) {
		ContainerResource notified = new ContainerResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		ContainerResource createdContainer = mng.createContainer(reference, notified, destination, SERVER_PORT);
		
		ContainerResource ae = findAEfromContainer(destination);
		
		if(isMarker(destination))
			marker_resource.notify(createMarkerNotification(OM2MManager.CONTAINER, ae));
		
		if(isAllSensor(createdContainer))
			sensor_resource.notify(createSensorNotification(OM2MManager.CONTAINER, ae));
		
		if(isSensor(createdContainer)) {
			sensor_resource.notify(createSensorNotification(createdContainer, OM2MManager.CONTAINER, ae));
			history_resource.notify(createHistoryNotification(createdContainer, OM2MManager.CONTAINER, ae));
		}
		
		if(isAllDam(createdContainer))
			dam_resource.notify(createDamNotification(OM2MManager.CONTAINER, ae));
		
		if(isDam(createdContainer))
			dam_resource.notify(createDamNotification(createdContainer, OM2MManager.CONTAINER, ae));
	}
	
	private void createContentInstance(JSONObject jo) {
		InstanceResource notified = new InstanceResource(jo);
		
		ContainerResource destination = findDestination(notified);
		
		if(destination == null)
			return;
		
		InstanceResource createdInstance = mng.createInstance(reference, notified, destination);
		
		ContainerResource ae = findAEfromContainer(destination);
		
		if(isMarker(createdInstance))
			marker_resource.notify(createMarkerNotification(createdInstance, OM2MManager.CONTENT_INSTANCE, ae));
		
		if(isSensor(createdInstance)) {
			sensor_resource.notify(createSensorNotification(createdInstance, OM2MManager.CONTENT_INSTANCE, ae));
			history_resource.notify(createHistoryNotification(createdInstance, OM2MManager.CONTENT_INSTANCE, ae));
		}
		
		if(isDam(createdInstance))
			dam_resource.notify(createDamNotification(createdInstance, OM2MManager.CONTENT_INSTANCE, ae));
	}
	
	private void handleNotify(CoapExchange exchange) {
		JSONObject json;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get("m2m:sgn");
			json = (JSONObject) json.get("m2m:nev");
			json = (JSONObject) json.get("m2m:rep");
			
			if(json.get("m2m:ae") != null) createAE((JSONObject) json.get("m2m:ae"));
			if(json.get("m2m:cnt") != null) createContainer((JSONObject) json.get("m2m:cnt"));
			if(json.get("m2m:cin") != null) createContentInstance((JSONObject) json.get("m2m:cin"));
			
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void handleGET(CoapExchange exchange) {}
	
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.CREATED);
		
		if(isNotify(exchange))
			new Notifier(exchange).start();
	}
	
	private class Notifier extends Thread {
		CoapExchange exchange;
		
		public Notifier(CoapExchange ex) {
			exchange = ex;
		}
		
		@Override
		public void run() {
			handleNotify(exchange);
			super.run();
		}
	}
}