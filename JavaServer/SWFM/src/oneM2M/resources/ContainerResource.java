package oneM2M.resources;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class ContainerResource extends OM2MResource {
	private String acpi; 
	private String et; 
	private long st; 
	private long mni; 
	private long mbs; 
	private long mia; 
	private long cni; 
	private long cbs; 
	private String ol;
	private String la;
	private ArrayList<String> lbl;
	

	public ContainerResource(String _rn, Long _ty, String _ri) {
		super(_rn, _ty, _ri);
	}
	
	public ContainerResource(String _rn, Long _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}
	
	public ContainerResource(String _rn, Long _ty, String _ri, String _pi, String _ct, String _lt, ArrayList<String> _lbl, String _acpi, String _et, Long _st,
			Long _mni, Long _mbs, Long _mia, Long _cni, Long _cbs, String _ol, String _la) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		acpi = _acpi; 
		et = _et;
		st = _st; 
		mni = _mni.intValue();
		mbs = _mbs.intValue();
		mia = _mia.intValue();
		cni = _cni.intValue(); 
		cbs = _cbs.intValue(); 
		ol = _ol; 
		la = _la;
		lbl = _lbl;
	}
	
	public ContainerResource(String json) {
		super(json, "m2m:cnt");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:cnt");
			for(Object key : created.keySet()) {
				if(key.toString().equals("acpi")) acpi = created.get(key).toString();
				else if(key.toString().equals("et")) et = created.get(key).toString();
				else if(key.toString().equals("st")) st = (Long) created.get(key);
				else if(key.toString().equals("mni")) mni = (Long) created.get(key);
				else if(key.toString().equals("mbs")) mbs = (Long) created.get(key);
				else if(key.toString().equals("mia")) mia = (Long) created.get(key);
				else if(key.toString().equals("cni")) cni = (Long) created.get(key);
				else if(key.toString().equals("cbs")) cbs = (Long) created.get(key);
				else if(key.toString().equals("ol")) ol = created.get(key).toString();
				else if(key.toString().equals("la")) la = created.get(key).toString();
				else if(key.toString().equals("lbl")) lbl = (ArrayList<String>) created.get(key);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public ContainerResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("acpi")) acpi = created.get(key).toString();
			else if(key.toString().equals("et")) et = created.get(key).toString();
			else if(key.toString().equals("st")) st = (Long) created.get(key);
			else if(key.toString().equals("mni")) mni = (Long) created.get(key);
			else if(key.toString().equals("mbs")) mbs = (Long) created.get(key);
			else if(key.toString().equals("mia")) mia = (Long) created.get(key);
			else if(key.toString().equals("cni")) cni = (Long) created.get(key);
			else if(key.toString().equals("cbs")) cbs = (Long) created.get(key);
			else if(key.toString().equals("ol")) ol = created.get(key).toString();
			else if(key.toString().equals("la")) la = created.get(key).toString();
			else if(key.toString().equals("lbl")) lbl = (ArrayList<String>) created.get(key);
		}
	}

	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();
		
		jo.put("acpi", acpi);
		jo.put("et", et);
		jo.put("st", st);
		jo.put("mni", mni);
		jo.put("mbs", mbs);
		jo.put("mia", mia);
		jo.put("cni", cni);
		jo.put("cbs", cbs);
		jo.put("ol", ol);
		jo.put("la", la);
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
	public long getSt() {
		return st;
	}
	public void setSt(long st) {
		this.st = st;
	}
	public long getMni() {
		return mni;
	}
	public void setMni(long mni) {
		this.mni = mni;
	}
	public long getMbs() {
		return mbs;
	}
	public void setMbs(long mbs) {
		this.mbs = mbs;
	}
	public long getMia() {
		return mia;
	}
	public void setMia(long mia) {
		this.mia = mia;
	}
	public long getCni() {
		return cni;
	}
	public void setCni(long cni) {
		this.cni = cni;
	}
	public long getCbs() {
		return cbs;
	}
	public void setCbs(long cbs) {
		this.cbs = cbs;
	}
	public String getOl() {
		return ol;
	}
	public void setOl(String ol) {
		this.ol = ol;
	}
	public String getLa() {
		return la;
	}
	public void setLa(String la) {
		this.la = la;
	}

	public ArrayList<String> getLbl() {
		return lbl;
	}

	public void setLbl(ArrayList<String> lbl) {
		this.lbl = lbl;
	} 
}
