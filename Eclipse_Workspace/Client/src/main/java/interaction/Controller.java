package interaction;

import java.util.concurrent.TimeUnit;
import Modules.DamActuator;
import Modules.ModulesConstants;
import communication.CoapClientADN;

public class Controller extends Thread {
	private static final int PERIOD	= 50;
	
	private CoapClientADN context = CoapClientADN.getInstance();

	private void openDam(String name) {
		
		context.DamPostJSON(name,ModulesConstants.OPEN);
		context.getDamModule().get(name).setOpened();
		
		for (String ws: context.getDamAssociations().get(name))
			context.SensorPostJSON(ws, null, -1, null, null, null);	
	}
		
	private void closeDam(String name) {
		context.DamPostJSON(name,ModulesConstants.CLOSED);
		context.getDamModule().get(name).setClosed();
		
		for (String ws: context.getDamAssociations().get(name))
			context.SensorPostJSON(ws, null, 0, null, null, null);
	
	}
	
	private void attuateLogic() {
		boolean close = true;
		
		for(DamActuator dam : context.getDamModule().values() ) {
			
			if(!dam.isOpened()) {
				
				for( String ws : context.getDamAssociations().get(dam.getName())) {
					context.getMonitoringModule().get(ws);
					
					if(context.getMonitoringModule().containsKey(ws) && context.getMonitoringModule().get(ws).isOverflowed() ) {
						openDam(dam.getName());
						break;
					}
				}
			
			}else if(dam.isOpened()) {
				
				for( String ws : context.getDamAssociations().get(dam.getName())) {
					
					if(context.getMonitoringModule().get(ws).isOverflowed() )
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
		context.checkDamAssociations();
		attuateLogic();
		
	   }
	}

	
}
