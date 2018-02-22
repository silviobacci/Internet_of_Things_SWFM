package unipi.iot.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.JSONObject;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.ReferenceResource;
import resources.Resource;

public class MNInstaller extends Thread {
	private Onem2mManager2 mng ;
	private ArrayList<AEResource> ae;
	private ArrayList<ContainerResource> cnt;
	private ArrayList<InstanceResource> inst;
	private ArrayList<AEResource> bridgedAe;
	private ArrayList<ContainerResource> bridhedCnt;
	private ArrayList<InstanceResource> bridgedInst;
	private CoapClientADN context = CoapClientADN.getInstance();
	//private ArrayList<Resource> resources;
	
	public MNInstaller() {
		mng = new Onem2mManager2();
		ae = new ArrayList<AEResource>();
		cnt = new ArrayList<ContainerResource>();
		inst = new ArrayList<InstanceResource>();
		bridgedAe = new ArrayList<AEResource>();
		bridhedCnt = new ArrayList<ContainerResource>();
		bridgedInst = new ArrayList<InstanceResource>();
	}
	
	public void createMN() {
		boolean isMN = true;
		/*
		//Water level AE
		JSONObject json = mng.jsonAE("Water_level", "Water_level", true);
		System.out.println("jsonAE :"+json.toJSONString());
		ae.add(mng.createAE(isMN, json));	
		
		json = mng.jsonContainer("Sensor1");
		System.out.println("jsonContainer :"+json.toJSONString());
		cnt.add(mng.createContainer(isMN, ae.get(0), json));	
		
		json = mng.jsonContainer("SensorNested1");
		System.out.println("jsonNestedContainer :" + json.toJSONString());
		cnt.add(mng.createContainer(isMN, cnt.get(0), json));	
		
		json = mng.jsonCI("new Reading", 10);
		System.out.println("jsonInstance :" + json.toJSONString());
		inst.add(mng.createContentInstance(isMN, cnt.get(1), json));
		inst.add(mng.createContentInstance(isMN, cnt.get(1), json));
		*/
		
		ArrayList<Resource> discovered = mng.bridgedDiscovery(!isMN, 2, null);
		for(Resource r : discovered) {
			bridgedAe.add((AEResource) r);
		}

		/*
		
		json = mng.jsonContainer("Sensor1");
	    mng.createContainer(true, json, "Water_level");
	   
	    json = mng.jsonContainer("Sensor1Nested");
	    mng.createNestedContainer(true, json, "Water_level", "Sensor1");
	    
	    json = mng.jsonCI("level", "69");
	    mng.createContentInstance(true, json, "Water_level", "Sensor1/Sensor1Nested");
	    
	    //System.out.println("discover:"+mng.discovery(true));
	    
	    String containerSet = mng.discovery(true, 3,null);
	    
		String []containers = containerSet.split("\"");
		containers = Arrays.copyOfRange(containers, 3, containers.length-3) ; //out of bound exception
		
		
		for( String cnt : containers)
			mng.getResource(true, cnt);
	    */
	    
	    //Dam AE
		
	    /* json = mng.jsonContainer("Sensor2");
	    mng.createContainer(true, json, "Water_level");
	    json = mng.jsonContainer("icardi");
	    mng.createNestedContainer(true, json, "Water_level", "Sensor2");*/
	    /*for(String name : context.getMonitoringModule().keySet()) {
		json = mng.jsonContainer(name);
	    mng.createContainer(true, json, "Water_level");
	}*/
	   /* json = mng.jsonAE("Dam", "Dam", "true");
		mng.createAE(true, json);
		
		/*System.out.println("discovery_ae"+mng.discovery(true));
		System.out.println("discovery_cnt"+mng.discovery(true, 3,null));
		System.out.println("discovery_ae"+mng.discovery(true, 2,null));
		
	 // System.out.println("discovery_ae"+mng.discovery(true, 3,"&"));
	/*	for(String name : context.getDamModule().keySet()) {
			json = mng.jsonContainer(name);
		    mng.createContainer(true, json, "Dam");
		}*/


	}

	/*public void createIN() {
		String json;
		//Water level AE
		json = mng.jsonStringAE("Pescia", "Pescia", "true");
		mng.createAE(false, json);
		
		json = mng.jsonStringContainer("Water_level");
		 mng.createContainer(false, json, "Pescia");
		
		for(String name : context.getMonitoringModule().keySet()) {
			json = mng.jsonStringContainer(name);
		    //mng.createNestedContainer(false, json, "Water_level");
		}
		
		
		//Dam AE
		json = mng.jsonStringAE("Dam", "Dam", "true");
		mng.createAE(false, json);
		
		for(String name : context.getDamModule().keySet()) {
			json = mng.jsonStringContainer(name);
		    mng.createContainer(false, json, "Dam");
		}
		

		
	}*/
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		createMN();
	}

	
}
