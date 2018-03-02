package oneM2M;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.*;
import org.eclipse.californium.core.server.resources.*;

public class SubscriptionResource extends CoapResource {

	public SubscriptionResource(String name) {
		super(name);
		setObservable(true);
        setObserveType(Type.CON);
        getAttributes().setObservable();
	}

	public SubscriptionResource(String name, boolean visible) {
		super(name, visible);
		setObserveType(Type.CON);
        getAttributes().setObservable();
	}
	
	public void handleGET(CoapExchange exchange) {}
	
	public void handlePOST(CoapExchange exchange) {
		System.out.println(exchange.getRequestText());
		exchange.respond(ResponseCode.CREATED);
	}

}
