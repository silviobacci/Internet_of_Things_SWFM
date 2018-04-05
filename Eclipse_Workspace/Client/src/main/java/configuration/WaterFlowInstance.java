package configuration;

import java.util.HashMap;

import Modules.WaterFlowSensor;
import unipi.iot.Client.JSONParser;

public class WaterFlowInstance {
	
	private String  name;
	private String	addressBR;
	private String 	addressMN;
	private int 	portBR;
	private int 	portMN;
	private double 	lat;
	private double 	lng;
	public static WaterFlowSensor wfs;
	
	public WaterFlowInstance(HashMap<String, Object> instance) {
		
		name		=	instance.get(JSONParser.NAME).toString();
		addressBR 	=	instance.get(JSONParser.IPBR).toString(); 
		addressMN	=	instance.get(JSONParser.IPMN).toString(); 
		portBR 		=  	Integer.parseInt(instance.get(JSONParser.PORTBR).toString());
		portMN 		=  	Integer.parseInt(instance.get(JSONParser.PORTMN).toString());
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

}
