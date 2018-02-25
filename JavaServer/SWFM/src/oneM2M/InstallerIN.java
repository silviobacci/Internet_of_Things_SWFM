package oneM2M;

import java.util.ArrayList;
import org.json.simple.JSONObject;

import resources.*;


public class InstallerIN extends Thread{
	private OM2MManager mng;
	private boolean IN;
	private JSONObject json;
	
	private AEResource createReferenceCopy(ReferenceResource r) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("rn=" + r.getRi());
		
		ArrayList<Resource> copiedMN = mng.discovery(IN, 2, f);
		
		if(copiedMN == null) {
			copiedMN = new ArrayList<Resource>();
			json = mng.jsonAE(r.getRi()+"-ID", r.getRi(), true);
			copiedMN.add(mng.createAE(IN, json));
		}
		
		return (AEResource) copiedMN.get(0);
	}
	
	private ContainerResource createAECopy(AEResource ae, AEResource copiedMN) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("pi=" + copiedMN.getRi());
		f.add("rn=" + ae.getRi());
		
		ArrayList<Resource> copiedAE = mng.discovery(IN, 3, f);
		
		if(copiedAE == null) {
			copiedAE = new ArrayList<Resource>();
			json = mng.jsonContainer(ae.getRi());
			copiedAE.add(mng.createContainer(IN, copiedMN, json));
		}
		
		return (ContainerResource) copiedAE.get(0);
	}
	
	private ContainerResource creatContainerCopy(ContainerResource c, ContainerResource copiedAE) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("rn=" + c.getRi());
		f.add("pi=" + copiedAE.getRi());
		ArrayList<Resource> copiedContainer = mng.discovery(IN, 3, f);
		
		if(copiedContainer == null) {
			copiedContainer = new ArrayList<Resource>();
			json = mng.jsonContainer(c.getRi());
			copiedContainer.add(mng.createContainer(IN, copiedAE, json));
		}
		
		return (ContainerResource) copiedContainer.get(0);
	}
	
	private void createMN() {
		ArrayList<Resource> references = mng.discovery(IN, 1, null);
		for(Resource ref : references) {
			ReferenceResource r = (ReferenceResource) ref;
			AEResource copiedMN = createReferenceCopy(r);
			
			createAE(r, copiedMN);
		}
	}
		
	private void createAE(ReferenceResource r, AEResource copiedMN) {
		ArrayList<Resource> aes = mng.bridgedDiscovery(IN, r.getCsi(), 2, null);
		for(Resource ae : aes) {
			AEResource a = (AEResource) ae;
		
			ContainerResource copiedAE = createAECopy(a, copiedMN);
			
			createContainer(r, a, copiedAE);
		}
	}
	
	private void createContainer(ReferenceResource r, Resource ac, ContainerResource copiedAE) {
		ArrayList<String> f = new ArrayList<String>();
		f.add("pi=" + ac.getRi());
		
		ArrayList<Resource> containers = mng.bridgedDiscovery(IN, r.getCsi(), 3, f);
		for(Resource cont : containers) {
			ContainerResource c = (ContainerResource) cont;
			
			ContainerResource copiedContainer = creatContainerCopy(c, copiedAE);
			
			createContainer(r, c, copiedContainer);
		}
	}

	public InstallerIN(boolean isIN) {
		mng = new OM2MManager();
		IN = isIN;
	}

	@Override
	public void run() {
		createMN();
	}
}