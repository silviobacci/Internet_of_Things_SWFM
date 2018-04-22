package communication;

import java.util.HashMap;

import org.eclipse.californium.core.CoapObserveRelation;

public class Observing {
	private static HashMap<String,ObservingPair> observings = new HashMap<String,ObservingPair>();
	private Observing() {};
	
	static class ObservingPair{ public CoapObserveRelation moduleObs,gpsObs;}
	
	public static void sensorObserving(final String name){
		LevelObserver tmp = new LevelObserver( LoWPANManager.getMonitoringModule().get(name));
	    registerObservers(name,tmp);
	}
	
	private static void registerObservers(String name, LevelObserver tmp) {
		ObservingPair tmpPair = new ObservingPair();
		tmpPair.moduleObs	=  LoWPANManager.getMonitoringModule().get(name).getSDConnection().observe(tmp);
		tmpPair.gpsObs 		=  LoWPANManager.getMonitoringModule().get(name).getGpsConnection().observe(tmp);
		observings.put(name, tmpPair);
	}
	
	private static void registerObservers(String name, DamObserver tmp) {
		ObservingPair tmpPair = new ObservingPair();
		tmpPair.moduleObs	=  LoWPANManager.getDamModule().get(name).getSDConnection().observe(tmp);
		tmpPair.gpsObs 		=  LoWPANManager.getDamModule().get(name).getGpsConnection().observe(tmp);
		observings.put(name, tmpPair);
	}
	
	public static void damObserving(final String name) {
		DamObserver tmp = new DamObserver( LoWPANManager.getDamModule().get(name));
		registerObservers(name,tmp);
	}
	
	public static void removeModule(String name) {
		observings.get(name).gpsObs.reactiveCancel();
		observings.get(name).moduleObs.reactiveCancel();
		observings.remove(name);	 
	}


}
