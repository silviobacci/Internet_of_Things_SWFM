package unipi.iot.Client;

import java.io.IOException;

import OM2M.MNManager;
import communication.CoapClientADN;
import configuration.Setup;

public class Starter {
	
	public static Setup s = Setup.getInstance();
	public static CoapClientADN WaterFlowManager = new CoapClientADN() ;

	
	public static void main(String[] args) throws IOException {

		s.start();
		try {
			s.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MNManager.setWFI(Setup.getWinstance());
		MNManager.createStructure();
		WaterFlowManager.setName("ADN");
		WaterFlowManager.start();

		
	}		
}

