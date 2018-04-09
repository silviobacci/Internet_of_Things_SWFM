package oneM2M;

import java.net.*;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.*;

import resources.ReferenceResource;

public class MNSubscriptionServer extends CoapServer {
	protected int COAP_PORT;
	private static MNSubscriptionResource resource;

	public MNSubscriptionServer(INManager m, ReferenceResource r, String ip, int port, String name, boolean is) throws SocketException{
		COAP_PORT = port;
		
		resource = new MNSubscriptionResource(m, r, ip, name, port, is);
		
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

	public MNSubscriptionResource getResource() {
		return resource;
	}
}
