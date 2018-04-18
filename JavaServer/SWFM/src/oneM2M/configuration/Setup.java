package oneM2M.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class Setup {
	private static final String filename = "config.txt";
	
	private static final String PORT_IN_STRING = "PORT_IN";
	private static final String SUBSCRIPTION_SERVER_START_PORT_STRING = "SUBSCRIPTION_SERVER_START_PORT";
	private static final String IN_CSE_ID_STRING = "IN_CSE_ID";
	private static final String RESOURCE_NAME_STRING = "RESOURCE_NAME";
	private static final String IN_IP_ADDRESS_STRING = "IN_IP_ADDRESS";
	
	private static int PORT_IN;
	private static int SUBSCRIPTION_SERVER_START_PORT;
	private static String IN_CSE_ID;
	private static String RESOURCE_NAME;
	private static String IN_IP_ADDRESS;
	
	public static void init(String filepath) {
		String configuration = readConfigurationFile(filepath);
		
		JSONObject config = null;
		
		try {
			config = (JSONObject) JSONValue.parseWithException(configuration);
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(config != null) {
			PORT_IN = (int) ((Long) config.get(PORT_IN_STRING)).intValue();
			SUBSCRIPTION_SERVER_START_PORT = (int) ((Long) config.get(SUBSCRIPTION_SERVER_START_PORT_STRING)).intValue();
			IN_CSE_ID = (String) config.get(IN_CSE_ID_STRING);
			RESOURCE_NAME = (String) config.get(RESOURCE_NAME_STRING);
			IN_IP_ADDRESS = (String) config.get(IN_IP_ADDRESS_STRING);
		}
	}
	
	private static String readConfigurationFile(String filepath){
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath + filename));
			String line = br.readLine();
	
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        
		        line = br.readLine();
		    }
		    
		    br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	public static int getPORT_IN() {
		return PORT_IN;
	}

	public static int getSUBSCRIPTION_SERVER_START_PORT() {
		return SUBSCRIPTION_SERVER_START_PORT;
	}

	public static String getIN_CSE_ID() {
		return IN_CSE_ID;
	}

	public static String getRESOURCE_NAME() {
		return RESOURCE_NAME;
	}

	public static String getIN_IP_ADDRESS() {
		return IN_IP_ADDRESS;
	}
}