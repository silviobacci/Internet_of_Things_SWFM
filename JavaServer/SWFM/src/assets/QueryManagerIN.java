package assets;

import java.util.ArrayList;

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
import resources.SubscriptionResource;

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
	
	private ArrayList<OM2MResource> getMNReference() {
		ArrayList<OM2MResource> references = mng.discovery(IN, OM2MManager.REMOTE_CSE);
		
		if(references == null || references.isEmpty())
			return null;
		
		return references;
	}
	
	private ReferenceResource getMNReference(AEResource copiedMN) {
		ArrayList<OM2MResource> references = getMNReference();
		
		if(references == null)
			return null;
		
		references = mng.getResourcesById(references, copiedMN.getRn());
		
		if(references == null || references.isEmpty() || references.size() != 1)
			return null;
		
		return (ReferenceResource) references.get(0);
	}
	
	private ReferenceResource getMNReference(ContainerResource copiedAE) {
		ArrayList<OM2MResource> copiedMN = getCopiedMN();
		
		if(copiedMN == null || copiedMN.isEmpty())
			return null;
		
		copiedMN = mng.getResourcesById(copiedMN, copiedAE.getPi());
		
		if(copiedMN == null || copiedMN.isEmpty() || copiedMN.size() != 1)
			return null;
		
		return getMNReference((AEResource) copiedMN.get(0));
	}
	
	private ArrayList<OM2MResource> getCopiedMN() {
		return mng.discovery(IN, OM2MManager.AE);
	}
	
	private ArrayList<OM2MResource> getCopiedAE() {
		ArrayList<OM2MResource> copiedMN = getCopiedMN();
		
		if(copiedMN == null || copiedMN.isEmpty())
			return null;
		
		ArrayList<OM2MResource> copiedAE = new ArrayList<OM2MResource>();
		
		for (OM2MResource mn : copiedMN) {
			ArrayList<String> f = new ArrayList<String>();
			f.add("lbl=" + mn.getRi());
			
			ArrayList<OM2MResource> tmp = mng.discovery(IN, OM2MManager.CONTAINER, f);
			
			for(OM2MResource t : tmp)
				copiedAE.add(t);
		}
		
		if(copiedAE == null || copiedAE.isEmpty())
			return null;
		
		return copiedAE;
	}
	
	private ContainerResource getCopiedAE(String id) {
		ArrayList<OM2MResource> copiedAE = getCopiedAE();
		
		if(copiedAE == null || copiedAE.isEmpty())
			return null;
		
		copiedAE = mng.getResourcesById(copiedAE, id);
		
		if(copiedAE == null || copiedAE.isEmpty() || copiedAE.size() != 1)
			return null;
		
		return (ContainerResource) copiedAE.get(0);
	}
	
	private ArrayList<OM2MResource> getAllContainers(String father_id) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + father_id);
		
		ArrayList<OM2MResource> containers = mng.discovery(IN, OM2MManager.CONTAINER, f);
		
		if(containers == null || containers.isEmpty())
			return null;
		
		return containers;
	}
	
	private OM2MResource getContainerByName(String father_id, String name) {
		ArrayList<OM2MResource> gps_containers = getAllContainers(father_id);
		
		if(gps_containers == null)
			return null;
		
		gps_containers = mng.getResourcesByName(gps_containers, name);
		
		if(gps_containers == null || gps_containers.isEmpty() || gps_containers.size() !=1)
			return null;
		
		return gps_containers.get(0);
	}
	
	private ArrayList<OM2MResource> getAll(String father_id, String type) {
		OM2MResource c = getContainerByName(father_id, type);
		
		if(c == null)
			return null;
		
		ArrayList<OM2MResource> all = getAllContainers(c.getRi());
		
		if(all == null)
			return null;
		
		return all;
	}
	
	private InstanceResource getSingleData(String father_id, String type) {
		ContainerResource data = (ContainerResource) getContainerByName(father_id, type);
		
		if(data == null)
			return null;
		
		InstanceResource value = mng.getContentInstance(IN, data.getLa());
		
		if(value == null)
			return null;	
		
		return value;
	}
	
	private JSONArray getData(String father_id, String type) {
		ArrayList<OM2MResource> copiedAE = getCopiedAE();
		
		if(copiedAE == null || copiedAE.isEmpty())
			return null;
		
		copiedAE = mng.getResourcesById(copiedAE, father_id);
		
		if(copiedAE == null || copiedAE.isEmpty() || copiedAE.size() != 1)
			return null;
		
		OM2MResource ae = (OM2MResource) copiedAE.get(0);
		
		ArrayList<OM2MResource> sensors = getAll(ae.getRi(), type);
		
		if(sensors == null)
			return null;
		
		JSONArray response = new JSONArray();
		
		if(type == SENSORS)
			for(OM2MResource s : sensors) {
				InstanceResource gps = getSingleData(s.getRi(), GPS);
				InstanceResource th = getSingleData(s.getRi(), THRESHOLD);
				InstanceResource level = getSingleData(s.getRi(), LEVEL);
				
				JSONObject gps_json = null, th_json = null;
				try {
					gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
					th_json = (JSONObject) JSONValue.parseWithException(th.getCon().toString().replace("'", "\""));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				if(gps_json != null && th_json != null) {
					JSONObject data = new JSONObject();
					data.put("id", s.getRi());
					data.put("lat", gps_json.get("LAT"));
					data.put("lng", gps_json.get("LNG"));
					data.put("level", Integer.parseInt(level.getCon().toString()));
					data.put("min", th_json.get("MIN"));
					data.put("max", th_json.get("MAX"));
					data.put("th", th_json.get("TH"));
					
					if(data != null)
						response.add(data);
				}
			}
		else
			for(OM2MResource s : sensors) {
				InstanceResource gps = getSingleData(s.getRi(), GPS);
				InstanceResource state = getSingleData(s.getRi(), STATE);
				
				JSONObject gps_json = null;
				try {
					gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				if(gps_json != null) {
					JSONObject data = new JSONObject();
					data.put("id", s.getRi());
					data.put("lat", gps_json.get("LAT"));
					data.put("lng", gps_json.get("LNG"));
					data.put("state", Boolean.parseBoolean(state.getCon().toString()));
					
					if(data != null)
						response.add(data);
				}
			}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	private boolean setData(String ae_id, String sensor_id, String type, Object data, String admin_name) {
		ContainerResource ae = getCopiedAE(ae_id);
		
		ContainerResource sensor = (ContainerResource) getContainerByName(sensor_id, type);
		
		if(sensor == null)
			return false;
		
		ArrayList<String> lables = sensor.getLbl();
		String sub_id = null;
		for(String label : lables)
			if(label.toLowerCase().contains("sub"))
				sub_id = label;
		
		if(sub_id == null)
			return false;
		
		ReferenceResource reference = getMNReference(ae);
		
		if(reference == null)
			return false;
		
		SubscriptionResource sub = mng.getBridgedSubscription(IN, reference.getCsi(), sub_id);
		
		if(sub == null)
			return false;
		
		ContainerResource c = mng.getBridgedContainer(IN, reference.getRi(), sub.getPi());
		
		if(c == null)
			return false;
		
		JSONObject json = mng.jsonCI(DEFAULT_CNF + admin_name, data, c.getRi());
		mng.createBridgedContentInstance(IN, reference.getCsi(), c.getRi(), json);
		
		return true;
	}
	
	public JSONArray getHistoryData(String sensor_id) {
		ContainerResource level = (ContainerResource) getContainerByName(sensor_id, LEVEL);
		
		if(level == null)
			return null;
		
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + level.getRi());
		
		ArrayList<OM2MResource> values = mng.discovery(IN, OM2MManager.CONTENT_INSTANCE, f);
		
		if(values == null || values.isEmpty())
			return null;	
		
		JSONArray response = new JSONArray();
		
		for (OM2MResource value : values) {
			InstanceResource v = (InstanceResource) value;
			
			JSONObject data = new JSONObject();
			data.put("x", values.indexOf(value));
			data.put("y", Integer.parseInt(v.getCon().toString()));
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	public JSONArray getMarkerData() {
		ArrayList<OM2MResource> copiedAE = getCopiedAE();
		
		if(copiedAE == null || copiedAE.isEmpty())
			return null;
		
		JSONArray response = new JSONArray();
		
		for (OM2MResource ae : copiedAE) {
			InstanceResource gps = getSingleData(ae.getRi(), GPS);
			InstanceResource state = getSingleData(ae.getRi(), STATE);
			
			JSONObject gps_json = null, state_json = null;
			try {
				gps_json = (JSONObject) JSONValue.parseWithException(gps.getCon().toString().replace("'", "\""));
				state_json = (JSONObject) JSONValue.parseWithException(state.getCon().toString().replace("'", "\""));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(gps_json != null && state_json != null) {
				JSONObject data = new JSONObject();
				data.put("id", ae.getRi());
				data.put("name", ae.getRn());
				data.put("lat", gps_json.get("LAT"));
				data.put("lng", gps_json.get("LNG"));
				data.put("level", state_json.get("LEVEL"));
				data.put("message", state_json.get("MESSAGE"));
			
				if(data != null)
					response.add(data);
			}
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	public JSONArray getSensorData(String sensor_id) {
		return getData(sensor_id, SENSORS);
	}
	
	public JSONArray getDamData(String dam_id) {
		return getData(dam_id, DAMS);
	}
	
	public boolean setDamData(String ae_id, String dam_id, Object data, String admin_name) {
		return setData(ae_id, dam_id, STATE, data, admin_name);
	}
	
	public boolean setSensorData(String ae_id, String sensor_id, Object data, String admin_name) {
		return setData(ae_id, sensor_id, THRESHOLD, data, admin_name);
	}
}
