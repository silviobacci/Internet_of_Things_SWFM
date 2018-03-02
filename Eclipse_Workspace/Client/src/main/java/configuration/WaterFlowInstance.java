package configuration;

import java.util.HashMap;

public class WaterFlowInstance {
	
	private String addressBR;
	private String addressMN;
	private int portBR;
	private int portMN;
	private double lat;
	private double lng;
	
	public WaterFlowInstance(HashMap<String, Object> instance) {
		addressBR = instance.get("ip_br").toString(); 
		addressMN = instance.get("ip_mn").toString(); 
		portBR =  Integer.parseInt(instance.get("port_br").toString());
		portMN =  Integer.parseInt(instance.get("port_mn").toString());
		lat =  Double.parseDouble( instance.get("lat").toString() );
		lng = Double.parseDouble( instance.get("lng").toString() );
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
