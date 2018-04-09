package oneM2M;

import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observer;

import org.json.simple.JSONObject;

import resources.*;

public class INManager {
	private final boolean IN = false;
	private String IP_ADDRESS_IN;
	private String RESOURCE_NAME;
	private int SERVER_PORT;
	private int LAST_PORT;
	private JSONObject json;
	
	private static ArrayList<MNSubscriptionServer> MNservers = new ArrayList<MNSubscriptionServer>();
	
	private static INSubscriptionServer INserver;
	
	private static ObservableResource ob_resource;
	
	protected OM2MManager mng;
	
	public void addObserver(Observer ob) {
		ob_resource.addObserver(ob);
	}
	
	public void deleteObserver(Observer ob) {
		ob_resource.deleteObserver(ob);
	}
	
	public AEResource createMNReference(ReferenceResource r) {
		AEResource copy = createReferenceCopy(r);
			
		if(copy != null)
			createAllAE(r, copy, LAST_PORT);
		
		return copy;
	}
		
	public ContainerResource createAE(ReferenceResource r, AEResource toCopy, AEResource destination, int port) {
		ContainerResource copy = createAECopy(r, toCopy, destination, port);
		
		if(copy != null)
			createAllContainers(r, toCopy, copy, port);
		
		return copy;
	}
	
	public ContainerResource createContainer(ReferenceResource r, ContainerResource toCopy, ContainerResource destination, int port) {
		ContainerResource copy = creatContainerCopy(r, toCopy, destination, port);
			
		if(copy != null) {
			createAllContainers(r, toCopy, copy, port);
			createAllInstances(r, toCopy, copy);
		}
		
		return copy;
	}
	
	public InstanceResource createInstance(ReferenceResource r, InstanceResource toCopy, ContainerResource destination) {
		return createInstanceCopy(r, toCopy, destination);
	}
	
	private int findPortNumber() {
		if(INserver == null)
			return SERVER_PORT;
		
		if(MNservers.isEmpty())
			return INserver.COAP_PORT + 1;
		
		int max_port = 0;
		for(MNSubscriptionServer ss : MNservers)
			if(max_port < ss.COAP_PORT)
				max_port = ss.COAP_PORT;
		
		return max_port + 1;
	}
	
