package resources;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class InstanceResource extends Resource {

	private int st; 
	private String cnf;
	private int cs; 
	private int con;
  
	public InstanceResource(String _rn, int _ty, String _ri, String _pi, String _ct, String _lt) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
	}
  
	public InstanceResource(String _rn, Integer _ty, String _ri) {
		super(_rn, _ty, _ri);
	}
  
	public InstanceResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt, int _st, String _cnf, int _cs, int _con) {
		super(_rn, _ty, _ri, _pi, _ct, _lt);
		st = _st; 
		cnf = _cnf;
		cs = _cs; 
		con = _con;
	}
	
	public InstanceResource(String json) {
		super(json, "m2m:cin");
		try {
			JSONObject created = (JSONObject) JSONValue.parseWithException(json);
			created = (JSONObject) created.get("m2m:cin");
			
			for(Object key : created.keySet()) {
				if(key.toString().equals("st")) st = (Integer) created.get(key);
				else if(key.toString().equals("cnf")) cnf = created.get(key).toString();
				else if(key.toString().equals("cs")) cs = (Integer) created.get(key);
				else if(key.toString().equals("con")) con = (Integer) created.get(key);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public InstanceResource(JSONObject created) {
		super(created);
		for(Object key : created.keySet()) {
			if(key.toString().equals("st")) st = (Integer) created.get(key);
			else if(key.toString().equals("cnf")) cnf = created.get(key).toString();
			else if(key.toString().equals("cs")) cs = (Integer) created.get(key);
			else if(key.toString().equals("con")) con = (Integer) created.get(key);
		}
	}
  
	public JSONObject toJSON() {
		JSONObject jo = super.toJSON();

		jo.put("st", st);
		jo.put("cnf", cnf);
		jo.put("cs", cs);
		jo.put("con", con);

	
		return jo;		
	}

	public int getSt() {
		return st;
	}

	public void setSt(int st) {
		this.st = st;
	}

	public String getCnf() {
		return cnf;
	}

	public void setCnf(String cnf) {
		this.cnf = cnf;
	}

	public int getCs() {
		return cs;
	}

	public void setCs(int cs) {
		this.cs = cs;
	}

	public int getCon() {
		return con;
	}

	public void setCon(int con) {
		this.con = con;
	}
	

}
