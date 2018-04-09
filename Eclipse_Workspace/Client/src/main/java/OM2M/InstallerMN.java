package OM2M;


import java.util.ArrayList;

import org.json.simple.JSONObject;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;

public class InstallerMN extends Thread {}
/*	private OM2MManager mng ;
	private ArrayList<AEResource> ae;
	private ArrayList<ContainerResource> cnt;
	private ArrayList<ContainerResource> nestedCnt;
	private ArrayList<ContainerResource> nestedNestedCnt;
	private ArrayList<InstanceResource> inst;
	private boolean isMN;
	
	public InstallerMN(String ip) {
		mng = new OM2MManager(ip);
		ae = new ArrayList<AEResource>();
		cnt = new ArrayList<ContainerResource>();
		nestedCnt = new ArrayList<ContainerResource>();
		nestedNestedCnt = new ArrayList<ContainerResource>();
		inst = new ArrayList<InstanceResource>();
		isMN = true;
	}
	
	@SuppressWarnings("unchecked")
	public void createMN() {
		ArrayList<OM2MResource> references = mng.discovery(isMN, OM2MManager.REMOTE_CSE, null);	
		
		// AE
		JSONObject json = mng.jsonAE("PESCIA-ID", "PESCIA", true, references.get(0).getRi());
		ae.add(mng.createAE(isMN, json));
		
		// GPS AE
		
		json = mng.jsonContainer("GPS",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject gps = new JSONObject();
		gps.put("LAT", 43.843176);
		gps.put("LNG", 10.734928);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), cnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(0).getRi(), json));
		
		// STATE AE
		
		json = mng.jsonContainer("STATE",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject state = new JSONObject();
		state.put("LEVEL", 1);
		state.put("MESSAGE", "Tutto ok");
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", state.toJSONString().replace("\"", "'"), cnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(1).getRi(), json));
		
		// SENSORI E DAM
		json = mng.jsonContainer("SENSORS",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		json = mng.jsonContainer("DAMS",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		// 3 SENSORI
		json = mng.jsonContainer("SENSOR_21342331", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));	
		
		json = mng.jsonContainer("SENSOR_32432424", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));	
		
		json = mng.jsonContainer("SENSOR_34256465", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("SENSOR_43545464", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("SENSOR_67575657", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("SENSOR_12332434", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));
		
		// 4 DAMS
		json = mng.jsonContainer("DAM_43253434", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));	
		
		json = mng.jsonContainer("DAM_54654755", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));	
		
		json = mng.jsonContainer("DAM_24324323", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));
		
		json = mng.jsonContainer("DAM_78676777", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));
		
		// CONTAINER INNESTATI AI SENSORI
		json = mng.jsonContainer("LEVEL", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));	

		json = mng.jsonContainer("LEVEL", nestedCnt.get(3).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(3).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(4).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(4).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(5).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(5).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(3).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(3).getRi(), json));
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(4).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(4).getRi(), json));
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(5).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(5).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(3).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(3).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(4).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(4).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(5).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(5).getRi(), json));
		
		// CONTAINER INNESTATI ALLE DAM
		
		json = mng.jsonContainer("STATE", nestedCnt.get(6).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(6).getRi(), json));	
		
		json = mng.jsonContainer("STATE", nestedCnt.get(7).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(7).getRi(), json));	
		
		json = mng.jsonContainer("STATE", nestedCnt.get(8).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(8).getRi(), json));	
		
		json = mng.jsonContainer("STATE", nestedCnt.get(9).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(9).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(6).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(6).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(7).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(7).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(8).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(8).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(9).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(9).getRi(), json));
		
		// ISTANZE NEI SENSORI
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 50, nestedNestedCnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 100, nestedNestedCnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 150, nestedNestedCnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 100, nestedNestedCnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(1).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 100, nestedNestedCnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(1).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 150, nestedNestedCnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(1).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(1).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(2).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(2).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(2).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(2).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(2).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(2).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(2).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(2).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(3).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(3).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(3).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(3).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(3).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(3).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 200, nestedNestedCnt.get(3).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(3).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(4).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(4).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(4).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(4).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(4).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(4).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 250, nestedNestedCnt.get(4).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(4).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 300, nestedNestedCnt.get(5).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(5).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 300, nestedNestedCnt.get(5).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(5).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 300, nestedNestedCnt.get(5).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(5).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 300, nestedNestedCnt.get(5).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(5).getRi(), json));
		
		JSONObject th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(6).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(6).getRi(), json));
		
		th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(7).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(7).getRi(), json));
		
		th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(8).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(8).getRi(), json));
		
		th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(9).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(9).getRi(), json));
		
		th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(10).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(10).getRi(), json));
		
		th = new JSONObject();
		th.put("MIN", 0);
		th.put("MAX", 500);
		th.put("TH", 250);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", th.toJSONString().replace("\"", "'"), nestedNestedCnt.get(11).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(11).getRi(), json));

		gps = new JSONObject();
		gps.put("LAT", 1);
		gps.put("LNG", 11);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(12).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(12).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 1);
		gps.put("LNG", 27);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(13).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(13).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 11);
		gps.put("LNG", 14);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(14).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(14).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 11);
		gps.put("LNG", 26);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(15).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(15).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 21);
		gps.put("LNG", 18);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(16).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(16).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 28);
		gps.put("LNG", 18);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(17).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(17).getRi(), json));
		
		// ISTANZE NELLE DAM
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", true, nestedNestedCnt.get(18).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(18).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", false, nestedNestedCnt.get(19).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(19).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", false, nestedNestedCnt.get(20).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(20).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", true, nestedNestedCnt.get(21).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(21).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 9);
		gps.put("LNG", 3);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(22).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(22).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 28);
		gps.put("LNG", 3);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(23).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(23).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 15);
		gps.put("LNG", 22);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(24).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(24).getRi(), json));
		
		gps = new JSONObject();
		gps.put("LAT", 21);
		gps.put("LNG", 30);
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", gps.toJSONString().replace("\"", "'"), nestedNestedCnt.get(25).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(25).getRi(), json));
	
	}
	
	public void createContentInstance() {
		/*
		ArrayList<OM2MResource> references = mng.discovery(!isMN, OM2MManager.REMOTE_CSE, null);
		
		ReferenceResource ref = (ReferenceResource) references.get(0);
		
		ArrayList<OM2MResource> containers = mng.bridgedDiscovery(!isMN, ref.getCsi(), 3, null);
		ArrayList<ContainerResource> bridgedContainers = new ArrayList<ContainerResource>();
		for(OM2MResource r : containers) {
			bridgedContainers.add((ContainerResource) r);
		}
		*/
		
/*		JSONObject json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(12).getRi(), inst.get(inst.size() - 1).getRn());
		
		System.out.println(json.toJSONString());
		System.out.println(nestedNestedCnt.get(12).getRi());
		System.out.println(nestedNestedCnt.get(12).getPi());
		
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(12).getRi(), json));
	}
	
	private static void sleep(int period) {
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("INIZIO CREAZIONE CONTENUTO MN");
		createMN();
		System.out.println("FINE CREAZIONE CONTENUTO MN");
		
		sleep(10000);
		
		//createContentInstance();
	}
}*/