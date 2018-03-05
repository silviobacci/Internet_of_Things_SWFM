package assets;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		ArrayList<OM2MResource> references = getMNReference();
		
		if(references == null)
			return null;
		
		ArrayList<OM2MResource> copiedMN = mng.getResourcesById(references, copiedAE.getPi());
		
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
	
	private ArrayList<OM2MResource> getContainerByName(String father_id, String name) {
		ArrayList<OM2MResource> gps_containers = getAllContainers(father_id);
		
		if(gps_containers == null)
			return null;
		
		gps_containers = mng.getResourcesByName(gps_containers, name);
		
		if(gps_containers == null || gps_containers.isEmpty() || gps_containers.size() !=1)
			return null;
		
		return gps_containers;
	}
	
	private ArrayList<OM2MResource> getAll(String father_id, String type) {
		ArrayList<OM2MResource> container = getContainerByName(father_id, type);
		
		if(container == null)
			return null;
		
		ContainerResource c = (ContainerResource) container.get(0);
		
		ArrayList<OM2MResource> all = getAllContainers(c.getRi());
		
		if(all == null)
			return null;
		
		return all;
	}
	
	private JSONObject getSingleData(String father_id, String type) {
		ArrayList<OM2MResource> types = getContainerByName(father_id, type);
		
		if(types == null)
			return null;
		
		ContainerResource data = (ContainerResource) types.get(0);
		
		InstanceResource value = mng.getContentInstance(IN, data.getRi());
		
		if(value == null)
			return null;	
		
		JSONObject jo = new JSONObject();
		jo.put(father_id, value);

		return jo;
	}
	
	private JSONArray getAllData(String father_id, String type, String d) {
		ArrayList<OM2MResource> copiedAE = getCopiedAE();
		
		if(copiedAE == null || copiedAE.isEmpty())
			return null;
		
		copiedAE = mng.getResourcesById(copiedAE, father_id);
		
		if(copiedAE == null || copiedAE.isEmpty() || copiedAE.size() != 1)
			return null;
		
		AEResource ae = (AEResource) copiedAE.get(0);
		
		ArrayList<OM2MResource> sensors = getAll(ae.getRi(), type);
		
		if(sensors == null)
			return null;
		
		JSONArray response = new JSONArray();
		
		for(OM2MResource s : sensors) {
			JSONObject data = getSingleData(s.getRi(), d);
			
			if(data != null)
				response.add(data);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	private boolean changeData(String father_id, String d_id, String type, String d, String data, String admin_name) {
		ContainerResource ae = getCopiedAE(father_id);
		
		ArrayList<OM2MResource> sensors = getAll(ae.getRi(), type);
		
		if(sensors == null)
			return false;
		
		sensors = mng.getResourcesById(sensors, d_id);
		
		if(sensors == null || sensors.isEmpty() || sensors.size() != 1)
			return false;
		
		ContainerResource s = (ContainerResource) sensors.get(0);
		
		ArrayList<String> lables = s.getLbl();
		String sub_id = null;
		for(String label : lables)
			if(label.toLowerCase().contains("sub"))
				sub_id = label;
		
		if(sub_id == null)
			return false;
		
		ReferenceResource reference = getMNReference(ae);
		
		ArrayList<OM2MResource> subscriptions = mng.bridgedDiscovery(IN, reference.getCsi(), OM2MManager.SUBSCRIPTION, null);
		
		if(subscriptions == null || subscriptions.isEmpty())
			return false;
		
		subscriptions = mng.getResourcesById(subscriptions, sub_id);
		
		if(subscriptions == null || subscriptions.isEmpty() || subscriptions.size() != 1)
			return false;
		
		SubscriptionResource sub = (SubscriptionResource) subscriptions.get(0);
		
		ContainerResource c = mng.getBridgedContainer(IN, reference.getRi(), sub.getPi());
		
		if(c == null)
			return false;
		
		JSONObject json = mng.jsonCI(DEFAULT_CNF + admin_name, data, s.getRi());
		mng.createBridgedContentInstance(IN, reference.getRi(), c.getRi(), json);
		
		return true;
	}
	
	public JSONArray getMNPosition() {
		ArrayList<OM2MResource> copiedMN = getCopiedMN();
		JSONArray response = new JSONArray();
		
		if(copiedMN == null || copiedMN.isEmpty())
			return null;
		
		for (OM2MResource mn : copiedMN) {
			JSONObject value = getSingleData(mn.getRi(), GPS);
			
			if(value != null) {
				JSONObject jo = new JSONObject();
				jo.put(mn.getRi(), value);
				response.add(jo);
			}
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	public JSONArray getMNNames() {
		ArrayList<OM2MResource> references = getMNReference();
		
		if(references == null || references.isEmpty())
			return null;
		
		JSONArray response = new JSONArray();
		
		for (OM2MResource r : references) {
			JSONObject jo = new JSONObject();
			jo.put(r.getRi(), r.getRn());
			response.add(jo);
		}
		
		if(response == null || response.isEmpty())
			return null;
		
		return response;
	}
	
	public JSONArray getSensorLevel(String copiedAE_id) {
		return getAllData(copiedAE_id, SENSORS, LEVEL);
	}
	
	public JSONArray getSensorThreshold(String copiedAE_id) {
		return getAllData(copiedAE_id, SENSORS, THRESHOLD);
	}
	
	public JSONArray getSensorPosition(String copiedAE_id) {
		return getAllData(copiedAE_id, SENSORS, GPS);
	}
	
	public JSONArray getDamState(String copiedAE_id) {
		return getAllData(copiedAE_id, DAMS, STATE);
	}
	
	public JSONArray getDamPosition(String copiedAE_id) {
		return getAllData(copiedAE_id, DAMS, GPS);
	}
	
	public boolean changeDamState(String copiedAE_id, String dam_id, String data, String admin_name) {
		return changeData(copiedAE_id, dam_id, DAMS, STATE, data, admin_name);
	}
	
	public boolean changeSensorThreshold(String copiedAE_id, String sensor_id, String data, String admin_name) {
		return changeData(copiedAE_id, sensor_id, SENSORS, THRESHOLD, data, admin_name);
	}
}
