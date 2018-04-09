package OM2M;


import java.net.SocketException;

public class AdnIN extends Thread {
	private static final int DEFAULT_INSTALLER_PERIOD = 3000;
	private static final int DEFAULT_COAP_PORT = 5685;
	
	private String IP_ADDRESS_OM2M;
	private int COAP_PORT;
	private String RESOURCE_NAME;
	private int INSTALLER_PERIOD;
	boolean IN;
	
	public AdnIN(String ip, int port, String name, int period) {
		IP_ADDRESS_OM2M = ip;
		COAP_PORT = port;
		INSTALLER_PERIOD = period;
		RESOURCE_NAME = name;
		IN = false;
	}
	
	public AdnIN(String ip, int port, String name) {
		this(ip, port, name, DEFAULT_INSTALLER_PERIOD);
	}
	
	public AdnIN(String ip, String name, int period) {
		this(ip, DEFAULT_COAP_PORT, name, period);
	}
	
	public AdnIN(String ip, String name) {
		this(ip, DEFAULT_COAP_PORT, name, DEFAULT_INSTALLER_PERIOD);
	}
	
	@Override
	public void run() {
		SubscriptionServer ss;
		try {
			ss = new SubscriptionServer(IP_ADDRESS_OM2M, COAP_PORT, RESOURCE_NAME, IN);
			ss.addEndpoints();
			ss.start();
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		new InstallerIN(IP_ADDRESS_OM2M, INSTALLER_PERIOD, COAP_PORT, RESOURCE_NAME).start();
	}
}
