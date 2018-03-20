package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import unipi.iot.Client.*;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


import Modules.DamActuator;
import Modules.ModulesConstants;
import Modules.WaterFlowSensor;
import Modules.Module; 
import configuration.WaterFlowInstance;

	public class CoapClientADN extends Thread {

	private static final int 	PERIOD		= 30;
	private static final String GET 		= "GET";
	private static final String HTTP		= "http://";
	private static final String COAP 		= "coap://[";
	private static final String COAP_END	= "]:5683/";
	private static final String WELL_KNOWN	= ".well-known/core"; 
	
	private static CoapClientADN				instance;
	private WaterFlowInstance 					wInstance;
	private HashMap<String,WaterFlowSensor> 	monitoringModule =	new HashMap<String, WaterFlowSensor>();
	private HashMap<String,DamActuator> 		damModule =			new HashMap<String, DamActuator>();
	private HashMap<String, ArrayList<String>>	damAssociations = 	new HashMap<String, ArrayList<String>>();
	
    public void setWInstance(WaterFlowInstance inst) {
    	wInstance = inst; 
    }
 
    private CoapClientADN() {}
    
    public static CoapClientADN getInstance() {
    	if(instance == null)
    		instance = new CoapClientADN();
    	return instance; 
    	
    }
    
	public void addMonitoringModule(String name, String address) {
		monitoringModule.put( name, new WaterFlowSensor(name,address) ); 
	}
	
	public CoapClientADN(WaterFlowInstance inst){
		wInstance = inst; 
	}
	
	private void printAssociations() {
		System.out.println("ASSOCIATIONS:");
		
		for(String s : damAssociations.keySet()) {
			System.out.print(s+":");
			
			for(String sensor : damAssociations.get(s))
				System.out.print("  "+sensor);
			
			System.out.println();
		}
		System.out.println("-------------------\n");
		
	}

	public void checkDamAssociations() {
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
	
	private double euclideanDistance(DamActuator m, WaterFlowSensor ws) {
		double diffX = Math.pow((((Integer)m.getState().get(JSONParser.GPSX)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSX)).intValue()),2);
		double diffY = Math.pow((((Integer)m.getState().get(JSONParser.GPSY)).intValue() - ((Integer)ws.getState().get(JSONParser.GPSY)).intValue()),2);
		return Math.sqrt(diffX + diffY);
	}
	
	public void associateToDam(WaterFlowSensor ws) {
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
	
	public void getModulesAddresses() throws IOException {	
		String ip = "["+wInstance.getAddressBR()+"]"; 
	    String uri,coreUri,str;
	    String inputLine;
	    StringBuffer content;
	    BufferedReader in;
	    char id; 
	    String [] routes; 
	    URL url;
	    HttpURLConnection con;  
	    int i=0;
	    
	    	try {
	    		url = new URL(HTTP+ip);
	    		
	    		con = (HttpURLConnection) url.openConnection();
	    		con.setRequestMethod(GET);
	        	  
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				
				System.out.println("HTTP response:"+con.getResponseCode());
				
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				content = new StringBuffer();
				  
				while ((inputLine = in.readLine()) != null)
					content.append(inputLine);
				 
				in.close();
		          
		        str = content.substring(content.lastIndexOf("<pre>")+5);
		        str = str.substring(0,str.indexOf("<"));
		        routes =str.split(" ");
		     
		        while(routes[i] != null) {
		        	 
		        	if(routes[i].contains("/") == true) {
		        		 
		        		if(routes[i].contains("s"))
		        			routes[i]= routes[i].substring(routes[i].indexOf("s")+1);
		        		 
		        		routes[i] = routes[i].substring(0,routes[i].length()-4);
		        		uri = COAP+routes[i]+COAP_END;
		        		coreUri = uri+WELL_KNOWN;
		        		id = uri.charAt(uri.lastIndexOf("]")-1);
		        	  
		        		 if(isDam(coreUri) && !damModule.containsKey( ModulesConstants.DAM + id ))
		        				 initializeDam( ModulesConstants.DAM + id,uri);
		        		   	     
		        		 else if(isSensor(coreUri) && !monitoringModule.containsKey( ModulesConstants.SENSOR + id)) 
		        			     initializeSensor(ModulesConstants.SENSOR + id,uri);
		        	}else
		        		 i++;       	 
		          }
		      
	      } catch (Exception e) {
	          System.out.println("eccezione "+e.getMessage());
      }	
	}

	private void initializeDam(String moduleName, String uri) {
		 damModule.put( moduleName, new DamActuator(moduleName,uri)); 
   	   	 Observing.dObserve(moduleName);
   	     damAssociations.put(moduleName, new ArrayList<String>());
   	     DamPostJSON(moduleName, ModulesConstants.CLOSED);
   	     damModule.get(moduleName).setClosed();
   	   
	}
	
	private void initializeSensor(String moduleName, String uri) {
		 monitoringModule.put(  moduleName, new WaterFlowSensor( moduleName,uri) ); 
		 Observing.sObserve( moduleName);
	   //  SensorPostJSON(moduleName, new Integer(70), 0,new Integer(60) ,new Integer(500), new Integer(420));
		  SensorPostJSON(moduleName, null, 1, null ,null, null);
	     System.out.println(moduleName +" created");
	}
	
	private boolean isSensor(String address) {
		Request req = new Request(Code.GET);
   	 	CoapResponse res =  new CoapClient(address).advanced(req);
  
   	 	if(res.getResponseText().contains(ModulesConstants.SENSOR))
   	 		return true;
   	 	return false;
   	 	
	}
	
	private boolean isDam(String address) {
		Request req = new Request(Code.GET);
   	 	CoapResponse res =  new CoapClient(address).advanced(req);
  
   	 	if(res.getResponseText().contains(ModulesConstants.DAM))
   	 		return true;
   	 	return false;
   	 	
	}
		
	public  CoapResponse gpsPostJSON(String name, int x, int y) {
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
	
	public void InitializeContext(int wl, int threshold, int min, int max, int wt) {
		System.out.println("initializing context..");
		for(String s : monitoringModule.keySet()) {
			SensorPostJSON(s, wl, 0, min, max, threshold);
		}
		
	}
	
	public  CoapResponse SensorPostJSON(String name, Integer wl, Integer evo, Integer min, Integer max, Integer wt) {
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
		System.out.println("jjjjjjjjjjj:"+json);
		req.setPayload(json);
		monitoringModule.get(name).updateState(json.substring(5));
    	
		return monitoringModule.get(name).getSDConnection().advanced(req);
	}
	
	public CoapResponse DamPostJSON(String name, String control) {	
		Request req = new Request(Code.POST);
		
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\""+JSONParser.STATE+"\":\""+control+"\"}");
    	
		return damModule.get(name).getSDConnection().advanced(req);
	}
	
	public String getRaw(CoapResponse response) {
    	return response.getResponseText();
	}

	public HashMap<String, ArrayList<String>> getDamAssociations() {
		return damAssociations;
	}
	
	public  HashMap<String, WaterFlowSensor> getMonitoringModule() {
		return monitoringModule;
	}

	public  void setMonitoringModule(HashMap<String, WaterFlowSensor> monitoringModule) {
		this.monitoringModule = monitoringModule;
	}

	public  HashMap<String, DamActuator> getDamModule() {
		return damModule;
	}

	public  void setDamModule(HashMap<String, DamActuator> dModule) {
		damModule = dModule;
	}

	@Override
	public void run() {
		
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