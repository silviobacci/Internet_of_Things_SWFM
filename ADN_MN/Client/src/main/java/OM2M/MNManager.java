package OM2M;

import java.util.ArrayList;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.simple.JSONObject;
import Modules.DamActuator;
import Modules.Module;
import Modules.WaterFlowSensor;
import configuration.Setup;
import configuration.AdnInstance;
import interaction.Controller;
import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;
import resources.ReferenceResource;
import unipi.iot.Client.Constants;

public class MNManager {

	private static AdnInstance 			WFinstance ;
	private static ArrayList<AEResource> 		ae 				= new ArrayList<AEResource>();
	private static ArrayList<ContainerResource> cnt 			= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> SensorCnt 		= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> DamCnt 			= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> INSensorCnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> INDamCnt 		= new ArrayList<ContainerResource>();
	private static ArrayList<InstanceResource> inst 			= new ArrayList<InstanceResource>();
	
	private static boolean isMN = true;
	
	public  static void setWFI(AdnInstance w) {
		WFinstance = w; 
	}
	
	public static void createStructure() {
		createAE();
		createAEGPS();
		createAEState();
		createDamAndSensorCNT();
	}
	
	private static void createAE() {
		
		WFinstance = Setup.getWinstance();
		JSONObject json = OM2MManager.jsonAE(WFinstance.getName()+"-ID", WFinstance.getName(), true);
		synchronized(ae) {
		ae.add(OM2MManager.createAE(isMN, json));
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void createAEGPS() {
		JSONObject json = OM2MManager.jsonContainer(Constants.GPS,  ae.get(0).getRi());
		synchronized(cnt) {
			cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		}
		JSONObject gps = new JSONObject();
		gps.put(Constants.LAT_AE, WFinstance.getLat());
		gps.put(Constants.LNG_AE, WFinstance.getLng());
		
		json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, gps.toJSONString().replace("\"", "'"), cnt.get(0).getRi());
		synchronized(inst) {
			inst.add(OM2MManager.createContentInstance(isMN, cnt.get(Constants.AE_GPS_CNT).getRi(), json));
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void createAEState() {
		JSONObject json = OM2MManager.jsonContainer(Constants.STATE,  ae.get(0).getRi());
		synchronized(ae) {
			cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		}
		JSONObject state = new JSONObject();
		state.put(Constants.RLEVEL, 1);
		state.put(Constants.RMESSAGE, Controller.NORISK_MESSAGE);
		
		json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, state.toJSONString().replace("\"", "'"), cnt.get(1).getRi());
		synchronized(inst) {
			inst.add(OM2MManager.createContentInstance(isMN, cnt.get(Constants.AE_STATE_CNT).getRi(), json));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static   void createAEStateCI(String message, int risk) {
		JSONObject state = new JSONObject();
		state.put(Constants.RMESSAGE, message);
		state.put(Constants.RLEVEL, risk);
		
		JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, state.toJSONString().replace("\"", "'"), cnt.get(1).getRi());
		synchronized(inst) {
			inst.add(OM2MManager.createContentInstance(isMN, cnt.get(Constants.AE_STATE_CNT).getRi(), json));
		}
		
	}
	private static String getSensorsCNTID() {
		for(ContainerResource c: cnt)
			if(c.getRn().equals(Constants.SENSORS))
				return c.getRi();
		return Constants.ERROR;
	}
	
	private static String getDamsCNTID() {
		for(ContainerResource c: cnt)
			if(c.getRn().equals(Constants.DAMS))
				return c.getRi();
		
		return Constants.ERROR;
	}
	
	private static void createDamAndSensorCNT() {
		JSONObject json = OM2MManager.jsonContainer(Constants.SENSORS,  ae.get(0).getRi());
		synchronized(cnt) {
			cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
			json = OM2MManager.jsonContainer(Constants.DAMS,  ae.get(0).getRi());
			cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		}
	}
	
	private static String checkCNTExists(WaterFlowSensor mod) {
		String filter = "lbl="+getSensorsCNTID();
		return findCNT(mod, filter); 
	}


	private static String findCNT(Module mod, String filter) {
		ArrayList<OM2MResource> tmp =  OM2MManager.discovery(isMN, OM2MManager.CONTAINER, filter);
		
		if(tmp != null) {
			for(OM2MResource res: tmp)
				if(res.getRn().equals(mod.getName()))
					return res.getRi();
				
			return null;
		
		}else 
			return null;
	}

	private static String checkCNTExists(DamActuator mod) {
		String filter = "lbl="+getDamsCNTID();
		return findCNT(mod, filter);
	}
	
	private static void createStateSUB(DamActuator dam) {
		String nu;
		ArrayList<OM2MResource> tmp = null;
		ArrayList<OM2MResource> list	= OM2MManager.discovery(true, OM2MManager.REMOTE_CSE);
		ReferenceResource ref = (ReferenceResource)list.get(0);
		ArrayList<String> filters  = new ArrayList<String>();
		filters.add("lbl="+Constants.DAMS);
		filters.add("lbl="+dam.getRi());
		
		do {
			 tmp= OM2MManager.bridgedDiscovery(true, ref.getCsi(), OM2MManager.CONTAINER, filters);
		}while(tmp == null || tmp.isEmpty() );
		
		ContainerResource damIN = (ContainerResource) tmp.get(0);
		filters  = new ArrayList<String>();
		filters.add("lbl="+damIN.getRn());
		filters.add("lbl="+damIN.getRi());
		
		do {
			 tmp= OM2MManager.bridgedDiscovery(true, ref.getCsi(), OM2MManager.CONTAINER, filters);
			 if(tmp != null && !tmp.isEmpty())
				 tmp = OM2MManager.getResourcesByName(tmp,"CONTROL");
			  
		}while(tmp == null || tmp.isEmpty() );
	
		nu = "coap://" +Setup.SSRESOURCE_IP+ ":" + Constants.SSERVER_PORT + "/"+Constants.SSRESOURCE_NAME;
		
		synchronized(INDamCnt) {
			JSONObject json = OM2MManager.jsonSubscription(Constants.SUB+ tmp.get(0).getRn(), nu, OM2MManager.MODIFIED_ATTRIBUTES);
			dam.setsubID(OM2MManager.createSubscription(isMN, tmp.get(0).getRi(), json).getRi());	
		}
	}
	
	
	private static void createThresholdSUB(WaterFlowSensor wfs) {
		String 	nu;
		ArrayList<OM2MResource> tmp = null;
		ArrayList<OM2MResource> list	= OM2MManager.discovery(true, OM2MManager.REMOTE_CSE);
		ReferenceResource ref = (ReferenceResource)list.get(0);
		ArrayList<String> filters  = new ArrayList<String>();
		filters.add("lbl="+Constants.SENSORS);
		filters.add("lbl="+wfs.getRi());
		
		do {
			 tmp= OM2MManager.bridgedDiscovery(true, ref.getCsi(), OM2MManager.CONTAINER, filters);
		}while(tmp == null || tmp.isEmpty() );
		
		ContainerResource damIN = (ContainerResource) tmp.get(0);
		filters  = new ArrayList<String>();
		filters.add("lbl="+damIN.getRn());
		filters.add("lbl="+damIN.getRi());
		
		do {
			 tmp= OM2MManager.bridgedDiscovery(true, ref.getCsi(), OM2MManager.CONTAINER, filters);
			 if(tmp != null && !tmp.isEmpty())
				 tmp = OM2MManager.getResourcesByName(tmp,"CONTROL");			 
		
		}while(tmp == null || tmp.isEmpty() );		
		
		nu = "coap://" +Setup.SSRESOURCE_IP+ ":" + Constants.SSERVER_PORT + "/"+Constants.SSRESOURCE_NAME;
	
		synchronized(INSensorCnt) {
			JSONObject json = OM2MManager.jsonSubscription(Constants.SUB+ tmp.get(0).getRn(), nu, OM2MManager.MODIFIED_ATTRIBUTES);
			wfs.setsubID(OM2MManager.createSubscription(isMN, tmp.get(0).getRi(), json).getRi());	
		}
	}
	
	public   static void deleteThresholdSUB(WaterFlowSensor wfs) {
		OM2MManager.deleteSubscription(isMN, wfs.getsubID());
	}
	
	public   static void deleteStateSUB(DamActuator dam) {
		OM2MManager.deleteSubscription(isMN, dam.getsubID());
	}
	
	public  static void createSensorCNT(WaterFlowSensor wfs) {
		//main CNT
		String ri = checkCNTExists(wfs);
		if( ri != null) {
			wfs.setRi(ri);
			createActiveContentInstance(wfs,true);	
			createThresholdSUB(wfs);
			
		}else {
			JSONObject json = OM2MManager.jsonContainer(wfs.getName(), cnt.get(Constants.SENSORS_TREE).getRi());
			ContainerResource tmp = OM2MManager.createContainer(isMN, cnt.get(Constants.SENSORS_TREE).getRi(), json);
			   if(tmp == null)
				   return;

			wfs.setRi(tmp.getRi());
			
			synchronized(SensorCnt) {
				SensorCnt.add(tmp);	
			}
			
			//level CNT
			json = OM2MManager.jsonContainer(Constants.LEVEL, SensorCnt.get(SensorCnt.size()-1) .getRi());
			synchronized(INSensorCnt) {
				INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1).getRi(), json));
			}		
			//Threshold CNT
			json = OM2MManager.jsonContainer(Constants.THRES, SensorCnt.get(SensorCnt.size()-1) .getRi());
			synchronized(INSensorCnt) {
				INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));
			}
				createThresholdSUB(wfs);
			
