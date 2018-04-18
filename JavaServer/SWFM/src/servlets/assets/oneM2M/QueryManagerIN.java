package servlets.assets.oneM2M;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.INManager;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MManager;
import oneM2M.utilities.OM2MPayloader;
import oneM2M.utilities.OM2MUtilities;
import servlets.assets.oneM2M.constants.Dam;
import servlets.assets.oneM2M.constants.JSMessage;
import servlets.assets.oneM2M.constants.Marker;
import servlets.assets.oneM2M.constants.OneM2M;
import servlets.assets.oneM2M.constants.Sensor;

@SuppressWarnings("unchecked")
public class QueryManagerIN {
	private static OM2MManager manager;
	
	public static void init() {
		manager = INManager.getManager();
	}
	
	private static ReferenceResource getRemoteCSEbyId(String id) {
		return manager.getter.getReference(id);
	}
	
	private static ArrayList<OM2MResource> getSonContainers(String reference_id, String father_name) {
		ArrayList<String> filters = OM2MUtilities.createFilters(reference_id, father_name);
		ArrayList<OM2MResource> containers = manager.discoverer.discoveryContainer(filters);
		
		if(containers == null || containers.isEmpty())
			return null;
		
		return containers;
	}
	
	private static ContainerResource getSonContainerByName(String reference_id, String father_name, String name) {
		ArrayList<OM2MResource> containers = getSonContainers(reference_id, father_name);
		
		if(containers == null)
			return null;
		
		containers = OM2MUtilities.getResourcesByName(containers, name);
		
		if(containers == null || containers.isEmpty() || containers.size() != 1)
			return null;
		
		return (ContainerResource) containers.get(0);
	}
	
	public static ContainerResource getFatherContainerById(String id) {
		ContainerResource container = getContainerById(id);
		
		if(container == null)
			return null;
		
		return manager.getter.getContainer(container.getPi());
	}
	
	public static ContainerResource getContainerById(String id) {
		return manager.getter.getContainer(id);
	}
	
	public static InstanceResource getLastCI(ContainerResource container) {
		return manager.getter.getContentInstance(container.getLa());
	}
	
