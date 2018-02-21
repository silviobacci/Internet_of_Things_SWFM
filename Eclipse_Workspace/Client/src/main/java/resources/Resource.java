package resources;

import org.json.simple.JSONObject;

public class Resource {
	private String rn; 
	private int 	ty; 
	private String 	ri;
	private String 	pi;
	private String 	ct;
	private String 	lt;
	
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		
		jo.put("rn", rn);
		jo.put("ty", ty);
		jo.put("ri", ri);
		jo.put("pi", pi);
		jo.put("ct", ct);
		jo.put("lt", lt);
		
		return jo;
		
	}
	
	public Resource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt ) {
		rn = _rn; 
		ty = _ty.intValue();
		ri = _ri;
		pi = _pi;
		ct = _ct;
		lt = _lt;
	}
	
	public Resource(String _rn, Integer _ty, String _ri) {
		rn = _rn; 
		ty = _ty.intValue();
		ri = _ri;
	}
	
	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public int getTy() {
		return ty;
	}

	public void setTy(int ty) {
		this.ty = ty;
	}

	public String getRi() {
		return ri;
	}

	public void setRi(String ri) {
		this.ri = ri;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public String getCt() {
		return ct;
	}

	public void setCt(String ct) {
		this.ct = ct;
	}

	public String getLt() {
		return lt;
	}

	public void setLt(String lt) {
		this.lt = lt;
	}
}
