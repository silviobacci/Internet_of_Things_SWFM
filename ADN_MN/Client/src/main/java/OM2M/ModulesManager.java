package OM2M;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;

import communication.LoWPANManager;
import unipi.iot.Client.JSONParser;

public class ModulesManager {
	private static final int MAXIMUM_NUMBER_ATTEMPTS = 3;

	public static CoapResponse SensorPostJSON(String name, Integer wl, Integer evo, Integer min, Integer max, Integer wt) {

		String json;
		
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		
		json = buildJSON(wl,evo,min,max,wt);		
		json += "}";
		
		req.setPayload(json);
		
		CoapClient client = null;
		
		if(LoWPANManager.monitoringModule.get(name) != null)
			client =  LoWPANManager.monitoringModule.get(name).getSDConnection();
		
		client.setTimeout(5000);
		CoapResponse res = null;
		int attempts = 0;
		do {
			res = client.advanced(req);
			if(res == null)
				attempts++;
		} while((req.isTimedOut() || res == null) && attempts < MAXIMUM_NUMBER_ATTEMPTS);
			
		
		if(res == null)
			return null;
   	 	return res;

	}
	
	public static String buildJSON(Integer wl, Integer evo, Integer min, Integer max, Integer wt) {
		String json = "json={";
		boolean atLeastOne = false;
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
		return json;
	}
	
	public   static CoapResponse DamPostJSON(String name, String control) {	
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.setPayload("json={\""+JSONParser.STATE+"\":\""+control+"\"}");
		
		CoapClient client =  LoWPANManager.damModule.get(name).getSDConnection();
		client.setTimeout(5000);
		CoapResponse res = null;
		int attempts = 0;
		do {
			res = client.advanced(req);
			
			if(res == null)
				attempts++;
		} while((req.isTimedOut() || res == null) && attempts < MAXIMUM_NUMBER_ATTEMPTS);
		
		if(res == null)
			return null;
   	 	return res;

	}
	
}
