package resources;

import org.json.simple.JSONObject;

public class ContainerResource extends Resource {
	

	private String acpi; 
	private String et; 
	private int st; 
	private int mni; 
	private int mbs; 
	private int mia; 
	private int cni; 
	private int cbs; 
	private String ol;
	private String la;
	

	public ContainerResource(String _rn, Integer _ty, String _ri) {
		super(_rn, _ty, _ri);
		// TODO Auto-generated constructor stub
	}
	
	public ContainerResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		// TODO Auto-generated constructor stub
	}
	
	public ContainerResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt, String _acpi, String _et, Integer _st,
			Integer _mni, Integer _mbs, Integer _mia, Integer _cni, Integer _cbs, String _ol, String _la) {
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
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	public int getMni() {
		return mni;
	}
	public void setMni(int mni) {
		this.mni = mni;
	}
	public int getMbs() {
		return mbs;
	}
	public void setMbs(int mbs) {
		this.mbs = mbs;
	}
	public int getMia() {
		return mia;
	}
	public void setMia(int mia) {
		this.mia = mia;
	}
	public int getCni() {
		return cni;
	}
	public void setCni(int cni) {
		this.cni = cni;
	}
	public int getCbs() {
		return cbs;
	}
	public void setCbs(int cbs) {
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
	
	
	
	
}