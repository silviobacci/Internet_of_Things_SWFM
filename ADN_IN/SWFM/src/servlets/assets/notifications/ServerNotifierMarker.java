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
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Marker;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierMarker extends ServerNotifier {

	public ServerNotifierMarker(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}

	protected void addObserver() {
		INManagerNotifier.addMarkerObserver(this);
	}
	
	protected void deleteObserver() {
		INManagerNotifier.deleteMarkerObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JSONObject json) {
		String reference_id = (String) json.get(JSMessage.REFERENCE_ID);
		String ae_id = (String) json.get(JSMessage.AE_ID);
		String ae_name = (String) json.get(JSMessage.AE_NAME);
		
		JSONObject data = new JSONObject();
			
		data.put(JSMessage.REFERENCE_ID, reference_id);
		data.put(JSMessage.AE_ID, ae_id);
		data.put(JSMessage.AE_NAME, ae_name);
		
		InstanceResource instance = (InstanceResource) json.get(JSMessage.CONTENT);
		
		json = null;
		try {
			if(instance != null) json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(json != null && json.get(Marker.LAT) != null) data.put(JSMessage.LAT, json.get(Marker.LAT));
		if(json != null && json.get(Marker.LNG) != null) data.put(JSMessage.LNG, json.get(Marker.LNG));
		
		JSONArray response = new JSONArray();
		
		if(!data.isEmpty())
			response.add(data);
		
		sendNotification(response);
	}
}
