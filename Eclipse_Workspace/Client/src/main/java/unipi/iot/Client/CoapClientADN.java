package unipi.iot.Client;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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




public class CoapClientADN {
	private static CoapClientADN instance;
	private static HashMap<String,WaterFlowSensor> monitoringModule=new HashMap<String, WaterFlowSensor>();
	private static HashMap<String,DamActuator> DamModule=new HashMap<String, DamActuator>();
	private static ArrayList<CoapObserveRelation> relation = new ArrayList<CoapObserveRelation>();
	
	private int nSensors = 7; 
	private int nDams = 4; 
	
	
	public void addMonitoringModule(String name, String address) {
		monitoringModule.put( name, new WaterFlowSensor(getJProperties(),name,address) ); 
	}
	private CoapClientADN() {
	
	}
	
	public static CoapClientADN getInstance(){
	    if (instance == null)
	    	instance = new CoapClientADN();

	    	return instance; 
	 }
	

public ArrayList<String> getJProperties() {
	ArrayList<String> jsonProp= new ArrayList<String>();
	jsonProp.add("to_reach");
	jsonProp.add("w_l");
	jsonProp.add("evolution");
	jsonProp.add("w_t");
	
	return jsonProp;
}



public void getModulesAddresses() throws IOException {	
	String ip = "[fd00::c30c:0:0:1]", port = "80";
    String send_message = "GET / HTTP/1.1\r\n\r\n";
    String uri, coreUri;
    char  id; 
    String []routes;    
    int i=0,s=1,d=1;
    
        try {
          Socket sock = new Socket(ip, Integer.parseInt(port));
          System.out.println("Connected OK!\n");
          PrintWriter request = new PrintWriter(sock.getOutputStream(), true);
          request.println(send_message);
       
          InputStream is = sock.getInputStream();
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr);
          String message = br.readLine();
          String str = null,tmp="";
          do {
        	  
            str = br.readLine();
            tmp = tmp+" "+ str;
            
          } while (str != null);
          System.out.println("\nServer Reponse: " + message);
          
          str = tmp.substring(tmp.lastIndexOf("<pre>")+5);
          str=str.substring(0,str.indexOf("<"));
          routes =str.split(" ");
         
 
          while(routes[i] != null) {
        	 
        	 if(routes[i].contains("/") == true) {
        	   routes[i] = routes[i].substring(0,routes[i].length()-4);
        	   uri = "coap://["+routes[i]+"]:5683/example";
        	   coreUri = "coap://["+routes[i]+"]:5683/.well-known/core";
        	   //System.out.println("name:"+"Sensor"+(s)+" address:"+"coap://["+routes[i]+"]:5683/example");
        	   id = uri.charAt(uri.lastIndexOf("]")-1);
        	   if(isDam(coreUri)) {
        		   DamModule.put( "Dam"+(id), new DamActuator(getJProperties(),"Dam"+(id),uri)); 
        		   System.out.println("dam"+id+" created");
        	   }else {
        		   monitoringModule.put( "Sensor"+(id), new WaterFlowSensor(getJProperties(),"Sensor"+(id),"coap://["+routes[i++]+"]:5683/example") ); 
        		   System.out.println("Sensor"+id+" created");
        	   }
        	 }else
        		  	i++;
        	 
          }
       
      
          sock.close();
      } catch (Exception e) {
          System.out.println(e.getMessage());
      }
      
	
}

	
	
	public static CoapObserveRelation observe( final String name) {
		monitoringModule.get(name).getConnection();
		return monitoringModule.get(name).getConnection().observe(
    		new CoapHandler() {
    			public void onLoad(CoapResponse response) {
    				//System.out.println("received:"+response.getResponseText());
    				monitoringModule.get(name).updateState( response.getResponseText());
    			
    			}
			
    			public void onError() {
    				System.err.println("FAILED--------"); 
    			}
			}
    	);
	}
	
	/*public CoapResponse postRaw(int i) {
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.TEXT_PLAIN);
		req.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		req.setPayload(Integer.toString(i));
    	return coapClient.get(i).advanced(req);
	}*/
	
	public static  CoapResponse SensorPostJSON(String name, Integer wl, Integer evo, Integer toReach, Integer wt) {
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
		if(toReach != null) {
			if(atLeastOne)
				json+=",";
			else
				atLeastOne=true;
			json += "\"to_reach\":"+toReach.intValue();
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
    	return monitoringModule.get(name).getConnection().advanced(req);
	}
	

	public static CoapResponse DamPostJSON(String name, String control) {
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\"dam\":\""+control+"\"}");
    	return DamModule.get(name).getConnection().advanced(req);
	}
	
	public boolean isDam(String address) {
		Request req = new Request(Code.GET);
    	 CoapResponse res =  new CoapClient(address).advanced(req);
    	 String type = res.getResponseText();
    	 type = type.substring(  type.lastIndexOf("rt=")+4 , type.length()-1);
    	
    	 if(type.equalsIgnoreCase("Sensor"))
    		 return false;
    	 else
    		 return true;
    	 
    	 
	}
	
	public String getRaw(CoapResponse response) {
    	return response.getResponseText();
	}
	
	//public WaterFlowSensor getJSON(CoapResponse response) throws ParseException {
		/*return new WaterFlowSensor(response.getResponseText());
		parser.getValues(properties,  toParse);
		*/
		//jo.get("title");
		//JSONArray ja = (JSONArray) jo.get("dataset");
		//JSONObject first_jo = (JSONObject) ja.get(0);
//	}
	
	public  void get()  {
		Request req = new Request(Code.GET);
		//req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		//req.setPayload("json={\"w_l\":10,\"evolution\":1,\"to_reach\":201}");
    	CoapResponse r= (new CoapClient("coap://[fd00::c30c:0:0:1]").advanced(req) );
    	//System.out.println("get:"+r.getResponseText());
	
	}



	public static HashMap<String, WaterFlowSensor> getMonitoringModule() {
		return monitoringModule;
	}



	public static void setMonitoringModule(HashMap<String, WaterFlowSensor> monitoringModule) {
		CoapClientADN.monitoringModule = monitoringModule;
	}



	public static HashMap<String, DamActuator> getDamModule() {
		return DamModule;
	}



	public static void setDamModule(HashMap<String, DamActuator> damModule) {
		DamModule = damModule;
	}
}