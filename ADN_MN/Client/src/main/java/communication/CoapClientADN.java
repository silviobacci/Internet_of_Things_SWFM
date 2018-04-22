package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import unipi.iot.Client.*;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import Modules.DamActuator;
import Modules.ModulesConstants;
import Modules.WaterFlowSensor;
import OM2M.MNManager;
import Modules.Module;
import configuration.Setup;
import configuration.AdnInstance;
import interaction.GUI;

	public class CoapClientADN extends Thread {

	private static final int 	PERIOD									= 45;
	private static final String GET								 		= "GET";
	private static final String HTTP									= "http://";
	private static final String COAP 									= "coap://[";
	private static final String COAP_END								= "]:5683/";
	private static final String WELL_KNOWN								= ".well-known/core"; 
	
	private static final int SUCCESS									= 1;
	private static final int UNREACHABLE								= 0;
	private static final int UNSUCCESS									= -1;
  
	private static AdnInstance					wInstance ;
	private static HashMap<String,WaterFlowSensor> 		monitoringModule= new HashMap<String, WaterFlowSensor>();
	private static HashMap<String,DamActuator>			damModule 		= new HashMap<String, DamActuator>();
	private static HashMap<String, ArrayList<String>>	damAssociations = new HashMap<String, ArrayList<String>>();
	public static  HashMap<Module, Boolean>				OM2MUpdate		= new HashMap<Module, Boolean>();
	private static GUI myGUI; 
	
	public static void setGUI(GUI g) {
		myGUI	= g; 
	}
	
	public static GUI getGUI() {
		return  myGUI; 
	}
	
	public static Object lock = new Object();
	
    public CoapClientADN() {}

	public static void addMonitoringModule(String name, String address) {
		monitoringModule.put( name, new WaterFlowSensor(name,address) ); 
	}
	
	public CoapClientADN(AdnInstance inst){
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

	public static void checkDamAssociations() {
		String nearestDam = null;
		double minDistance = 0;
		
		for(String s : damAssociations.keySet())
			damAssociations.put(s, new ArrayList<String>());
		
		for(WaterFlowSensor ws : monitoringModule.values()) {
			
			for (DamActuator dam : damModule.values()) {	
				
				if(minDistance > euclideanDistance(dam,ws) || minDistance == 0) {
					minDistance = euclideanDistance(dam,ws);
					nearestDam = dam.getName();
				}	
			}
			
			if(nearestDam != null) {
				damAssociations.get(nearestDam).add(ws.getName());
				minDistance= 0;
				nearestDam = null;
			}
		}
		printAssociations();	
	}
	
	private static double euclideanDistance(DamActuator m, WaterFlowSensor ws) {
		double diffX = Math.pow((((Integer)m.getState().get(JSONParser.GPSX)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSX)).intValue()),2);
		double diffY = Math.pow((((Integer)m.getState().get(JSONParser.GPSY)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSY)).intValue()),2);
		return Math.sqrt(diffX + diffY);
	}
	
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
	
	public static void getModulesAddresses() throws IOException {	
		String ip = "["+wInstance.getAddressBR()+"]"; 
	    String uri,coreUri,str;
	    String inputLine;
	    StringBuffer content;
	    BufferedReader in;
	    char id; 
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
		        		id = uri.charAt(uri.lastIndexOf("]")-1);
		        	  
		        		if(!damModule.containsKey( ModulesConstants.DAM + id ) && !monitoringModule.containsKey( ModulesConstants.SENSOR + id) ) {
		        			isD = isDam(coreUri);
		        			if(isD == SUCCESS )
		        				initializeDam( ModulesConstants.DAM + id,uri);
		        		  
		        			else if(isD != UNREACHABLE ) {
		        				isD = isSensor(coreUri);
		        				if(isD == SUCCESS)
		        					initializeSensor(ModulesConstants.SENSOR + id,uri);
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

	public AdnInstance getwInstance() {
		return wInstance;
	}

	public static void setwInstance(AdnInstance instance) {
		wInstance = instance;
	}

	private static void initializeDam(String moduleName, String uri) {
		
		synchronized(CoapClientADN.lock) {
			 damModule.put( moduleName, new DamActuator(moduleName,uri)); 
			 DamPostJSON(moduleName, ModulesConstants.CLOSED);
			 
			 MNManager.createDamCNT(damModule.get(moduleName));
			 Observing.damObserving(moduleName);
	   	     
			 damAssociations.put(moduleName, new ArrayList<String>());
		}   
		System.out.println(moduleName +" created");
	}
	
	private static void initializeSensor(String moduleName, String uri) throws InterruptedException {
		
		synchronized(CoapClientADN.lock) {
			 monitoringModule.put(  moduleName, new WaterFlowSensor( moduleName,uri) ); 
			 SensorPostJSON(moduleName, new Integer(70), 0,new Integer(60) ,new Integer(500), new Integer(420));
			 
			 MNManager.createSensorCNT(monitoringModule.get(moduleName));
			 Observing.sensorObserving(moduleName);
			 myGUI.addSensorGUI(moduleName);
		 }
	     System.out.println(moduleName +" created");
	}
	
	private  static int isSensor(String address) {
		Request req = new Request(Code.GET);
   	 
		CoapResponse res =  new CoapClient(address).advanced(req);
		
		if(res == null)
			return UNREACHABLE;
   	 	if(res.getResponseText().contains(ModulesConstants.SENSOR))
   	 		return SUCCESS;
   	 	return UNSUCCESS;
   	 	
	}
	
	private  static int isDam(String address) {
		Request req = new Request(Code.GET);
	
		CoapResponse res =  new CoapClient(address).advanced(req);

		if(res == null)
			return UNREACHABLE;
   	 	if(res.getResponseText().contains(ModulesConstants.DAM))
   	 		return SUCCESS;
   	 	return  UNSUCCESS;
   	 	
	}
		
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
	
	public static void InitializeContext(int wl, int threshold, int min, int max, int wt) {
		System.out.println("initializing context..");
		for(String s : monitoringModule.keySet()) {
			SensorPostJSON(s, wl, 0, min, max, threshold);
		}
		
	}
	
	public  static CoapResponse SensorPostJSON(String name, Integer wl, Integer evo, Integer min, Integer max, Integer wt) {
		synchronized(lock) {
		boolean atLeastOne=false;
		String json = "json={";
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		
		if(wl != null) {
			json += "\""+JSONParser.WL+"\":"+wl.intValue();
			atLeastOne= true;
		}
		
		if(evo != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\""+JSONParser.EVO+"\":"+evo.intValue();
		
		}
			
		if(wt != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\""+JSONParser.WT+"\":"+wt.intValue();

		}
		if(min != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\""+JSONParser.MIN+"\":"+min.intValue();

		}
		if(max != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\""+JSONParser.MAX+"\":"+max.intValue();

		}
		

		
		json += "}";
		
		req.setPayload(json);

		return monitoringModule.get(name).getSDConnection().advanced(req);
		}
	}
	
	public static CoapResponse DamPostJSON(String name, String control) {	
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\""+JSONParser.STATE+"\":\""+control+"\"}");
    	if(control == ModulesConstants.OPEN)
			damModule.get(name).setOpened();
    	else
    		damModule.get(name).setClosed();
		return damModule.get(name).getSDConnection().advanced(req);
	}
	
	public String getRaw(CoapResponse response) {
    	return response.getResponseText();
	}

	public static HashMap<String, ArrayList<String>> getDamAssociations() {
		return damAssociations;
	}
	
	public static  HashMap<String, WaterFlowSensor> getMonitoringModule() {
		return monitoringModule;
	}

	public  static void setMonitoringModule(HashMap<String, WaterFlowSensor> mm) {
		monitoringModule = mm;
	}

	public static HashMap<String, DamActuator> getDamModule() {
		return damModule;
	}

	public static void setDamModule(HashMap<String, DamActuator> dModule) {
		damModule = dModule;
	}

	@Override
	public void run() {
		wInstance 	= Setup.getWinstance();
		super.run();
		
		while(true) {
			
			try {
				getModulesAddresses();
			    TimeUnit.SECONDS.sleep(PERIOD);
			
			} catch (IOException e) {
				
				e.printStackTrace();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}
}