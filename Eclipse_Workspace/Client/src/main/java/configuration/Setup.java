package configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import unipi.iot.Client.JSONParser;

public class Setup extends Thread {
	private static Setup instance;
	private String instanceJSON; 
	private ArrayList<String> jprop; 
	private WaterFlowInstance wInstance ;
	 
	public WaterFlowInstance getWinstance() {
		return wInstance;
	}
	
	
	private Setup() {
		jprop = new ArrayList<String>();
	

	}
	
	public static Setup getInstance() {
		if(instance == null)
			instance = new Setup();
		return instance;
	}
	
	private String getInstances(){
		String line,all;
		BufferedReader br;
		StringBuilder sb= new StringBuilder();
		ArrayList<String> instances = new ArrayList<String>();
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
			 
	}

}
