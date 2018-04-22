package oneM2M.configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class Setup {
	private static final String filename = "config.txt";
	
	private static final String PORT_IN_STRING = "PORT_IN";
	private static final String SUBSCRIPTION_SERVER_START_PORT_STRING = "SUBSCRIPTION_SERVER_START_PORT";
	private static final String IN_CSE_ID_STRING = "IN_CSE_ID";
	private static final String COPY_CHECKER_PERIOD_STRING = "COPY_CHECKER_PERIOD";
	private static final String RESOURCE_NAME_STRING = "RESOURCE_NAME";
	private static final String IN_IP_ADDRESS_STRING = "IN_IP_ADDRESS";
	private static final String MY_IP_ADDRESS_STRING = "IN_IP_ADDRESS";
	private static final String GET_MY_IP_ADDRESS_AUTOMATICALLY_STRING = "GET_MY_IP_ADDRESS_AUTOMATICALLY";
	
	private static int PORT_IN;
	private static int SUBSCRIPTION_SERVER_START_PORT;
	private static int COPY_CHECKER_PERIOD;
	private static String IN_CSE_ID;
	private static String RESOURCE_NAME;
	private static String IN_IP_ADDRESS;
	private static String MY_IP_ADDRESS;
	private static boolean GET_MY_IP_ADDRESS_AUTOMATICALLY;
	
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
			COPY_CHECKER_PERIOD = (int) ((Long) config.get(COPY_CHECKER_PERIOD_STRING)).intValue();
			IN_CSE_ID = (String) config.get(IN_CSE_ID_STRING);
			RESOURCE_NAME = (String) config.get(RESOURCE_NAME_STRING);
			IN_IP_ADDRESS = (String) config.get(IN_IP_ADDRESS_STRING);
			GET_MY_IP_ADDRESS_AUTOMATICALLY = (Boolean) config.get(GET_MY_IP_ADDRESS_AUTOMATICALLY_STRING);
			
			if(GET_MY_IP_ADDRESS_AUTOMATICALLY)
				MY_IP_ADDRESS = findMyIpAddress();
			else
				MY_IP_ADDRESS = (String) config.get(MY_IP_ADDRESS_STRING);
		}
	}
	
	private static String findMyIpAddress(){
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)){
				if(netint.isUp() && !netint.isVirtual()) {
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
				    		if(inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress())
				    			return inetAddress.getHostAddress();
				    }
				}
			}
		} 
		catch (SocketException e) {
			e.printStackTrace();
		}
		
		return null;
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

	public static String getMY_IP_ADDRESS() {
		return MY_IP_ADDRESS;
	}

	public static int getCOPY_CHECKER_PERIOD() {
		return COPY_CHECKER_PERIOD;
	}
}