package oneM2M.resources;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class OM2MResource  implements Comparable<OM2MResource> {
	private String rn; 
	private long 	ty; 
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
	
	public OM2MResource(String _rn, long _ty, String _ri, String _pi, String _ct, String _lt) {
		rn = _rn; 
		ty = _ty;
		ri = _ri;
		pi = _pi;
		ct = _ct;
		lt = _lt;
	}
	
	public OM2MResource(String _rn, long _ty, String _ri) {
		rn = _rn; 
		ty = _ty;
		ri = _ri;
	}
	
	public OM2MResource(String json, String type) {
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get(type);
			for(Object key : created.keySet()) {
				if(key.toString().equals("rn")) rn = created.get(key).toString();
				else if(key.toString().equals("ty")) ty = (Long) created.get(key);
				else if(key.toString().equals("ri")) ri = created.get(key).toString();
				else if(key.toString().equals("pi")) pi = created.get(key).toString();
				else if(key.toString().equals("ct")) ct = created.get(key).toString();
				else if(key.toString().equals("lt")) lt = created.get(key).toString();
				else if(key.toString().equals("lbl")) lt = created.get(key).toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public OM2MResource(JSONObject created) {
		for(Object key : created.keySet()) {
			if(key.toString().equals("rn")) rn = created.get(key).toString();
			else if(key.toString().equals("ty")) ty = (Long) created.get(key);
			else if(key.toString().equals("ri")) ri = created.get(key).toString();
			else if(key.toString().equals("pi")) pi = created.get(key).toString();
			else if(key.toString().equals("ct")) ct = created.get(key).toString();
			else if(key.toString().equals("lt")) lt = created.get(key).toString();
			else if(key.toString().equals("lt")) lt = created.get(key).toString();
		}
	}
	
	public int compareTo(OM2MResource o) {
		SimpleDateFormat d_format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		Date d1 = null, d2 = null;
		
		try {
			d1 = d_format.parse(ct);
			d2 = d_format.parse(o.getCt());
		} 
		catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		if(d1 == null || d2 == null)
			return 0;
		
		return d1.compareTo(d2);
	}
	
	public String getRn() {
		return rn;
	}

	public void setRn(String rn) {
		this.rn = rn;
	}

	public long getTy() {
		return ty;
	}

	public void setTy(long ty) {
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
