package servlets.assets.notifications;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManager;
import oneM2M.notifications.MNSubscriptionResource;
import oneM2M.resources.InstanceResource;
import oneM2M.subscriptions.SubscriptionServer;
import servlets.assets.notifications.ServerNotifier;
import servlets.assets.oneM2M.QueryManagerIN;
import servlets.assets.oneM2M.constants.Dam;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierDam extends ServerNotifier {
	private String reference_id;
	private String ae_id;
	private String ae_name;

	public ServerNotifierDam(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}

	public ServerNotifierDam(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name) {
		super(father, request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
	}
	
	protected void addObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).addDamObserver(this);
	}
	
	protected void deleteObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).deleteDamObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JSONObject json) {
		JSONObject data = null;
		JSONArray response = new JSONArray();
		
		if(!reference_id.equals((String) json.get(JSMessage.REFERENCE_ID)) || !ae_id.equals((String) json.get(JSMessage.AE_ID)) || !ae_name.equals((String) json.get(JSMessage.AE_NAME)))
			return;
		
		String dam_id = (String) json.get(JSMessage.DAM_ID);
		String dam_name = (String) json.get(JSMessage.DAM_NAME);
		
		if(json.get(JSMessage.CONTENT) == null && dam_id == null)
			response = QueryManagerIN.getSensorData(reference_id, ae_id, ae_name);
		else if(json.get(JSMessage.CONTENT) == null && dam_id != null)
			response = QueryManagerIN.getSensorData(reference_id, dam_id);
		else if(json.get(JSMessage.CONTENT) != null && dam_id != null) {
			data = new JSONObject();
			
			data.put(JSMessage.DAM_ID, dam_id);
			data.put(JSMessage.DAM_NAME, dam_name);
			
			InstanceResource instance = (InstanceResource) json.get(JSMessage.CONTENT);
			
			data.put(JSMessage.CREATION_TIME, QueryManagerIN.getDate(instance.getCt()).getTime());
			
			json = null;
			try {
				if(instance != null) json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
		
			if(json != null && json.get(Dam.LAT) != null) data.put(JSMessage.LAT, json.get(Dam.LAT));
			if(json != null && json.get(Dam.LNG) != null) data.put(JSMessage.LNG, json.get(Dam.LNG));
			if(json != null && json.get(Dam.DAM_IS_WORKING) != null) data.put(JSMessage.IS_WORKING, json.get(Dam.DAM_IS_WORKING));
			if(json != null && json.get(Dam.STATE) != null) data.put(JSMessage.STATE, json.get(Dam.STATE));
			
			if(!data.isEmpty())
				response.add(data);
			else
				System.out.println(json.toJSONString());
		}
		
		if(response.size() == 1 && response.get(0) == null)
			System.out.println(json.toJSONString());

		sendNotification(response);
	}
}
