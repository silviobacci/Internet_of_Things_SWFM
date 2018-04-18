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
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Sensor;
import servlets.backend.getters.AsyncContextInterface;

public class ServerNotifierSensor extends ServerNotifier {
	private String reference_id;
	private String ae_id;
	private String ae_name;

	public ServerNotifierSensor(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response) {
		super(father, request, response);
	}
	
	public ServerNotifierSensor(AsyncContextInterface father, HttpServletRequest request, HttpServletResponse response, String reference_id, String ae_id, String ae_name) {
		super(father, request, response);
		
		this.reference_id = reference_id;
		this.ae_id = ae_id;
		this.ae_name = ae_name;
	}
	
	protected void addObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).addSensorObserver(this);
	}
	
	protected void deleteObserver() {
		for(SubscriptionServer ss : INManager.getMNservers())
			((MNSubscriptionResource) ss.getResource()).deleteSensorObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(JSONObject json) {
		JSONObject data = null;
		JSONArray response = new JSONArray();
		
		if(!reference_id.equals((String) json.get(JSMessage.REFERENCE_ID)) || !ae_id.equals((String) json.get(JSMessage.AE_ID)) || !ae_name.equals((String) json.get(JSMessage.AE_NAME)))
			return;
		
		String sensor_id = (String) json.get(JSMessage.SENSOR_ID);
		String sensor_name = (String) json.get(JSMessage.SENSOR_NAME);
		
		if(json.get(JSMessage.CONTENT) == null && sensor_id == null)
			response = QueryManagerIN.getSensorData(reference_id, ae_id, ae_name);
		else if(json.get(JSMessage.CONTENT) == null && sensor_id != null)
			response = QueryManagerIN.getSensorData(reference_id, sensor_id);
		else if(json.get(JSMessage.CONTENT) != null && sensor_id != null) {
			data = new JSONObject();
			
			data.put(JSMessage.SENSOR_ID, sensor_id);
			data.put(JSMessage.SENSOR_NAME, sensor_name);
			
			InstanceResource instance = (InstanceResource) json.get(JSMessage.CONTENT);
			
			data.put(JSMessage.CREATION_TIME, QueryManagerIN.getDate(instance.getCt()).getTime());
			
			json = null;
			try {
				if(instance != null) json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(json != null && json.get(Sensor.LAT) != null) data.put(JSMessage.LAT, json.get(Sensor.LAT));
			if(json != null && json.get(Sensor.LNG) != null) data.put(JSMessage.LNG, json.get(Sensor.LNG));
			if(json != null && json.get(Sensor.MIN) != null) data.put(JSMessage.MIN, json.get(Sensor.MIN));
			if(json != null && json.get(Sensor.MAX) != null) data.put(JSMessage.MAX, json.get(Sensor.MAX));
			if(json != null && json.get(Sensor.THRESHOLD) != null) data.put(JSMessage.THRESHOLD, json.get(Sensor.THRESHOLD));
			if(json != null && json.get(Sensor.WATER_LEVEL) != null) data.put(JSMessage.WATER_LEVEL, json.get(Sensor.WATER_LEVEL));
			if(json != null && json.get(Sensor.SENSOR_IS_WORKING) != null) data.put(JSMessage.IS_WORKING, json.get(Sensor.SENSOR_IS_WORKING));
			
			if(!data.isEmpty())
				response.add(data);
		}
		
		if(response.size() == 1 && response.get(0) == null)
			System.out.println(json.toJSONString());
		
		sendNotification(response);
	}
}
