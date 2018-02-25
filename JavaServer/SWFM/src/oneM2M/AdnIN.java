package oneM2M;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;

public class AdnIN extends CoapServer {
	private int period;
	private boolean IN;
	
	public AdnIN(int [] ports, int p, boolean isIN) {
		super(ports);
		period = p;
		IN = isIN;
	}
}
