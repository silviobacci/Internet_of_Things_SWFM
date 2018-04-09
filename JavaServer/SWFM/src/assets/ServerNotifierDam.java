package assets;

import java.util.Observable;
import java.util.Observer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import assets.ServerNotifier;
import oneM2M.INManager;
import oneM2M.MNSubscriptionServer;
import resources.InstanceResource;
import resources.ReferenceResource;

public class ServerNotifierDam extends ServerNotifier implements Observer {
	private String reference_id;
	private String ae_id;
	private String ae_name;

	public ServerNotifierDam(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	public ServerNotifierDam(HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name) {
		super(request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
		
		INManager.getObservableResource().addObserver(this);
		
		addDamObserver();
	}
	
	private void addDamObserver() {
		for(MNSubscriptionServer ss : INManager.getMNservers())
			ss.getResource().addDamObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDam(JSONObject json) {
		System.out.println(Thread.currentThread().getId() + " " + json.toJSONString());
		JSONObject data = null;
		JSONArray response = new JSONArray();
		
		if(!reference_id.equals((String) json.get("reference_id")) || !ae_id.equals((String) json.get("ae_id")) || !ae_name.equals((String) json.get("ae_name")))
			return;
		
		String dam_id = (String) json.get("dam_id");
		
		if(json.get("content") == null && dam_id == null)
			response = new QueryManagerIN().getSensorData(reference_id, ae_id, ae_name);
		else if(json.get("content") == null && dam_id != null)
			response = new QueryManagerIN().getSensorData(reference_id, dam_id);
		else if(json.get("content") != null && dam_id != null) {
			data = new JSONObject();
			
			data.put("sensor_id", dam_id);
			
			InstanceResource instance = (InstanceResource) json.get("content");
			json = (JSONObject) instance.getCon();
			
			if(json.get("LAT_SENSOR") != null) data.put("lat", json.get("LAT_SENSOR"));
			if(json.get("LNG_SENSOR") != null) data.put("lng", json.get("LNG_SENSOR"));
			if(json.get("LEVEL") != null) data.put("level", json.get("LEVEL"));
			if(json.get("MIN") != null) data.put("min", json.get("MIN"));
			if(json.get("MAX") != null) data.put("max", json.get("MAX"));
			if(json.get("TH") != null) data.put("th", json.get("TH"));
		}
		
		response.add(data);
		sendNotification(response);
	}

	@Override
	public void update(Observable o, Object arg) {
		new Notifier(arg).start();
	}
	
	private class Notifier extends Thread {
		Object arg;
		
		public Notifier(Object a) {
			arg = a;
		}
		
		@Override
		public void run() {
			if(arg instanceof ReferenceResource)
				addDamObserver();
			else if(arg instanceof JSONObject)
				updateDam((JSONObject) arg);
			super.run();
		}
	}
}
