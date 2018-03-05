package oneM2M;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;

public class InstallerMN extends Thread {
	private OM2MManager mng ;
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
	
	public void createMN() {
		ArrayList<OM2MResource> references = mng.discovery(isMN, OM2MManager.REMOTE_CSE, null);
	
		// GPS FOR MN
		JSONObject json = mng.jsonAE("GPS-ID", "GPS", true, references.get(0).getRi());
		ae.add(mng.createAE(isMN, json));
		
		// LAT E LON PER MN
		json = mng.jsonContainer("LATITUDE",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, cnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(0).getRi(), json));
		
		json = mng.jsonContainer("LONGITUDE",  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, cnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(1).getRi(), json));
				
		// AE
		json = mng.jsonAE("PESCIA-ID", "PESCIA", true, references.get(0).getRi());
		ae.add(mng.createAE(isMN, json));
		
		// SENSORI E DAM
		json = mng.jsonContainer("SENSORS",  ae.get(1).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(1).getRi(), json));
		
		json = mng.jsonContainer("DAMS",  ae.get(1).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(1).getRi(), json));
		
		// 3 SENSORI INNESTATI
		json = mng.jsonContainer("SENSOR_21342331", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));	
		
		json = mng.jsonContainer("SENSOR_32432424", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));	
		
		json = mng.jsonContainer("SENSOR_34256465", cnt.get(2).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(2).getRi(), json));
		
		// 2 DAM INNESTATE
		json = mng.jsonContainer("DAM_34344345", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));	
		
		json = mng.jsonContainer("DAM_56756456", cnt.get(3).getRi());
		nestedCnt.add(mng.createContainer(isMN, cnt.get(3).getRi(), json));	
		
		// INNESTATI SENSORI
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("LEVEL", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("THRESHOLD", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(0).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(0).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(1).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(1).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(2).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(2).getRi(), json));
		
		// INNESTATI DAM
		
		json = mng.jsonContainer("STATE", nestedCnt.get(3).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(3).getRi(), json));
		
		json = mng.jsonContainer("STATE", nestedCnt.get(4).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(4).getRi(), json));	
		
		json = mng.jsonContainer("GPS", nestedCnt.get(3).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(3).getRi(), json));
		
		json = mng.jsonContainer("GPS", nestedCnt.get(4).getRi());
		nestedNestedCnt.add(mng.createContainer(isMN, nestedCnt.get(4).getRi(), json));	
		
		// ISTANZE NEI SENSORI
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(0).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(1).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(2).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(2).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(3).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(3).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(4).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(4).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(5).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(5).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(6).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(6).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(7).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(7).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(8).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(8).getRi(), json));
		
		// ISTANZE NEI DAM
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(9).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(9).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(10).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(10).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(11).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(11).getRi(), json));
		
		json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(12).getRi());
		inst.add(mng.createContentInstance(isMN, nestedNestedCnt.get(12).getRi(), json));
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
		
		JSONObject json = mng.jsonCI("CHANGED AUTOMATICALLY", 10, nestedNestedCnt.get(12).getRi(), inst.get(inst.size() - 1).getRn());
		
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
		
		createContentInstance();
	}
}
