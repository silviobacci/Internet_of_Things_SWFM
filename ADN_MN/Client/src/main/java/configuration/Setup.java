package configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import OM2M.OM2MManager;
import communication.LoWPANManager;
import interaction.Controller;
import unipi.iot.Client.JSONParser;

public class Setup extends Thread {
	private static Setup instance;
	private String instanceJSON; 
	private static AdnInstance wInstance ;
	
	public static  String SSRESOURCE_IP;
	 
	public static AdnInstance getWinstance() {
		return wInstance;
	}
	
	//Getting localhost address 
	private Setup() {
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	        for (NetworkInterface netint : Collections.list(nets)){
	        	try {
					if(netint.isUp()) {
						Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					    	if(inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress())
					    		SSRESOURCE_IP =  inetAddress.getHostAddress();
					    }
 
					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
	
	}
	
	public static Setup getInstance() {
		if(instance == null)
			instance = new Setup();
		return instance;
	}
	
	//read configuration file (JSON)
	private String getInstances(){
		String line;
		BufferedReader br;
		StringBuilder sb= new StringBuilder();
		try {
			br = new BufferedReader(new FileReader("config.txt"));
		    line = br.readLine();
	
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        
		        line = br.readLine();
		    }
		    
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		instanceJSON = getInstances();
		wInstance = new AdnInstance(JSONParser.getConfValues(instanceJSON));	
		OM2MManager.setIP(wInstance.getAddressMN());
		OM2MManager.setMNcse(wInstance.getMnCSE());
		Controller.setPERIOD(wInstance.getController_period());
		LoWPANManager.setPERIOD(wInstance.getManager_period());
	}

}
