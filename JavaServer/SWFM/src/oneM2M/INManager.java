package oneM2M;

import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observer;

import org.json.simple.JSONObject;

import oneM2M.configuration.Setup;
import oneM2M.notifications.INSubscriptionResource;
import oneM2M.notifications.MNSubscriptionResource;
import oneM2M.notifications.ObservableResource;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.resources.SubscriptionResource;
import oneM2M.subscriptions.SubscriptionServer;
import oneM2M.utilities.OM2MConstants;
import oneM2M.utilities.OM2MManager;
import oneM2M.utilities.OM2MPayloader;
import oneM2M.utilities.OM2MUtilities;

public class INManager {
	private static int CURRENT_PORT;
	private static ArrayList<SubscriptionServer> MNservers;
	private static SubscriptionServer INserver;
	private static ObservableResource observable_resource;
	private static OM2MManager manager;
	
	public static void init() {
		if(manager == null) manager = new OM2MManager(Setup.getIN_IP_ADDRESS(), Setup.getPORT_IN(), Setup.getIN_CSE_ID());
		if(MNservers == null) MNservers = new ArrayList<SubscriptionServer>();
		if(observable_resource == null) observable_resource = new ObservableResource();
	}

	synchronized public static ArrayList<SubscriptionServer> getMNservers() {
		return MNservers;
	}

	synchronized public static SubscriptionServer getINserver() {
		return INserver;
	}
	
	synchronized public static OM2MManager getManager() {
		return manager;
	}
	
	synchronized public static ObservableResource getObservableResource() {
		return observable_resource;
	}
	
	synchronized public static void addObserver(Observer ob) {
		observable_resource.addObserver(ob);
	}
	
	synchronized public static void deleteObserver(Observer ob) {
		observable_resource.deleteObserver(ob);
	}
	
	synchronized public static void start() {
		if(manager != null && MNservers != null && observable_resource != null)
			createAllMNReferences();
	}
	
	synchronized public static AEResource createMNReference(ReferenceResource r) {
		AEResource copy = createReferenceCopy(r, false);
			
		if(copy != null)
			createAllAE(r, copy, CURRENT_PORT, false);
		
		return copy;
	}
		
	synchronized public static ContainerResource createAE(ReferenceResource r, AEResource toCopy, AEResource destination, int port) {
		ContainerResource copy = createAECopy(r, toCopy, destination, port, false);
		
		if(copy != null)
			createAllContainers(r, toCopy, copy, port, false);
		
		return copy;
	}
	
	synchronized public static ContainerResource createContainer(ReferenceResource r, ContainerResource toCopy, ContainerResource destination, int port) {
		ContainerResource copy = creatContainerCopy(r, toCopy, destination, port, false);
			
		if(copy != null) {
			createAllContainers(r, toCopy, copy, port, false);
			createAllInstances(r, toCopy, copy);
		}
		
		return copy;
	}
	
	synchronized public static InstanceResource createInstance(ReferenceResource r, InstanceResource toCopy, ContainerResource destination) {
		return createInstanceCopy(r, toCopy, destination);
	}
	
	private static int findPortNumber() {
		if(INserver == null)
			return Setup.getSUBSCRIPTION_SERVER_START_PORT();
		
		if(MNservers.isEmpty())
			return INserver.getPort() + 1;
		
		int max_port = 0;
		for(SubscriptionServer ss : MNservers)
			if(max_port < ss.getPort())
				max_port = ss.getPort();
		
		return max_port + 1;
	}

	private static SubscriptionServer createSubscriptionServer(ReferenceResource ref, int port) {
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
	
	private static SubscriptionServer createSubscriptionServer(int port) {
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
	
	private static SubscriptionResource createSubscription(int port) {
		String nu = null;
		String rn = "subscription_" + manager.getCSE_ID().replace("/", "");
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct);

		return manager.creater.createSubscription(manager.getCSE_ID(), json);
	}
	
	private static SubscriptionResource createSubscription(ReferenceResource ref, int port) {
		String nu = null;
		String rn = "subscription_" + ref.getCsi().replace("/", "");
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi());
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct, labels);

