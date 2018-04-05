package interaction;

import java.util.concurrent.TimeUnit;
import Modules.DamActuator;
import Modules.ModulesConstants;
import communication.CoapClientADN;

public class Controller extends Thread {
	private static final int PERIOD	= 50;

	private void openDam(String name) {
		
		CoapClientADN.DamPostJSON(name,ModulesConstants.OPEN);
		
		for (String ws: CoapClientADN.getDamAssociations().get(name))
			CoapClientADN.SensorPostJSON(ws, null, -1, null, null, null);	
	}
		
	private void closeDam(String name) {
		CoapClientADN.DamPostJSON(name,ModulesConstants.CLOSED);
		
		for (String ws: CoapClientADN.getDamAssociations().get(name))
			CoapClientADN.SensorPostJSON(ws, null, 0, null, null, null);
	
	}
	
	private void attuateLogic() {
		boolean close = true;
		
		for(DamActuator dam : CoapClientADN.getDamModule().values() ) {
			
			if(!dam.isOpened()) {
				
				for( String ws : CoapClientADN.getDamAssociations().get(dam.getName())) {
					CoapClientADN.getMonitoringModule().get(ws);
					
					if(CoapClientADN.getMonitoringModule().containsKey(ws) && CoapClientADN.getMonitoringModule().get(ws).isOverflowed() ) {
						openDam(dam.getName());
						break;
					}
				}
			
			}else if(dam.isOpened()) {
				
				for( String ws : CoapClientADN.getDamAssociations().get(dam.getName())) {
					
					if(CoapClientADN.getMonitoringModule().get(ws).isOverflowed() )
						close = false;
				}
				if(close) {
					closeDam(dam.getName());
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
		CoapClientADN.checkDamAssociations();
		attuateLogic();
		
	   }
	}

	
}
