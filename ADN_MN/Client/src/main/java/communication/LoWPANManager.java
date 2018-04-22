package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import unipi.iot.Client.*;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import Modules.DamActuator;
import Modules.WaterFlowSensor;
import OM2M.MNManager;
import OM2M.ModulesManager;
import Modules.Module;
import configuration.Setup;
import configuration.AdnInstance;
import interaction.GUI;

	public class LoWPANManager extends Thread {
	private static int 	PERIOD									= 70;
	private static final String GET								 		= "GET";
	private static final String HTTP									= "http://";
	private static final String COAP 									= "coap://[";
	private static final String COAP_END								= "]:5683/";
	private static final String WELL_KNOWN								= ".well-known/core"; 
	private static final int SUCCESS									= 1;
	private static final int UNREACHABLE								= 0;
	
	private static AdnInstance					wInstance ;
	public static HashMap<String,WaterFlowSensor> 		monitoringModule= new HashMap<String, WaterFlowSensor>();
	public static HashMap<String,DamActuator>			damModule 		= new HashMap<String, DamActuator>();
	private static HashMap<String, ArrayList<String>>	damAssociations = new HashMap<String, ArrayList<String>>();
	public static  HashMap<Module, Boolean>				OM2MUpdate		= new HashMap<Module, Boolean>();
	private static GUI myGUI; 
	public static Object lock = new Object();
	
	public static void setGUI(GUI g) {
		myGUI	= g; 
	}
	
	public static GUI getGUI() {
		return  myGUI; 
	}
	
    public LoWPANManager() {}

	public static void addMonitoringModule(String name, String address) {
		monitoringModule.put( name, new WaterFlowSensor(name,address) ); 
	}
	
	public LoWPANManager(AdnInstance inst){
		wInstance = inst; 
	}
	
	private static void printAssociations() {
		System.out.println("ASSOCIATIONS:");
		
		for(String s : damAssociations.keySet()) {
			System.out.print(s+":");
			
			for(String sensor : damAssociations.get(s))
				System.out.print("  "+sensor);
			
			System.out.println();
		}
		System.out.println("-------------------\n");	
	}

	//Associate dams to sensors
	public synchronized static void checkDamAssociations() {
		String nearestDam = null;
		double minDistance = 0;
		synchronized(LoWPANManager.lock) {
			for(String s : damAssociations.keySet())
				damAssociations.put(s, new ArrayList<String>());
			
			for(WaterFlowSensor ws : monitoringModule.values()) {		
				for (DamActuator dam : damModule.values()) {	
					if(minDistance > euclideanDistance(dam,ws) || minDistance == 0) {
						minDistance = euclideanDistance(dam,ws);
						nearestDam = dam.getName();
					}	
				}
				
				if(damAssociations.get(nearestDam)!= null && nearestDam != null) {
					damAssociations.get(nearestDam).add(ws.getName());
					minDistance= 0;
					nearestDam = null;
				}
			}
			printAssociations();	
		}
	}
	
	private static double euclideanDistance(DamActuator m, WaterFlowSensor ws) {
		double diffX = Math.pow((((Integer)m.getState().get(JSONParser.GPSX)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSX)).intValue()),2);
		double diffY = Math.pow((((Integer)m.getState().get(JSONParser.GPSY)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSY)).intValue()),2);
		return Math.sqrt(diffX + diffY);
	}
	
	//Associate a sensor to a dam
	public static void associateToDam(WaterFlowSensor ws) {
		String nearestDam = null;
		double minDistance = 0;
		
		for (DamActuator dam : damModule.values()) {
			if(minDistance < euclideanDistance(dam,ws) || minDistance == 0) {
				minDistance = euclideanDistance(dam,ws);
				nearestDam = dam.getName();
			}	
		}
		
		damAssociations.get(nearestDam).add(ws.getName());
	}
	
	//6LoWPAN addresses retrieving, initializing internal structures
	private   static void getModulesAddresses() throws IOException {	
		String ip = "["+wInstance.getAddressBR()+"]"; 
	    String uri,coreUri,str;
	    String inputLine;
	    StringBuffer content;
	    BufferedReader in;
	    String [] routes; 
	    URL url;
	    HttpURLConnection con;  
	    int i=0,isD=0;
	    
	    	try {
	    		url = new URL(HTTP+ip);
	    		con = (HttpURLConnection) url.openConnection();
	    		con.setRequestMethod(GET);
				
				System.out.println("HTTP response:"+con.getResponseCode());
				
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				content = new StringBuffer();
				  
				while ((inputLine = in.readLine()) != null)
					content.append(inputLine);
				 
				in.close();   
		        str = content.substring(content.lastIndexOf("<pre>")+5);
		        str = str.substring(0,str.indexOf("<"));
		        routes =str.split(" ");
		        
		        while( routes.length != i && routes[i] != null) {
		        	 
		        	if(routes[i].contains("/") == true) {
		        		 
		        		if(routes[i].contains("s"))
		        			routes[i]= routes[i].substring(routes[i].indexOf("s")+1);
		        		 
		        		routes[i] = routes[i].substring(0,routes[i].length()-4);
		        		uri = COAP+routes[i]+COAP_END;
		        		coreUri = uri+WELL_KNOWN;
		        		String core = MNManager.getCore(coreUri);
		        		if(core != null) {
			        		if(!damModule.containsKey( Constants.DAM +  getID(core)) && !monitoringModule.containsKey( Constants.SENSOR +  getID(core)) ) {
			        			
			        			isD = MNManager.isDam(core);
			        			if(isD == SUCCESS )
			        				initializeDam( Constants.DAM + getID(core),uri);
			        		  
			        			else if(isD != UNREACHABLE ) {
			      
			        				isD = MNManager.isSensor(core);
			        				if(isD == SUCCESS)
			        					initializeSensor(Constants.SENSOR + getID(core),uri);
			        			}
			        		}
		        		}
		        	}else
		        		 i++;       	 
		          }
		      
	     } catch (Exception e) {
	          System.out.println("eccezione ");
	          e.printStackTrace();
	         
	     }	
	    	checkDamAssociations() ;
	}
	
	private static String getID(String toParse) {
		return toParse.substring(toParse.indexOf("_")+1,toParse.indexOf("_")+toParse.substring(toParse.indexOf("_")+1, toParse.length()-1).indexOf(">")+1); 	
	}

	public   AdnInstance getwInstance() {
		return wInstance;
	}

	public   static void setwInstance(AdnInstance instance) {
		wInstance = instance;
	}

	//Initialize dam internal structures
	private static void initializeDam(String moduleName, String uri) {
		DamActuator dam =  new DamActuator(moduleName,uri);
		
		 damModule.put( moduleName, dam); 
		 ModulesManager.DamPostJSON(moduleName, Constants.CLOSED);
	
		 MNManager.createDamCNT(dam);
		 Observing.damObserving(moduleName);
		 myGUI.addDamGUI(moduleName);
		 damAssociations.put(moduleName, new ArrayList<String>());  
		 
		 System.out.println(moduleName +" created");
	}
	
	//Initialize sensor internal structures
	private static void initializeSensor(String moduleName, String uri) throws InterruptedException {

		WaterFlowSensor wfs =new WaterFlowSensor( moduleName,uri);
		monitoringModule.put(  moduleName, wfs  ); 
		ModulesManager.SensorPostJSON(moduleName, new Integer(190), 0,new Integer(0) ,new Integer(500), new Integer(380));			 
		MNManager.createSensorCNT(wfs);
		Observing.sensorObserving(moduleName);
		myGUI.addSensorGUI(moduleName);
		System.out.println(moduleName +" created");
	}
	
	//GPS resource getter (COAP GET)
	public static CoapResponse gpsPostJSON(String name, int x, int y) {
		String json = "json={";
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
	
		json += "\""+JSONParser.GPSX+"\":"+x+",";
		json += "\""+JSONParser.GPSX+"\":"+y;
		json += "}";
		
		req.setPayload(json);
    	return monitoringModule.get(name).getGpsConnection().advanced(req);
	}
	
	//Initialize sensor (COAP POST)
	public   static void InitializeContext(int wl, int threshold, int min, int max, int wt) {
		System.out.println("initializing context..");
		for(String s : monitoringModule.keySet()) {
			ModulesManager.SensorPostJSON(s, wl, 0, min, max, threshold);
		}
		
	}
	
	public   String getRaw(CoapResponse response) {
    	return response.getResponseText();
	}

	public  synchronized  static HashMap<String, ArrayList<String>> getDamAssociations() {
		return damAssociations;
	}
	
	public synchronized static  HashMap<String, WaterFlowSensor> getMonitoringModule() {
		return monitoringModule;
	}

	public static void setMonitoringModule(HashMap<String, WaterFlowSensor> mm) {
		monitoringModule = mm;
	}

	public synchronized  static HashMap<String, DamActuator> getDamModule() {
		return damModule;
	}

	public   static void setDamModule(HashMap<String, DamActuator> dModule) {
		damModule = dModule;
	}

	//Removing sensor from dam association structure
	@SuppressWarnings("unlikely-arg-type")
	public synchronized static void removeSensor(String name) {
		synchronized(LoWPANManager.lock) {
			LoWPANManager.getMonitoringModule().remove(name);
			HashMap<String,ArrayList<String>> associations =  LoWPANManager.getDamAssociations();
			
				for (String dam : associations.keySet()) {
					for(String sensor : associations.get(dam)){
						if(sensor.equals(name)) {
							Iterator<String> iter = associations.get(dam).iterator();
							while(iter.hasNext()  ) {
								iter.next();
								
								if(iter.equals(name))
									iter.remove();
							}
						}
					}
				}
			}
		}
	
	//remove dam structure
	public synchronized static void removeDam(String name) {
		synchronized(LoWPANManager.lock) {
			LoWPANManager.getDamAssociations().remove(name);
			LoWPANManager.getDamModule().remove(name);
		}
	}
	
	@Override
	public  void run() {
		wInstance 	= Setup.getWinstance();
		super.run();
		
		while(true) {
			try {
				getModulesAddresses();
				System.out.println("wait");
			    TimeUnit.SECONDS.sleep(PERIOD);
				System.out.println("wakw up");
			
			} catch (IOException e) {
				
				e.printStackTrace();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}

	public static void setPERIOD(int pERIOD) {
		PERIOD = pERIOD;
	}
}