			//GPS CNT
			json = OM2MManager.jsonContainer(Constants.GPS, SensorCnt.get(SensorCnt.size()-1) .getRi());
			synchronized(INSensorCnt) {
				INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));
			}
			
			//ACTIVE CI
			createActiveContentInstance(wfs,true);	
		}
	}
	
	public static int isSensor(String core) {		
		if(core == null)
			return Constants.UNREACHABLE;
   	 	if(core.contains(Constants.SENSOR))
   	 		return Constants.SUCCESS;
   	 	return Constants.UNSUCCESS;	
	}
	
	public static String  getCore(String address) {
		int times = 0;
		Request req = new Request(Code.GET);
		CoapClient client =  new CoapClient(address);
		CoapResponse res =  client.advanced(req);
		client.setTimeout(5000);
		while(res == null && times++ < 2)
			res = client.advanced(req);
		if(res == null)
			return null;
   	 	return res.getResponseText();	
	}

	
	public static int isDam(String core) {
		if(core == null)
			return Constants.UNREACHABLE;
   	 	if(core.contains(Constants.DAM))
   	 		return Constants.SUCCESS;
   	 	return  Constants.UNSUCCESS;	 	
	}
	
	public   static void createDamCNT(DamActuator dam) {
		String ri = checkCNTExists(dam);
		if( ri != null) {
			dam.setRi(ri);
			createActiveContentInstance(dam,true);
			createStateSUB( dam);
		}else {
		
			//main CNT
			JSONObject json = OM2MManager.jsonContainer(dam.getName(), cnt.get(Constants.DAMS_TREE).getRi());
			ContainerResource tmp = (OM2MManager.createContainer(isMN, cnt.get(Constants.DAMS_TREE).getRi(), json));	
		    if(tmp == null)
			   return;
		   
			dam.setRi(tmp.getRi());
			synchronized(DamCnt) {
				DamCnt.add(tmp);
			}
			
			//State CNT
			json = OM2MManager.jsonContainer(Constants.STATE, DamCnt.get(DamCnt.size()-1).getRi());
			synchronized(INDamCnt) {
				INDamCnt.add(OM2MManager.createContainer(isMN,  DamCnt.get(DamCnt.size()-1).getRi(), json));
			}
		
			createStateSUB( dam);
			
			//GPS CNT
			json = OM2MManager.jsonContainer(Constants.GPS, DamCnt.get(DamCnt.size()-1).getRi());
			synchronized(INDamCnt) {
				INDamCnt.add(OM2MManager.createContainer(isMN, DamCnt.get(DamCnt.size()-1).getRi(), json));
			}
		
			//ACTIVE CI
			createActiveContentInstance(dam,true);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public   static void createLevelContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(Constants.W_L, wfs.getLevel());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(Constants.LEVEL) && cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public   static void createThresholdsContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(Constants.THRES, wfs.getThreshold());
		th.put(Constants.MIN, wfs.getMin());
		th.put(Constants.MAX, wfs.getMax());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(Constants.THRES) &&  cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(wfs.getCnf(), th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN,cnt.getRi(), json));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public   static void createGPSContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(Constants.LAT_SENSOR,wfs.getLat()); 
		th.put(Constants.LNG_SENSOR,wfs.getLng());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(Constants.GPS) &&  cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public   static void createActiveContentInstance(Module mod, boolean isActive) {
		JSONObject th = new JSONObject();

		for (ContainerResource cnt : SensorCnt) {
			if(cnt.getRi().equals(mod.getRi())) {		
				th.put(Constants.WORKING_S,isActive);
				JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				}
				return;
			}
		}
		for ( ContainerResource cnt1 : DamCnt) {
			if(cnt1.getRi().equals(mod.getRi())) {	
				th.put(Constants.WORKING_D,isActive);
				JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt1.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt1.getRi(), json));
				}
			return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public   static void createGPSContentInstance(DamActuator dam) {
		JSONObject th = new JSONObject();
		th.put(Constants.LAT_DAM,dam.getLat());
		th.put(Constants.LNG_DAM,dam.getLng());
		
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(Constants.GPS) && cnt.getPi().equals(dam.getRi())) {	
				JSONObject json = OM2MManager.jsonCI(Constants.AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				}
			}
		}
	}
	
	
	public static   void deleteCNT(String id, WaterFlowSensor wfs) {
		createActiveContentInstance(wfs,false);
	}
	
	public static   void deleteCNT(String id, DamActuator dam) {
		createActiveContentInstance(dam,false);
	}
	
	@SuppressWarnings("unchecked")
	public static void damCI(DamActuator dam) {
		JSONObject th = new JSONObject();
		th.put(Constants.STATE,dam.isOpened());
		
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(Constants.STATE) && cnt.getPi().equals(dam.getRi())) {	
				JSONObject  json = OM2MManager.jsonCI(dam.getCnf(), th.toJSONString().replace("\"", "'"), cnt.getRi());
				synchronized(inst) {
					inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				}
			}
		}
	}		
}
