package communication;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

public class Observing {
	private static CoapClientADN context = CoapClientADN.getInstance();

	private Observing() {};
	
	private static void levelObserving(final String name){
		context.getMonitoringModule().get(name).getSDConnection().observe(
		    		new CoapHandler() {
		    			public void onLoad(CoapResponse response) {
		    				context.getMonitoringModule().get(name).updateState( response.getResponseText());
		    				//	System.out.println("updated because level:"+name+" "+response.getResponseText());
		    				
		    			
		    			}
					
		    			public void onError() {
		    				System.err.println("FAILED-----GPS---"+name); 
		    				removeSensor(name);
		    				
		    			}
					}
		    	);
	}
	
	private static void removeSensor(String name) {
		context.getMonitoringModule().remove(name);
		for (String dam : context.getDamAssociations().keySet()) {
			for(String sensor :context.getDamAssociations().get(dam)){
				if(sensor.equals(name))
					context.getDamAssociations().get(dam).remove(sensor);
			}
		}
	}
	
	private static void sGpsObserving(final String name) {
		context.getMonitoringModule().get(name).getGpsConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				context.getMonitoringModule().get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because gps:"+name+" "+response.getResponseText());
	    				//context.checkDamAssociations() ;
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED----GPS----"+name); 
	    				removeSensor(name);
	    			
	    			}
				}
	    	);
	}	
	
	private static void dGpsObserving(final String name) {
		context.getDamModule().get(name).getGpsConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				context.getDamModule().get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because gps:"+name+" "+response.getResponseText());
	    				//context.checkDamAssociations() ;
	    				
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED--------"+name); 
	    				context.getDamModule().remove(name);
	    			}
				}
	    	);
		
	}
	
	private static void damObserving(final String name) {
		context.getDamModule().get(name).getSDConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				context.getDamModule().get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because dam:"+name+" "+response.getResponseText());
	    				
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED--------"+name); 
	    				context.getDamModule().remove(name);
	    			}
				}
	    	);
		
	}
	
	
	public  static void sObserve( final String name) {
		levelObserving(name);
		sGpsObserving(name);
		 
	}
	public static void dObserve( final String name) {
		damObserving(name);
		dGpsObserving(name);
		 
	}
	public static void observeAllSensors() {		
		for(String s : context.getMonitoringModule().keySet()) {
			sObserve(s);
		}
	}
}
