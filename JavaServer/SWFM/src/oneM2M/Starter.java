package oneM2M;

public class Starter {
	public static void main( String[] args ) {
		String RESOURCE_NAME = "notification_resource";
		String IP_ADDRESS_OM2M = "127.0.0.1"; 
		int COAP_PORT = 5685;
		
		new InstallerMN(IP_ADDRESS_OM2M).start();
		
		//AdnIN adn = new AdnIN(IP_ADDRESS_OM2M, COAP_PORT, RESOURCE_NAME);
		//adn.start();
    }
}
