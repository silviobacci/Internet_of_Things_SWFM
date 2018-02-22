package unipi.iot.Client;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.json.simple.JSONObject;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.Resource;

public class SubscriptionServer extends CoapServer {
	public SubscriptionServer(int [] ports) {
		super(ports);
		// TODO Auto-generated constructor stub
	}

	public SubscriptionServer(NetworkConfig config, int [] ports) {
		super(config, ports);
		// TODO Auto-generated constructor stub
	}
	
	public SubscriptionServer() {
	}
	
    public static void main( String[] args ) {
    	SubscriptionServer ss = new SubscriptionServer(new int[] {5685});
    	ss.add(new SubscriptionResource("pippo"));
    	ss.start();
    	
    	Onem2mManager2 mng = new Onem2mManager2();
    	String nu = null;
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":5685/pippo";
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	JSONObject json = mng.jsonSubscription("subscription_pippo", nu, 2);
		System.out.println("subscription :"+json.toJSONString());
		
		ArrayList<Resource> discovered = mng.discovery(true, 2, null);
		ArrayList<AEResource> bridgedAe = new ArrayList<AEResource>();
		for(Resource r : discovered) {
			bridgedAe.add((AEResource) r);
		}
		
		mng.createSubscription(true, bridgedAe.get(0), json);
		
		
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		}
    }
}
