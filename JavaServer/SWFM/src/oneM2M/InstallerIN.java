package oneM2M;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import resources.*;


public class InstallerIN extends Thread {
	private int period;
	private OM2MManager mng;
	private boolean IN;
	private JSONObject json;
	ArrayList<ReferenceResource> references;
	
	private ArrayList<AEResource> createReferenceCopy() {
		ArrayList<AEResource> copiedMN;
		
		ArrayList<Resource> ref = mng.discovery(IN, 1, null);
		
		for(Resource rf : ref) {
			ReferenceResource r = (ReferenceResource) rf;
			references.add(r);
			
			ArrayList<String> f = new ArrayList<String>();
			f.add("rn=" + r.getRi());
			
			if(mng.discovery(IN, 2, f) == null) {
				json = mng.jsonAE(r.getRi()+"-ID", r.getRi(), true);
				copiedMN.add(mng.createAE(IN, json));
			}
		}
		
		if(copiedMN.isEmpty())
			return null;
		
		return copiedMN;
	}
	
	private ArrayList<ContainerResource> createAECopy(ReferenceResource r) {
		ArrayList<ContainerResource> copiedAE;
		
		ArrayList<Resource> aes = mng.bridgedDiscovery(IN, r.getCsi(), 2, null);
		
		for(Resource ae : aes) {
			AEResource a = (AEResource) ae;
			
			ArrayList<String> f = new ArrayList<String>();
			f.add("rn=" + a.getRn());
			
			if(mng.discovery(IN, 3, f) == null) {
				json = mng.jsonContainer(a.getRn());
				copiedAE.add(mng.createContainer(IN, r, json));
			}
		}
		
		if(copiedAE.isEmpty())
			return null;
		
		return copiedAE;
	}
	
	private ContainerResource createReferenceContainer(AEResource a) {
		ArrayList<ContainerResource> copiedCnt;
		
		ArrayList<String> f = new ArrayList<String>();
		f.add("pi="+a.getPi());
		ArrayList<Resource> containers = mng.bridgedDiscovery(IN, r.getCsi(), 3, f);
		for(Resource container : containers) {
			ContainerResource cont = (ContainerResource) container;
			copiedCnt.add(createReferenceContainer(cont, copiedAE.get(copiedAE.size() - 1)));
			
		json = mng.jsonContainer(c.getRn());
		return mng.createContainer(IN, ae, json);
	}
	
	private ContainerResource createReferenceContainer(ContainerResource c, ContainerResource cnt) {
		json = mng.jsonContainer(c.getRn());
		return mng.createContainer(IN, cnt, json);
	}

	public InstallerIN(int p) {
		period = p;
		mng = new OM2MManager();
		IN = true;
		references = new ArrayList<ReferenceResource>();
	}

	@Override
	public void run() {
		super.run();
		
		while(true) {
			ArrayList<AEResource> copiedMN = createReferenceCopy();
			ArrayList<ContainerResource> copiedAE;
			for(ReferenceResource r : references)
				createAECopy(r);
			ArrayList<ContainerResource> copiedAE;
			ArrayList<ContainerResource> copiedCnt;
			ArrayList<String> f;

			ArrayList<Resource> references = mng.discovery(IN, 1, null);
			for(Resource ref : references) {
				ReferenceResource r = (ReferenceResource) ref;
				copiedMN.add(createReferenceCopy(r));
				
				ArrayList<Resource> aes = mng.bridgedDiscovery(IN, r.getCsi(), 2, null);
				for(Resource ae : aes) {
					AEResource a = (AEResource) ae;
					copiedAE.add(createReferenceAE(a, copiedMN.get(copiedMN.size() - 1)));
					
					f = new ArrayList<String>();
					f.add("pi="+a.getPi());
					ArrayList<Resource> containers = mng.bridgedDiscovery(IN, r.getCsi(), 3, f);
					for(Resource container : containers) {
						ContainerResource cont = (ContainerResource) container;
						copiedCnt.add(createReferenceContainer(cont, copiedAE.get(copiedAE.size() - 1)));
						
						f = new ArrayList<String>();
						f.add("pi=" + cont.getPi());
						while((ArrayList<Resource> cnt = mng.bridgedDiscovery(IN, r.getCsi(), 3, f)) != null) {
							for(Resource cn : cnt) {
								ContainerResource c = (ContainerResource) cn;
								copiedCnt.add(createReferenceContainer(c, copiedAE.get(copiedAE.size() - 1)));
							
								
							}
						}
						f = new ArrayList<String>();
						f.add("pi="+c.getPi());
						ArrayList<Resource> containers = mng.bridgedDiscovery(IN, r.getCsi(), 3, f);
				}
			}
				
			try {
				Thread.sleep(period);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}