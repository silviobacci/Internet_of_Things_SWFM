package oneM2M;

import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONObject;

import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.OM2MResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MPayloader;
import oneM2M.utilities.OM2MUtilities;
import servlets.assets.oneM2M.constants.OneM2M;

public class INManagerCreater {
	private static boolean isDamsSensors(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		if(cnt.getRn().equals(OneM2M.SENSORS) || cnt.getRn().equals(OneM2M.DAMS))
			return true;
		
		return false;
	}
	
	private static boolean isDamSensor(ContainerResource cnt) {
		if(cnt == null)
			return false;
		
		return isDamsSensors(INManager.getManager().getter.getContainer(cnt.getPi()));
	}
	
	protected static AEResource createReferenceCopy(ReferenceResource ref, boolean is_checker) {
		String name =  ref.getRi().substring(ref.getPi().length() + 1, ref.getRi().length());
		
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi());
		JSONObject json = OM2MPayloader.jsonAE(name + "-ID", name, labels);
		AEResource copy = INManager.manager.creater.createAE(json);
		
		if(!is_checker)
			INManagerSubscriptioner.addMNserver(ref);
		
		if(copy == null) {
			ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi());
			ArrayList<OM2MResource> copiedMN = INManager.manager.discoverer.discoveryAE(filters);
			
