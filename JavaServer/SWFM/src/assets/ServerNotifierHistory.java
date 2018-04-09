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

public class ServerNotifierHistory extends ServerNotifier implements Observer {
	private String reference_id;
	private String ae_id;
	private String ae_name;
	private String sensor_id;

	public ServerNotifierHistory(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}
	public ServerNotifierHistory(HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name, String sensor_id) {
		super(request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
		this.sensor_id = sensor_id;
		
		INManager.getObservableResource().addObserver(this);
		
		addHistoryObserver();
	}
	
	private void addHistoryObserver() {
		for(MNSubscriptionServer ss : INManager.getMNservers())
			ss.getResource().addHistoryObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	private void updateHistory(JSONObject json) {
		System.out.println(Thread.currentThread().getId() + " " + json.toJSONString());
		JSONObject data = null;
		JSONArray response = new JSONArray();
		
		if(!reference_id.equals((String) json.get("reference_id")) || !ae_id.equals((String) json.get("ae_id")) || !ae_name.equals((String) json.get("ae_name")) || !sensor_id.equals((String) json.get("sensor_id")))
			return;
			
		if(json.get("content") == null)
			response = new QueryManagerIN().getSensorHistory(reference_id, sensor_id);
		else
			response = new QueryManagerIN().getSensorHistory((InstanceResource) json.get("content"));
		
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
				addHistoryObserver();
			else if(arg instanceof JSONObject)
				updateHistory((JSONObject) arg);
			super.run();
		}
	}
}
