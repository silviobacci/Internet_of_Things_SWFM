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
    		client.postJSON("Sensor1");
    		client.postJSON("Sensor2");
    		client.postJSON("Sensor3");
    		//while(true) {
	    		try {
					TimeUnit.SECONDS.sleep(1);
					System.out.println("wait");
				} catch (InterruptedException e) {
						
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		client.observe("Sensor1");
	    		
	   		client.observe("Sensor3");
	    		
	    		while(true) {}
    		}
    		
    

}