	private MNSubscriptionServer createSubscriptionServer(ReferenceResource r, int port) {
		MNSubscriptionServer ss = null;
		
		try {
			ss = new MNSubscriptionServer(this, r, IP_ADDRESS_IN, port, RESOURCE_NAME, IN);
			ss.addEndpoints();
			ss.start();
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		return ss;
	}
	
	private INSubscriptionServer createSubscriptionServer(int port) {
		INSubscriptionServer ss = null;
		
		try {
			ss = new INSubscriptionServer(this, IP_ADDRESS_IN, port, RESOURCE_NAME);
			ss.addEndpoints();
			ss.start();
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		return ss;
	}
	
	private SubscriptionResource createSubscription(int port) {
		String nu = null;
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + RESOURCE_NAME;
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		json = mng.jsonSubscription("subscription_" + mng.getCSE(IN).replace("/", ""), nu, OM2MManager.MODIFIED_ATTRIBUTES);

		return mng.createSubscription(IN, mng.getCSE(IN), json);
	}
	
	private SubscriptionResource createSubscription(ReferenceResource ref, int port) {
		String nu = null;
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + RESOURCE_NAME;
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		json = mng.jsonSubscription("subscription_" + mng.getCSE(!IN).replace("/", ""), nu, OM2MManager.MODIFIED_ATTRIBUTES);

		return mng.createBridgedSubscription(IN, ref.getCsi(), null, json);
	}
	
	private SubscriptionResource createSubscription(ReferenceResource ref, OM2MResource resource, int port) {
		String nu = null;
		
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + port + "/" + RESOURCE_NAME;
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		json = mng.jsonSubscription("subscription_" + resource.getRn(), nu, OM2MManager.MODIFIED_ATTRIBUTES);

		return mng.createBridgedSubscription(IN, ref.getCsi(), resource.getRi(), json);
	}
	
	private AEResource createReferenceCopy(ReferenceResource r) {
		String name =  r.getRi().substring(r.getPi().length() + 1, r.getRi().length());
		
		String filter = "lbl=" + r.getRi();
		ArrayList<OM2MResource> copiedMN = mng.discovery(IN, OM2MManager.AE, filter);
		
		if(copiedMN != null && !copiedMN.isEmpty() && copiedMN.size() == 1)
			return (AEResource) copiedMN.get(0);
		
		LAST_PORT = findPortNumber();
		MNservers.add(createSubscriptionServer(r, LAST_PORT));
		ob_resource.notify(r);

		json = mng.jsonAE(name + "-ID", name, true, r.getRi());
		AEResource copy = mng.createAE(IN, json);
		
		createSubscription(r, LAST_PORT);
		
		return copy;
	}
	
	private ContainerResource createAECopy(ReferenceResource ref, AEResource toCopy, AEResource destination, int port) {
		String name = toCopy.getRn() + "_" + toCopy.getRi().substring(toCopy.getPi().length() + 1, toCopy.getRi().length());
		
		String filter = "lbl=" + toCopy.getRi() + "+" + ref.getRi() + "+AE";
		ArrayList<OM2MResource> copiedAE = mng.discovery(IN, OM2MManager.CONTAINER, filter);
		
		if(copiedAE != null && !copiedAE.isEmpty() && copiedAE.size() == 1)
			return (ContainerResource) copiedAE.get(0);
		
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(ref.getRi());
		filters.add(toCopy.getRi());
		filters.add(destination.getRn());
		filters.add("AE");
		json = mng.jsonContainer(name, filters);
		ContainerResource copy = mng.createContainer(IN, destination.getRi(), json);
		
		createSubscription(ref, toCopy, port);
		
		return copy;
	}
	
	private ContainerResource creatContainerCopy(ReferenceResource ref, ContainerResource toCopy, ContainerResource destination, int port) {
		String filter = "lbl=" + toCopy.getRi() + "+" + ref.getRi();
		ArrayList<OM2MResource> copiedContainer = mng.discovery(IN, OM2MManager.CONTAINER, filter);
		
		if(copiedContainer != null && !copiedContainer.isEmpty() && copiedContainer.size() == 1)
			return (ContainerResource) copiedContainer.get(0);
		
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(ref.getRi());
		filters.add(toCopy.getRi());
		filters.add(destination.getRn());
		json = mng.jsonContainer(toCopy.getRn(), filters);
		ContainerResource copy = mng.createContainer(IN, destination.getRi(), json);
		
		createSubscription(ref, toCopy, port);
		
		return copy;
	}
	
	private InstanceResource createInstanceCopy(ReferenceResource ref, InstanceResource toCopy, ContainerResource destination) {
		String filter = "lbl=" + toCopy.getRi() + "+" + ref.getRi();
		ArrayList<OM2MResource> copiedInstances = mng.discovery(IN, OM2MManager.CONTENT_INSTANCE, filter);
		
		if(copiedInstances != null && !copiedInstances.isEmpty() && copiedInstances.size() == 1)
			return (InstanceResource) copiedInstances.get(0);
		
		ArrayList<String> filters = new ArrayList<String>();
		filters.add(ref.getRi());
		filters.add(toCopy.getRi());
		filters.add(destination.getRn());
		json = mng.jsonCI(toCopy.getCnf(), toCopy.getCon(), filters,  toCopy.getRn());
		InstanceResource copy = mng.createContentInstance(IN, destination.getRi(), json);
		
		return copy;
	}
	
	private void createAllMNReferences() {
		LAST_PORT = findPortNumber();
		INserver = createSubscriptionServer(LAST_PORT);
		
		while(createSubscription(LAST_PORT) == null) {
			try {
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<OM2MResource> references = mng.discovery(IN, OM2MManager.REMOTE_CSE);
		
		if(references == null || references.isEmpty())
			return;
		
		Collections.sort(references);
		
		for(OM2MResource ref : references) {
			ReferenceResource r = (ReferenceResource) ref;
			
			AEResource copiedMN = createReferenceCopy(r);
			
			if(copiedMN != null)
				createAllAE(r, copiedMN, LAST_PORT);
		}
	}
	
	private void createAllAE(ReferenceResource r, AEResource destination, int port) {
		ArrayList<OM2MResource> aes = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.AE);
		
		if(aes == null || aes.isEmpty())
			return;
		
		Collections.sort(aes);
		
		for(OM2MResource ae : aes) {
			AEResource a = (AEResource) ae;
		
			ContainerResource copy = createAECopy(r, a, destination, port);
			
			if(copy != null)
				createAllContainers(r, a, copy, port);
		}
	}
	
	private void createAllContainers(ReferenceResource r, OM2MResource father, ContainerResource destination, int port) {
		String filter = "lbl=" + father.getRi();
		ArrayList<OM2MResource> sons = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.CONTAINER, filter);
		
		if(sons == null || sons.isEmpty())
			return;
		
		Collections.sort(sons);
		
		for(OM2MResource son : sons) {
			ContainerResource s = (ContainerResource) son;
			
			ContainerResource copy = creatContainerCopy(r, s, destination, port);
			
			if(copy != null) {
				createAllContainers(r, s, copy, port);
				createAllInstances(r, s, copy);
			}
		}
	}
	
	private void createAllInstances(ReferenceResource r, ContainerResource father, ContainerResource destination) {
		String filter = "lbl=" + father.getRi();
		ArrayList<OM2MResource> instances = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.CONTENT_INSTANCE, filter);
		
		if(instances == null || instances.isEmpty())
			return;
		
		Collections.sort(instances);
		
		for(OM2MResource inst : instances) {
			InstanceResource i = (InstanceResource) inst;
			
			createInstanceCopy(r, i, destination);
		}
	}

	public INManager(String ip, String rn, int port) {
		mng = new OM2MManager(ip);
		ob_resource = new ObservableResource();
		IP_ADDRESS_IN = ip;
		RESOURCE_NAME = rn;
		SERVER_PORT = port;
		
		createAllMNReferences();
	}

	public static ArrayList<MNSubscriptionServer> getMNservers() {
		return MNservers;
	}

	public static INSubscriptionServer getINserver() {
		return INserver;
	}
	
	public static ObservableResource getObservableResource() {
		return ob_resource;
	}
}
