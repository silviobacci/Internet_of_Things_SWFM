package assets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.OM2MManager;
import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;
import resources.ReferenceResource;

@SuppressWarnings("unchecked")
public class QueryManagerIN {
	public static final String GPS = "GPS";
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String SENSORS = "SENSORS";
	public static final String DAMS = "DAMS";
	public static final String LEVEL = "LEVEL";
	public static final String THRESHOLD = "THRESHOLD";
	public static final String STATE = "STATE";
	public static final String DEFAULT_CNF = "CHANGED BY ";
	private static final String IP_ADDRESS_OM2M = "127.0.0.1";
	private static final boolean IN = false;
	private OM2MManager mng;

	public QueryManagerIN() {
		mng = new OM2MManager(IP_ADDRESS_OM2M);
	}
	
	private ReferenceResource getRemoteCSEbyId(String id) {
		return mng.getReference(IN, id);
	}
	
	private ArrayList<OM2MResource> getSonContainers(String reference_id, String father_name) {
		ArrayList<String> filters = new ArrayList<String>();
		filters.add("lbl=" + reference_id);
		filters.add("lbl=" + father_name);
		ArrayList<OM2MResource> containers = mng.discovery(IN, OM2MManager.CONTAINER, filters);
		
		if(containers == null || containers.isEmpty())
			return null;
		
		return containers;
	}
	
	private ContainerResource getSonContainerByName(String reference_id, String father_name, String name) {
		ArrayList<OM2MResource> containers = getSonContainers(reference_id, father_name);
		
		if(containers == null)
			return null;
		
		containers = mng.getResourcesByName(containers, name);
		
		if(containers == null || containers.isEmpty() || containers.size() != 1)
			return null;
		
		return (ContainerResource) containers.get(0);
	}
	
	public ContainerResource getFatherContainerById(String id) {
		ContainerResource container = getContainerById(id);
		
		if(container == null)
			return null;
		
		return mng.getContainer(IN, container.getPi());
	}
	
	public ContainerResource getContainerById(String id) {
		return mng.getContainer(IN, id);
	}
	
	public InstanceResource getLastCI(ContainerResource container) {
		return mng.getContentInstance(IN, container.getLa());
	}
	
