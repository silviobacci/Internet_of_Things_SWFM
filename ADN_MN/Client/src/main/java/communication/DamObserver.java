package communication;

import java.util.HashMap;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import Modules.DamActuator;
import OM2M.DDeleter;
import OM2M.DUpdater;
import unipi.iot.Client.JSONParser;

public class DamObserver implements CoapHandler {
    
	private final String name;
	private DUpdater damUP ;
	private boolean first = true;
	protected HashMap<String, Object> state = new HashMap<String, Object>();
	protected DamActuator tmp;
	
	public DamObserver(DamActuator dam) {
		name = dam.getName();
		damUP = new DUpdater(dam);
		
	}
	
	private void savePreviousState() {
		tmp = LoWPANManager.getDamModule().get(name);
		state.put(JSONParser.STATE, tmp.getDState());
		state.put(JSONParser.GPSX, tmp.getLng());
		state.put(JSONParser.GPSY, tmp.getLat());
	}	
	
	private void checkWhatChanged() {
		tmp = LoWPANManager.getDamModule().get(name);
		state.get(JSONParser.STATE).toString();
		if(!(state.get(JSONParser.STATE)).toString().equals(tmp.getDState()) )
			damUP.stateChanged = true;
		
		if(((Integer)state.get(JSONParser.GPSX)).intValue() != tmp.getLng() || ((Integer)state.get(JSONParser.GPSY)).intValue() != tmp.getLat()  )
			damUP.gpsChanged = true;
		
	}
	
	public  void onLoad(CoapResponse response) {
		Thread.currentThread().setName(name); 	
		
		savePreviousState();
		LoWPANManager.getDamModule().get(name).updateState( response.getResponseText());
		checkWhatChanged();
		
		if(first) {
			damUP.stateChanged = true;
			damUP.start();
			first = false; 
		}else {
			synchronized(damUP) {
				damUP.notify();
			}
		}
	}

	public void onError() {
		System.err.println("FAILED-----GPS---"+name); 
		DDeleter tmp = new DDeleter(name);
		tmp.start();			
	}
	
}
