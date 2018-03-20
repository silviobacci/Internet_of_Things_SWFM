package unipi.iot.Client;

import java.io.IOException;

import communication.CoapClientADN;
import configuration.Setup;
import interaction.Controller;
import interaction.GUI;

public class Starter {
	
	
	public static CoapClientADN WaterFlowManager = CoapClientADN.getInstance();
	public static Controller DamController = new Controller();
	public static Setup s = Setup.getInstance();
	//private static Initializer init = new Initializer();
	
	public static void main(String[] args) throws IOException {

		s.start();
		try {
			s.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WaterFlowManager.setWInstance(s.getWinstance());
		WaterFlowManager.start();
		DamController.start();

		
	}
		
	}

