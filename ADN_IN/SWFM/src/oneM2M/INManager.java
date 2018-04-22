package oneM2M;

import oneM2M.configuration.Setup;
import oneM2M.resources.AEResource;
import oneM2M.resources.ContainerResource;
import oneM2M.resources.InstanceResource;
import oneM2M.resources.ReferenceResource;
import oneM2M.utilities.OM2MManager;
public class INManager {
	protected static OM2MManager manager;
	
	public static void init() {
		if(manager == null) {
			manager = new OM2MManager(Setup.getIN_IP_ADDRESS(), Setup.getPORT_IN(), Setup.getIN_CSE_ID());
			INManagerSubscriptioner.init();
			INManagerNotifier.init();
		}
	}
	
	public static OM2MManager getManager() {
		return manager;
	}
	
	public static void start() {
		boolean is_checker = false;
		
		if(manager != null)
			INManagerCreater.createAllMNReferences(is_checker);
		
		new CopyChecker().start();
	}
	
	public static void createMNReference(ReferenceResource r, int port) {
		new ReferenceCreater(r, port).start();
	}
		
	public static void createAE(ReferenceResource r, AEResource toCopy, AEResource destination, int port) {
		new AECreater(r, toCopy, destination, port).start();
	}
	
	public static void createContainer(ReferenceResource r, ContainerResource toCopy, ContainerResource destination, int port) {
		new ContainerCreater(r, toCopy, destination, port).start();
	}
	
	public static void createInstance(ReferenceResource r, InstanceResource toCopy, ContainerResource destination) {
		new InstanceCreater(r, toCopy, destination).start();
	}
	
	static class ReferenceCreater extends Thread {
		private final boolean is_checker = false;
		private ReferenceResource r;
		private int port;
		
		public ReferenceCreater(ReferenceResource r, int port) {
			this.r = r;
			this.port = port;
		}
		
		@Override
		public void run() {
			AEResource copy = INManagerCreater.createReferenceCopy(r, is_checker);
			
			if(copy != null)
				INManagerCreater.createAllAE(r, copy, port);
		}
	}
	
	static class AECreater extends Thread {
		private ReferenceResource r;
		private AEResource toCopy;
		private AEResource destination;
		private int port;
		
		public AECreater(ReferenceResource r, AEResource toCopy, AEResource destination, int port) {
			this.r = r;
			this.toCopy = toCopy;
			this.destination = destination;
			this.port = port;
		}
		
		@Override
		public void run() {
			ContainerResource copy = INManagerCreater.createAECopy(r, toCopy, destination, port);
			
			if(copy != null)
				INManagerCreater.createAllContainers(r, toCopy, copy, port);
		}
	}
	
	static class ContainerCreater extends Thread {
		private ReferenceResource r;
		private ContainerResource toCopy;
		private ContainerResource destination;
		private int port;
		
		public ContainerCreater(ReferenceResource r, ContainerResource toCopy, ContainerResource destination, int port) {
			this.r = r;
			this.toCopy = toCopy;
			this.destination = destination;
			this.port = port;
		}
		
		@Override
		public void run() {
			ContainerResource copy = INManagerCreater.creatContainerCopy(r, toCopy, destination, port);
			
			if(copy != null) {
				INManagerCreater.createAllInstances(r, toCopy, copy);
				INManagerCreater.createAllContainers(r, toCopy, copy, port);
			}
		}
	}
	
	static class InstanceCreater extends Thread {
		private ReferenceResource r; 
		private InstanceResource toCopy;
		private ContainerResource destination;
		
		public InstanceCreater(ReferenceResource r, InstanceResource toCopy, ContainerResource destination) {
			this.r = r;
			this.toCopy = toCopy;
			this.destination = destination;
		}
		
		@Override
		public void run() {
			INManagerCreater.createInstanceCopy(r, toCopy, destination);
		}
	}
	
	static class CopyChecker extends Thread {
		private final boolean is_checker = true;
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(Setup.getCOPY_CHECKER_PERIOD());
					INManagerCreater.createAllMNReferences(is_checker);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
