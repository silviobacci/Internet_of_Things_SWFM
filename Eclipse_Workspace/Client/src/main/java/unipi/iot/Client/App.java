package unipi.iot.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App {

	
	public void startRandomSimulation() {
		
		
		
	}
    public static void main( String[] args ){
    	
    	
    		//client.addMonitoringModule("Sensor1", "coap://[fd00::c30c:0:0:2]:5683/example");
    		//client.addMonitoringModule("Sensor2", "coap://[fd00::c30c:0:0:3]:5683/example");
    	CoapClientADN client= CoapClientADN.getInstance();
    		try {
				client.getModulesAddresses();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    		try {
				TimeUnit.SECONDS.sleep(1);
			//	System.out.println("wait..");
			} catch (InterruptedException e) {
					
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		client.SensorPostJSON("Sensor2",30,1,null,100);
    	//	client.SensorPostJSON("Sensor2",1,null,300,200);
    	//	client.DamPostJSON("Dam1", "open");
    	
    		//while(true) {
	    		
	    		client.observe("Sensor2");
	    		
	   	//	client.observe("Sensor2");
	    		
	   		Controller DamController  = new Controller();
			DamController.start();
	    		while(true) {
	    			
	    		}
    		}
    		
    

}