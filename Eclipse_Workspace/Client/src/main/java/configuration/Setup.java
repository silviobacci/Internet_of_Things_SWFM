package configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import OM2M.OM2MManager;
import unipi.iot.Client.JSONParser;

public class Setup extends Thread {
	private static Setup instance;
	private String instanceJSON; 
	private static WaterFlowInstance wInstance ;
	 
	public static WaterFlowInstance getWinstance() {
		return wInstance;
	}
	
	
	private Setup() {}
	
	public static Setup getInstance() {
		if(instance == null)
			instance = new Setup();
		return instance;
	}
	
	private String getInstances(){
		String line;
		BufferedReader br;
		StringBuilder sb= new StringBuilder();
		try {
			br = new BufferedReader(new FileReader("Conf.txt"));
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
		wInstance = new WaterFlowInstance(JSONParser.getConfValues(instanceJSON));
		
		OM2MManager.setIP(wInstance.getAddressMN());
			 
	}

}
