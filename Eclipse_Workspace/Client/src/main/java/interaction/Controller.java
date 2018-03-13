package interaction;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import Modules.DamActuator;
import Modules.ModulesConstants;
import Modules.WaterFlowSensor;
import communication.CoapClientADN;

public class Controller extends Thread {
	private static final int PERIOD	= 10;
	
	private CoapClientADN context = CoapClientADN.getInstance();

	
	private void openDam(String name) {
		
		context.DamPostJSON(name,ModulesConstants.OPEN);
		context.getDamModule().get(name).setOpened();
		
		for (String ws: context.getDamAssociations().get(name))
			context.SensorPostJSON(ws, null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
	
	}
	
	
	private void closeDam(String name) {
		
		context.DamPostJSON(name,ModulesConstants.CLOSED);
		context.getDamModule().get(name).setClosed();
		
		for (String ws: context.getDamAssociations().get(name))
			context.SensorPostJSON(ws, null, 0, null, null); // getMonitoringModule().get("Sensor1").getConnection().
	
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
	/*	
		HashMap <String,WaterFlowSensor> tmps =context.getMonitoringModule();
		HashMap <String,DamActuator> tmpd =context.getDamModule();
		//System.out.println("controller-> S2:"+tmps.get("Sensor2").getLevel()+" S4:"+tmps.get("Sensor4").getLevel()+" dam3:"+ tmpd.get("Dam3").isOpened());
		if( ( tmps.get("Sensor2").isOverflowed() || tmps.get("Sensor4").isOverflowed() ) && !tmpd.get("Dam3").isOpened() ) {
			context.DamPostJSON("Dam3","open");
			tmpd.get("Dam3").setOpened();
			context.SensorPostJSON("Sensor2", null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor4", null, -1, null, null); 
			
		}else if( !tmps.get("Sensor2").isOverflowed() && !tmps.get("Sensor4").isOverflowed() && tmpd.get("Dam3").isOpened() ) {
			System.out.println("entrato");
			context.DamPostJSON("Dam3","closed");
			tmpd.get("Dam3").setClosed();
			context.SensorPostJSON("Sensor2", null, 0, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor4", null, 0, null, null); 
		}
		//aggiungere parentesi nelle condizioni e i metodi setopen e setclosed a tutte.
		
	/*	if( tmps.get("Sensor5").isOverflowed() || tmps.get("Sensor6").isOverflowed() && !tmpd.get("Dam9").isOpened() ) {
			context.DamPostJSON("Dam9","open");
			context.SensorPostJSON("Sensor5", null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor6", null, -1, null, null); 
		
		}else if( !tmps.get("Sensor5").isOverflowed() && !tmps.get("Sensor6").isOverflowed() && tmpd.get("Dam9").isOpened() ) {
			context.DamPostJSON("Dam9","closed");
			context.SensorPostJSON("Sensor5", null, 0, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor6", null, 0, null, null); 
		}
		
		
		if( tmps.get("Sensor7").isOverflowed() || tmps.get("Sensor8").isOverflowed() && !tmpd.get("Dam9").isOpened()  ) {
			context.DamPostJSON("Dama","open");
			context.SensorPostJSON("Sensor7", null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor8", null, -1, null, null); 
		
		}else if( !tmps.get("Sensor7").isOverflowed() && !tmps.get("Sensor8").isOverflowed() && tmpd.get("Dama").isOpened() ) {
			context.DamPostJSON("Dama","closed");
			context.SensorPostJSON("Sensor7", null, 0, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensor8", null, 0, null, null); 
		}
		
		
		if( tmps.get("Sensor8").isOverflowed() || tmps.get("Sensorc").isOverflowed()  && !tmpd.get("Damb").isOpened()) {
			context.DamPostJSON("Damb","open");
			context.SensorPostJSON("Sensor8", null, -1, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensorc", null, -1, null, null); 
		
		}else if( !tmps.get("Sensor8").isOverflowed() && !tmps.get("Sensorc").isOverflowed()  && tmpd.get("Damb").isOpened()) {
			context.DamPostJSON("Damb","closed");
			context.SensorPostJSON("Sensor8", null, 0, null, null); // getMonitoringModule().get("Sensor1").getConnection().
			context.SensorPostJSON("Sensorc", null, 0, null, null); 
		}*/
				
		
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
