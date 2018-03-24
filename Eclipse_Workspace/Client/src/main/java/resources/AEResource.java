package resources;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class AEResource extends OM2MResource {
	private String acpi; 
	private String et; 
	private String api;
	private String aei; 
	private boolean rr;
	private String lbl;

	public AEResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}
	
	public AEResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt , String _lbl, String _acpi, String _et, String _api, String _aei, boolean _rr) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		acpi = _acpi; 
		et = _et;
		api = _api; 
		aei = _aei; 
		rr = _rr; 
		lbl = _lbl;
	}
	
	public AEResource(String _rn, Integer _ty, String _ri, String _api, boolean _rr, String _lbl) {
		super(_rn, _ty, _ri);
		api = _api; 
		rr = _rr;
		lbl = _lbl;
	}
	
	public AEResource(String json) {
		super(json, "m2m:ae");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:ae");
			for(Object key : created.keySet()) {
				if(key.toString().equals("acpi")) acpi = created.get(key).toString();
				else if(key.toString().equals("et")) et = created.get(key).toString();
				else if(key.toString().equals("api")) api = created.get(key).toString();
				else if(key.toString().equals("aei")) aei = created.get(key).toString();
				else if(key.toString().equals("rr")) rr = (Boolean) created.get(key);
				else if(key.toString().equals("lbl")) lbl = created.get(key).toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public AEResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("acpi")) acpi = created.get(key).toString();
			else if(key.toString().equals("et")) et = created.get(key).toString();
			else if(key.toString().equals("api")) api = created.get(key).toString();
			else if(key.toString().equals("aei")) aei = created.get(key).toString();
			else if(key.toString().equals("rr")) rr = (Boolean) created.get(key);
			else if(key.toString().equals("lbl")) lbl = created.get(key).toString();
		}
	}

	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();
		
		jo.put("acpi", acpi);
		jo.put("et", et);
		jo.put("api", api);
		jo.put("aei", aei);
		jo.put("rr", rr);
		jo.put("lbl", lbl);
		
		return jo;	
	}

	public String getAcpi() {
		return acpi;
	}

	public void setAcpi(String acpi) {
		this.acpi = acpi;
	}

	public String getEt() {
		return et;
	}

	public void setEt(String et) {
		this.et = et;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getAei() {
		return aei;
	}

	public void setAei(String aei) {
		this.aei = aei;
	}

	public boolean isRr() {
		return rr;
	}

	public void setRr(boolean rr) {
		this.rr = rr;
	}

	public String getLbl() {
		return lbl;
	}

	public void setLbl(String lbl) {
		this.lbl = lbl;
	}
}
