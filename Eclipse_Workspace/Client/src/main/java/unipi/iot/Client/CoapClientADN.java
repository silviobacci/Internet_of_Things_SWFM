package unipi.iot.Client;

import java.util.ArrayList;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;


public class CoapClientADN {
	private static CoapClientADN instance;
	private static ArrayList<CoapClient> coapClient = new ArrayList<CoapClient>();
	private static ArrayList<CoapObserveRelation> relation = new ArrayList<CoapObserveRelation>();
	

	
	
	private CoapClientADN() {
		//qui andranno aggiunti tutti basandosi su stringhe contenenti gli indirizzi 
		coapClient.add( new CoapClient("coap://[fd00::c30c:0:0:2]:5683/example") ); 
		
		
		//relation.add();
	}
	
	public static CoapClientADN getInstance(){
	    if (instance == null)
	    	instance = new CoapClientADN();

	    	return instance; 
	 }
	
	
	
	public static CoapObserveRelation observe(/*String indirizzi*/ int i) {
		return coapClient.get(i).observe(
    		new CoapHandler() {
    			public void onLoad(CoapResponse response) {
    				System.out.println("RISPOSTA OBSERVING: " + response.getResponseText());
    			}
			
    			public void onError() {
    				System.err.println("FAILED--------"); 
    			}
			}
    	);
	}
	
	public CoapResponse postRaw(int i) {
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.TEXT_PLAIN);
		req.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
		req.setPayload(Integer.toString(i));
    	return coapClient.get(i).advanced(req);
	}
	
	public CoapResponse postJSON(int i) {
		DamActuator da = new DamActuator(true, 40, "pippo");
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\"w_level\":2000,\"evolution\":-5,\"to_reach\":100}");
    	return coapClient.get(i).advanced(req);
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
	
	public WaterFlowSensor getJSON(CoapResponse response) throws ParseException {
		return new WaterFlowSensor(response.getResponseText());
		parser.getValues(properties,  toParse);
		
		//jo.get("title");
		//JSONArray ja = (JSONArray) jo.get("dataset");
		//JSONObject first_jo = (JSONObject) ja.get(0);
	}
	
	public void get(int i) throws ParseException {
		CoapResponse response = coapClient.get(i).get();
		if (response.getOptions().getContentFormat() == MediaTypeRegistry.TEXT_PLAIN)
			getRaw(response);
		else
			getJSON(response);
	}
}