package unipi.iot.Client;

import java.io.IOException;

import communication.CoapClientADN;
import configuration.Setup;
import interaction.Controller;

public class Starter {
	
	private static final int 	WL 			=	50;
	private static final int	THRESHOLD	=	200;
	
	public static CoapClientADN WaterFlowManager = CoapClientADN.getInstance();
	public static Controller DamController = new Controller();
	public static Setup s = Setup.getInstance();
	private static Initializer init = new Initializer();
	
	public static void main(String[] args) throws IOException {
		//client = CoapClientADN.getInstance();
		//GUI.main();
		 //new MNInstaller().start();	
		//client.getModulesAddresses();
		//client.InitializeContext(WL, THRESHOLD);
		//client.observeAllSensors();

		s.start();
		try {
			s.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WaterFlowManager.setWInstance(s.getWinstance());
		WaterFlowManager.start();
		
		//DamController.start();
		
		
		
		//while(true);
		//init.start();
		
	}

}
