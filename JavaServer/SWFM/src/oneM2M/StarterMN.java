package oneM2M;

public class StarterMN {
	public static void main( String[] args ) {
		String IP_ADDRESS_OM2M = "127.0.0.1"; 
		
		new InstallerMN(IP_ADDRESS_OM2M).start();
    }
}
