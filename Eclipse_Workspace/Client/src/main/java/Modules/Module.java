package Modules;

import java.util.HashMap;
import java.util.Observable;

import org.eclipse.californium.core.CoapClient;

public class Module extends Observable {
	
	protected HashMap<String, Object> state;
	protected String name;
	protected CoapClient sdConnection,gpsConnection;
	protected String ri;

	public boolean isSensor() {
		if(name.contains(ModulesConstants.SENSOR))
			return true;
		return false; 
	}
}
