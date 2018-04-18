package servlets.assets.notifications;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManager;
import oneM2M.notifications.MNSubscriptionResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.subscriptions.SubscriptionServer;
import servlets.assets.notifications.ServerNotifier;
import servlets.assets.oneM2M.QueryManagerIN;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Marker;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierMarker extends ServerNotifier {

	public ServerNotifierMarker(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}

	protected void addObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).addMarkerObserver(this);
	}
	
	protected void deleteObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).deleteMarkerObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JSONObject json) {
		JSONObject data = null;
		JSONArray response = new JSONArray();
		ContainerResource container = null;
		InstanceResource instance = null;
		
		String reference_id = (String) json.get(JSMessage.REFERENCE_ID);
		String ae_id = (String) json.get(JSMessage.AE_ID);
		String ae_name = (String) json.get(JSMessage.AE_NAME);
		
		container = QueryManagerIN.getContainerById(ae_id);
		
		if(json.get(JSMessage.CONTENT) == null)
			instance = QueryManagerIN.getLastCI(container);
		else
			instance = (InstanceResource) json.get(JSMessage.CONTENT);
		
		if(json.get(JSMessage.CONTENT) == null && instance == null)
				data = QueryManagerIN.getMarkerData(reference_id, ae_id, ae_name);
		else if(json.get(JSMessage.CONTENT) == null && instance != null){
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
		else if(json.get(JSMessage.CONTENT) != null) {
			data = new JSONObject();
			
			data.put(JSMessage.REFERENCE_ID, reference_id);
			data.put(JSMessage.AE_ID, ae_id);
			data.put(JSMessage.AE_NAME, ae_name);
			
			json = null;
			try {
				if(instance != null) json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(json != null && json.get(Marker.LAT) != null) data.put(JSMessage.LAT, json.get(Marker.LAT));
			if(json != null && json.get(Marker.LNG) != null) data.put(JSMessage.LNG, json.get(Marker.LNG));
			if(json != null && json.get(Marker.RISK_MESSAGE) != null) data.put(JSMessage.RISK_MESSAGE, json.get(Marker.RISK_MESSAGE));
			if(json != null && json.get(Marker.RISK_LEVEL) != null) data.put(JSMessage.RISK_LEVEL, json.get(Marker.RISK_LEVEL));
			
			if(data.isEmpty())
				System.out.println(json.toJSONString());
		}
		
		if(data == null)
			System.out.println(json.toJSONString());
		
		if(data != null && !data.isEmpty())
			response.add(data);
		
		if(response.size() == 1 && response.get(0) == null)
			System.out.println(json.toJSONString());
		
		sendNotification(response);
	}
}
