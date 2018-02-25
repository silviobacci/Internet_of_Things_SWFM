package oneM2M;

import java.net.*;
import resources.*;
import org.json.JSONObject;

public class Starter {

	public Starter() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main( String[] args ) {
		int period = 3000;
		boolean isIN = true;
		String resource_name = "notification_resource";
		int port = 5685;
		String nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + resource_name;
		
		AdnIN adn = new AdnIN(new int[] {port}, period, isIN);
		adn.add(new NotificationResource("resource_name", isIN));
	    	adn.start();
	    	
	    	InstallerIN installer = new InstallerIN(isIN);
	    OM2MManager mng = new OM2MManager();
		
	    	JSONObject json = mng.jsonSubscription("subscription_pippo", nu, 2);
		System.out.println("subscription :"+json.toJSONString());
		
		ArrayList<Resource> discovered = mng.discovery(true, 2, null);
		ArrayList<AEResource> bridgedAe = new ArrayList<AEResource>();
		for(Resource r : discovered) {
			bridgedAe.add((AEResource) r);
		}
			
			mng.createSubscription(true, bridgedAe.get(0), json);
			
			
		while(true) {
			installer.start();
			
			ArrayList<AEResource> bridgedAe = new ArrayList<AEResource>();
			for(Resource r : discovered) {
				bridgedAe.add((AEResource) r);
			}
			
			discovered = mng.discovery(true, 3, null);
			ArrayList<ContainerResource> cnt = new ArrayList<ContainerResource>();
			for(Resource r : discovered) {
				cnt.add((ContainerResource) r);
			}
			
			json = mng.jsonCI("new Reading", 10);
			System.out.println("jsonInstance :" + json.toJSONString());
			ArrayList<InstanceResource> inst = new ArrayList<InstanceResource>();
			inst.add(mng.createContentInstance(true, cnt.get(0), json));
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}
