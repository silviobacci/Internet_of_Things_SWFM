package unipi.iot.Client;

import java.io.IOException;

public class Starter {
	public static CoapClientADN client;
	public static Controller DamController  = new Controller();
	
	public static void main(String[] args) {
		//client = CoapClientADN.getInstance();
		//GUI.main();
		 new MNInstaller().start();		
	}

}
