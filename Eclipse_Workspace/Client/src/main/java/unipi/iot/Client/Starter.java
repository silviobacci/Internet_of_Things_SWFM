package unipi.iot.Client;

import java.io.IOException;

import communication.CoapClientADN;
import configuration.Setup;
import interaction.Controller;
import interaction.GUI;

public class Starter {
	
	
	
	public static Setup s = Setup.getInstance();
	public static CoapClientADN WaterFlowManager;
	
	//public static Controller DamController = new Controller();
	//private static Initializer init = new Initializer();
	
	public static void main(String[] args) throws IOException {

		s.start();
		try {
			s.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WaterFlowManager = CoapClientADN.getInstance();
		WaterFlowManager.start();
		//DamController.start();

		
	}
		
	}

