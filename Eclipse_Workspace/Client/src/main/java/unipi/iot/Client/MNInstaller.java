package unipi.iot.Client;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.californium.core.CoapClient;
import org.json.simple.JSONObject;

import resources.Resource;

public class MNInstaller extends Thread {

	private Onem2mManager mng ;
	private CoapClientADN context = CoapClientADN.getInstance();
	//private ArrayList<Resource> resources;
	
	public MNInstaller() {
		Onem2mManager mng = new Onem2mManager();
	}
	

	
	public void createMN() {
		//Water level AE
		JSONObject json = mng.jsonAE("Water_level", "Water_level", "true");
		System.out.println("json:"+json.toJSONString());
		mng.createAE(true, json);	
		
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
