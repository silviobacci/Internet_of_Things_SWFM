package servlets.assets.notifications;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import oneM2M.INManagerNotifier;
import oneM2M.resources.InstanceResource;
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
		INManagerNotifier.addHistoryObserver(this);
	}
	
	protected void deleteObserver() {
		INManagerNotifier.deleteHistoryObserver(this);
	}
	
	protected void update(JSONObject json) {
		if(!reference_id.equals((String) json.get(JSMessage.REFERENCE_ID)) || !ae_id.equals((String) json.get(JSMessage.AE_ID)) || !ae_name.equals((String) json.get(JSMessage.AE_NAME)) || !sensor_id.equals((String) json.get(JSMessage.SENSOR_ID)))
			return;
		
		JSONArray response = QueryManagerIN.getSensorHistory((InstanceResource) json.get(JSMessage.CONTENT));
		
		sendNotification(response);
	}
}
