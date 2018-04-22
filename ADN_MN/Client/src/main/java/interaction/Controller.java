package interaction;

import java.util.concurrent.TimeUnit;
import Modules.DamActuator;
import Modules.WaterFlowSensor;
import OM2M.MNManager;
import OM2M.ModulesManager;
import communication.LoWPANManager;
import unipi.iot.Client.Constants;

public class Controller extends Thread {
	private	static	int PERIOD		= 30;
	public	static 	final 	int MEDIUM_RISK = 60;
	public 	static 	final 	int HIGH_RISK 	= 80;
	
	public	static	final String NORISK_MESSAGE = "Normal water flows, no risk detected. ";
	private static 	final String MRISK_MESSAGE = "Increasing water flows, pay attention. ";
	private static 	final String HRISK_MESSAGE = "Critical situation. ";
	private static 	final String CHECK = "Please check ";
	
	private static	String lastMessage = "";
	
	private void openDam(String name) {
		ModulesManager.DamPostJSON(name,Constants.OPEN);
		for (String ws: LoWPANManager.getDamAssociations().get(name))
			ModulesManager.SensorPostJSON(ws, null, -1, null, null, null);	
	}
		
	private void closeDam(String name) {
		ModulesManager.DamPostJSON(name,Constants.CLOSED);
		
		for (String ws: LoWPANManager.getDamAssociations().get(name))
			ModulesManager.SensorPostJSON(ws, null, 0, null, null, null);
	
	}

	private String buildStateMessage(float risk, String wfs) {
		String message;
		if(risk > MEDIUM_RISK && risk <= HIGH_RISK)
			message = MRISK_MESSAGE;
		
		else if( risk > HIGH_RISK)
			message = HRISK_MESSAGE;

		else
			message = NORISK_MESSAGE;
	
		if(wfs != null)
			message += CHECK + wfs; 
		
		return message;
	}
	
	private void checkRisk() {
		float riskness  = 0;
		int level=0;
		int n=0;
		float higherRiskness = 0; 
		String name = null;
		String msg;
		
		for(WaterFlowSensor wfs : LoWPANManager.getMonitoringModule().values()) {
			if(wfs.getThreshold() != 0) {
				n++;
				if(wfs.getThreshold() - wfs.getLevel() > 0) {
					riskness += 1 - ((wfs.getThreshold() - wfs.getLevel() ))/((float)wfs.getThreshold());
				}else {
					riskness += 1 + (( wfs.getLevel() - wfs.getThreshold()))/((float)wfs.getThreshold());
					
					if(riskness >= higherRiskness) {
						higherRiskness = riskness; 
						name = wfs.getName();
					}		
				}
			}
		}	
		
		if(n == 0)
			return;
		
		riskness *= 100/n;
		if (riskness > MEDIUM_RISK && riskness < HIGH_RISK )
			level = 2;
		if(riskness >  HIGH_RISK )
			level = 3;
		else if(riskness < MEDIUM_RISK)
			level = 1;
		
		msg = buildStateMessage(riskness,name);
		
		if(!msg.equals(lastMessage)) {
			MNManager.createAEStateCI(buildStateMessage(riskness,name),level);
			lastMessage = msg;
		}
	}
	
	private void attuateLogic() {
		boolean close = true;
		synchronized(LoWPANManager.lock) {
			for(DamActuator dam : LoWPANManager.getDamModule().values()  ) {
				if(dam.isControllable()) {
					if(LoWPANManager.getDamAssociations().get(dam.getName()) != null){
						if(!dam.isOpened()) {
							for( String ws : LoWPANManager.getDamAssociations().get(dam.getName())) {
								LoWPANManager.getMonitoringModule().get(ws);
								
								if(LoWPANManager.getMonitoringModule().containsKey(ws) && LoWPANManager.getMonitoringModule().get(ws).isOverflowed()) {
									openDam(dam.getName());
									break;
								}
							}
						
						}else if(dam.isOpened()) {
							
							for( String ws : LoWPANManager.getDamAssociations().get(dam.getName())) {
								
								if(LoWPANManager.getMonitoringModule().get(ws).isOverflowed() )
									close = false;
							}
							if(close) {
								closeDam(dam.getName());
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		super.run();
		while(true) {
			
		try {
			TimeUnit.SECONDS.sleep(PERIOD);
			System.out.println("----------CONTROLLER-----------");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LoWPANManager.checkDamAssociations();
		attuateLogic();
		checkRisk();
		
	   }
	}

	public static void setPERIOD(int pERIOD) {
		PERIOD = pERIOD;
	}

	
}
