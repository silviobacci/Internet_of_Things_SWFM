package OM2M;

import Modules.WaterFlowSensor;
import communication.LoWPANManager;
import communication.Observing;

public class SDeleter extends Thread {
	private final String name;
	private WaterFlowSensor toDelete ;

	
	public SDeleter(String toDeleteName){
		name = toDeleteName;
		toDelete = LoWPANManager.getMonitoringModule().get(name);
	}
	
	@Override
	public void run() {
		super.run();
	
		Observing.removeModule(name);
		MNManager.deleteThresholdSUB( toDelete);	
		MNManager.deleteCNT(toDelete.getRi(),toDelete); 	
		LoWPANManager.removeSensor(name);	
	}
}
