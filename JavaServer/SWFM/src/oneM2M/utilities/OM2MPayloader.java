package oneM2M.utilities;

import java.util.ArrayList;

import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class OM2MPayloader {
	private static JSONObject create_jsonAE(String api, String rn, Boolean rr, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		if(api != null) jo.put("api", api);
		if(rn != null) jo.put("rn", rn);
		if(rr != null) jo.put("rr", rr);
		if(lbl != null) jo.put("lbl", lbl);
		
		return jo;
	}
	
	public synchronized static JSONObject jsonAE(String api, String rn) {
		return create_jsonAE(api, rn, true, null);
	}
	
	public synchronized static JSONObject jsonAE(String api, String rn, ArrayList<String> lbl) {
		return create_jsonAE(api, rn, true, lbl);
	}
	
	public synchronized static JSONObject jsonAE(String api, String rn, boolean rr) {
		return create_jsonAE(api, rn, rr, null);
	}
	
	public synchronized static JSONObject jsonAE(String api, String rn, boolean rr, ArrayList<String> lbl) {
		return create_jsonAE(api, rn, rr, lbl);
	}
	
	public synchronized static JSONObject create_jsonContainer(String rn, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		jo.put("rn", rn);
		jo.put("lbl", lbl);

		return jo;
	}
	
	public synchronized static JSONObject jsonContainer(String rn) {
		return create_jsonContainer(rn, null);
	}
	
	public synchronized static JSONObject jsonContainer(String rn, ArrayList<String> lbl) {
		return create_jsonContainer(rn, lbl);
	}
	
	private static JSONObject create_jsonContentInstance(String rn, String cnf, Object con, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		if(rn != null) jo.put("rn", rn);
		if(cnf != null) jo.put("cnf", cnf);
		if(con != null) jo.put("con", con);
		if(lbl != null) jo.put("lbl", lbl);
		
		return jo;
	}
	
	public synchronized static JSONObject jsonContentInstance(String rn, String cnf, Object con, ArrayList<String> lbl) {
		return create_jsonContentInstance(rn, cnf, con, lbl);
	}
	
	public synchronized static JSONObject jsonContentInstance(String cnf, Object con, ArrayList<String> lbl) {
		return create_jsonContentInstance(null, cnf, con, lbl);
	}
	
	private static JSONObject create_jsonSubscription(String rn, String nu, Integer nct, ArrayList<String> lbl) {
		JSONObject jo = new JSONObject();
		if(rn != null) jo.put("rn", rn);
		if(nu != null) jo.put("nu", nu);
		if(nct != null) jo.put("nct", nct);
		if(lbl != null) jo.put("lbl", lbl);
		
		return jo;
	}
	
	public synchronized static JSONObject jsonSubscription(String rn, String nu, int nct) {
		return create_jsonSubscription(rn, nu, nct, null);
	}
	
	public synchronized static JSONObject jsonSubscription(String rn, String nu, int nct, ArrayList<String> lbl) {
		return create_jsonSubscription(rn, nu, nct, lbl);
	}
}
