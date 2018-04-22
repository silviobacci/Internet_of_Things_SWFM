package OM2M;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;


public abstract class SubscriptionServerResource extends CoapResource {	
	protected final int SERVER_PORT;
	
	public SubscriptionServerResource(String name, int port) {
		super(name);
		SERVER_PORT = port;
		getAttributes().setTitle(name);
	}
	
	private boolean isNotify(CoapExchange exchange) {
		JSONObject json;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get(OM2MManager.NOTIFICATION);
			json = (JSONObject) json.get(OM2MManager.NOTIFICATION_EVENT);
			
			return json != null;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void handleNotify(CoapExchange exchange) {
		JSONObject json = null;
		try {
			json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
			
			json = (JSONObject) json.get(OM2MManager.NOTIFICATION);
			json = (JSONObject) json.get(OM2MManager.NOTIFICATION_EVENT);
			json = (JSONObject) json.get(OM2MManager.REPRESENTATION);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		handleNotify(json);
	}
	
	protected abstract void handleNotify(JSONObject notified);
	
	public void handleGET(CoapExchange exchange) {}
	
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.CREATED);
		
		if(isNotify(exchange))
			new Notifier(exchange).start();
	}
	
	protected class Notifier extends Thread {
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