package unipi.iot.Client;

import java.io.IOException;

public class Starter {
	public static CoapClientADN client;
	public static Controller DamController;  //= new Controller();
	private static Initializer init = new Initializer();
	
	public static void main(String[] args) {
		//client = CoapClientADN.getInstance();
		//GUI.main();
		 new MNInstaller().start();		
		//init.start();
	}

}
