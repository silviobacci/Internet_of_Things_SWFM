package unipi.iot.Client;

import java.util.concurrent.TimeUnit;

public class Controller extends Thread {

	private CoapClientADN context = CoapClientADN.getInstance();

	@Override
	public void run() {
		super.run();
		while(true) {
		try {
			TimeUnit.SECONDS.sleep(10);
			System.out.println("controller");
		} catch (InterruptedException e) {
				
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( CoapClientADN.getMonitoringModule().get("Sensor2").isOverflowed() ) {
			CoapClientADN.DamPostJSON("Dam3","open");
			CoapClientADN.SensorPostJSON("Sensor2", null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
		
		}
	   }
	}
	
	
	
	
	
	
}
