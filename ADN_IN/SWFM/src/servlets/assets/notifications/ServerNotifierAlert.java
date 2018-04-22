package servlets.assets.notifications;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManagerNotifier;
import oneM2M.resources.InstanceResource;
import servlets.assets.notifications.ServerNotifier;
import servlets.assets.oneM2M.constants.Alert;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierAlert extends ServerNotifier {
	private String reference_id;
	private String ae_id;
	private String ae_name;

	public ServerNotifierAlert(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}

	public ServerNotifierAlert(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name) {
		super(father, request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
	}
	
	protected void addObserver() {
		INManagerNotifier.addAlertObserver(this);
	}
	
	protected void deleteObserver() {
		INManagerNotifier.deleteAlertObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JSONObject json) {
		if(!reference_id.equals((String) json.get(JSMessage.REFERENCE_ID)) || !ae_id.equals((String) json.get(JSMessage.AE_ID)) || !ae_name.equals((String) json.get(JSMessage.AE_NAME)))
			return;
		
		InstanceResource instance = (InstanceResource) json.get(JSMessage.CONTENT);
			
		json = null;
		try {
			if(instance != null) json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject data = new JSONObject();
	
		if(json != null && json.get(Alert.RISK_LEVEL) != null) data.put(JSMessage.RISK_LEVEL, json.get(Alert.RISK_LEVEL));
		if(json != null && json.get(Alert.RISK_MESSAGE) != null) data.put(JSMessage.RISK_MESSAGE, json.get(Alert.RISK_MESSAGE));
		
		JSONArray response = new JSONArray();
		
		if(!data.isEmpty())
			response.add(data);

		sendNotification(response);
	}
}
