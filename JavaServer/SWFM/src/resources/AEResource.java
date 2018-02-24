package resources;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class AEResource extends Resource {
	private String acpi; 
	private String et; 
	private String api;
	private String aei; 
	private boolean rr;
	
	public AEResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		// TODO Auto-generated constructor stub
	}
	
	public AEResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt, String _acpi, String _et, String _api, String _aei, boolean _rr) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		acpi = _acpi; 
		et = _et;
		api = _api;
		aei = _aei; 
		rr = _rr; 
		
	}
	
	public AEResource(String _rn, Integer _ty, String _ri, String _api, boolean _rr) {
		super(_rn,_ty,_ri);
		api = _api; 
		rr = _rr;
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
		}
	}

	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();
		
		jo.put("acpi", acpi);
		jo.put("et", et);
		jo.put("api", api);
		jo.put("aei", aei);
		jo.put("rr", rr);
		
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

	public boolean getRr() {
		return rr;
	}

	public void setRr(boolean rr) {
		this.rr = rr;
	}
}
