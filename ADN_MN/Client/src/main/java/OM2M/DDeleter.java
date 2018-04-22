package OM2M;

import communication.LoWPANManager;
import communication.Observing;

public class DDeleter extends Thread {
private final String name;
	
	public DDeleter(String toDeleteName){
		name = toDeleteName;
	}
	
	@Override
	public void run() {
		super.run();	
		
		Observing.removeModule(name);	
		MNManager.deleteStateSUB(LoWPANManager.getDamModule().get(name));
		MNManager.deleteCNT(LoWPANManager.getDamModule().get(name).getRi(), LoWPANManager.getDamModule().get(name));
		LoWPANManager.removeDam(name);
	}
	
}
