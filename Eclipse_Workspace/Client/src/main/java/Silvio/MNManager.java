package Silvio;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import Modules.DamActuator;
import Modules.Module;
import Modules.WaterFlowSensor;
import communication.CoapClientADN;
import configuration.Setup;
import configuration.WaterFlowInstance;
import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.OM2MResource;

public class MNManager {
	private static final String GPS			=	"GPS";
	private static final String AUTO_CHANGE	=	"CHANGED AUTOMATICALLY";
	private static final String STATE		=	"STATE";
	private static final String SENSORS		=	"SENSORS";
	private static final String DAMS		=	"DAMS";
	private static final String LEVEL		=	"LEVEL";
	private static final String THRES		=	"THRESHOLD";
	private static final String LAT			=	"LAT";
	private static final String LNG			=	"LNG";
	private static final String MESSAGE		=	"MESSAGE";
	private static final String OK			=	"Tutto OK";
	private static final String MIN			=	"MIN";
	private static final String MAX			=	"MAX";
	
	private static final int AE_GPS_CNT		=	0;
	private static final int AE_STATE_CNT	=	1;
	private static final int SENSORS_TREE	=	2;
	private static final int DAMS_TREE		=	3;
	
	//private static final String MAX			=	"MAX";
    private static MNManager instance;
	
	private CoapClientADN context;
	private WaterFlowInstance WFinstance ;
	private OM2MManager mng ;
	private ArrayList<AEResource> ae;
	private ArrayList<ContainerResource> cnt;
	private ArrayList<ContainerResource> SensorCnt;
	private ArrayList<ContainerResource> DamCnt;
	private ArrayList<ContainerResource> INSensorCnt;
	private ArrayList<ContainerResource> INDamCnt;
	private ArrayList<InstanceResource> inst;
	private boolean isMN;
	
	public static MNManager getInstance(String ip) {
		if(instance == null)
			instance = new MNManager(ip);
			return instance;
	}
	
	private MNManager(String ip) {
		context = CoapClientADN.getInstance();
		WFinstance = context.getwInstance();
		
		mng			= new OM2MManager(ip);
		ae 			= new ArrayList<AEResource>();
		cnt 		= new ArrayList<ContainerResource>();
		SensorCnt 	= new ArrayList<ContainerResource>();
		INSensorCnt = new ArrayList<ContainerResource>();
		DamCnt 		= new ArrayList<ContainerResource>();
		INDamCnt 	= new ArrayList<ContainerResource>();
		inst 		= new ArrayList<InstanceResource>();
		isMN 		= true;
		
		
	}
	
	public void createStructure() {
		createAE();
		createAEGPS();
		createAEState();
		createDamAndSensorCNT();
	}
	
	private  void createAE() {
		//ArrayList<OM2MResource> references = mng.discovery(isMN, OM2MManager.REMOTE_CSE, null);	
		WFinstance = Setup.getWinstance();
		JSONObject json = mng.jsonAE(WFinstance.getName()+"-ID", WFinstance.getName(), true);
		ae.add(mng.createAE(isMN, json));
	}
	
	private void createAEGPS() {
		JSONObject json = mng.jsonContainer(GPS,  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject gps = new JSONObject();
		gps.put(LAT, WFinstance.getLat());
		gps.put(LNG, WFinstance.getLng());
		
		json = mng.jsonCI(AUTO_CHANGE, gps.toJSONString().replace("\"", "'"), cnt.get(0).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(AE_GPS_CNT).getRi(), json));
	}
	
	private void createAEState() {
		JSONObject json = mng.jsonContainer(STATE,  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject state = new JSONObject();
		state.put(LEVEL, 1);
		state.put(MESSAGE, OK);
		
		json = mng.jsonCI(AUTO_CHANGE, state.toJSONString().replace("\"", "'"), cnt.get(1).getRi());
		inst.add(mng.createContentInstance(isMN, cnt.get(AE_STATE_CNT).getRi(), json));
	}
	
	//1° level 
	private void createDamAndSensorCNT() {
		JSONObject json = mng.jsonContainer(SENSORS,  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
		
		json = mng.jsonContainer(DAMS,  ae.get(0).getRi());
		cnt.add(mng.createContainer(isMN, ae.get(0).getRi(), json));
	}
	
	private void createSensorCNT(WaterFlowSensor wfs) {
		//main CNT
		JSONObject json = mng.jsonContainer(wfs.getName(), cnt.get(SENSORS_TREE).getRi());
		ContainerResource tmp = mng.createContainer(isMN, cnt.get(SENSORS_TREE).getRi(), json);
		wfs.setRi(tmp.getRi());
		SensorCnt.add(tmp);	
		
		//level CNT
		json = mng.jsonContainer(LEVEL, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(mng.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1).getRi(), json));	
		
		//Threshold CNT
		json = mng.jsonContainer(THRES, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(mng.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));	
		
		//GPS CNT
		json = mng.jsonContainer(GPS, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(mng.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));	
		
	}
	
	private void createDamCNT(String damName) {
		//main CNT
		JSONObject json = mng.jsonContainer(damName, cnt.get(DAMS_TREE).getRi());
		DamCnt.add(mng.createContainer(isMN, cnt.get(DAMS_TREE).getRi(), json));	
		
		//State CNT
		json = mng.jsonContainer(STATE, DamCnt.get(DamCnt.size()-1).getRi());
		INDamCnt.add(mng.createContainer(isMN,  DamCnt.get(DamCnt.size()-1).getRi(), json));	
		
		//GPS CNT
		json = mng.jsonContainer(GPS, DamCnt.get(DamCnt.size()-1).getRi());
		INDamCnt.add(mng.createContainer(isMN, DamCnt.get(DamCnt.size()-1).getRi(), json));
	}
	
	private void createLevelContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(LEVEL, wfs.getLevel());
		
		//cercare il giusto contenitore
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(LEVEL) && cnt.getPi() == wfs.getRi()) {		
				JSONObject json = mng.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				inst.add(mng.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	private void createThresholdsContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(THRES, wfs.getThreshold());
		th.put(MIN, wfs.getMin());
		th.put(MAX, wfs.getMax());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(THRES) && cnt.getPi() == wfs.getRi()) {		
				JSONObject json = mng.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				inst.add(mng.createContentInstance(isMN,cnt.getRi(), json));
			}
		}
	}
	
	private void createGPSContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(LAT,wfs.getLat()); 
		th.put(LNG,wfs.getLng());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(GPS) && cnt.getPi() == wfs.getRi()) {		
				JSONObject json = mng.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				inst.add(mng.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	private void createGPSContentInstance(DamActuator dam) {
		JSONObject th = new JSONObject();
		th.put(LAT,dam.getLat());
		th.put(LNG,dam.getLng());
		
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(GPS) && cnt.getPi() == dam.getRi()) {	
					JSONObject json = mng.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
						inst.add(mng.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	private void createDamContentInstance(DamActuator dam) {
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(GPS) && cnt.getPi() == dam.getRi()) {	
				JSONObject  json = mng.jsonCI(AUTO_CHANGE, true, cnt.getRi());
				inst.add(mng.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	
}
