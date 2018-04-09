package assets;

import java.util.Observable;
import java.util.Observer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import assets.ServerNotifier;
import oneM2M.INManager;
import oneM2M.MNSubscriptionServer;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.ReferenceResource;

public class ServerNotifierMarker extends ServerNotifier implements Observer {

	public ServerNotifierMarker(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
		
		INManager.getObservableResource().addObserver(this);
		
		addMarkerObserver();
	}

	private void addMarkerObserver() {
		for(MNSubscriptionServer ss : INManager.getMNservers())
			ss.getResource().addMarkerObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	private void updateMarker(JSONObject json) {
		System.out.println(Thread.currentThread().getId() + " " + json.toJSONString());
		JSONObject data = null;
		JSONArray response = new JSONArray();
		ContainerResource container = null;
		InstanceResource instance = null;
		
		String reference_id = (String) json.get("reference_id");
		String ae_id = (String) json.get("ae_id");
		String ae_name = (String) json.get("ae_name");
		
		container = new QueryManagerIN().getContainerById(ae_id);
		System.out.println(Thread.currentThread().getId() + " CONTAINER: " + container.getRn());
		
		if(json.get("content") == null) {
			instance = new QueryManagerIN().getLastCI(container);

			if(instance == null)
				data = new QueryManagerIN().getMarkerData(reference_id, ae_id, ae_name);
			else {
				System.out.println(Thread.currentThread().getId() + " INSTANCE: " + instance.getRn());
				
				data = new JSONObject();
				
				JSONObject con = null;
				try {
					con = (JSONObject) (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
				} 
				catch (ParseException e) {
					e.printStackTrace();
				}
				
				if(con != null)
					for(Object key : con.keySet())
						data.put(key, con.get(key));
			}
		}
		else {
			data = new JSONObject();
			
			data.put("reference_id", reference_id);
			data.put("ae_id", ae_id);
			data.put("ae_name", ae_name);
			
			instance = (InstanceResource) json.get("content");
			json = (JSONObject) instance.getCon();
			
			if(json.get("LAT_AE") != null) data.put("lat", json.get("LAT_AE"));
			if(json.get("LNG_AE") != null) data.put("lng", json.get("LNG_AE"));
			if(json.get("MESSAGE") != null) data.put("message", json.get("MESSAGE"));
			if(json.get("LEVEL") != null) data.put("level", json.get("LEVEL"));
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
				addMarkerObserver();
			else if(arg instanceof JSONObject)
				updateMarker((JSONObject) arg);
			super.run();
		}
	}
}