			if(copiedMN != null && !copiedMN.isEmpty() && copiedMN.size() == 1)
				copy = (AEResource) copiedMN.get(0);
		}
		
		return copy;
	}
	
	protected static ContainerResource createAECopy(ReferenceResource ref, AEResource toCopy, AEResource destination, int port) {
		String name = toCopy.getRn() + "_" + toCopy.getRi().substring(toCopy.getPi().length() + 1, toCopy.getRi().length());
		
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
		JSONObject json = OM2MPayloader.jsonContainer(name, labels);
		ContainerResource copy = INManager.manager.creater.createContainer(destination.getRi(), json);
		
		INManagerSubscriptioner.createMNSubscription(ref, port, toCopy);
		
		if(copy == null) {
			ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
			ArrayList<OM2MResource> copiedAE = INManager.manager.discoverer.discoveryContainer(filters);
			if(copiedAE != null && !copiedAE.isEmpty() && copiedAE.size() == 1)
				copy = (ContainerResource) copiedAE.get(0);
		}
		
		return copy;
	}
	
	protected static ContainerResource creatContainerCopy(ReferenceResource ref, ContainerResource toCopy, ContainerResource destination, int port) {
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
		JSONObject json = OM2MPayloader.jsonContainer(toCopy.getRn(), labels);
		ContainerResource copy = INManager.manager.creater.createContainer(destination.getRi(), json);
		
		INManagerSubscriptioner.createMNSubscription(ref, port, toCopy);
		
		if(copy == null) {
			ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
			ArrayList<OM2MResource> copiedContainer = INManager.manager.discoverer.discoveryContainer(filters);
			if(copiedContainer != null && !copiedContainer.isEmpty() && copiedContainer.size() == 1)
				copy = (ContainerResource) copiedContainer.get(0);
		}
		
		return copy;
	}
	
	protected static InstanceResource createInstanceCopy(ReferenceResource ref, InstanceResource toCopy, ContainerResource destination) {
		ArrayList<String> labels = OM2MUtilities.createLabels(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
		JSONObject json = OM2MPayloader.jsonContentInstance(toCopy.getRn(), toCopy.getCnf(), toCopy.getCon(), labels);
		InstanceResource copy = INManager.manager.creater.createContentInstance(destination.getRi(), json);
		
		if(copy == null) {
			ArrayList<String> filters = OM2MUtilities.createFilters(ref.getRi(), toCopy.getRi(), destination.getRi(), destination.getRn());
			ArrayList<OM2MResource> copiedInstances = INManager.manager.discoverer.discoveryContentInstance(filters);
			if(copiedInstances != null && !copiedInstances.isEmpty() && copiedInstances.size() == 1)
				copy = (InstanceResource) copiedInstances.get(0);
		}
		else
			INManagerNotifier.sendNotification(ref, copy, destination);
		
		return copy;
	}
	
	protected static void createAllMNReferences(boolean is_checker) {
		new ReferenceCreater(is_checker).start();
	}
	
	protected static void createAllAE(ReferenceResource r, AEResource destination, int port) {
		new AECreater(r, destination, port).start();
	}
	
	protected static void createAllContainers(ReferenceResource r, OM2MResource father, ContainerResource destination, int port) {
		new ContainerCreater(r, father, destination, port).start();
	}
	
	protected static void createAllInstances(ReferenceResource r, ContainerResource father, ContainerResource destination) {
		new InstanceCreater(r, father, destination).start();
	}
	
	static class ReferenceCreater extends Thread {
		private boolean is_checker;
		
		public ReferenceCreater(boolean is_checker) {
			this.is_checker = is_checker;
		}
		
		@Override
		public void run() {
			int port = -1;
			
			if(!is_checker) {
				INManagerSubscriptioner.deleteINSubscription();
				
				port = INManagerSubscriptioner.addINserver();
			}
			
			ArrayList<OM2MResource> references = INManager.manager.discoverer.discoveryRemoteCSE();
			
			if(references == null || references.isEmpty())
				return;
			
			Collections.sort(references);
			
			for(OM2MResource reference : references) {
				ReferenceResource ref = (ReferenceResource) reference;
				
				if(!is_checker)
					INManagerSubscriptioner.deleteMNSubscriptions(ref);
				
				AEResource copiedMN = createReferenceCopy(ref, is_checker);
				
				if(is_checker)
					port = INManagerSubscriptioner.getPortNumber(ref);
				
				if(copiedMN != null && port != -1)
					createAllAE(ref, copiedMN, port);
			}
		}
	}
	
	static class AECreater extends Thread {
		private ReferenceResource r;
		private AEResource destination;
		private int port;
		
		public AECreater(ReferenceResource r, AEResource destination, int port) {
			this.r = r;
			this.destination = destination;
			this.port = port;
		}
		
		@Override
		public void run() {
			ArrayList<OM2MResource> aes = INManager.manager.discoverer.bridgedDiscoveryAE(r.getCsi());
			
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
	}
	
	static class ContainerCreater extends Thread {
		private ReferenceResource r;
		private OM2MResource father;
		private ContainerResource destination;
		private int port;
		
		public ContainerCreater(ReferenceResource r, OM2MResource father, ContainerResource destination, int port) {
			this.r = r;
			this.father = father;
			this.destination = destination;
			this.port = port;
		}
		
		@Override
		public void run() {
			ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
			ArrayList<OM2MResource> sons = INManager.manager.discoverer.bridgedDiscoveryContainer(r.getCsi(), filters);
			
			if(isDamSensor((ContainerResource) destination)) {
				ArrayList<String> labels = OM2MUtilities.createLabels(r.getRi(), destination.getRi(), destination.getRn());
				JSONObject json = OM2MPayloader.jsonContainer(OneM2M.CONTROL_CONTAINER, labels);
				INManager.manager.creater.createContainer(destination.getRi(), json);
			}
			
			if(sons == null || sons.isEmpty())
				return;
			
			Collections.sort(sons);
			
			for(OM2MResource son : sons) {
				ContainerResource s = (ContainerResource) son;
				
				ContainerResource copy = creatContainerCopy(r, s, destination, port);
				
				if(copy != null) {
					createAllInstances(r, s, copy);
					createAllContainers(r, s, copy, port);
				}
			}
		}
	}
	
	static class InstanceCreater extends Thread {
		private ReferenceResource r; 
		private ContainerResource father;
		private ContainerResource destination;
		
		public InstanceCreater(ReferenceResource r, ContainerResource father, ContainerResource destination) {
			this.r = r;
			this.father = father;
			this.destination = destination;
		}
		
		@Override
		public void run() {
			ArrayList<String> filters = OM2MUtilities.createFilters(father.getRi());
			ArrayList<OM2MResource> instances = INManager.manager.discoverer.bridgedDiscoveryContentInstance(r.getCsi(), filters);
			
			if(instances == null || instances.isEmpty())
				return;
			
			Collections.sort(instances);
			
			for(OM2MResource inst : instances) {
				InstanceResource i = (InstanceResource) inst;
				
				createInstanceCopy(r, i, destination);
			}
		}
	}
}
