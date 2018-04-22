package oneM2M;

import java.util.Observer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import oneM2M.notifications.AlertNotifier;
import oneM2M.notifications.DamNotifier;
import oneM2M.notifications.HistoryNotifier;
import oneM2M.notifications.MarkerNotifier;
import oneM2M.notifications.ObservableResource;
import oneM2M.notifications.SensorNotifier;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.ReferenceResource;
import servlets.assets.oneM2M.constants.Dam;
import servlets.assets.oneM2M.constants.Sensor;

public class INManagerNotifier {
	private static ObservableResource reference;
	
	private static MarkerNotifier marker;
	private static AlertNotifier alert;
	private static SensorNotifier sensor; 
	private static DamNotifier dam; 
	private static HistoryNotifier history; 
	
	protected static void init() {
		if(reference == null) reference = new ObservableResource();
		if(marker == null) marker = new MarkerNotifier();
		if(alert == null) alert = new AlertNotifier();
		if(sensor == null) sensor = new SensorNotifier();
		if(dam == null) dam = new DamNotifier();
		if(history == null) history = new HistoryNotifier();
	}
	
	public static void addObserver(Observer ob) {
		synchronized(reference) {reference.addObserver(ob);}
	}
	
	public static void deleteObserver(Observer ob) {
		synchronized(reference) {reference.deleteObserver(ob);}
	}

	public static void addMarkerObserver(Observer ob) {
		synchronized(marker) {marker.addObserver(ob);}
	}
	
	public static void addAlertObserver(Observer ob) {
		synchronized(alert) {alert.addObserver(ob);}
	}
	
	public static void addSensorObserver(Observer ob) {
		synchronized(sensor) {sensor.addObserver(ob);}
	}
	
	public static void addDamObserver(Observer ob) {
		synchronized(dam) {dam.addObserver(ob);}
	}
	
	public static void addHistoryObserver(Observer ob) {
		synchronized(history) {history.addObserver(ob);}
	}
	
	public static void deleteMarkerObserver(Observer ob) {
		synchronized(marker) {marker.deleteObserver(ob);}
	}
	
	public static void deleteAlertObserver(Observer ob) {
		synchronized(alert) {alert.deleteObserver(ob);}
	}
	
	public static void deleteSensorObserver(Observer ob) {
		synchronized(sensor) {sensor.deleteObserver(ob);}
	}
	
	public static void deleteDamObserver(Observer ob) {
		synchronized(dam) {dam.deleteObserver(ob);}
	}
	
	public static void deleteHistoryObserver(Observer ob) {
		synchronized(history) {history.deleteObserver(ob);}
	}
	
	private static ContainerResource findAEfromContainer(ContainerResource cnt) {
		AEResource copiedMN = INManager.getManager().getter.getAE(cnt.getPi());
		
		while(copiedMN == null) {
			cnt = INManager.getManager().getter.getContainer(cnt.getPi());
			copiedMN = INManager.getManager().getter.getAE(cnt.getPi());
		}
		
		return cnt;
	}
	
	protected static void sendNotification(ReferenceResource ref, InstanceResource instance, ContainerResource destination) {
		ContainerResource ae = findAEfromContainer(destination);
				
		JSONObject json = null;
				
		try {
			json = (JSONObject) JSONValue.parseWithException(instance.getCon().toString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
			
		if(json.get(Sensor.SENSOR_IS_WORKING) == null && json.get(Dam.DAM_IS_WORKING) == null)
			destination = INManager.getManager().getter.getContainer(destination.getPi());
		
		synchronized(marker) {marker.sendNotification(ref, instance, ae, destination);}
		
		synchronized(alert) {alert.sendNotification(ref, instance, ae, destination);}
		
		synchronized(sensor) {sensor.sendNotification(ref, instance, ae, destination);}
		
		synchronized(history) {history.sendNotification(ref, instance, ae, destination);}
		
		synchronized(dam) {dam.sendNotification(ref, instance, ae, destination);}
	}
}
