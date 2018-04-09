package resources;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class ReferenceResource extends OM2MResource {
	private JSONArray acpi;
	private JSONArray poa;
	private String cb;
	private String csi;
	private boolean rr;
	private ArrayList<String> lbl;

	public ReferenceResource(String _rn, long _ty, String _ri, String _pi, String _ct, String _lt, String _lbl) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}
	
	public ReferenceResource(String _rn, long _ty, String _ri) {
		super(_rn, _ty, _ri);
	}
	
	public ReferenceResource(String _rn, long _ty, String _ri, String _pi, String _ct, String _lt, JSONArray _acpi, JSONArray _poa, String _cb, String _csi, boolean _rr, ArrayList<String> _lbl) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		acpi = _acpi;
		poa = _poa;
		cb = _cb;
		csi = _csi;
		rr = _rr;
		lbl = _lbl;
	}
	
	public ReferenceResource(String json) {
		super(json, "m2m:csr");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:csr");
			for(Object key : created.keySet()) {
				if(key.toString().equals("acpi")) acpi = (JSONArray) created.get(key);
				else if(key.toString().equals("poa")) poa = (JSONArray)created.get(key);
				else if(key.toString().equals("cb")) cb = created.get(key).toString();
				else if(key.toString().equals("csi")) csi = created.get(key).toString();
				else if(key.toString().equals("rr")) rr = (Boolean) created.get(key);
				else if(key.toString().equals("lbl")) lbl = (ArrayList<String>) created.get(key);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public ReferenceResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("acpi")) acpi = (JSONArray) created.get(key);
			else if(key.toString().equals("poa")) poa = (JSONArray)created.get(key);
			else if(key.toString().equals("cb")) cb = created.get(key).toString();
			else if(key.toString().equals("csi")) csi = created.get(key).toString();
			else if(key.toString().equals("rr")) rr = (Boolean) created.get(key);
			else if(key.toString().equals("lbl")) lbl = (ArrayList<String>) created.get(key);
		}
	}
	
	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();
		
		jo.put("acpi", acpi);
		jo.put("poa", poa);
		jo.put("cb", cb);
		jo.put("csi", csi);
		jo.put("rr", rr);
		jo.put("lbl", lbl);
		
		return jo;
	}

	public JSONArray getAcpi() {
		return acpi;
	}

	public void setAcpi(JSONArray acpi) {
		this.acpi = acpi;
	}

	public JSONArray getPoa() {
		return poa;
	}

	public void setPoa(JSONArray poa) {
		this.poa = poa;
	}

	public String getCb() {
		return cb;
	}

	public void setCb(String cb) {
		this.cb = cb;
	}

	public String getCsi() {
		return csi;
	}

	public void setCsi(String csi) {
		this.csi = csi;
	}

	public boolean isRr() {
		return rr;
	}

	public void setRr(boolean rr) {
		this.rr = rr;
	}

	public ArrayList<String> getLbl() {
		return lbl;
	}

	public void setLbl(ArrayList<String> lbl) {
		this.lbl = lbl;
	}
}