		return manager.creater.createBridgedSubscription(ref.getCsi(), null, json);
	}
	
	private static SubscriptionResource createSubscription(ReferenceResource ref, OM2MResource resource, int port) {
		String nu = null;
		String rn = "subscription_" + resource.getRn();
		int nct = OM2MConstants.MODIFIED_ATTRIBUTES;
		ArrayList<String> labels = OM2MUtilities.createLabels(resource.getRi());
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + Setup.getRESOURCE_NAME();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		JSONObject json = OM2MPayloader.jsonSubscription(rn, nu, nct, labels);

		return manager.creater.createBridgedSubscription(ref.getCsi(), resource.getRi(), json);
	}
	
	private static AEResource createReferenceCopy(ReferenceResource ref, boolean init) {
		String name =  ref.getRi().substring(ref.getPi().length() + 1, ref.getRi().length());
		
		ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi());
		ArrayList<OM2MResource> copiedMN = manager.discoverer.discoveryAE(filters);
		
		if(init) {
			CURRENT_PORT = findPortNumber();
			MNservers.add(createSubscriptionServer(ref, CURRENT_PORT));
			observable_resource.notify(ref);
			
			createSubscription(ref, CURRENT_PORT);
		}
		
		if(copiedMN != null && !copiedMN.isEmpty() && copiedMN.size() == 1)
			return (AEResource) copiedMN.get(0);
		
		if(!init) {
			CURRENT_PORT = findPortNumber();
			MNservers.add(createSubscriptionServer(ref, CURRENT_PORT));
			observable_resource.notify(ref);
			
			createSubscription(ref, CURRENT_PORT);
		}

		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi());
		JSONObject json = OM2MPayloader.jsonAE(name + "-ID", name, labels);
		AEResource copy = manager.creater.createAE(json);
		
		return copy;
	}
	
	private static ContainerResource createAECopy(ReferenceResource ref, AEResource toCopy, AEResource destination, int port, boolean init) {
		String name = toCopy.getRn() + "_" + toCopy.getRi().substring(toCopy.getPi().length() + 1, toCopy.getRi().length());
		
		ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRn(), "AE");
		ArrayList<OM2MResource> copiedAE = manager.discoverer.discoveryContainer(filters);
		
		if(init)
			createSubscription(ref, toCopy, port);
		
		if(copiedAE != null && !copiedAE.isEmpty() && copiedAE.size() == 1)
			return (ContainerResource) copiedAE.get(0);
		
		if(!init)
			createSubscription(ref, toCopy, port);
		
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn(), "AE");
		JSONObject json = OM2MPayloader.jsonContainer(name, labels);
		ContainerResource copy = manager.creater.createContainer(destination.getRi(), json);
		
		return copy;
	}
	
	private static ContainerResource creatContainerCopy(ReferenceResource ref, ContainerResource toCopy, ContainerResource destination, int port, boolean init) {
		ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRn());
		ArrayList<OM2MResource> copiedContainer = manager.discoverer.discoveryContainer(filters);
		
		if(init)
			createSubscription(ref, toCopy, port);
		
		if(copiedContainer != null && !copiedContainer.isEmpty() && copiedContainer.size() == 1)
			return (ContainerResource) copiedContainer.get(0);
		
		if(!init)
			createSubscription(ref, toCopy, port);
		
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
		JSONObject json = OM2MPayloader.jsonContainer(toCopy.getRn(), labels);
		ContainerResource copy = manager.creater.createContainer(destination.getRi(), json);
		
		return copy;
	}
	
	private static InstanceResource createInstanceCopy(ReferenceResource ref, InstanceResource toCopy, ContainerResource destination) {
		ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRn());
		ArrayList<OM2MResource> copiedInstances = manager.discoverer.discoveryContentInstance(filters);
		
		if(copiedInstances != null && !copiedInstances.isEmpty() && copiedInstances.size() == 1)
			return (InstanceResource) copiedInstances.get(0);
		
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
		JSONObject json = OM2MPayloader.jsonContentInstance(toCopy.getRn(), toCopy.getCnf(), toCopy.getCon(), labels);
		InstanceResource copy = manager.creater.createContentInstance(destination.getRi(), json);
		
		return copy;
	}
	
	private static void deleteINSubscription() {
		ArrayList<OM2MResource> subscriptions = manager.discoverer.discoverySubscription();
		
		if(subscriptions == null || subscriptions.isEmpty())
			return;
		
		for(OM2MResource subscription : subscriptions) {
			SubscriptionResource sub = (SubscriptionResource) subscription;
			
			manager.deleter.deleteSubscription(sub.getRi());
		}
	}
	
	private static void deleteSonSubscriptions(ReferenceResource reference, OM2MResource father) {
		ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
		ArrayList<OM2MResource> subscriptions = manager.discoverer.bridgedDiscoverySubscription(reference.getCsi(), filters);
		
		if(subscriptions == null || subscriptions.isEmpty())
			return;
		
		for(OM2MResource subscription : subscriptions) {
			SubscriptionResource sub = (SubscriptionResource) subscription;
			
			manager.deleter.deleteBridgedSubscription(reference.getCsi(), sub.getRi());
		}
		
		ArrayList<OM2MResource> sons = manager.discoverer.bridgedDiscoveryContainer(reference.getCsi(), filters);
		
		if(sons == null || sons.isEmpty())
			return;
		
		for(OM2MResource son : sons)
			deleteSonSubscriptions(reference, (ContainerResource) son);
	}
	
	private static void deleteMNSubscriptions(ReferenceResource reference) {
		ArrayList<String> filters = OM2MUtilities.createFilters(reference.getRi());
		ArrayList<OM2MResource> subscriptions = manager.discoverer.bridgedDiscoverySubscription(reference.getCsi(), filters);
		
		if(subscriptions != null && !subscriptions.isEmpty())
			for(OM2MResource subscription : subscriptions) {
				SubscriptionResource sub = (SubscriptionResource) subscription;
				
				manager.deleter.deleteBridgedSubscription(reference.getCsi(), sub.getRi());
			}
			
		ArrayList<OM2MResource> aes = manager.discoverer.bridgedDiscoveryAE(reference.getCsi());
		
		if(aes == null || aes.isEmpty())
			return;
		
		for(OM2MResource ae : aes)
			deleteSonSubscriptions(reference, (AEResource) ae);
	}
	
	private static void createAllMNReferences() {
		deleteINSubscription();
		
		CURRENT_PORT = findPortNumber();
		INserver = createSubscriptionServer(CURRENT_PORT);
		
		while(createSubscription(CURRENT_PORT) == null) {
			try {
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<OM2MResource> references = manager.discoverer.discoveryRemoteCSE();
		
		if(references == null || references.isEmpty())
			return;
		
		Collections.sort(references);
		
		for(OM2MResource ref : references) {
			ReferenceResource r = (ReferenceResource) ref;
			
			deleteMNSubscriptions(r);
			
			AEResource copiedMN = createReferenceCopy(r, true);
			
			if(copiedMN != null)
				createAllAE(r, copiedMN, CURRENT_PORT, true);
		}
	}
	
	private static void createAllAE(ReferenceResource r, AEResource destination, int port, boolean init) {
		ArrayList<OM2MResource> aes = manager.discoverer.bridgedDiscoveryAE(r.getCsi());
		
		if(aes == null || aes.isEmpty())
			return;
		
		Collections.sort(aes);
		
		for(OM2MResource ae : aes) {
			AEResource a = (AEResource) ae;
		
			ContainerResource copy = createAECopy(r, a, destination, port, init);
			
			if(copy != null)
				createAllContainers(r, a, copy, port, init);
		}
	}
	
	private static void createAllContainers(ReferenceResource r, OM2MResource father, ContainerResource destination, int port, boolean init) {
		ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
		ArrayList<OM2MResource> sons = manager.discoverer.bridgedDiscoveryContainer(r.getCsi(), filters);
		
		if(sons == null || sons.isEmpty())
			return;
		
		Collections.sort(sons);
		
		for(OM2MResource son : sons) {
			ContainerResource s = (ContainerResource) son;
			
			ContainerResource copy = creatContainerCopy(r, s, destination, port, init);
			
			if(copy != null) {
				createAllContainers(r, s, copy, port, init);
				createAllInstances(r, s, copy);
			}
		}
	}
	
	private static void createAllInstances(ReferenceResource r, ContainerResource father, ContainerResource destination) {
		ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
		ArrayList<OM2MResource> instances = manager.discoverer.bridgedDiscoveryContentInstance(r.getCsi(), filters);
		
		if(instances == null || instances.isEmpty())
			return;
		
		Collections.sort(instances);
		
		for(OM2MResource inst : instances) {
			InstanceResource i = (InstanceResource) inst;
			
			createInstanceCopy(r, i, destination);
		}
	}
}
