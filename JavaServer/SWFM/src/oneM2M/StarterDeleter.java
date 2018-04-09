package oneM2M;

import java.util.ArrayList;

import resources.ContainerResource;
import resources.OM2MResource;

public class StarterDeleter {

	public static void main(String[] args) {
		boolean MN = true;
		String IP_ADDRESS_OM2M = "127.0.0.1"; 
		OM2MManager mng = new OM2MManager(IP_ADDRESS_OM2M);

		ArrayList<OM2MResource> ae = mng.discovery(MN, OM2MManager.AE);
		
		if(ae == null || ae.isEmpty()) {
			System.out.println("NULL");
			return;
		}
		
		//System.out.println(mng.deleteAE(MN, ae.get(0).getRi()));
		
		
		String filter = "lbl=" + ae.get(0).getRi();
		
		ArrayList<OM2MResource> containers = mng.discovery(MN, OM2MManager.CONTAINER, filter);
		ContainerResource container = (ContainerResource) containers.get(2);
		System.out.println(mng.deleteContainer(MN, container.getRi()));
		//System.out.println(mng.deleteContentInstance(MN, container.getLa()));
		 
	}

}
