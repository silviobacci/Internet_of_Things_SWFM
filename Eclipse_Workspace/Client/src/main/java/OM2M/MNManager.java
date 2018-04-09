package OM2M;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import Modules.DamActuator;
import Modules.WaterFlowSensor;
import configuration.Setup;
import configuration.WaterFlowInstance;
import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;

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
	private static WaterFlowInstance WFinstance ;
	private static ArrayList<AEResource> ae = new ArrayList<AEResource>();
	private static ArrayList<ContainerResource> cnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> SensorCnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> DamCnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> INSensorCnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<ContainerResource> INDamCnt 	= new ArrayList<ContainerResource>();
	private static ArrayList<InstanceResource> inst 	= new ArrayList<InstanceResource>();
	private static boolean isMN = true;
	
	public static void setWFI(WaterFlowInstance w) {
		WFinstance = w; 
	}
	
	public  static void createStructure() {
		createAE();
		createAEGPS();
		createAEState();
		createDamAndSensorCNT();
	}
	
	private static void createAE() {
		
		WFinstance = Setup.getWinstance();
		JSONObject json = OM2MManager.jsonAE(WFinstance.getName()+"-ID", WFinstance.getName(), true);
		ae.add(OM2MManager.createAE(isMN, json));
	}
	
	@SuppressWarnings("unchecked")
	private static void createAEGPS() {
		JSONObject json = OM2MManager.jsonContainer(GPS,  ae.get(0).getRi());
		cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject gps = new JSONObject();
		gps.put(LAT, WFinstance.getLat());
		gps.put(LNG, WFinstance.getLng());
		
		json = OM2MManager.jsonCI(AUTO_CHANGE, gps.toJSONString().replace("\"", "'"), cnt.get(0).getRi());
		inst.add(OM2MManager.createContentInstance(isMN, cnt.get(AE_GPS_CNT).getRi(), json));
	}
	
	@SuppressWarnings("unchecked")
	private static void createAEState() {
		JSONObject json = OM2MManager.jsonContainer(STATE,  ae.get(0).getRi());
		cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		
		JSONObject state = new JSONObject();
		state.put(LEVEL, 1);
		state.put(MESSAGE, OK);
		
		json = OM2MManager.jsonCI(AUTO_CHANGE, state.toJSONString().replace("\"", "'"), cnt.get(1).getRi());
		inst.add(OM2MManager.createContentInstance(isMN, cnt.get(AE_STATE_CNT).getRi(), json));
	}
	
	//1° level 
	private static void createDamAndSensorCNT() {
		JSONObject json = OM2MManager.jsonContainer(SENSORS,  ae.get(0).getRi());
		cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
		
		json = OM2MManager.jsonContainer(DAMS,  ae.get(0).getRi());
		cnt.add(OM2MManager.createContainer(isMN, ae.get(0).getRi(), json));
	}
	
	public static void createSensorCNT(WaterFlowSensor wfs) {
		//main CNT
		JSONObject json = OM2MManager.jsonContainer(wfs.getName(), cnt.get(SENSORS_TREE).getRi());
		ContainerResource tmp = OM2MManager.createContainer(isMN, cnt.get(SENSORS_TREE).getRi(), json);
		wfs.setRi(tmp.getRi());
		SensorCnt.add(tmp);	
		
		//level CNT
		json = OM2MManager.jsonContainer(LEVEL, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1).getRi(), json));	
		
		//Threshold CNT
		json = OM2MManager.jsonContainer(THRES, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));	
		
		//GPS CNT
		json = OM2MManager.jsonContainer(GPS, SensorCnt.get(SensorCnt.size()-1) .getRi());
		INSensorCnt.add(OM2MManager.createContainer(isMN, SensorCnt.get(SensorCnt.size()-1) .getRi(), json));	
		
	}
	
	public static void createDamCNT(DamActuator dam) {
		//main CNT
		JSONObject json = OM2MManager.jsonContainer(dam.getName(), cnt.get(DAMS_TREE).getRi());
		ContainerResource tmp = (OM2MManager.createContainer(isMN, cnt.get(DAMS_TREE).getRi(), json));	
		dam.setRi(tmp.getRi());
		DamCnt.add(tmp);	
		
		//State CNT
		json = OM2MManager.jsonContainer(STATE, DamCnt.get(DamCnt.size()-1).getRi());
		INDamCnt.add(OM2MManager.createContainer(isMN,  DamCnt.get(DamCnt.size()-1).getRi(), json));	
		
		//GPS CNT
		json = OM2MManager.jsonContainer(GPS, DamCnt.get(DamCnt.size()-1).getRi());
		INDamCnt.add(OM2MManager.createContainer(isMN, DamCnt.get(DamCnt.size()-1).getRi(), json));
	}
	
	
	public synchronized static void createLevelContentInstance(WaterFlowSensor wfs) {
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(LEVEL) && cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(AUTO_CHANGE, wfs.getLevel(), cnt.getRi());
				inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createThresholdsContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(THRES, wfs.getThreshold());
		th.put(MIN, wfs.getMin());
		th.put(MAX, wfs.getMax());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(THRES) &&  cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				inst.add(OM2MManager.createContentInstance(isMN,cnt.getRi(), json));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createGPSContentInstance(WaterFlowSensor wfs) {
		JSONObject th = new JSONObject();
		th.put(LAT,wfs.getLat()); 
		th.put(LNG,wfs.getLng());
		
		for (ContainerResource cnt : INSensorCnt) {
			if(cnt.getRn().equals(GPS) &&  cnt.getPi().equals(wfs.getRi())) {		
				JSONObject json = OM2MManager.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
				inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void createGPSContentInstance(DamActuator dam) {
		JSONObject th = new JSONObject();
		th.put(LAT,dam.getLat());
		th.put(LNG,dam.getLng());
		
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(GPS) && cnt.getPi().equals(dam.getRi())) {	
					JSONObject json = OM2MManager.jsonCI(AUTO_CHANGE, th.toJSONString().replace("\"", "'"), cnt.getRi());
						inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	public static void damCI(DamActuator dam) {
		for (ContainerResource cnt : INDamCnt) {
			if(cnt.getRn().equals(STATE) && cnt.getPi().equals(dam.getRi())) {	
				JSONObject  json = OM2MManager.jsonCI(AUTO_CHANGE, dam.isOpened(), cnt.getRi());
				inst.add(OM2MManager.createContentInstance(isMN, cnt.getRi(), json));
			}
		}
	}
	
	
}
