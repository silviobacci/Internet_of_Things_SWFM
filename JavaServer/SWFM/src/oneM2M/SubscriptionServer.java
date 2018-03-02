package oneM2M;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import java.net.*;
import java.util.ArrayList;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.*;
import org.eclipse.californium.core.server.resources.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import resources.*;

public class SubscriptionServer extends CoapServer {
	private String IP_ADDRESS_OM2M;
	private int COAP_PORT;
	private boolean isMN;

	public SubscriptionServer(String ip, int port, String name, boolean is) throws SocketException{
		IP_ADDRESS_OM2M = ip;
		COAP_PORT = port;
		isMN = is;
		
		add(new Resource[] {new SubscriptionResource(name)});
	}
	
	void addEndpoints(){
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress())){
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}
	
	class SubscriptionResource extends CoapResource {	
		public SubscriptionResource(String name) {
			super(name);
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
		
		private void createContentInstance(String sub_id, InstanceResource i) {
			ArrayList<String> f = new ArrayList<String>();
			f.add("lbl=" + sub_id);
			
			OM2MManager mng = new OM2MManager(IP_ADDRESS_OM2M);
			
			ArrayList<OM2MResource> containers = mng.discovery(isMN, OM2MManager.CONTENT_INSTANCE, f);
			
			if(containers == null || containers.isEmpty())
				return;
			
			for(OM2MResource r : containers) {
				System.out.println("CREO INSTANCE DA NOTIFICA");
				JSONObject json = mng.jsonCI(i.getCnf(), i.getCon(), i.getPi(), i.getRn());
				mng.createContentInstance(isMN, r.getRi(), json);
			}
		}
		
		private void handleNotify(CoapExchange exchange) {
			JSONObject json;
			try {
				json = (JSONObject) JSONValue.parseWithException(exchange.getRequestText());
				
				json = (JSONObject) json.get("m2m:sgn");
				
				String sub_id = (String) json.get("m2m:sur");
				
				json = (JSONObject) json.get("m2m:nev");
				json = (JSONObject) json.get("m2m:rep");
				
				if(json.get("m2m:cin") != null) {
					System.out.println("NOTIFICA");
					InstanceResource notified = new InstanceResource((JSONObject) json.get("m2m:cin"));
					createContentInstance(sub_id, notified);
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			exchange.respond(ResponseCode.CREATED);
		}
		
		private void handleVerification(CoapExchange exchange) {
			System.out.println("VERIFICATION SUBSCRIPTION");
			exchange.respond(ResponseCode.CREATED);
		}
		
		public void handleGET(CoapExchange exchange) {}
		
		public void handlePOST(CoapExchange exchange) {
			if(isNotify(exchange))
				handleNotify(exchange);
			else
				handleVerification(exchange);
		}

	}
}
