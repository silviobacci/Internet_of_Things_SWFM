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
	private static ArrayList<CoapObserveRelation> relation = new ArrayList<CoapObserveRelation>();
	

public ArrayList<String> getJProperties() {
	ArrayList<String> jsonProp= new ArrayList<String>();
	jsonProp.add("to_reach");
	jsonProp.add("w_l");
	jsonProp.add("evolution");
	jsonProp.add("w_t");
	
	return jsonProp;
}

public void getModulesAddresses() throws IOException {	
	String ip = "[fd00::c30c:0:0:1]";
    String port = "80";
    String send_message = "GET / HTTP/1.1\r\n\r\n";
    StringTokenizer tokenizer;
    
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
            //System.out.println("Content: " + str);
          } while (str != null);
          //System.out.println("\nServer Reponse: " + message);
          System.out.println(""+tmp);
          
          str = tmp.substring(tmp.lastIndexOf("<pre>")+5);
          str=str.substring(0,str.indexOf("<"));
          String [] routes =str.split(" ");
          int i=0,j=1;
          System.out.println(routes[1]+"  "+routes[1].contains("/"));
          while(routes[i] != null) {
        	 
        	 if(routes[i].contains("/") == true) {
        	   routes[i] = routes[i].substring(0,routes[i].length()-4);
      
        	   System.out.println("name:"+"Sensor"+(j)+" address:"+"coap://["+routes[i]+"]:5683/example");
        	   monitoringModule.put( "Sensor"+(j), new WaterFlowSensor(getJProperties(),"Sensor"+(j++),"coap://["+routes[i++]+"]:5683/example") ); 
        	 }else
        		  	i++;
        	 
          }
       
      
          
      } catch (Exception e) {
          System.out.println(e.getMessage());
      }
	
}
	public void addMonitoringModule(String name, String address) {
		monitoringModule.put( name, new WaterFlowSensor(getJProperties(),name,address) ); 
	}
	private CoapClientADN() {
		
		//monitoringModule.put( "Sensor1", new WaterFlowSensor(getJProperties(),"Sensor1","coap://[fd00::c30c:0:0:2]:5683/example") ); 
		//monitoringModule.put( "Sensor2", new WaterFlowSensor(getJProperties(),"Sensor2","coap://[fd00::c30c:0:0:3]:5683/example") ); 
		
		//relation.add();
	}
	
	public static CoapClientADN getInstance(){
	    if (instance == null)
	    	instance = new CoapClientADN();

	    	return instance; 
	 }
	
	
	
	public static CoapObserveRelation observe(final String name) {
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
	
	public CoapResponse postJSON(String name) {
		DamActuator da = new DamActuator(true, 40, "pippo");
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\"w_l\":10,\"evolution\":1,\"to_reach\":250}");
    	return monitoringModule.get(name).getConnection().advanced(req);
	}
	
	/*public CoapResponse getJSON(int i) {
		DamActuator da = new DamActuator(true, 40, "pippo");
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setObserve();
    	return coapClient.get(i).advanced(req);
	}*/
	
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
    	CoapResponse r= (new CoapClient("[fd00::c30c:0:0:1]").advanced(req) );
    	System.out.println("get:"+r.getResponseText());
	
	}
}