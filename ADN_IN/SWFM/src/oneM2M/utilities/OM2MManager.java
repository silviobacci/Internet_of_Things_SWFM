package oneM2M.utilities;

public class OM2MManager {
	private static final String coap = "coap://";
	private static final String ip_port_separator = ":";
	private static final String tilde = "/~";
	public final OM2MCreater creater = new OM2MCreater();
	public final OM2MGetter getter = new OM2MGetter();
	public final OM2MDeleter deleter = new OM2MDeleter();
	public final OM2MDiscoverer discoverer = new OM2MDiscoverer();

	private String IP_ADDRESS;
	private String CSE_ID;
	
	public OM2MManager(String ip, int port, String cse_id) {
		IP_ADDRESS = coap + ip + ip_port_separator + port + tilde;
		CSE_ID = cse_id;
		
		creater.setCSE_ID(CSE_ID);
		discoverer.setCSE_ID(CSE_ID);
		
		creater.setIP_ADDRESS(IP_ADDRESS);
		getter.setIP_ADDRESS(IP_ADDRESS);
		deleter.setIP_ADDRESS(IP_ADDRESS);
		discoverer.setIP_ADDRESS(IP_ADDRESS);
	}
	
	public String getIP_ADDRESS() {
		return IP_ADDRESS;
	}

	public String getCSE_ID() {
		return CSE_ID;
	}
}