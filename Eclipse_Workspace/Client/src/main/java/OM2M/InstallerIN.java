package OM2M;


import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONObject;

import resources.*;


public class InstallerIN extends Thread{
	private static final int DEFAULT_PERIOD = 5000;
	private static final boolean IN = false;
	
	private OM2MManager mng;
	private int period;
	
	private String RESOURCE_NAME;
	private int COAP_PORT;
	
	private JSONObject json;
	
	private AEResource createReferenceCopy(ReferenceResource r) {
		String name =  r.getRi().substring(r.getPi().length() + 1, r.getRi().length());

		ArrayList<OM2MResource> copiedMN = mng.discovery(IN, OM2MManager.AE);
		
		if(copiedMN != null)
			copiedMN = mng.getResourcesByName(copiedMN, name);

		if(copiedMN == null || copiedMN.isEmpty()) {
			System.out.println("CREO COPIA REFERENCE");
			copiedMN = new ArrayList<OM2MResource>();
			json = mng.jsonAE(name + "-ID", name, true, r.getRi());

			copiedMN.add(mng.createAE(IN, json));
		}
		
		return (AEResource) copiedMN.get(0);
	}
	
	private ContainerResource createAECopy(AEResource ae, AEResource copiedMN) {
		//String name =  ae.getRi().substring(ae.getPi().length() + 1, ae.getRi().length());
		String name =  ae.getRn();
		
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + copiedMN.getRi());
		
		ArrayList<OM2MResource> copiedAE = mng.discovery(IN, OM2MManager.CONTAINER, f);
		
		if(copiedAE != null)
			copiedAE = mng.getResourcesByName(copiedAE, name);

		if(copiedAE == null || copiedAE.isEmpty()) {
			System.out.println("CREO CONTAINER PER AE");
			copiedAE = new ArrayList<OM2MResource>();
			
			json = mng.jsonContainer(name, copiedMN.getRi());
			
			copiedAE.add(mng.createContainer(IN, copiedMN.getRi(), json));
		}
		
		return (ContainerResource) copiedAE.get(0);
	}
	
	private ContainerResource creatContainerCopy(ContainerResource c, ContainerResource copiedAE) {
		//String name =  c.getRi().substring(c.getPi().length() + 1, c.getRi().length());
		String name =  c.getRn();
		
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + copiedAE.getRi());
		ArrayList<OM2MResource> copiedContainer = mng.discovery(IN, OM2MManager.CONTAINER, f);
		
		if(copiedContainer != null)
			copiedContainer = mng.getResourcesByName(copiedContainer, name);
		
		if(copiedContainer == null || copiedContainer.isEmpty()) {
			System.out.println("CREO SUBSCRIPTION PER CONTAINER");
			SubscriptionResource sub = createSubscription(c);
			
			System.out.println("CREO CONTAINER PER CONTAINER");
			copiedContainer = new ArrayList<OM2MResource>();
			
			ArrayList<String> labels = new ArrayList<String>();
			labels.add(copiedAE.getRi());
			labels.add(sub.getRi());
			
			json = mng.jsonContainer(name, labels);
			copiedContainer.add(mng.createContainer(IN, copiedAE.getRi(), json));
		}
		
		return (ContainerResource) copiedContainer.get(0);
	}
	
	private void createInstanceCopy(InstanceResource i, ContainerResource copiedContainer) {
		//String name =  c.getRi().substring(c.getPi().length() + 1, c.getRi().length());
		String name =  i.getRn();
		
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + copiedContainer.getRi());
		ArrayList<OM2MResource> copiedInsatnces = mng.discovery(IN, OM2MManager.CONTENT_INSTANCE, f);
		
		if(copiedInsatnces != null)
			copiedInsatnces = mng.getResourcesByName(copiedInsatnces, name);
		
		if(copiedInsatnces == null || copiedInsatnces.isEmpty()) {
			json = mng.jsonCI(i.getCnf(), i.getCon(), copiedContainer.getRi(), name);
			if(mng.createContentInstance(IN, copiedContainer.getRi(), json) != null)
				System.out.println("CREO INSTANCE PER CONTAINER");
		}
	}
	
	private SubscriptionResource createSubscription(ContainerResource container) {
		String nu = null;
		try {
			nu = "coap://" + Inet4Address.getLocalHost().getHostAddress() + ":" + COAP_PORT + "/" + RESOURCE_NAME;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		JSONObject json = mng.jsonSubscription("subscription_" + container.getRn(), nu, OM2MManager.MODIFIED_ATTRIBUTES);

		return mng.createSubscription(IN, container.getRi(), json);
	}
	
	private void createMNReference() {
		ArrayList<OM2MResource> references = mng.discovery(IN, OM2MManager.REMOTE_CSE, null);
		
		if(references == null)
			return;
		
		Collections.sort(references);
		
		for(OM2MResource ref : references) {
			ReferenceResource r = (ReferenceResource) ref;
			AEResource copiedMN = createReferenceCopy(r);
			
			createAE(r, copiedMN);
		}
	}
		
	private void createAE(ReferenceResource r, AEResource copiedMN) {
		ArrayList<OM2MResource> aes = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.AE, null);
		
		if(aes == null)
			return;
		
		Collections.sort(aes);
		
		for(OM2MResource ae : aes) {
			AEResource a = (AEResource) ae;
		
			ContainerResource copiedAE = createAECopy(a, copiedMN);
			
			createContainer(r, a, copiedAE);
		}
	}
	
	private void createContainer(ReferenceResource r, OM2MResource ac, ContainerResource copiedAE) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + ac.getRi());
		ArrayList<OM2MResource> containers = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.CONTAINER, f);
		
		if(containers == null)
			return;
		
		Collections.sort(containers);
		
		for(OM2MResource cont : containers) {
			ContainerResource c = (ContainerResource) cont;
			
			ContainerResource copiedContainer = creatContainerCopy(c, copiedAE);
			
			createContainer(r, c, copiedContainer);
			
			createInstance(r, c, copiedContainer);
		}
	}
	
	private void createInstance(ReferenceResource r, ContainerResource ac, ContainerResource copiedContainer) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("lbl=" + ac.getRi());
		ArrayList<OM2MResource> instances = mng.bridgedDiscovery(IN, r.getCsi(), OM2MManager.CONTENT_INSTANCE, f);
		
		if(instances == null)
			return;
		
		Collections.sort(instances);
		
		for(OM2MResource inst : instances) {
			InstanceResource i = (InstanceResource) inst;
			
			createInstanceCopy(i, copiedContainer);
		}
	}

	public InstallerIN(String ip, int T, int port, String rn) {
		mng = new OM2MManager(ip);
		period = T;
		COAP_PORT = port;
		RESOURCE_NAME = rn;
	}
	
	public InstallerIN(String ip, int port, String rn) {
		this(ip, DEFAULT_PERIOD, port, rn);
	}

	@Override
	public void run() {
		super.run();
		while(true) {
			try {
				System.out.println("INIZIO A COPIARE");
				createMNReference();
				Thread.sleep(period);
				System.out.println("FINITO DI COPIARE");
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}