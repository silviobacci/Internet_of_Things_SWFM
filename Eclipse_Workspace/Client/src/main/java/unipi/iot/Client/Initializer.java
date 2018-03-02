package unipi.iot.Client;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import resources.AEResource;
import resources.ContainerResource;
import resources.InstanceResource;
import resources.Resource;

public class Initializer extends Thread {
	private static final String 	WL		= "Water level";
	private static final boolean 	isMN	= true; 
	private static final int 		CI		= 4;			
	private static final String		DAM		= "Dam";
	
	private Onem2mManager2 mng = new Onem2mManager2();
	private ArrayList<AEResource> ae;
	private ArrayList<ContainerResource> cnt;
	private ArrayList<InstanceResource> inst;
	private ArrayList<AEResource> bridgedAe;
	private ArrayList<ContainerResource> bridhedCnt;
	private ArrayList<InstanceResource> bridgedInst;
	private CoapClientADN context = CoapClientADN.getInstance();
	private static int i; 
	
	public Initializer() {
		mng = new Onem2mManager2();
		ae = new ArrayList<AEResource>();
		cnt = new ArrayList<ContainerResource>();
		inst = new ArrayList<InstanceResource>();
		bridgedAe = new ArrayList<AEResource>();
		bridhedCnt = new ArrayList<ContainerResource>();
		bridgedInst = new ArrayList<InstanceResource>();
		i=0;
	}
	
	private void addInstance(InstanceResource r) {
		if(r != null)
			inst.add(r);
	}
	
	private boolean addContainer(ContainerResource r) {
		if(r != null && !cnt.contains(r)) {
			cnt.add(r);
			return true;
		}
		return false;
	}
	
	private void addAE(AEResource r) {
		if(r != null && !ae.contains(r))
			ae.add(r);
	}
	
	private void createDamSubscriptions(Resource father,String name) {
		 mng.createSubscription( isMN, father, mng.jsonSubscription(father.getRn(), father.getRi(), CI));
		
	}
	
	private void createMiddleNode() {
		

		int measured;
		Boolean damState;
		
		//Cooja discovery 
		try {
			context.getModulesAddresses();
			context.InitializeContext( new Integer(70),new Integer(150));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		JSONObject json = mng.jsonAE("Pescia", "Pescia", true);
		//System.out.println("jsonAE :"+json.toJSONString());
		addAE(mng.createAE(isMN, json));
		
		//Sensors Container and instances
		for (String id :context.getMonitoringModule().keySet()) {
			
			if(addContainer(mng.createContainer(isMN, ae.get(0), mng.jsonContainer(id)))) {
				measured = context.getMonitoringModule().get(id).getLevel();
				addInstance(mng.createContentInstance(isMN, cnt.get(i++), mng.jsonCI(WL,measured)));
			}
			
		}
		
		for (String id :context.getDamModule().keySet()) {
			if(addContainer(mng.createContainer(isMN, ae.get(0), mng.jsonContainer(id)))) {
				damState = context.getDamModule().get(id).isOpened();
				inst.add(mng.createContentInstance(isMN, cnt.get(i++), mng.jsonCI(DAM,damState)));
			}
		}
		
		
		
		
		
	}

	private void updateMiddleNode() {
		
		int i = 0; 
		int measured;
		Boolean damState;
		
		//Cooja discovery 
		try {
			context.getModulesAddresses();
			context.InitializeContext( new Integer(70),new Integer(150));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		JSONObject json = mng.jsonAE("Pescia", "Pescia", true);
		//System.out.println("jsonAE :"+json.toJSONString());
		addAE(mng.createAE(isMN, json));
		
		//Sensors Container and instances
		for (String id :context.getMonitoringModule().keySet()) {
			
			if(addContainer(mng.createContainer(isMN, ae.get(0), mng.jsonContainer(id)))) {
				measured = context.getMonitoringModule().get(id).getLevel();
				addInstance(mng.createContentInstance(isMN, cnt.get(i++), mng.jsonCI(WL,measured)));
			}
			
		}
		i=0;
		for (String id :context.getDamModule().keySet()) {
			cnt.add(mng.createContainer(isMN, ae.get(0), mng.jsonContainer(id)));	
			damState = context.getDamModule().get(id).isOpened();
			inst.add(mng.createContentInstance(isMN, cnt.get(i++), mng.jsonCI(DAM,damState)));
		}
		
	}



	@Override
	public void run() {
		super.run();
		createMiddleNode();
		/*for(InstanceResource dam: inst) {
			InstanceResource r = dam;
			if(dam.getCnf().contains(DAM))
				mng.createSubscription( isMN, dam, mng.jsonSubscription( dam.getRn(),dam.getRi(), CI));
				//subscription: Current resource does not have any ACP attached
			
		}*/
		while(true) {
			
			try {
				sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			createMiddleNode();
		}
	} 
}