	private JSONObject getSensorDataById(String reference_id, ContainerResource sensor) {
		ContainerResource gps_container = null;
		ContainerResource th_container = null;
		ContainerResource level_container = null;
		
		InstanceResource gps = null;
		InstanceResource th = null;
		InstanceResource level = null;
				
		gps_container = getSonContainerByName(reference_id, sensor.getRn(), GPS);
		th_container = getSonContainerByName(reference_id, sensor.getRn(), THRESHOLD);
		level_container = getSonContainerByName(reference_id, sensor.getRn(), LEVEL);
		
		if(gps_container != null) gps = getLastCI(gps_container);
		if(th_container != null) th = getLastCI(th_container);
		if(level_container != null) level = getLastCI(level_container);
		
		JSONObject gps_json = null, th_json = null;
		try {
			if(gps != null) gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
			if(th != null) th_json = (JSONObject) JSONValue.parseWithException(th.getCon().toString().replace("'", "\""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject data = null;
		
		if(gps_json != null && th_json != null && level != null) {
			data = new JSONObject();
			data.put("sensor_id", sensor.getRi());
			data.put("lat", gps_json.get("LAT_SENSOR"));
			data.put("lng", gps_json.get("LNG_SENSOR"));
			data.put("level", Integer.parseInt(level.getCon().toString()));
			data.put("min", th_json.get("MIN"));
			data.put("max", th_json.get("MAX"));
			data.put("th", th_json.get("TH"));
		}
		
		return data;
	}
	
	private JSONObject getDamDataById(String reference_id, ContainerResource dam) {
		ContainerResource gps_container = null;
		ContainerResource state_container = null;
		
		InstanceResource gps = null;
		InstanceResource state = null;
		
		gps_container = getSonContainerByName(reference_id, dam.getRn(), GPS);
		state_container = getSonContainerByName(reference_id, dam.getRn(), STATE);
		
		if(gps_container != null) gps = getLastCI(gps_container);
		if(state_container != null) state = getLastCI(state_container);
		
		JSONObject gps_json = null;
		try {
			if(gps != null) gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject data = null;
		
		if(gps_json != null && state != null) {
			data = new JSONObject();
			data.put("dam_id", dam.getRi());
			data.put("lat", gps_json.get("LAT_DAM"));
			data.put("lng", gps_json.get("LNG_DAM"));
			data.put("state", Boolean.parseBoolean(state.getCon().toString()));
		}
		
		return data;
	}
	
	private JSONArray getSensorData(String reference_id, ContainerResource sensors) {
		ArrayList<OM2MResource> sons = getSonContainers(reference_id, sensors.getRn());
		
		if(sons == null || sons.isEmpty())
			return null;
		
		JSONArray response = new JSONArray();
		
		for(OM2MResource s : sons) {
			JSONObject data = getSensorDataById(reference_id, (ContainerResource) s);
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	private JSONArray getDamData(String reference_id, ContainerResource dams) {
		ArrayList<OM2MResource> sons = getSonContainers(reference_id, dams.getRn());
		
		if(sons == null || sons.isEmpty())
			return null;
		
		JSONArray response = new JSONArray();
		
		for(OM2MResource s : sons) {
			JSONObject data = getDamDataById(reference_id, (ContainerResource) s);
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	private JSONObject getSensorHistoryData(InstanceResource value) {
		SimpleDateFormat d_format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		
		Date current_time = null;
		
		try {
			current_time = d_format.parse(value.getCt());
		} 
		catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		if(current_time == null)
			return null;
		
		JSONObject data = new JSONObject();
			
		data.put("x", current_time.getTime());
		data.put("y", Integer.parseInt(value.getCon().toString()));
			
		return data;
	}
	
	private JSONArray getSensorHistoryData(String reference_id, ContainerResource level) {
		String filter = "lbl=" + reference_id + "+" + level.getRn();
		ArrayList<OM2MResource> values = mng.discovery(IN, OM2MManager.CONTENT_INSTANCE, filter);
		
		if(values == null || values.isEmpty())
			return null;	
		
		Collections.sort(values);
		
		JSONArray response = new JSONArray();
		
		for (OM2MResource value : values) {
			InstanceResource v = (InstanceResource) value;
			
			JSONObject data = getSensorHistoryData(v);
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	private boolean setData(ReferenceResource reference, ContainerResource resource, ContainerResource container, Object data, String admin_name) {
		if(container == null || reference == null)
			return false;
		
		String containerMN_id = null;
		for(String label : container.getLbl())
			if(!label.equals(reference.getRi()) && !label.equals(resource.getRn()))
				containerMN_id = label;
		
		if(containerMN_id == null)
			return false;
		
		container = mng.getBridgedContainer(IN, reference.getCsi(), containerMN_id);
		
		JSONObject json = mng.jsonCI(DEFAULT_CNF + admin_name, data, container.getRi());
		if(mng.createBridgedContentInstance(IN, reference.getCsi(), container.getRi(), json) == null)
			return false;
		
		return true;
	}
	
	public JSONObject getMarkerData(String reference_id, String ae_id, String ae_name) {
		JSONObject gps_json = null, state_json = null, data = null;
		
		ContainerResource gps_container = getSonContainerByName(reference_id, ae_name, GPS);
		ContainerResource state_container = getSonContainerByName(reference_id, ae_name, STATE);
		
		if(gps_container == null || state_container == null)
			return null;
		
		InstanceResource gps = getLastCI(gps_container);
		InstanceResource state = getLastCI(state_container);
	
		try {
			if(gps != null) gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
			if(state != null) state_json = (JSONObject) JSONValue.parseWithException(state.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(gps_json != null && state_json != null) {
			data = new JSONObject();
			data.put("reference_id", reference_id);
			data.put("ae_id", ae_id);
			data.put("ae_name", ae_name);
			data.put("lat", gps_json.get("LAT_AE"));
			data.put("lng", gps_json.get("LNG_AE"));
			data.put("level", state_json.get("LEVEL"));
			data.put("message", state_json.get("MESSAGE"));
		}
		
		return data;
	}
	
	public JSONArray getMarkerData() {
		ArrayList<OM2MResource> copiedMN = mng.discovery(IN, OM2MManager.AE);
		
		if(copiedMN == null || copiedMN.isEmpty())
			return null;
		
		JSONArray response = new JSONArray();
		
		for (OM2MResource mn : copiedMN) {
			AEResource m = (AEResource) mn;
			JSONObject data = null;
			
			ArrayList<OM2MResource> copiedAE = getSonContainers(m.getLbl().get(0), m.getRn());
			
			if(copiedAE != null && !copiedAE.isEmpty() && copiedAE.size() == 1)
				data = getMarkerData(m.getLbl().get(0), copiedAE.get(0).getRi(), copiedAE.get(0).getRn());
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	public JSONArray getSensorData(String reference_id, String ae_id, String ae_name) {
		ContainerResource sensors = getSonContainerByName(reference_id, ae_name, SENSORS);
		return getSensorData(reference_id, sensors);
	}
	
	public JSONArray getSensorData(String reference_id, String sensor_id) {
		ContainerResource sensor = getContainerById(sensor_id);
		
		JSONArray response = new JSONArray();
		response.add(getSensorDataById(reference_id, sensor));
		
		return response;
	}
	
	public JSONArray getSensorHistory(String reference_id, String sensor_id) {
		ContainerResource sensor = getContainerById(sensor_id);
		ContainerResource level = getSonContainerByName(reference_id, sensor.getRn(), LEVEL);
		
		return getSensorHistoryData(reference_id, level);
	}
	
	public JSONArray getSensorHistory(InstanceResource value) {
		JSONArray response = new JSONArray();
		response.add(getSensorHistoryData(value));
		
		return response;
	}
	
	public JSONArray getDamData(String reference_id, String ae_id, String ae_name) {
		ContainerResource dams = getSonContainerByName(reference_id, ae_name, DAMS);
		return getDamData(reference_id, dams);
	}
	
	public JSONArray getDamData(String reference_id, String dam_id) {
		ContainerResource dam = getContainerById(dam_id);
		
		JSONArray response = new JSONArray();
		response.add(getDamDataById(reference_id, dam));
		
		return response;
	}
	
	public boolean setDamData(String reference_id, String ae_id, String ae_name, String dam_id, Object data, String admin_name) {
		ContainerResource dam = getContainerById(dam_id);
		ContainerResource state = getSonContainerByName(reference_id, dam.getRn(), STATE);
		ReferenceResource reference = getRemoteCSEbyId(reference_id);
		
		return setData(reference, dam, state, data, admin_name);
	}
	
	public boolean setSensorData(String reference_id, String ae_id, String ae_name, String sensor_id, Object data, String admin_name) {
		ContainerResource sensor = getContainerById(sensor_id);
		ContainerResource threshold = getSonContainerByName(reference_id, sensor.getRn(), THRESHOLD);
		ReferenceResource reference = getRemoteCSEbyId(reference_id);
		
		return setData(reference, sensor, threshold, data, admin_name);
	}
}
