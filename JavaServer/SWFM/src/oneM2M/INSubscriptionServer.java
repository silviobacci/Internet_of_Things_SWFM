package oneM2M;

import java.net.*;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.*;

public class INSubscriptionServer extends CoapServer {
	protected int COAP_PORT;
	private static INSubscriptionResource resource;
	
	public INSubscriptionServer(INManager m, String ip, int port, String name) throws SocketException{
		COAP_PORT = port;
		
		resource = new INSubscriptionResource(m, ip, name, port);
		
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

	public INSubscriptionResource getResource() {
		return resource;
	}
}
