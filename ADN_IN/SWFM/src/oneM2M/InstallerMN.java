package oneM2M;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.utilities.OM2MManager;
import oneM2M.utilities.OM2MPayloader;
import oneM2M.utilities.OM2MUtilities;
import servlets.assets.oneM2M.constants.Alert;
import servlets.assets.oneM2M.constants.Dam;
import servlets.assets.oneM2M.constants.Marker;
import servlets.assets.oneM2M.constants.Sensor;

public class InstallerMN extends Thread {
	private OM2MManager mng ;
	private ArrayList<AEResource> ae;
	private ArrayList<ContainerResource> cnt;
	private ArrayList<ContainerResource> nestedCnt;
	private ArrayList<ContainerResource> nestedNestedCnt;
	private ArrayList<InstanceResource> inst;
	
	public InstallerMN(String ip) {
		mng = new OM2MManager("192.168.1.2", 5684, "/SWFM-mn-cse");
		ae = new ArrayList<AEResource>();
		cnt = new ArrayList<ContainerResource>();
		nestedCnt = new ArrayList<ContainerResource>();
		nestedNestedCnt = new ArrayList<ContainerResource>();
		inst = new ArrayList<InstanceResource>();
	}
	
	@SuppressWarnings("unchecked")
	public void createMN() {
		
		// AE
		JSONObject json = OM2MPayloader.jsonAE("SERCHIO-ID", "SERCHIO", true);
		ae.add(mng.creater.createAE(json));
		
		// GPS AE
		ArrayList<String> labels = OM2MUtilities.createLabels(ae.get(0).getRi());
		json = OM2MPayloader.jsonContainer("GPS",  labels);
		cnt.add(mng.creater.createContainer(ae.get(0).getRi(), json));
		
		JSONObject gps = new JSONObject();
		gps.put(Marker.LAT, 43.843000);
		gps.put(Marker.LNG, 10.734000);
		
		labels = OM2MUtilities.createLabels(cnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(cnt.get(0).getRi(), json));
		
		// STATE AE
		labels = OM2MUtilities.createLabels(ae.get(0).getRi());
		json = OM2MPayloader.jsonContainer("STATE", labels);
		cnt.add(mng.creater.createContainer(ae.get(0).getRi(), json));
		
		JSONObject state = new JSONObject();
		state.put(Alert.RISK_LEVEL, 1);
		state.put(Alert.RISK_MESSAGE, "Tutto ok");
		
		labels = OM2MUtilities.createLabels(cnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(cnt.get(1).getRi(), json));
		
		// SENSORI E DAM
		labels = OM2MUtilities.createLabels(ae.get(0).getRi());
		json = OM2MPayloader.jsonContainer("SENSORS", labels);
		cnt.add(mng.creater.createContainer(ae.get(0).getRi(), json));
		
		labels = OM2MUtilities.createLabels(ae.get(0).getRi());
		json = OM2MPayloader.jsonContainer("DAMS", labels);
		cnt.add(mng.creater.createContainer(ae.get(0).getRi(), json));
		
		// 3 SENSORI
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_21342331", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_32432424", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_34256465", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));
		
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_43545464", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));
		
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_67575657", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));
		
		labels = OM2MUtilities.createLabels(cnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("SENSOR_12332434", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(2).getRi(), json));
		
		// CONTENT INSTANCE PER FUNZIONAMENTO DAM
		JSONObject is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(0).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(1).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, false);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(2).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(3).getRi(), json));

		is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(4).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(4).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Sensor.SENSOR_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(5).getRi(), json));

		// 4 DAMS
		labels = OM2MUtilities.createLabels(cnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("DAM_43253434", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(3).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(cnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("DAM_54654755", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(3).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(cnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("DAM_24324323", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(3).getRi(), json));
		
		labels = OM2MUtilities.createLabels(cnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("DAM_78676777", labels);
		nestedCnt.add(mng.creater.createContainer(cnt.get(3).getRi(), json));
		
		// CONTENT INSTANCE PER FUNZIONAMENTO DAM
		is_working = new JSONObject();
		is_working.put(Dam.DAM_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(6).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(6).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Dam.DAM_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(7).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(7).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Dam.DAM_IS_WORKING, false);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(8).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(8).getRi(), json));
		
		is_working = new JSONObject();
		is_working.put(Dam.DAM_IS_WORKING, true);
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(9).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", is_working.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedCnt.get(9).getRi(), json));
		
		// CONTAINER INNESTATI AI SENSORI
		labels = OM2MUtilities.createLabels(nestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(0).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(1).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(2).getRi(), json));	

		labels = OM2MUtilities.createLabels(nestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(3).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(4).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(4).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContainer("LEVEL", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(5).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(0).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(1).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(2).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(3).getRi(), json));

		labels = OM2MUtilities.createLabels(nestedCnt.get(4).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(4).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContainer("THRESHOLD", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(5).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(0).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(1).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(2).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(3).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(4).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(4).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(5).getRi(), json));
		
		// CONTAINER INNESTATI ALLE DAM
		labels = OM2MUtilities.createLabels(nestedCnt.get(6).getRi());
		json = OM2MPayloader.jsonContainer("STATE", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(6).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(7).getRi());
		json = OM2MPayloader.jsonContainer("STATE", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(7).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(8).getRi());
		json = OM2MPayloader.jsonContainer("STATE", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(8).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(9).getRi());
		json = OM2MPayloader.jsonContainer("STATE", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(9).getRi(), json));	
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(6).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(6).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(7).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(7).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(8).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(8).getRi(), json));
		
		labels = OM2MUtilities.createLabels(nestedCnt.get(9).getRi());
		json = OM2MPayloader.jsonContainer("GPS", labels);
		nestedNestedCnt.add(mng.creater.createContainer(nestedCnt.get(9).getRi(), json));
		
		// ISTANZE NEI SENSORI
		JSONObject level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 50);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(0).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 100);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(0).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 150);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(0).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 200);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(0).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(0).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 100);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(1).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 100);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(1).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 150);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(1).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 200);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(1).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(1).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(2).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(2).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(2).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(2).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(2).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 200);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(3).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 200);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(3).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 200);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(3).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(3).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(4).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(4).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 300);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(5).getRi(), json));
		
		level = new JSONObject();
		level.put(Sensor.WATER_LEVEL, 300);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(5).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", level.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(5).getRi(), json));
		
		JSONObject th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(6).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(6).getRi(), json));
		
		th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(7).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(7).getRi(), json));
		
		th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(8).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(8).getRi(), json));
		
		th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(9).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(9).getRi(), json));
		
		th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(10).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(10).getRi(), json));
		
		th = new JSONObject();
		th.put(Sensor.MIN, 0);
		th.put(Sensor.MAX, 500);
		th.put(Sensor.THRESHOLD, 250);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(11).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(11).getRi(), json));

		gps = new JSONObject();
		gps.put(Sensor.LAT, 11);
		gps.put(Sensor.LNG, 1);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(12).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(12).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Sensor.LAT, 27);
		gps.put(Sensor.LNG, 1);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(13).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(13).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Sensor.LAT, 14);
		gps.put(Sensor.LNG, 11);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(14).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(14).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Sensor.LAT, 26);
		gps.put(Sensor.LNG, 11);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(15).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(15).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Sensor.LAT, 18);
		gps.put(Sensor.LNG, 21);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(16).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(16).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Sensor.LAT, 18);
		gps.put(Sensor.LNG, 28);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(17).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(17).getRi(), json));
		
		// ISTANZE NELLE DAM
		state = new JSONObject();
		state.put(Dam.STATE, true);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(18).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(18).getRi(), json));
		
		state = new JSONObject();
		state.put(Dam.STATE, false);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(19).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(19).getRi(), json));
		
		state = new JSONObject();
		state.put(Dam.STATE, false);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(20).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(20).getRi(), json));
		
		state = new JSONObject();
		state.put(Dam.STATE, true);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(21).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(21).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Dam.LAT, 21);
		gps.put(Dam.LNG, 30);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(22).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(22).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Dam.LAT, 15);
		gps.put(Dam.LNG, 22);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(23).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(23).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Dam.LAT, 28);
		gps.put(Dam.LNG, 3);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(24).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(24).getRi(), json));
		
		gps = new JSONObject();
		gps.put(Dam.LAT, 9);
		gps.put(Dam.LNG, 3);
		
		labels = OM2MUtilities.createLabels(nestedNestedCnt.get(25).getRi());
		json = OM2MPayloader.jsonContentInstance("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), labels);
		inst.add(mng.creater.createContentInstance(nestedNestedCnt.get(25).getRi(), json));
	}

	public void run() {
		System.out.println("INIZIO CREAZIONE CONTENUTO MN");
		createMN();
		System.out.println("FINE CREAZIONE CONTENUTO MN");
	}
}
