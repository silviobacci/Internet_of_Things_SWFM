package OM2M;

import Modules.DamActuator;


public class DUpdater extends Thread {

	private	DamActuator dam;
	public 	boolean 	stateChanged = 	false; 
	public 	boolean 	gpsChanged 	 = 	false; 
	
	public DUpdater(DamActuator d) {
		dam = d;
		this.setName(d.getName());
	}
	
	private  void updateDam() {
		if(stateChanged) {
			MNManager.damCI(dam);
			stateChanged = false; 
		}
		
		if(gpsChanged) {
			MNManager.createGPSContentInstance(dam);
			gpsChanged = false;
		}
	}

	@Override
	public void run() {	
		super.run();
		while(true) {
			dam.printState();
			updateDam();
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
