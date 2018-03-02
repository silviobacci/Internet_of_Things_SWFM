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

import com.google.common.collect.BiMap;

import Modules.DamActuator;
import Modules.ModulesConstants;
import Modules.WaterFlowSensor;
import Modules.Module; 
import configuration.WaterFlowInstance;

	public class CoapClientADN extends Thread {

	private static final String HTTP_GET	= "GET / HTTP/1.1\r\n\r\n";
	private static final int 	HTTP_PORT	= 80;
	private static final int 	PERIOD		= 20;	
	
	private static CoapClientADN instance;
	private HashMap<String,WaterFlowSensor> monitoringModule=new HashMap<String, WaterFlowSensor>();
	private HashMap<String,DamActuator> damModule=new HashMap<String, DamActuator>();
	private ArrayList<CoapObserveRelation> relation = new ArrayList<CoapObserveRelation>();
	private HashMap<String, ArrayList<String>> damAssociations = new HashMap<String, ArrayList<String>>();
    private WaterFlowInstance wInstance;
	
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

	public void checkDamAssociations() {
		String nearestDam = null;
		double minDistance = 0;
		
		for(WaterFlowSensor ws : monitoringModule.values())
			
			for (DamActuator dam : damModule.values()) {	
				
				if(minDistance < euclideanDistance(dam,ws) || minDistance == 0) {
					minDistance = euclideanDistance(dam,ws);
					nearestDam = dam.getName();
				}	
				damAssociations.get(nearestDam).add(ws.getName());
				minDistance= 0;
				nearestDam = null;
		}
		
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
		String port = wInstance.getPortBR()+"";
	    String uri, moduleName,coreUri,str,tmp="";
	    char id; 
	    String [] routes;    
	    InputStream is;
	    InputStreamReader isr;
	    BufferedReader br;
	    
	    int i=0;
	    
	        try {
		          Socket sock = new Socket(ip, HTTP_PORT);
		         // System.out.println("Connected OK!\n");noti
		          PrintWriter request = new PrintWriter(sock.getOutputStream(), true);
		          request.println(HTTP_GET);
		       
		          is = sock.getInputStream();
		          isr = new InputStreamReader(is);
		          br = new BufferedReader(isr);
		          String message = br.readLine();
		          
		          do {  
		        	  str = br.readLine();
		        	  tmp = tmp+" "+ str;
		            
		          }while (str != null);
		          
		          System.out.println("\nServer Reponse: " + message);
		          sock.close();
		          
		          str = tmp.substring(tmp.lastIndexOf("<pre>")+5);
		          str = str.substring(0,str.indexOf("<"));
		          routes =str.split(" ");
		     
		          while(routes[i] != null) {
		        	 
		        	 if(routes[i].contains("/") == true) {
		        		 routes[i] = routes[i].substring(0,routes[i].length()-4);
		        		 uri = "coap://["+routes[i]+"]:5683/";
		        		 coreUri = "coap://["+routes[i]+"]:5683/.well-known/core";
		        		 id = uri.charAt(uri.lastIndexOf("]")-1);
		        	  
		        		 if(!isSensor(coreUri)) {
		        			 moduleName =  ModulesConstants.DAM + id;
		        			 if(!damModule.containsKey(moduleName)) {
		        				 damModule.put( moduleName, new DamActuator(moduleName,uri)); 
		        		   	   	 System.out.println("dam"+id+" created");
		        		   	     dObserve(moduleName);
		        		   	     damAssociations.put(moduleName, new ArrayList<String>());
		        			 }
		        		 }else if(!monitoringModule.containsKey( ModulesConstants.SENSOR + id)) {
		        			     moduleName =  ModulesConstants.SENSOR + id;
		        				 monitoringModule.put(  moduleName, new WaterFlowSensor( moduleName,uri) ); 
		        				 System.out.println( moduleName +" created");
		        				 sObserve( moduleName);
		        				 //associateToDam(monitoringModule.get(moduleName));
		        				
		        		 }
		        	 }else
		        		 i++;       	 
		          }
		      
	      } catch (Exception e) {
	          System.out.println(e.getMessage());
      }	
	}

	public void observeAllSensors() {		
		for(String s : monitoringModule.keySet()) {
			sObserve(s);
		}
	}
	
	private boolean isSensor(String address) {
		Request req = new Request(Code.GET);
   	 	CoapResponse res =  new CoapClient(address).advanced(req);
  
   	 	if(res.getResponseText().contains(ModulesConstants.SENSOR))
   	 		return true;
		return false;
	}
	
	private String [] SensorNames(String str) {
		 String [] names = null; 
		 names[0] = str.substring(  str.indexOf("rt=")+4 ,  str.indexOf("rt=")+9);
		 names[1] = str.substring(  str.lastIndexOf("rt=")+4 ,  str.lastIndexOf("rt=")+9);
		 return names;

	}
	
	public void InitializeContext(int wl, int threshold) {
		System.out.println("initializing context..");
		for(String s : monitoringModule.keySet()) {
			SensorPostJSON(s, wl, 0, null, threshold);
			//System.out.println(s+" initialized");
		}
		
	}
	
	private void levelObserving(final String name){
		 monitoringModule.get(name).getSDConnection().observe(
		    		new CoapHandler() {
		    			public void onLoad(CoapResponse response) {
		    				monitoringModule.get(name).updateState( response.getResponseText());
		    			//	System.out.println("updated because level:"+name+" "+response.getResponseText());
		    			
		    			}
					
		    			public void onError() {
		    				System.err.println("FAILED--------"); 
		    			}
					}
		    	);
	}
	
	private void sGpsObserving(final String name) {
		monitoringModule.get(name).getGpsConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				monitoringModule.get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because gps:"+name+" "+response.getResponseText());
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED--------"); 
	    			}
				}
	    	);
	}	
	
	private void dGpsObserving(final String name) {
		damModule.get(name).getGpsConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				damModule.get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because gps:"+name+" "+response.getResponseText());
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED--------"); 
	    			}
				}
	    	);
		
	}
	
	private void damObserving(final String name) {
		damModule.get(name).getSDConnection().observe(
	    		new CoapHandler() {
	    			public void onLoad(CoapResponse response) {
	    				damModule.get(name).updateState( response.getResponseText());
	    					//System.out.println("updated because dam:"+name+" "+response.getResponseText());
	    			
	    			}
				
	    			public void onError() {
	    				System.err.println("FAILED--------"); 
	    			}
				}
	    	);
		
	}
	
	
	public void sObserve( final String name) {
		levelObserving(name);
		sGpsObserving(name);
		 
	}
	public void dObserve( final String name) {
		damObserving(name);
		dGpsObserving(name);
		 
	}
	
	public  CoapResponse gpsPostJSON(String name, int x, int y) {
		String json = "json={";
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		
		json += "\"gps_x\":"+x+",";
		json += "\"gps_y\":"+y;
		json += "}";
		
		req.setPayload(json);
    	return monitoringModule.get(name).getGpsConnection().advanced(req);
	}
	
	
	public  CoapResponse SensorPostJSON(String name, Integer wl, Integer evo, Integer toReach, Integer wt) {
		//DamActuator da = new DamActuator(true, 40, "pippo");
		boolean atLeastOne=false;
		String json = "json={";
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		if(wl != null) {
			json += "\"w_l\":"+wl.intValue();
			atLeastOne= true;
		}
		
		if(evo != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\"evolution\":"+evo.intValue();
		
		}
			
		if(wt != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\"w_t\":"+wt.intValue();

		}
			json += "}";
		req.setPayload(json);
		//System.out.println("json:"+json);
    	return monitoringModule.get(name).getSDConnection().advanced(req);
	}
	

	public CoapResponse DamPostJSON(String name, String control) {
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\"dam\":\""+control+"\"}");
    	return damModule.get(name).getSDConnection().advanced(req);
	}
	
	public boolean isDam(String address) {
		Request req = new Request(Code.GET);
    	 CoapResponse res =  new CoapClient(address).advanced(req);
    	 String type = res.getResponseText();
    	 
    	
    	 type = type.substring(  type.indexOf("rt=")+4 , type.length()-1);
    	
    	 if(type.equalsIgnoreCase("Sensor"))
    		 return false;
    	 else
    		 return true;	 
	}
	
	public String getRaw(CoapResponse response) {
    	return response.getResponseText();
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
				checkDamAssociations();
				TimeUnit.SECONDS.sleep(PERIOD);
			
			} catch (IOException e) {
				
				e.printStackTrace();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}