package communication;

public class Observing {

	private Observing() {};
	
	public static void sensorObserving(final String name){
		LevelObserver tmp = new LevelObserver( CoapClientADN.getMonitoringModule().get(name));
		CoapClientADN.getMonitoringModule().get(name).getSDConnection().observe(tmp);
		CoapClientADN.getMonitoringModule().get(name).getGpsConnection().observe(tmp);
	}

	
	public static void damObserving(final String name) {
		DamObserver tmp = new DamObserver( CoapClientADN.getDamModule().get(name));
		CoapClientADN.getDamModule().get(name).getSDConnection().observe(tmp);
		CoapClientADN.getDamModule().get(name).getGpsConnection().observe(tmp);
	}


}
