package communication;

import java.util.HashMap;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import Modules.WaterFlowSensor;
import OM2M.SUpdater;
import unipi.iot.Client.JSONParser;


public class LevelObserver implements CoapHandler {
	
	private final String name;
	private SUpdater wfsUP ;
	private boolean first = true;
	protected HashMap<String, Object> state = new HashMap<String, Object>();
	WaterFlowSensor tmp;
	
	public LevelObserver(WaterFlowSensor w) {
		name = w.getName();
		wfsUP = new SUpdater(w);
		
	}
	
	private void savePreviousState() {
		tmp = CoapClientADN.getMonitoringModule().get(name);
		state.put(JSONParser.WL, tmp.getLevel());
		state.put(JSONParser.WT, tmp.getThreshold());
		state.put(JSONParser.GPSX, tmp.getLng());
		state.put(JSONParser.GPSY, tmp.getLat());
		state.put(JSONParser.MIN, tmp.getMin());
		state.put(JSONParser.MAX, tmp.getMax());
	}	
	
	private void checkWhatChanged() {
		tmp = CoapClientADN.getMonitoringModule().get(name);
		synchronized(wfsUP) {
			if(((Integer)state.get(JSONParser.WL)).intValue() != tmp.getLevel()  )
				wfsUP.levelChanged = true;
			
			if(((Integer)state.get(JSONParser.WT)).intValue() != tmp.getThreshold() || ((Integer)state.get(JSONParser.MIN)).intValue() != tmp.getMin() || 
					((Integer)state.get(JSONParser.MAX)).intValue() != tmp.getMax() )
				wfsUP.thresChanged = true;
			
			if(((Integer)state.get(JSONParser.GPSX)).intValue() != tmp.getLng () || ((Integer)state.get(JSONParser.GPSY)).intValue() != tmp.getLat()  )
				wfsUP.gpsChanged = true;
		
		}
	}
	
	public  void onLoad(CoapResponse response) {
		Thread.currentThread().setName(name); 
			
		savePreviousState();
		CoapClientADN.getMonitoringModule().get(name).updateState( response.getResponseText());
		checkWhatChanged();
		
		if(first) {
			wfsUP.start();
			first = false; 
		}else {
			synchronized(wfsUP) {
				wfsUP.notify();
			}
		}
		 System.out.println("Coap ending on load of:"+CoapClientADN.getMonitoringModule().get(name).getName());
	
	}

	public void onError() {
		System.err.println("FAILED-----GPS---"+name); 
		removeSensor(name);	
	}



	private static void removeSensor(String name) {
		CoapClientADN.getMonitoringModule().remove(name);
		
		for (String dam : CoapClientADN.getDamAssociations().keySet()) {
			for(String sensor :CoapClientADN.getDamAssociations().get(dam)){
				if(sensor.equals(name))
					CoapClientADN.getDamAssociations().get(dam).remove(sensor);
			}
		}
	}
}
