package oneM2M;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;

import oneM2M.configuration.Setup;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.resources.SubscriptionResource;
import oneM2M.subscriptions.INSubscriptionResource;
import oneM2M.subscriptions.MNSubscriptionResource;
import oneM2M.subscriptions.SubscriptionServer;
import oneM2M.utilities.OM2MConstants;
import oneM2M.utilities.OM2MPayloader;
import oneM2M.utilities.OM2MUtilities;

public class INManagerSubscriptioner {
	private static HashMap<ReferenceResource, SubscriptionServer> MNservers;
	private static SubscriptionServer INserver;
	
	public static void init() {
		if(MNservers == null) MNservers = new HashMap<ReferenceResource, SubscriptionServer>();
	}
	
	protected static int addMNserver(ReferenceResource ref) {
		synchronized(MNservers) {
			int port = getPortNumber();
			MNservers.put(ref, createMNSubscriptionServer(ref, port));
			
			createMNSubscription(ref, port);
			
			return port;
		}
	}
	
	protected static void removeMNserver(ReferenceResource ref, SubscriptionServer server) {
		synchronized(MNservers) {MNservers.remove(ref, server);}
	}
	
	protected static int getPortNumber(ReferenceResource ref) {
		synchronized(MNservers) {
			SubscriptionServer server = MNservers.get(ref);
			
			if(server == null)
				return -1;
			
			return MNservers.get(ref).getPort();
		}
	}
	
	protected static int addINserver() {
		int port = getPortNumber();
		
		INserver = createINSubscriptionServer(port);
		
		while(INManagerSubscriptioner.createINSubscription(port) == null) {
			try {
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return port;
	}
	
	private static int getPortNumber() {
		if(INserver == null)
			return Setup.getSUBSCRIPTION_SERVER_START_PORT();
		
		if(MNservers.isEmpty())
			return INserver.getPort() + 1;
		
		int max_port = 0;
		for(SubscriptionServer ss : MNservers.values())
			if(max_port < ss.getPort())
				max_port = ss.getPort();
		
		return max_port + 1;
	}

	protected static SubscriptionServer createMNSubscriptionServer(ReferenceResource ref, int port) {
		SubscriptionServer ss = null;
		
		try {
			ss = new SubscriptionServer(port, new MNSubscriptionResource(Setup.getRESOURCE_NAME(), port, ref));
			ss.addEndpoints();
			ss.start();
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		return ss;
	}
	
	protected static SubscriptionServer createINSubscriptionServer(int port) {
		SubscriptionServer ss = null;
		
		try {
			ss = new SubscriptionServer(port, new INSubscriptionResource(Setup.getRESOURCE_NAME(), port));
			ss.addEndpoints();
			ss.start();
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		return ss;
	}
	
	protected static SubscriptionResource createINSubscription(int port) {
		String nu = "coap://" + Setup.getMY_IP_ADDRESS() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		String rn = "subscription_" + INManager.manager.getCSE_ID().replace("/", "");
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct);

		return INManager.manager.creater.createSubscription(INManager.manager.getCSE_ID(), json);
	}
	
	protected static void deleteINSubscription() {
		ArrayList<OM2MResource> subscriptions = INManager.manager.discoverer.discoverySubscription();
		
		if(subscriptions == null || subscriptions.isEmpty())
			return;
		
		for(OM2MResource subscription : subscriptions) {
			SubscriptionResource sub = (SubscriptionResource) subscription;
			
			INManager.manager.deleter.deleteSubscription(sub.getRi());
		}
	}
	
	protected static SubscriptionResource createMNSubscription(ReferenceResource ref, int port) {
		String nu = "coap://" + Setup.getMY_IP_ADDRESS() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		String rn = "subscription_" + ref.getCsi().replace("/", "");
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi());
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct, labels);

		return INManager.manager.creater.createBridgedSubscription(ref.getCsi(), null, json);
	}
	
	protected static SubscriptionResource createMNSubscription(ReferenceResource ref, int port, OM2MResource resource) {
		String nu = "coap://" + Setup.getMY_IP_ADDRESS() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		String rn = "subscription_" + resource.getRn();
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		ArrayList<String> labels = OM2MUtilities.createLabels(resource.getRi());
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct, labels);

		return INManager.manager.creater.createBridgedSubscription(ref.getCsi(), resource.getRi(), json);
	}
	
	protected static void deleteMNSubscriptions(ReferenceResource reference) {
		ArrayList<String> filters = OM2MUtilities.createFilters(reference.getRi());
		ArrayList<OM2MResource> subscriptions = INManager.manager.discoverer.bridgedDiscoverySubscription(reference.getCsi(), filters);
		
		if(subscriptions != null && !subscriptions.isEmpty())
			for(OM2MResource subscription : subscriptions) {
				SubscriptionResource sub = (SubscriptionResource) subscription;
				
				INManager.manager.deleter.deleteBridgedSubscription(reference.getCsi(), sub.getRi());
			}
			
		ArrayList<OM2MResource> aes = INManager.manager.discoverer.bridgedDiscoveryAE(reference.getCsi());
		
		if(aes == null || aes.isEmpty())
			return;
		
		for(OM2MResource ae : aes)
			deleteMNSubscriptions(reference, (AEResource) ae);
	}
	
	private static void deleteMNSubscriptions(ReferenceResource reference, OM2MResource father) {
		ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
		ArrayList<OM2MResource> subscriptions = INManager.manager.discoverer.bridgedDiscoverySubscription(reference.getCsi(), filters);
		
		if(subscriptions == null || subscriptions.isEmpty())
			return;
		
		for(OM2MResource subscription : subscriptions) {
			SubscriptionResource sub = (SubscriptionResource) subscription;
			
			INManager.manager.deleter.deleteBridgedSubscription(reference.getCsi(), sub.getRi());
		}
		
		ArrayList<OM2MResource> sons = INManager.manager.discoverer.bridgedDiscoveryContainer(reference.getCsi(), filters);
		
		if(sons == null || sons.isEmpty())
			return;
		
		for(OM2MResource son : sons)
			deleteMNSubscriptions(reference, (ContainerResource) son);
	}
}
