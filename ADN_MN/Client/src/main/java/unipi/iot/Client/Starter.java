package unipi.iot.Client;

import java.io.IOException;

import OM2M.MNManager;
import OM2M.MNSubServerResource;
import OM2M.SubscriptionServer;
import communication.LoWPANManager;
import configuration.Setup;
import interaction.Controller;

public class Starter {
	
	public static Setup s = Setup.getInstance();
	public static LoWPANManager WaterFlowManager = new LoWPANManager() ;
	public static SubscriptionServer sServer;
	public static Controller ctrl = new Controller();
	
	public static void main(String[] args) throws IOException {
		sServer =	new SubscriptionServer(Constants.SSERVER_PORT, new MNSubServerResource(Constants.SSRESOURCE_NAME, Constants.SSERVER_PORT));
		sServer.addEndpoints();
		sServer.start();
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

