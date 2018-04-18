package servlets.assets.notifications;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import oneM2M.INManager;
import oneM2M.notifications.MNSubscriptionResource;
import oneM2M.resources.InstanceResource;
import oneM2M.subscriptions.SubscriptionServer;
import servlets.assets.notifications.ServerNotifier;
import servlets.assets.oneM2M.QueryManagerIN;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierHistory extends ServerNotifier {
	private String reference_id;
	private String ae_id;
	private String ae_name;
	private String sensor_id;

	public ServerNotifierHistory(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}
	
	public ServerNotifierHistory(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name, String sensor_id) {
		super(father, request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
		this.sensor_id = sensor_id;
	}
	
	protected void addObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).addHistoryObserver(this);
	}
	
	protected void deleteObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).deleteHistoryObserver(this);
	}
	
	protected void update(JSONObject json) {
		System.out.println(Thread.currentThread().getId() + " " + json.toJSONString());
		JSONArray response = new JSONArray();
		
		if(!reference_id.equals((String) json.get(JSMessage.REFERENCE_ID)) || !ae_id.equals((String) json.get(JSMessage.AE_ID)) || !ae_name.equals((String) json.get(JSMessage.AE_NAME)) || !sensor_id.equals((String) json.get(JSMessage.SENSOR_ID)))
			return;
			
		if(json.get(JSMessage.CONTENT) == null)
			response = QueryManagerIN.getSensorHistory(reference_id, sensor_id);
		else
			response = QueryManagerIN.getSensorHistory((InstanceResource) json.get(JSMessage.CONTENT));
		
		if(response.size() == 1 && response.get(0) == null)
			System.out.println(json.toJSONString());
		
		sendNotification(response);
	}
}
