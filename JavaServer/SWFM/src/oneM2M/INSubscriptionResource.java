package oneM2M;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import resources.ReferenceResource;

public class INSubscriptionResource extends CoapResource {	
	INManager mng;
	
	public INSubscriptionResource(INManager m, String ip, String name, int port) {
		super(name);
		mng = m;
		getAttributes().setTitle(name);
	}
	
	private boolean isNotify(CoapExchange exchange) {
		JSONObject json;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get("m2m:sgn");
			json = (JSONObject) json.get("m2m:nev");
			
			return json != null;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void createMNReference(JSONObject jo) {
		ReferenceResource notified = new ReferenceResource(jo);
		
		mng.createMNReference(notified);
	}
	
	private void handleNotify(CoapExchange exchange) {
		JSONObject json;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get("m2m:sgn");
			json = (JSONObject) json.get("m2m:nev");
			json = (JSONObject) json.get("m2m:rep");
			
			if(json.get("m2m:csr") != null) createMNReference((JSONObject) json.get("m2m:csr"));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void handleGET(CoapExchange exchange) {}
	
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.CREATED);
		
		if(isNotify(exchange))
			new Notifier(exchange).start();
	}
	
	private class Notifier extends Thread {
		CoapExchange exchange;
		
		public Notifier(CoapExchange ex) {
			exchange = ex;
		}
		
		@Override
		public void run() {
			handleNotify(exchange);
			super.run();
		}
	}
}