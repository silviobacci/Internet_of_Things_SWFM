package OM2M;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import communication.LoWPANManager;
import resources.InstanceResource;
import unipi.iot.Client.Constants;
import unipi.iot.Client.JSONParser;

public class MNSubServerResource extends SubscriptionServerResource {

	public MNSubServerResource(String n, int p) {
		super(n,p);
	}
	
	//enable/disable controller dam modification 
	private void setControllable(String name, InstanceResource inst) {
		if(LoWPANManager.getDamModule().get(name).isControllable()) {
			LoWPANManager.getDamModule().get(name).setControllable(false);
		LoWPANManager.getDamModule().get(name).setCnf(inst.getCnf());
		}else {
			LoWPANManager.getDamModule().get(name).setControllable(true);
			LoWPANManager.getDamModule().get(name).setCnf(Constants.AUTO_CHANGE);
		}
	}

	//Subscription notification 
	@Override
	protected void handleNotify(JSONObject notified) {
		JSONObject CONTENT_INSTANCE = (JSONObject) notified.get(OM2MManager.RESOURCE_TYPE_CONTENT_INSTANCE);
		
		if(CONTENT_INSTANCE != null) {
			InstanceResource inst = new InstanceResource(CONTENT_INSTANCE);
			if(inst.getCnf().equals(Constants.AUTO_CHANGE))
				return;
			
			JSONObject json = null;
			try {
				json = (JSONObject) JSONValue.parseWithException(inst.getCon().toString().replace("'", "\""));
			} 
			catch (ParseException e) {
				e.printStackTrace();
			}
			   
			   
		   if( json.get(Constants.THRES) != null ) {
			   String name = OM2MManager.getContainer(true,OM2MManager.getContainer(true, inst.getPi()).getPi()).getRn();
			   LoWPANManager.getMonitoringModule().get(name).setCnf(inst.getCnf());
			   ModulesManager.SensorPostJSON(name, null, null, null, null, ((Long)json.get(Constants.THRES)).intValue()) ;
				
			}else{
				
				boolean tmp = ((Boolean)(json.get(Constants.STATE)));
				String name = OM2MManager.getContainer(true,OM2MManager.getContainer(true, inst.getPi()).getPi()).getRn();
				String control = tmp?JSONParser.OPEN:JSONParser.CLOSED;
					
				setControllable(name, inst);
				ModulesManager.DamPostJSON(name, control);
				
				if(control.equals(JSONParser.OPEN)) {
					for (String ws: LoWPANManager.getDamAssociations().get(name))
						ModulesManager.SensorPostJSON(ws, null, -1, null, null, null);
				
				}else {
					for (String ws: LoWPANManager.getDamAssociations().get(name))
						ModulesManager.SensorPostJSON(ws, null, 0, null, null, null);
				}	
			}	   
		} 	
	}
	
	public JSONObject getJSONObj(JSONObject notified) {
		try {
			return (JSONObject) JSONValue.parseWithException(notified.toJSONString().replace("'", "\""));
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
