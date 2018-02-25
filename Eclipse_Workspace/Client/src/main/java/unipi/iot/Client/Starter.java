package unipi.iot.Client;

import java.io.IOException;

public class Starter {
	
	private static final int 	WL 			=	50;
	private static final int	THRESHOLD	=	200;
	
	public static CoapClientADN client = new CoapClientADN();;
	public static Controller DamController;  //= new Controller();
	private static Initializer init = new Initializer();
	
	public static void main(String[] args) {
		//client = CoapClientADN.getInstance();
		//GUI.main();
		 //new MNInstaller().start();	
		client.InitializeContext(WL, THRESHOLD);
		client.observeAllSensors();
		init.start();
		
	}

}
