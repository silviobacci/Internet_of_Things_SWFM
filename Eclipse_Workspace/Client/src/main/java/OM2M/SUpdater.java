package OM2M;

import Modules.WaterFlowSensor;


public class SUpdater extends Thread {

	private WaterFlowSensor wfs;
	public boolean levelChanged = 	false; 
	public boolean gpsChanged = 	false; 
	public boolean thresChanged = 	false;  
	
	public SUpdater(WaterFlowSensor w) {
		wfs = w;
		this.setName(w.getName());
	}
	
	private  void updateSensor() {
		if(levelChanged) {
			MNManager.createLevelContentInstance(wfs);
			levelChanged = false; 
		}
		
		if(gpsChanged) {
			MNManager.createGPSContentInstance(wfs);
			gpsChanged = false;
		}
			
		if(thresChanged) {
			MNManager.createThresholdsContentInstance(wfs);
			thresChanged = false;
		}
	}

	@Override
	public void run() {	
		super.run();
		while(true) {
			System.out.println("Started");
			updateSensor();
			System.out.println("updated");
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("waited");
		}
	}
}
