package resources;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class SubscriptionResource extends OM2MResource {
	private JSONArray acpi;
	private JSONArray nu;
	private long nct;
	
	public SubscriptionResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}
	
	public SubscriptionResource(String _rn, long _ty, String _ri) {
		super(_rn, _ty, _ri);
	}

	public SubscriptionResource(String _rn, Integer _ty, String _ri, JSONArray _acpi, JSONArray _nu, long _nct) {
		super(_rn, _ty, _ri);
		acpi = _acpi;
		nu = _nu;
		nct = _nct;
	}

	public SubscriptionResource(String json) {
		super(json, "m2m:sub");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:sub");
			for(Object key : created.keySet()) {
				if(key.toString().equals("acpi")) acpi = (JSONArray) created.get(key);
				else if(key.toString().equals("nu")) nu = (JSONArray)created.get(key);
				else if(key.toString().equals("nct")) nct = (Long) created.get(key);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public SubscriptionResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("acpi")) acpi = (JSONArray) created.get(key);
			else if(key.toString().equals("nu")) nu = (JSONArray)created.get(key);
			else if(key.toString().equals("nct")) nct = (Long) created.get(key);
		}
	}

	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();
		
		jo.put("acpi", acpi);
		jo.put("nu", nu);
		jo.put("nct", nct);
		
		return jo;
	}

	public JSONArray getAcpi() {
		return acpi;
	}

	public void setAcpi(JSONArray acpi) {
		this.acpi = acpi;
	}

	public JSONArray getNu() {
		return nu;
	}

	public void setNu(JSONArray nu) {
		this.nu = nu;
	}

	public long getNct() {
		return nct;
	}

	public void setNct(long nct) {
		this.nct = nct;
	}
}
