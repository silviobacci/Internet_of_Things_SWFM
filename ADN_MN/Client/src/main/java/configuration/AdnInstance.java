package configuration;

import java.util.HashMap;

import Modules.WaterFlowSensor;
import unipi.iot.Client.JSONParser;

public class AdnInstance {
	
	private String  name;
	private String	addressBR;
	private String 	addressMN;
	private String 	mnCSE;
	private int 	portBR;
	private int 	portMN;
	private int 	manager_period;
	private int 	controller_period;
	private double 	lat;
	private double 	lng;
	public static WaterFlowSensor wfs;
	
	public AdnInstance(HashMap<String, Object> instance) {
		name		=	instance.get(JSONParser.NAME).toString();
		addressBR 	=	instance.get(JSONParser.IPBR).toString(); 
		addressMN	=	instance.get(JSONParser.IPMN).toString(); 
		setMnCSE(instance.get(JSONParser.MN_CSE).toString());
		portBR 		=  	Integer.parseInt(instance.get(JSONParser.PORTBR).toString());
		portMN 		=  	Integer.parseInt(instance.get(JSONParser.PORTMN).toString());
		manager_period 		=  	Integer.parseInt(instance.get(JSONParser.MANAGER_PERIOD).toString());
		controller_period 		=  	Integer.parseInt(instance.get(JSONParser.CONTROLLER_PERIOD).toString());
		lat 		=  	Double.parseDouble( instance.get(JSONParser.LAT).toString() );
		lng 		=	Double.parseDouble( instance.get(JSONParser.LNG).toString() );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddressBR() {
		return addressBR;
	}

	public String getAddressMN() {
		return addressMN;
	}

	public int getPortBR() {
		return portBR;
	}

	public int getPortMN() {
		return portMN;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public String getMnCSE() {
		return mnCSE;
	}

	public void setMnCSE(String mnCSE) {
		this.mnCSE = mnCSE;
	}

	public int getManager_period() {
		return manager_period;
	}

	public int getController_period() {
		return controller_period;
	}

}