	private static JSONObject getSensorDataById(String reference_id, ContainerResource sensor) {
		ContainerResource gps_container = null;
		ContainerResource th_container = null;
		ContainerResource level_container = null;
		
		InstanceResource is_working = getLastCI(sensor);
		InstanceResource gps = null;
		InstanceResource th = null;
		InstanceResource level = null;
				
		gps_container = getSonContainerByName(reference_id, sensor.getRn(), OneM2M.GPS);
		th_container = getSonContainerByName(reference_id, sensor.getRn(), OneM2M.THRESHOLD);
		level_container = getSonContainerByName(reference_id, sensor.getRn(), OneM2M.LEVEL);
		
		if(gps_container != null) gps = getLastCI(gps_container);
		if(th_container != null) th = getLastCI(th_container);
		if(level_container != null) level = getLastCI(level_container);
		
		JSONObject gps_json = null, th_json = null, is_working_json = null, level_json = null;
		try {
			if(gps != null) gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
			if(th != null) th_json = (JSONObject) JSONValue.parseWithException(th.getCon().toString().replace("'", "\""));
			if(is_working != null) is_working_json = (JSONObject) JSONValue.parseWithException(is_working.getCon().toString().replace("'", "\""));
			if(level != null) level_json = (JSONObject) JSONValue.parseWithException(level.getCon().toString().replace("'", "\""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject data = new JSONObject();
		data.put(JSMessage.SENSOR_ID, sensor.getRi());
		data.put(JSMessage.SENSOR_NAME, sensor.getRn());
		
		if(gps_json != null) data.put(JSMessage.LAT, gps_json.get(Sensor.LAT));
		if(gps_json != null) data.put(JSMessage.LNG, gps_json.get(Sensor.LNG));
		if(level_json != null) data.put(JSMessage.WATER_LEVEL, level_json.get(Sensor.WATER_LEVEL));
		if(level_json != null) data.put(JSMessage.CREATION_TIME, getDate(level.getCt()).getTime());
		if(th_json != null) data.put(JSMessage.MIN, th_json.get(Sensor.MIN));
		if(th_json != null) data.put(JSMessage.MAX, th_json.get(Sensor.MAX));
		if(th_json != null) data.put(JSMessage.THRESHOLD, th_json.get(Sensor.THRESHOLD));
		if(is_working_json != null) data.put(JSMessage.IS_WORKING, is_working_json.get(Sensor.SENSOR_IS_WORKING));
		
		return data;
	}
	
	private static JSONObject getDamDataById(String reference_id, ContainerResource dam) {
		ContainerResource gps_container = null;
		ContainerResource state_container = null;
		
		InstanceResource is_working = getLastCI(dam);
		InstanceResource gps = null;
		InstanceResource state = null;
		
		gps_container = getSonContainerByName(reference_id, dam.getRn(), OneM2M.GPS);
		state_container = getSonContainerByName(reference_id, dam.getRn(), OneM2M.STATE);
		
		if(gps_container != null) gps = getLastCI(gps_container);
		if(state_container != null) state = getLastCI(state_container);
		
		JSONObject gps_json = null, is_working_json = null, state_json = null;
		try {
			if(gps != null) gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
			if(is_working != null) is_working_json = (JSONObject) JSONValue.parseWithException(is_working.getCon().toString().replace("'", "\""));
			if(state != null) state_json = (JSONObject) JSONValue.parseWithException(state.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject data = new JSONObject();
		
		data.put(JSMessage.DAM_ID, dam.getRi());
		data.put(JSMessage.DAM_NAME, dam.getRn());
		
		if(gps_json != null) data.put(JSMessage.LAT, gps_json.get(Dam.LAT));
		if(gps_json != null) data.put(JSMessage.LNG, gps_json.get(Dam.LNG));
		if(state_json != null) data.put(JSMessage.STATE, state_json.get(Dam.STATE));
		if(state_json != null) data.put(JSMessage.CREATION_TIME, getDate(state.getCt()).getTime());
		if(is_working_json != null) data.put(JSMessage.IS_WORKING, is_working_json.get(Dam.DAM_IS_WORKING));
		
		return data;
	}
	
	private static JSONArray getSensorData(String reference_id, ContainerResource sensors) {
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
	
	private static JSONArray getDamData(String reference_id, ContainerResource dams) {
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
	
	private static JSONObject getSensorHistoryData(InstanceResource value) {
		Date current_time = getDate(value.getCt());
		
		if(current_time == null)
			return null;
		
		JSONObject data = new JSONObject();
		
		JSONObject level_json = null;
		
		try {
			if(value != null) level_json = (JSONObject) JSONValue.parseWithException(value.getCon().toString().replace("'", "\""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(level_json == null)
			return null;
			
		data.put(JSMessage.HISTORY_TIME, current_time.getTime());
		data.put(JSMessage.HISTORY_LEVEL, level_json.get(Sensor.WATER_LEVEL));
			
		return data;
	}
	
	private static JSONArray getSensorHistoryData(String reference_id, ContainerResource level) {
		ArrayList<String> filters = OM2MUtilities.createFilters(reference_id, level.getRi(), level.getRn());
		ArrayList<OM2MResource> values = manager.discoverer.discoveryContentInstance(filters);
		
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
	
	private static boolean setDamData(ReferenceResource reference, ContainerResource resource, ContainerResource container, JSONObject data, String admin_name) {
		if(container == null || reference == null)
			return false;
		
		String containerMN_id = null;
		for(String label : container.getLbl())
			if(!label.equals(reference.getRi()) && !label.equals(resource.getRn()))
				containerMN_id = label;
		
		if(containerMN_id == null)
			return false;
		
		container = manager.getter.getBridgedContainer(reference.getCsi(), containerMN_id);
		
		ArrayList<String> labels = OM2MUtilities.createLabels(container.getRi());
		JSONObject json = OM2MPayloader.jsonContentInstance(OneM2M.DEFAULT_CNF + admin_name, data.toJSONString().replace("\"", "'"), labels);
		if(manager.creater.createBridgedContentInstance(reference.getCsi(), container.getRi(), json) == null)
			return false;
		
		return true;
	}
	
	public static Date getDate(String date) {
		Date current_time = null;
		
		try {
			current_time = new SimpleDateFormat(JSMessage.DATE_FORMAT).parse(date);
		} 
		catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		if(current_time == null)
			return null;
		
		return current_time;
	}
	
	private static boolean setSensorData(ReferenceResource reference, ContainerResource resource, ContainerResource container, JSONObject data, String admin_name) {
		if(container == null || reference == null)
			return false;
		
		String containerMN_id = null;
		for(String label : container.getLbl())
			if(!label.equals(reference.getRi()) && !label.equals(resource.getRn()))
				containerMN_id = label;
		
		if(containerMN_id == null)
			return false;
		
		container = manager.getter.getBridgedContainer(reference.getCsi(), containerMN_id);
		
		ArrayList<String> labels = OM2MUtilities.createLabels(container.getRi());
		JSONObject json = OM2MPayloader.jsonContentInstance(OneM2M.DEFAULT_CNF + admin_name, data.toJSONString().replace("\"", "'"), labels);
		if(manager.creater.createBridgedContentInstance(reference.getCsi(), container.getRi(), json) == null)
			return false;
		
		return true;
	}
	
	public static JSONObject getMarkerData(String reference_id, String ae_id, String ae_name) {
		JSONObject gps_json = null, state_json = null;
		
		ContainerResource gps_container = getSonContainerByName(reference_id, ae_name, OneM2M.GPS);
		ContainerResource state_container = getSonContainerByName(reference_id, ae_name, OneM2M.STATE);
		
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
		
		JSONObject data = new JSONObject();
		
		data.put(JSMessage.REFERENCE_ID, reference_id);
		data.put(JSMessage.AE_ID, ae_id);
		data.put(JSMessage.AE_NAME, ae_name);

		if(gps_json != null) data.put(JSMessage.LAT, gps_json.get(Marker.LAT));
		if(gps_json != null) data.put(JSMessage.LNG, gps_json.get(Marker.LNG));
		if(state_json != null) data.put(JSMessage.RISK_LEVEL, state_json.get(Marker.RISK_LEVEL));
		if(state_json != null) data.put(JSMessage.RISK_MESSAGE, state_json.get(Marker.RISK_MESSAGE));
		
		return data;
	}
	
	public static JSONArray getMarkerData() {
		ArrayList<OM2MResource> copiedMN = manager.discoverer.discoveryAE();
		
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
	
	public static JSONArray getSensorData(String reference_id, String ae_id, String ae_name) {
		ContainerResource sensors = getSonContainerByName(reference_id, ae_name, OneM2M.SENSORS);
		return getSensorData(reference_id, sensors);
	}
	
	public static JSONArray getSensorData(String reference_id, String sensor_id) {
		ContainerResource sensor = getContainerById(sensor_id);
		
		JSONArray response = new JSONArray();
		response.add(getSensorDataById(reference_id, sensor));
		
		return response;
	}
	
	public static JSONArray getSensorHistory(String reference_id, String sensor_id) {
		ContainerResource sensor = getContainerById(sensor_id);
		ContainerResource level = getSonContainerByName(reference_id, sensor.getRn(), OneM2M.LEVEL);
		
		return getSensorHistoryData(reference_id, level);
	}
	
	public static JSONArray getSensorHistory(InstanceResource value) {
		JSONArray response = new JSONArray();
		response.add(getSensorHistoryData(value));
		
		return response;
	}
	
	public static JSONArray getDamData(String reference_id, String ae_id, String ae_name) {
		ContainerResource dams = getSonContainerByName(reference_id, ae_name, OneM2M.DAMS);
		return getDamData(reference_id, dams);
	}
	
	public static JSONArray getDamData(String reference_id, String dam_id) {
		ContainerResource dam = getContainerById(dam_id);
		
		JSONArray response = new JSONArray();
		response.add(getDamDataById(reference_id, dam));
		
		return response;
	}
	
	public static boolean setDamData(String reference_id, String ae_id, String ae_name, String dam_id, Object data, String admin_name) {
		ContainerResource dam = getContainerById(dam_id);
		ContainerResource state = getSonContainerByName(reference_id, dam.getRn(), OneM2M.STATE);
		ReferenceResource reference = getRemoteCSEbyId(reference_id);
		JSONObject content = new JSONObject();
		content.put(Dam.STATE, data);
		
		return setDamData(reference, dam, state, content, admin_name);
	}
	
	public static boolean setSensorData(String reference_id, String ae_id, String ae_name, String sensor_id, Object data, String admin_name) {
		ContainerResource sensor = getContainerById(sensor_id);
		ContainerResource threshold = getSonContainerByName(reference_id, sensor.getRn(), OneM2M.THRESHOLD);
		ReferenceResource reference = getRemoteCSEbyId(reference_id);
		
		return setSensorData(reference, sensor, threshold, (JSONObject) data, admin_name);
	}
}
