package oneM2M.subscriptions;

import java.net.*;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.*;

public class SubscriptionServer extends CoapServer {
	private final int COAP_PORT;
	private static SubscriptionServerResource resource;
	
	public SubscriptionServer(int port, SubscriptionServerResource sr) throws SocketException{
		COAP_PORT = port;
		
		resource = sr;
		
		add(resource);
	}
	
	public void addEndpoints(){
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress())){
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}

	public SubscriptionServerResource getResource() {
		return resource;
	}
	
	public int getPort() {
		return COAP_PORT;
	}
}
