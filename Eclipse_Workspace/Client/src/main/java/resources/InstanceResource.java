package resources;

import org.json.simple.JSONObject;

public class InstanceResource extends Resource {

	  private int st; 
	  private String cnf;
	  private int cs; 
	  private int con;
	  
	  public InstanceResource(String _rn, int _ty, String _ri, String _pi, String _ct, String _lt) {
			super(_rn, _ty, _ri, _pi, _ct, _lt);
			// TODO Auto-generated constructor stub
		}
	  
	  public InstanceResource(String _rn, Integer _ty, String _ri) {
		super(_rn, _ty, _ri);
		// TODO Auto-generated constructor stub
	}
	  
	  public InstanceResource(String _rn, Integer _ty, String _ri, String _pi, String _ct, String _lt, int _st, String _cnf, int _cs, int _con) {
			super(_rn, _ty, _ri, _pi, _ct, _lt);
			// TODO Auto-generated constructor stub
			st = _st; 
			cnf = _cnf;
			cs = _cs; 
			con = _con;
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
