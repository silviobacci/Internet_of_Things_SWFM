package Modules;

import java.util.HashMap;

import org.eclipse.californium.core.CoapClient;

public class Module {
	
	protected HashMap<String, Object> state;
	protected String name;
	protected CoapClient sdConnection,gpsConnection;
	protected String ri;
	
}
