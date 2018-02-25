package oneM2M;

import java.util.ArrayList;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import org.eclipse.californium.core.server.resources.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import resources.*;

public class NotificationResource extends CoapResource {
	OM2MManager mng;
	boolean IN;

	public NotificationResource(String name, boolean isIN) {
		super(name, true);
        setObserveType(Type.CON);
        getAttributes().setObservable();
        mng = new OM2MManager();
        IN = isIN;
	}
	
	private ArrayList<InstanceResource> getInstances(CoapExchange res) {
		ArrayList<InstanceResource> instances = new ArrayList<InstanceResource>();
		try {
			JSONObject discJSON = (JSONObject) JSONValue.parseWithException(res.getRequestText());
			JSONArray json = (JSONArray) discJSON.get("m2m:uril");
			
			for(Object jo : json) {
				JSONObject j = (JSONObject) jo;
				if(j.get("m2m:cin") != null)
					instances.add(new InstanceResource((JSONObject) j.get("m2m:cin")));
			}
		} 
		catch (ParseException e) {
			return null;
		}
		
		if(instances.isEmpty())
			return null;
		
		return instances;
	}
	
	public void handleGET(CoapExchange exchange) {}
	
	public void handlePOST(CoapExchange res) {
		JSONObject json;
		System.out.println(res.getRequestText());
		ArrayList<InstanceResource> instances = getInstances(res);
		
		for(InstanceResource i : instances) {
			json = mng.jsonCI("new value", i.getCon());
			
			ArrayList<String> f = new ArrayList<String>();
			f.add("rn=" + i.getPi());
			ArrayList<Resource> containers = mng.discovery(IN, 3, f);
			
			ContainerResource c = (ContainerResource) containers.get(0);
			mng.createContentInstance(IN, c, json);
		}
	}
}
