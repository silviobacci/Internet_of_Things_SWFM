package resources;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class InstanceResource extends OM2MResource {
	private long st; 
	private String cnf;
	private long cs; 
	private Object con;
	private String lbl;
  
	public InstanceResource(String _rn, long _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}

	public InstanceResource(String _rn, long _ty, String _ri) {
		super(_rn, _ty, _ri);
	}

	public InstanceResource(String _rn, long _ty, String _ri, String _pi, String _ct, String _lt, long _st, String _cnf, long _cs, Object _con, String _lbl) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		st = _st; 
		cnf = _cnf;
		cs = _cs; 
		con = _con;
		lbl = _lbl;
	}
	
	public InstanceResource(String json) {
		super(json, "m2m:cin");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:cin");
			
			for(Object key : created.keySet()) {
				if(key.toString().equals("st")) st = (Long) created.get(key);
				else if(key.toString().equals("cnf")) cnf = created.get(key).toString();
				else if(key.toString().equals("cs")) cs = (Long) created.get(key);
				else if(key.toString().equals("con")) con = created.get(key);
				else if(key.toString().equals("lbl")) lbl = created.get(key).toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public InstanceResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("st")) st = (Long) created.get(key);
			else if(key.toString().equals("cnf")) cnf = created.get(key).toString();
			else if(key.toString().equals("cs")) cs = (Long) created.get(key);
			else if(key.toString().equals("con")) con = created.get(key);
			else if(key.toString().equals("lbl")) lbl = created.get(key).toString();
		}
	}
  
	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();

		jo.put("st", st);
		jo.put("cnf", cnf);
		jo.put("cs", cs);
		jo.put("con", con);
		jo.put("lbl", lbl);
	
		return jo;		
	}

	public long getSt() {
		return st;
	}

	public void setSt(long st) {
		this.st = st;
	}

	public String getCnf() {
		return cnf;
	}

	public void setCnf(String cnf) {
		this.cnf = cnf;
	}

	public long getCs() {
		return cs;
	}

	public void setCs(long cs) {
		this.cs = cs;
	}

	public Object getCon() {
		return con;
	}

	public void setCon(Object con) {
		this.con = con;
	}

	public String getLbl() {
		return lbl;
	}

	public void setLbl(String lbl) {
		this.lbl = lbl;
	}
}
